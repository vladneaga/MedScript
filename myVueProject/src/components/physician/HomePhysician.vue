<template>
  <div class="physician-info">

    <!-- Toggle Tab Button in the top-left corner -->
    <button @click="toggleShowTab"  :class="{'corner-toggle-button': true, 'red-button': !showTab}">
      {{ showTab ? 'Change signature' : 'Cancel' }}
    </button>

    <div v-if="!showTab">
      <SignaturePad @signature-submitted="toggleShowTab"/>
    </div>

    <div v-if="physician && showTab">
      <h1 @click="toggleInfo">Physician Information</h1>
      <div>
        <div v-show="showInfo">
          <PhysicianInfo :physician="physician" />
        </div>
        <div></div>
      </div>

      <button @click="togglePrescriptionList" class="toggle-button">
        {{ showPrescriptions ? 'Hide Prescriptions' : 'Show Prescriptions' }}
      </button>
      <button @click="toggleAssignedPatientList" class="toggle-button">
        {{ showAssignedPatients ? 'Hide Assigned Patients' : 'Show Assigned Patients' }}
      </button>
      <button @click="toggleShowSearchBarAndResults" class="toggle-button">
        {{ showSearchBarAndResults ? 'Hide Search Bar' : 'Show Search Bar' }}
      </button>

      <div class="prescriptions" v-show="showPrescriptions">
        <div class="info-section">
          <PrescriptionTable :forPhysician="true" />
        </div>
      </div>
      <div class="patients" v-show="showAssignedPatients">
        <PatientList :patients="assignedPatients" :physicianId="physicianId" />
      </div>
      <div class="patients" v-show="showSearchBarAndResults">
        <input type="text" v-model="searchString" @keyup.enter="searchPatients"
          placeholder="Search patients... Date format: yyyy-mm-dd" />
        <PatientList :patients="resultPatients" :physicianId="physicianId" />
        <div>{{ physician.id }}</div> <!-- Check if physician.id is available -->
        <!-- implement result preview  -->
      </div>
    </div>
  </div>
</template>
  <script>
  import axios from 'axios';
  import PhysicianInfo from './PhysicianInfo.vue';
  import PrescriptionTable from '../prescriptionsList/PrescriptionTable.vue';
  import PatientList from '../patientList/PatientList.vue';
  import SignaturePad from './SignaturePad.vue';
  
  export default {
    name: 'HomePhysician',
    components: {
      PhysicianInfo,
      PrescriptionTable,
      PatientList,
      SignaturePad,
    },
    data() {
      return {
        physician: null,
        showInfo: true, 
        showTab: true,
        showPrescriptions: false,
        showAssignedPatients: false,
        showSearchBarAndResults: false,
        assignedPatients: [],
        resultPatients: [],
        searchString: '',
        physicianId: null,

      };
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
      toggleShowSearchBarAndResults() {
        this.showSearchBarAndResults = !this.showSearchBarAndResults;
      },
      toggleShowTab() {
        this.showTab = !this.showTab;
        
      },
      async searchPatients() {
       
  
        try {
          const token = localStorage.getItem('token');
          const response = await axios.get(`/physician/listPatients?searchString=${this.searchString}`, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          this.resultPatients = response.data.allPatients;
        } catch (error) {
          console.error('Error searching patients:', error);
        }
      },
    },
    async created() {
      try {
        const token = localStorage.getItem('token');
        let response = await axios.get('/physician/getDetails', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
  
        this.physician = response.data.physician;
        response = await axios.get('/physician/listAssignedPatients', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        this.assignedPatients = response.data.allPatients
        this.physicianId = this.physician.id
        console.log(this.physicianId)
        
      } catch (error) {
        console.error('Error fetching physician details:', error);
        this.$router.push('/login');
      }
    },
  };
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
  
 /* Styles for the button in the top-left corner */
 .physician-info {

  max-width: 1200px; /* Set a max-width for large screens */
  width: 90%; /* Width as a percentage of the viewport */
  margin: 0 auto; /* Center the container */

  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background-color: #f9f9f9;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: auto; /* Ensure proper layout when using float */
  position: relative; /* Added to position child elements absolutely */
}

.corner-toggle-button {
  position: absolute; /* Set to absolute to position it freely */
  top: 10px; /* Adjust to your desired position */
  right: 10px; /* Adjust to your desired position */
  padding: 10px;
  background-color: #000000;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.corner-toggle-button:hover {
  background-color: #37383a;
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
.red-button {
  background-color: red; /* Red color when showTab is false */
}
@media (max-width: 768px) {
  .physician-info {
    padding: 10px; /* Less padding on smaller screens */
  }

  .corner-toggle-button {
    top: 5px; /* Adjust positioning for smaller screens */
    right: 5px; 
  }
}
  </style>