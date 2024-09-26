<template>
    <div>
      <form @submit.prevent="handleSubmit">
        <h3>Sign Up</h3>
        
        <div class="form-group"> 
          <label>Dose</label>
          <input type="text" class="form-control" v-model="totalGrammage" placeholder="Dose" />
          <div v-if="validationMap.get('totalGrammage')" class="error-message">{{ validationMap.get('totalGrammage') }}</div>
        </div>
  
        <div class="form-group"> 
          <label>Medication</label>
          <input type="text" class="form-control" v-model="medication" placeholder="Medication..." />
          <div v-if="validationMap.get('medication')" class="error-message">{{ validationMap.get('medication') }}</div>
        </div>
  
        <div class="form-group"> 
          <label>Text</label>
          <input type="text" class="form-control" v-model="text" placeholder="Description..." />
          <div v-if="validationMap.get('text')" class="error-message">{{ validationMap.get('text') }}</div>
        </div>
        
        <div class="form-group"> 
          <label>Expiration Date</label>
          <input type="date" class="form-control" v-model="expirationDate" placeholder="Expiration Date" />
          <div v-if="validationMap.get('expirationDate')" class="error-message">{{ validationMap.get('expirationDate') }}</div>
        </div>
        
        <button class="btn btn-primary btn-block">Prescribe</button>
      </form>
      
      <div v-if="showSuccessAlert" class="info-box">
        <p>Successfully prescribed the medication to the patient!</p>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  export default {
    name: 'PrescriptionForm',
    props: {
      id: null,
    },
    data() {
      return {
        totalGrammage: null,
        medication: null,
        text: null,
        expirationDate: null,
        validationMap: new Map(),
        showSuccessAlert: false
      }
    }, 
    methods: {
      handleSubmit(e) {
        const data = {
          totalGrammage: this.totalGrammage,
          medication: this.medication,
          expirationDate: this.expirationDate,
          text: this.text,
        };
  
        const token = localStorage.getItem('token');
  
        axios.post(`/physician/prescribe/${this.id}`, data, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })
        .then(
          res => {
            if(res.data.statusCode === 200) {
              this.validationMap = new Map();
              this.totalGrammage = null,
              this.medication = null,
              this.text = null,
              this.expirationDate = null,
              this.$emit('prescription-success');
            }
          }
        ).catch(
          err => {
            this.validationMap = new Map();
            if(err.response.data.statusCode === 422) {
              for(let validation of err.response.data.bindingResult) {
                this.validationMap.set(validation.field, validation.defaultMessage);
              }
            }
          }
        )
      }
    }
  }
  </script>
  
  <style scoped>
  .error-message {
    color: #ff0000;
    font-weight: bold;
    margin-top: 10px;
  }
  
  
  </style>