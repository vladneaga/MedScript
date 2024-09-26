<template>
    <div v-if="patient" class="patient-details-container">
      <div class="patient-info-header">
    <h1 >Patient Information</h1>
    <div v-if="forAdmin">
        <router-link :to="{ name: 'EditPatientForm', params: { id: patient.id } }">
        <button>Edit Patient</button>
        </router-link>
    </div>
  </div>
      
      <div class="patient-info">
        <p><strong>Name:</strong> {{ patient.fullName }}</p>
        <p><strong>Birth Date:</strong> {{ formatDate(patient.birthDate) }}</p>
        <p><strong>Address:</strong> {{ patient.address }}</p>
        <p><strong>Email:</strong> {{ patient.account.email }}</p>
        <p><strong>Phone:</strong> {{ patient.account.phoneNumber }}</p>
      </div>
  
      <!-- Conditionally render the button based on whether physicianId is in patient.physicianIds -->
      <div class="button-header">
        <div v-if="isAssigned || forAdmin">
            <div v-if="isAssigned" class="button-container">
                <button @click="removePatient" class="btn btn-remove" >Remove {{ this.patient.fullName }} from assigned patients</button>
            </div>
            <div class="button-container">
                <button @click="togglePrescriptions" class="">{{!showPrescriptions ? 'Show Prescriptions' : 'Hide Prescriptions'}}</button>
                <button v-if="isAssigned" @click="togglePrescriptionForm" class="">{{!showPrescriptionForm ? 'Prescribe a recipe' : 'Hide prescription form'}}</button>
            </div>
        </div>
    </div>
            <div v-show="(showPrescriptions && isAssigned) || (forAdmin && showPrescriptions)">
                <div class="info-section">
                    <PrescriptionTable  :key="tableKey" :forAdmin="forAdmin"   :physicianInPatientDetails="forPhysician ? true : false" :patientId="id" :sortFieldOptional="sortField"/>
                </div>
            </div>
            <div v-show="showPrescriptionForm && forPhysician">
                <PrescriptionForm :id="id" @prescription-success="prescriptionSuccess"/>
            </div>
       
        <div v-if="!isAssigned && forPhysician">
            <button  @click="assignPatient" class="btn btn-assign">Assign Patient</button>
        </div>

        <div v-if="showSuccessAlert" class="info-box">Successfully added a prescription for {{ this.patient.fullName }}</div>
    

        
      
    </div>
    <div v-else class="loading-container">
      <p>Loading patient details...</p>
    </div> 
  </template>
  
  <script>
  import axios from 'axios'
  import PrescriptionTable from '../prescriptionsList/PrescriptionTable.vue';
import PrescriptionForm from '../prescriptionsList/PrescriptionForm.vue';
  export default {
    name: 'PatientDetails',
    props: {
      id: {
        type: Number,
        required: true
      },
    },
    components: {
      PrescriptionTable,
      PrescriptionForm,
    },
    data() {
      return {
        patient: null,
        forPhysician: false,
        forAdmin: false,
        physicianId: null,
        isAssigned: false, // To track if the patient is assigned to the current physician,
        showPrescriptions: true,
        showPrescriptionForm: false,
        showSuccessAlert: false,
        sortField: null,
        tableKey: 0,  // Re-render the prescription table after the new prescription has been assigned




      };
    },
        async created() {
      this.forPhysician = this.$route.query.forPhysician === 'true';
      this.forAdmin = this.$route.query.forAdmin === 'true';
      this.physicianId = Number(this.$route.query.physicianId);
      console.log('forAdmin', this.forAdmin)
      console.log('forPhysician', this.forPhysician)
      console.log('phisId', this.physicianId)
      console.log('patId', this.id)
      this.fetchPatientDetails();
    },
    methods: {
        togglePrescriptions() {
            this.showPrescriptions = !this.showPrescriptions
        },
        togglePrescriptionForm() {
            this.showPrescriptionForm =! this.showPrescriptionForm
        },

      formatDate(date) {
        const options = { year: 'numeric', month: 'long', day: 'numeric' };
        return new Date(date).toLocaleDateString(undefined, options);
      },
      async fetchPatientDetails() {
        try {
          const token = localStorage.getItem('token');
          let response;
          if (this.forPhysician) {
            response = await axios.get(`/physician/patient/${this.id}`, {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            });
          
          this.patient = response.data.patient;
          console.log(this.patient)
          this.isAssigned = response.data.physicianIds.includes(this.physicianId); // Check if the patient is assigned to the current physician
          console.log(this.isAssigned)
        } else if(this.forAdmin) {
          console.log(this.forAdmin)
          response = await axios.get(`/admin/patient/details/${this.id}`, {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            });
            this.patient = response.data.patient;
            console.log(this.patient)
        }
        } catch (error) {
          console.error('Error fetching patient details:', error);
        }
      },
      async assignPatient() {
        try {
          const token = localStorage.getItem('token');
          await axios.get(`/physician/assignPatient/${this.id}`, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          this.isAssigned = true; // Update the state
        } catch (error) {
          console.error('Error assigning patient:', error);
        }
      },
      async removePatient() {
        try {
          const token = localStorage.getItem('token');
          await axios.get(`/physician/removePatient/${this.id}`, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          this.isAssigned = false; // Update the state
          this.showPrescriptions= false;
          this.showPrescriptionForm = false;

        } catch (error) {
          console.error('Error removing patient:', error);
        }
      },
      prescriptionSuccess() {
        this.showPrescriptionForm = false,
        this.showPrescriptions = true,

        this.sortField = 'creationDate'
        this.tableKey += 1; //force the prescription table to reload by changing the key property
        this.showSuccessAlert = true;
              setTimeout(() => {
                this.showSuccessAlert = false;
              }, 2000); 
        

    }
    },


  };
  </script>
  
  <style >
  
.info-section {
  margin-bottom: 20px;
}

.info-section h3 {
  border-bottom: 2px solid #007bff;
  padding-bottom: 5px;
  color: #007bff;
}
  .info-section p {
  margin: 5px 0;
  color: #007bff;
}

.info-section ul {
  list-style-type: none;
  padding: 0;
}

.info-section ul li {
  background: #b9b5b5;
  margin: 5px 0;
  padding: 10px;
  border-radius: 5px;
}
  .patient-details-container {
    padding: 20px;
    border: 1px solid #ccc;
    border-radius: 8px;
    background-color: #f9f9f9;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .patient-details-container h1 {
    font-size: 24px;
    color: #007bff;
    margin-bottom: 20px;
  }
  
  .patient-info p {
    font-size: 18px;
    margin: 5px 0;
    color:#4e4b4b
  }
  .button-header {
    margin-top: 20px;
  }
  .button-container {
    display: flex;
    flex-flow: row nowrap;
    justify-content: space-evenly;
  }
  button {
  display: inline-block;
  margin-top: 10px;
  padding: 10px 20px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}
  .btn {
    display: inline-block;
    margin-top: 10px;
    padding: 10px 20px;
    color: #fff;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 16px;
  }
  
  .btn-remove {
    background-color: #ff4d4d;
  }
  
  .btn-remove:hover {
    background-color: #cc0000;
  }
  
  .btn-assign {
    background-color: #4CAF50;
  }
  
  .btn-assign:hover {
    background-color: #45a049;
  }
  
  .loading-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
    font-size: 18px;
  }
  .info-box {
    position: fixed;
    top: 100px;
    left: 50%;
    transform: translateX(-50%);
    background-color: #28a745;
    color: white;
    padding: 10px 20px;
    border-radius: 5px;
    border-style: solid;
    border-width: 2px;
    border-color: black;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease-in-out;
    z-index: 1000;
  }
  .patient-info-header {
  display: flex;
  align-items: center;
}

.patient-info-header h1 {
  margin-right: 20px;
  cursor: pointer;
}

.patient-info-header button {
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.patient-info-header button:hover {
  background-color: #0056b3;
}
  </style>