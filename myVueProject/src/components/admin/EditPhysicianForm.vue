<template>
  <CreatePhysicianForm :editPhysician="true" :id="id" />
</template>

<script>
import CreatePhysicianForm from './CreatePhysicianForm.vue';

export default {
    name: 'EditPhysicianForm',
    props: {
       id: {
         type: Number,
         required: true
       },
    },
    components: {
        CreatePhysicianForm
    }
}
</script>

<style>

</style>