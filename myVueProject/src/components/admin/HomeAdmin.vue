<template>
  <div class="patient-home">
    <div v-if="user" class="patient-info">
      <h2>Welcome, {{ user.firstname }} {{ user.secondName }}</h2>
      <div class="info-section">
        <h3>Personal Information</h3>
        <p><strong>Address:</strong> {{ user.address }}</p>
        <p><strong>Birth Date:</strong> {{ formatDate(user.birthDate) }}</p>
        <p><strong>Institution:</strong> {{ institution.name }}, {{ institution.city }}, {{ institution.country }}</p>
      </div>

      <div class="button-section">
        <button @click="toggleShowSearchBarAndResultsPatient" class="toggle-button">
          {{ showPatientSearchBarAndResults ? 'Hide Patient Search Bar' : 'Show Patient Search Bar' }}
        </button>
        <button @click="togglePhysicianSearchBarAndResults" class="toggle-button">
          {{ showPhysicianSearchBarAndResults ? 'Hide Physician Search Bar' : 'Show Physician Search Bar' }}
        </button>
      </div>
      <div class="searchSection" v-show="showPatientSearchBarAndResults">
        <input type="text" v-model="searchStringPatient" @keyup.enter="searchPatients" placeholder="Search for patients... Date format: yyyy-mm-dd" />
        <PatientList :patients="resultPatients" :forAdmin="true"/>
      </div>
      <div class="searchSection" v-show="showPhysicianSearchBarAndResults">
        <input type="text" v-model="searchStringPhysician" @keyup.enter="searchPhysicians" placeholder="Search for physicians... Date format: yyyy-mm-dd" />
        <PhysicianList :physicians="resultPhysicians" :forAdmin="true" :adminId="user.id"/>
      </div>




      <button @click="toggleCreatePhysicianForm" class="toggle-button">
        {{ showCreatePhysicianForm ? 'Hide Physician Create Form' : 'Create Physician' }}
      </button>
      <button @click="toggleCreateAdminForm" class="toggle-button">
        {{ showCreateAdminForm ? 'Hide Admin Create Form' : 'Add admin to ' + institution.name }}
      </button>
      <div :class="{'form-wrapper': showCreatePhysicianForm && showCreateAdminForm}">
        <div v-if="showCreatePhysicianForm" :class="{'form-container': showCreatePhysicianForm && showCreateAdminForm, 'full-width-container': !(showCreatePhysicianForm && showCreateAdminForm)}">
          <CreatePhysicianForm :createPhysician="true"  @createdPhysician="createdPhysicianSuccessfully"/>
        </div>
        <div v-if="showCreateAdminForm" :class="{'form-container': showCreatePhysicianForm && showCreateAdminForm, 'full-width-container': !(showCreatePhysicianForm && showCreateAdminForm)}">
          <CreateAdminForm @createdAdmin="createdAdminSuccessfully"/>
        </div>
      </div>

      <div v-if="showSuccessAlert" class="info-box">Successfully registered physician in {{ institution.name }}</div>
    </div>
    <div v-else>
      <h3>Loading...</h3>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import CreatePhysicianForm from './CreatePhysicianForm.vue';
import CreateAdminForm from './CreateAdminForm.vue';
import PatientList from '../patientList/PatientList.vue';
import PhysicianList from '../physicianList/PhysicianList.vue';
export default {
  name: 'HomeAdmin',
  components: {
    CreatePhysicianForm,
    CreateAdminForm,
    PatientList,
    PhysicianList,
  },
  data() {
    return {
      user: null,
      showCreatePhysicianForm: false,
      showCreateAdminForm: false,
      showSuccessAlert: false,
      showPhysicianSearchBarAndResults: false,
      showPatientSearchBarAndResults: false,
      searchStringPatient: null,
      searchStringPhysician: null,
      resultPatients: [],
      resultPhysicians: [],

    };
  },
  async created() {
    try {
      const token = localStorage.getItem('token');
      let response = await axios.get('/admin/getDetails', {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      
      this.user = response.data.admin;
      this.institution = this.user.institution;
    } catch (error) {
      console.error('Error fetching patient details:', error);
      this.$router.push('/login');
    }
  },
  methods: {
    toggleCreatePhysicianForm() {
      this.showCreatePhysicianForm = !this.showCreatePhysicianForm;
    },
    toggleCreateAdminForm() {
      this.showCreateAdminForm = !this.showCreateAdminForm;
    },
    formatDate(date) {
      const options = { year: 'numeric', month: 'long', day: 'numeric' };
      return new Date(date).toLocaleDateString(undefined, options);
    },
    togglePhysicianSearchBar() {
      this.showPhysicianSearchBar = !this.showPhysicianSearchBar;
    },
    toggleShowSearchBarAndResultsPatient() {
      this.showPatientSearchBarAndResults = !this.showPatientSearchBarAndResults;
    },
    togglePhysicianSearchBarAndResults() {
      this.showPhysicianSearchBarAndResults = !this.showPhysicianSearchBarAndResults;
    },
    async searchPatients() {
      try {
        const token = localStorage.getItem('token');
        if(!this.searchStringPatient) {
          this.searchStringPatient = ''
        }
        const response = await axios.get(`/admin/listPatients?searchString=${this.searchStringPatient}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        this.resultPatients = response.data.allPatients;
      } catch (error) {
        console.error('Error searching patients:', error);
      }
    },
    async searchPhysicians() {
      try {
        const token = localStorage.getItem('token');
        if(!this.searchStringPhysician) {
          this.searchStringPhysician = ''
        }
        const response = await axios.get(`/admin/listPhysicians?searchString=${this.searchStringPhysician}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
         console.log(response)
        this.resultPhysicians = response.data.allPhysicians;
      } catch (error) {
        console.error('Error searching physicians:', error);
      }
    },
    createdPhysicianSuccessfully() {
      console.log('Physician created successfully');
      this.showSuccessAlert = true;
      setTimeout(() => {
        this.showSuccessAlert = false;
      }, 3000);
      this.showCreatePhysicianForm = false;
    },
    createdAdminSuccessfully() {
      console.log('Admin created successfully');
      this.showSuccessAlert = true;
      setTimeout(() => {
        this.showSuccessAlert = false;
      }, 3000);
      this.showCreateAdminForm = false;
    }
  }
};
</script>

<style>
.button-section {
  display: block;
  border-radius: 4px;
  border: 2px;
  border-color: #343a40;
}
.patient-home {
  padding: 20px;
  font-family: Arial, sans-serif;
}

.patient-info {
  background: #f9f9f9;
  border: 1px solid #ddd;
  padding: 20px;
  border-radius: 8px;
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

h2 {
  color: #343a40;
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

.form-wrapper {
  display: flex;
  justify-content: space-between;
  gap: 20px;
}

.form-container {
  flex: 1;
  max-width: 48%;
}

.full-width-container {
  flex: 1;
  max-width: 100%;
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
.searchSection input[type="text"] {
    display: block;
    width: 100%;
    padding: 10px;
    margin-bottom: 20px;
    border: 1px solid #ccc;
    border-radius: 4px;
  }

</style>
