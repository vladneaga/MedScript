<template>
    <div class="patient-list">
        <div v-for="patient in patients" :key="patient.id" class="patient-item" @click="changeToPatientDetails(patient.id)" tabindex="0" @keyup.enter="changeToPatientDetails(patient.id)">
        <p><strong>Name:</strong> {{ patient.fullName }}</p>
        <p><strong>Birth Date:</strong> {{ formatDate(patient.birthDate) }}</p>
        <p><strong>Prescriptions:</strong>  {{ patient.prescriptions.length }}</p>
        <p><strong>Email:</strong> {{ patient.account.email }}</p>
      </div>
    </div>
  </template>
  
  <script>

  export default {
    name: 'PatientList',
    props: {
      patients: {
        type: Array,
        required: true
      },
      physicianId: {
    type: [Number, String],
    required: false,
    default: null
  },
  forAdmin: {
    type: Boolean,
    required: false,
    default: false,
   
  }

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
      changeToPatientDetails(id) {
        console.log(id)
        console.log(this.physicianId)
        console.log('forAdmin', this.forAdmin)
        this.$router.push({ name: 'PatientDetails', params: { id }, query: {  forPhysician: this.physicianId && !this.forAdmin? true : false,
          physicianId: this.physicianId ? this.physicianId : null, forAdmin: this.forAdmin} }); 
          //forAdmin:  (this.forAdmin) ||this.physicianId? false : true}

      }
    }
  }
  </script>
  
  <style scoped>
  .patient-list {
    margin: 20px;
  }
  
  .patient-item {
    border: 1px solid #ccc;
    padding: 10px;
    margin-bottom: 10px;
  }
  .patient-item:hover {
    background-color: #d0e45d;
    cursor: pointer;

  }
  .patient-item h2 {
    margin: 10px 0 5px;
  }
  
  .patient-item ul {
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