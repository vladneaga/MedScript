<template>
    <div v-if="physician">
      <p><strong>Address:</strong> {{ physician.address }}</p>
      <p><strong>First Name:</strong> {{ physician.firstname }}</p>
      <p><strong>Second Name:</strong> {{ physician.secondName }}</p>
      <p><strong>Birth Date:</strong> {{ formatDate(physician.birthDate) }}</p>
      <p><strong>Email:</strong> {{ physician.account.email }}</p>
      <p><strong>Phone Number:</strong> {{ physician.account.phoneNumber }}</p>
      <p><strong>Institution:</strong> {{ physician.institution.name }}, {{ physician.institution.city }}, {{ physician.institution.country }}</p>
      <p><strong>Patients Count:</strong> {{ physician.patients.length }}</p>
    </div>
  </template>
  
  <script>
  export default {
    name: "PhysicianInfo",
    props: {
      physician: {
        type: Object,
        required: false,
        default: null,
      },
    },
    methods: {
      formatDate(date) {
        const options = { year: 'numeric', month: 'long', day: 'numeric' };
        return new Date(date).toLocaleDateString(undefined, options);
      },
    },
  };
  </script>
  
  <style scoped>
  h1 {
    font-size: 24px;
    margin-bottom: 10px;
  }
  
  p {
    font-size: 18px;
    margin: 5px 0;
    color: rgb(107, 91, 91)
  }
  
  strong {
    color: #007bff;
  }
  </style>