<template>
    <div v-if="physician" class="physician-info">
      <!-- <h1 @click="toggleInfo">Physician Information</h1> -->
      <div class="physician-info-header">
    <h1 @click="toggleInfo">Physician Information</h1>
    <div v-if="forAdmin">
        <router-link :to="{ name: 'EditPhysicianForm', params: { id: physician.id } }">
        <button>Edit Physician</button>
        </router-link>
    </div>
  </div>
      <div v-show="showInfo">
        <PhysicianInfo :physician="physician" />
      </div>
      <button @click="togglePrescriptionList" class="toggle-button">
        {{ showPrescriptions ? 'Hide Prescriptions' : 'Show Prescriptions' }}
      </button>
      <button @click="toggleShowSearchBarAndResultsAssignedPatients" class="toggle-button">
        {{ showSearchBarAndResultsAssignedPatients ? 'Hide Search Bar' : 'Search for assigned patients' }}
      </button>



      <div class="prescriptions"  v-show="showPrescriptions"> 
        <div class="info-section">
            <PrescriptionTable :forAdminInPhysician="true" :physicianId="physician.id"/>
        </div>
      </div>
      <div class="patients" v-show="showSearchBarAndResultsAssignedPatients">
        <input type="text" v-model="searchString" @keyup.enter="searchInAssignedPatients" placeholder="Search for assigned patients... Date format: yyyy-mm-dd" />
        <PatientList :patients="resultPatients ? resultPatients : assignedPatients" :physicianId="id" :forAdmin="forAdmin"/>
        <div>{{ physician.id }}</div> <!-- Check if physician.id is available -->
        <!-- implement result preview  -->
      </div>
    </div>
  </template>

<script>
import axios from 'axios';
import PhysicianInfo from '../physician/PhysicianInfo.vue';
import PrescriptionTable from '../prescriptionsList/PrescriptionTable.vue';
import PatientList from '../patientList/PatientList.vue';

export default {
    name: 'PhysicianDetails',
    props: {
       id: {
         type: Number,
         required: true
       },
    },
    components: {
      PhysicianInfo,
      PrescriptionTable,
      PatientList,

    },
    data() {
        return {
            physician: null,
            forAdmin: false,
            forPhysician: false,
            adminId: null,
            showInfo: true,
            showPrescriptions: false,
            showAssignedPatients: false,
            showSearchBarAndResultsAssignedPatients: false,
            assignedPatients: [],
            resultPatients: [],
            searchString: null,
          





        }
   

    },
    async created() {
      this.forPhysician = this.$route.query.forPhysician === 'true';
      this.forAdmin = this.$route.query.forAdmin === 'true';
      this.adminId = Number(this.$route.query.adminId);
      this.fetchPhysicianDetails();
    },
    methods: {
        toggleInfo() {
        this.showInfo = !this.showInfo;
      },
      togglePrescriptionList() {
        this.showPrescriptions = !this.showPrescriptions;
      },
      toggleAssignedPatientList() {
        this.showAssignedPatients = !this.showAssignedPatients;
      },
      toggleShowSearchBarAndResultsAssignedPatients() {
        this.showSearchBarAndResultsAssignedPatients = !this.showSearchBarAndResultsAssignedPatients;
      },
      async searchInAssignedPatients() {
        
       
  
       try {
         const token = localStorage.getItem('token');
         if(!this.searchString) {
          this.searchString = ''
         }
         const response = await axios.get(`/admin/physician/listAssignedPatients/${this.id}?searchString=${this.searchString}`, {
           headers: {
             Authorization: `Bearer ${token}`,
           },
         });
         this.resultPatients = response.data.allPatients;
         console.log('All assigned patients', this.resultPatients)
       } catch (error) {
         console.error('Error searching patients:', error);
       }
     



 
      },
     async fetchPhysicianDetails() {
        try {
          const token = localStorage.getItem('token');
          let response;
          let url;
          if(this.forAdmin) {
            url = `/admin/physician/details/${this.id}`
            console.log(url)
          }
           response = await axios.get(url, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          this.physician = response.data.physician;
          if(this.forAdmin) {
            url = `/admin/physician/listAssignedPatients/${this.id}`
            console.log(url)
          }
          response = await axios.get(url, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          this.assignedPatients = response.data.allPatients;

        } catch (error) {
          console.error('Error searching patients:', error);
        }
      }
    }
}
</script>

<style>
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
  
  .physician-info {
    padding: 20px;
    border: 1px solid #ccc;
    border-radius: 8px;
    background-color: #f9f9f9;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .physician-info h1 {
    font-size: 24px;
    margin-bottom: 10px;
    cursor: pointer;
    color: #007bff;
  }
  
  .physician-info h1:hover {
    color: #0056b3;
  }
  
  .physician-info p {
    font-size: 18px;
    margin: 5px 0;
  }
  
  .physician-info strong {
    color: #007bff;
  }
  
  .patients input[type="text"] {
    display: block;
    width: 100%;
    padding: 10px;
    margin-bottom: 20px;
    border: 1px solid #ccc;
    border-radius: 4px;
  }
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
.physician-info-header {
  display: flex;
  align-items: center;
}

.physician-info-header h1 {
  margin-right: 20px;
  cursor: pointer;
}

.physician-info-header button {
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.physician-info-header button:hover {
  background-color: #0056b3;
}
</style>