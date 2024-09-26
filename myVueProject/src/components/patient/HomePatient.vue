<!-- <template>
    <div>
      <h3 v-if="user">  Hi, {{ user.firstname }}  {{ user.secondName }} </h3>
      <h3 v-if="!user">  Hello </h3>
    </div>
  </template>
  
  <script>
  import axios from 'axios'
  import { mapActions, mapGetters } from 'vuex';
  export default {
    name: 'HomePatient',
    data() {
      return {
        user: null
      }
    },
    async created() {
      try {
       // console.log('isLoggedIn in Nav:', this.isLoggedIn);
        const token = localStorage.getItem('token');
        const response = await axios.get('/patient/getDetails', {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
  
        this.user = response.data.patient;
        console.log(response);
      } catch (error) {
        console.error('Error fetching physician details:', error);
        // Handle unauthorized access, e.g., redirect to login
        this.$router.push('/login');
      }
    }
  }
  </script> -->

  <template>
    <div class="patient-home">
      <div v-if="user" class="patient-info">
        <h2>Welcome, {{ user.firstname }} {{ user.secondName }}</h2>
        <div class="info-section">
          <h3>Personal Information</h3>
          <p><strong>Address:</strong> {{ user.address }}</p>
          <p><strong>Birth Date:</strong> {{ formatDate(user.birthDate) }}</p>
          <p><strong>Insurance Number:</strong>
            <span :class="{'blurred': !showInsuranceNumber}" @click="toggleInsuranceNumber">
              {{ user.insurance.number }}
            </span>
          </p>
        </div>
        <div class="info-section">
          <h3>Prescriptions</h3>
          <button @click="togglePrescriptionList" class="toggle-button">
            {{ showPrescriptions ? 'Hide Prescriptions' : 'Show Prescriptions' }}
          </button>
        <div v-show="showPrescriptions">
            <PrescriptionTable   :forPatient="true"/>
</div>
        </div>
  
        <div class="info-section">
          <h3>Physicians</h3>
          <ul>
            <li v-for="physician in user.physicians" :key="physician.id">
              Dr. {{ physician.firstname }} {{ physician.secondName }} - {{ physician.specialization }}
            </li>
          </ul>
        </div>
      </div>
      <div v-else>
        <h3>Loading...</h3>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  import PrescriptionTable from '../prescriptionsList/PrescriptionTable.vue';
  export default {
    name: 'HomePatient',
    components: {
        PrescriptionTable
  },
    data() {
      return {
        user: null,
        showInsuranceNumber: false,
        showPrescriptions: true,

      };
    },
    async created() {
      try {
        const token = localStorage.getItem('token');
        let response = await axios.get('/patient/getDetails', {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        
        this.user = response.data.patient;
    
      } catch (error) {
        console.error('Error fetching patient details:', error);
        this.$router.push('/login');
      }
     
    },
    computed: {

    },
    methods: {
      toggleInsuranceNumber() {
        this.showInsuranceNumber = !this.showInsuranceNumber;
      },
      togglePrescriptionList() {
        this.showPrescriptions = !this.showPrescriptions;
      },
      formatDate(date) {
        const options = { year: 'numeric', month: 'long', day: 'numeric' };
        return new Date(date).toLocaleDateString(undefined, options);
      },
      dateIsLater(date1, date2 = new Date()) {
        const d1 = new Date(date1);
        const d2 = new Date(date2);
        return d1 > d2;
      },

    }
  };
  </script>
  <!-- //TODO: Make the pages display likw this: 1 ... last one -->
  <style>

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

.blurred {
  filter: blur(5px);
  transition: filter 0.3s;
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


  </style>