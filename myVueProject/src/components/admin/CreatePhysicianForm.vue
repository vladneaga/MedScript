<template>
    <form @submit.prevent="handleSubmit">
      <h3 v-if="editPhysician">Edit Physician</h3>
      <h3 v-else-if="createPhysician">Create Physician</h3>

  
      <div class="form-group">
        <label>Username</label>
        <input type="username" class="form-control" v-model="username" placeholder="Username" />
        <div v-if="validationMap.get('username')" class="error-message">{{ validationMap.get('username') }}</div>
      </div>
  
      <div class="form-group">
        <label>Password</label>
        <input type="password" class="form-control" v-model="password" placeholder="Password" />
        <div v-if="validationMap.get('password')" class="error-message">{{ validationMap.get('password') }}</div>
      </div>
  
      <div class="form-group">
        <label>Phone Number</label>
        <input type="text" class="form-control" v-model="phoneNumber" placeholder="Phone Number" />
        <div v-if="validationMap.get('phoneNumber')" class="error-message">{{ validationMap.get('phoneNumber') }}</div>
      </div>
  
      <div class="form-group">
        <label>Email</label>
        <input type="email" class="form-control" v-model="email" placeholder="Email" />
        <div v-if="validationMap.get('email')" class="error-message">{{ validationMap.get('email') }}</div>
      </div>
  
      <div class="form-group">
        <label>First Name</label>
        <input type="text" class="form-control" v-model="firstname" placeholder="First Name" />
        <div v-if="validationMap.get('firstname')" class="error-message">{{ validationMap.get('firstname') }}</div>
      </div>
  
      <div class="form-group">
        <label>Last Name</label>
        <input type="text" class="form-control" v-model="secondName" placeholder="Last Name" />
        <div v-if="validationMap.get('secondname')" class="error-message">{{ validationMap.get('secondname') }}</div>
      </div>
  
      <div class="form-group">
        <label>Address</label>
        <input type="text" class="form-control" v-model="address" placeholder="Address" />
        <div v-if="validationMap.get('address')" class="error-message">{{ validationMap.get('address') }}</div>
      </div>
  
      <div class="form-group">
        <label>Birth Date</label>
        <input type="date" class="form-control" v-model="birthDate" placeholder="Birth Date" />
        <div v-if="validationMap.get('birthDate')" class="error-message">{{ validationMap.get('birthDate') }}</div>
      </div>
  
      <div class="form-group">
  <label for="isUnblocked" class="form-label">Account is:</label>
  <select id="isUnblocked" class="form-select" v-model="isUnblocked">
    <option value="true">Valid</option>
    <option value="false">Blocked</option>
  </select>
</div>
  
      <button class="btn btn-primary btn-block" v-if=createPhysician>Sign up</button>
      <button class="btn btn-primary btn-block" v-else-if=editPhysician>Submit Chnges</button>
    </form>
  </template>
  
  <script>
  import axios from 'axios';
  export default {
    name: 'CreatePhysicianForm',
    props: {
      id: {
      type: Number,
      required: false
    },
    editPhysician: {
      type: Boolean,
      default: false
    },
    createPhysician: {
      type: Boolean,
      default: false
    },
    },
    
    data() {
      return {
        firstname: '',
        secondName: '',
        password: '',
        email: '',
        phoneNumber: '',
        address: '',
        birthDate: '',
        username: '',
        isUnblocked: true,
        validationMap: new Map()
      };
    },
    async created() {
      if(this.editPhysician) {
        const token = localStorage.getItem('token');
       axios.get(`/admin/physician/details/${this.id}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })
        .then(
          res => {
           console.log(res)
           const physician = res.data.physician;
           this.firstname = physician.firstname
           this.secondName = physician.secondName
           this.email = physician.account.email
           this.phoneNumber = physician.account.phoneNumber
           this.address = physician.address
           this.birthDate = physician.birthDate
           this.isUnblocked = physician.account.unblocked
           this.username = physician.account.username
           this.isUnblocked = physician.account.unblocked

        
       
       
       
        
  
          }
        ).catch(
          err => {
            console.log(err)
            // this.validationMap = new Map();
            // if(err.response.data.statusCode === 422) {
            //   for(let validation of err.response.data.bindingResult) {
            //     this.validationMap.set(validation.field, validation.defaultMessage);
            //   }
            // }
          }
        )
      }
    },
    methods: {

      handleSubmit(e) {
        const data = {
          firstname: this.firstname,
          secondname: this.secondName,
          email: this.email,
          username: this.username,
          password: this.password,
          phoneNumber: this.phoneNumber,
          address: this.address,
          birthDate: this.birthDate,
          isUnblocked: this.isUnblocked
        };
        const token = localStorage.getItem('token');
        let url;
        if(this.createPhysician) {
          console.log(this.createPhysician)
          url = `/admin/createPhysician`
        } else if(this.editPhysician) {
          console.log(data)
          url = `/admin/editPhysicianInfo/${this.id}`
        }
        axios.post(url, data, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })
        .then(
          res => {
            if(res.data.statusCode === 200) {
              this.validationMap = new Map();
              if(this.createPhysician) {
                this.$emit('createdPhysician');
              } else if(this.editPhysician) {
                this.$router.go(-1)
              }
              
            }
          }
        ).catch(
          err => {
            console.log(err)
            this.validationMap = new Map();
            if(err.response.data.statusCode === 422) {
              for(let validation of err.response.data.bindingResult) {
                this.validationMap.set(validation.field, validation.defaultMessage);
              }
            }
          }
        )
      },
      
    }
  };
  </script>
  
  <style scoped>
 
  .error-message {
    color: #ff0000;
    font-weight: bold;
    margin-top: 10px;
  }
  
  .form-select {
    display: block;
    width: 100%;
    padding: 0.375rem 0.75rem;
    font-size: 1rem;
    font-weight: 400;
    line-height: 1.5;
    color: #495057;
    background-color: #fff;
    background-clip: padding-box;
    border: 1px solid #ced4da;
    border-radius: 0.25rem;
    transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
  }
  
  .form-select:focus {
    border-color: #78e421;
    outline: 0;
    box-shadow: 0 0 0 0.2rem rgba(3, 197, 13, 0.25);
  }
  .form-label {
    
  color: #495057;
  display: block;
}
  h3 {
    color: #130b55
  }
  .btn-block {
    width: 100%;
  }
  </style>