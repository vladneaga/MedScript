<template>
    <CreatePatientForm :adminEditPatient="true" :id="id" />
  </template>
  
  <script>
  import CreatePatientForm from './CreatePatientForm.vue';
  
  export default {
      name: 'EditPhysicianForm',
      props: {
         id: {
           type: Number,
           required: true
         },
      },
      components: {
        CreatePatientForm,

      }
  }
  </script>
  
  <style>
  
  </style>