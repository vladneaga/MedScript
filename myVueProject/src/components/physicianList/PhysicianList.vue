<template>
    <div class="physician-list">
        <div v-for="physician in physicians" :key="physician.id" class="physician-item" @click="changeToPhysicianDetails(physician.id)" tabindex="0" @keyup.enter="changeToPatientDetails(physician.id)">
        <p><strong>Name:</strong> {{ physician.fullName }}</p>
        <p><strong>Birth Date:</strong> {{ formatDate(physician.birthDate) }}</p>
        <p><strong>Patients:</strong>  {{ physician.patients.length }}</p>
        <p><strong>Email:</strong> {{ physician.account.email }}</p>
      </div>
    </div>
  </template>
  
  <script>

  export default {
    name: 'PhysicianList',
    props: {
      physicians: {
        type: Array,
        required: true
      },
      adminId: {
    type: [Number, String],
    required: false,
    default: null
  },
  forAdmin: {
    type: [Boolean],
    required: false,
    default: false,
  },
  forPhysician: {
    type: [Boolean],
    required: false,
    default: false,
  },

    },
    data() {
        return {

        }
    },
    methods: {
      formatDate(date) {
        const options = { year: 'numeric', month: 'long', day: 'numeric' };
        return new Date(date).toLocaleDateString(undefined, options);
      },
      changeToPhysicianDetails(id) {
        this.$router.push({ name: 'PhysicianDetails', params: { id }, query: { forPhysician: this.forPhysician, 
          adminId: this.adminId, forAdmin:  this.forAdmin} });

      }
    }
  }
  </script>
  
  <style scoped>
  .physician-list {
    margin: 20px;
  }
  
  .physician-item {
    border: 1px solid #ccc;
    padding: 10px;
    margin-bottom: 10px;
  }
  .physician-item:hover {
    background-color: #d0e45d;
    cursor: pointer;

  }
  .physician-item h2 {
    margin: 10px 0 5px;
  }
  
  .physician-item ul {
    list-style-type: disc;
    padding-left: 20px;
  }
  
  .patient-item ul li {
    margin-bottom: 5px;
  }
  .patient-item p {
    color: rgb(78, 68, 68);
  }
  </style>