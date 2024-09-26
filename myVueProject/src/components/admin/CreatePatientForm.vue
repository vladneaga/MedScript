<template>
    <form @submit.prevent="handleSubmit">
    <h3 v-if="adminEditPatient"> Edit Patient</h3>
    <h3 v-else-if="createPatient"> Patient Registration</h3>
    <div class="form-group"> 
    <label>Username</label>
    <input type="username" class="form-control" v-model="username" placeholder= "Username" />
    <div v-if="validationMap.get('username')" class="error-message"> {{ validationMap.get('username') }} </div>
    </div>
    
    <div class="form-group"> 
    <label>Password</label>
    <input type="password" class="form-control" v-model="password" placeholder= "Password" />
    <div v-if="validationMap.get('password')" class="error-message"> {{ validationMap.get('password') }} </div>
    </div>
    
    <div class="form-group"> 
    <label>Phone Number</label>
    <input type="text" class="form-control" v-model="phoneNumber" placeholder= "Phone Number" />
    <div v-if="validationMap.get('phoneNumber')" class="error-message"> {{ validationMap.get('phoneNumber') }} </div>
    </div>
    
    <div class="form-group"> 
    <label>Email</label>
    <input type="email" class="form-control" v-model="email" placeholder= "Email" />
    <div v-if="validationMap.get('email')" class="error-message"> {{ validationMap.get('email') }} </div>
    </div>
    
    <div class="form-group">
    <label>First Name</label>
    <input type="text" class="form-control" v-model="firstname" placeholder="First Name" />
    <div v-if="validationMap.get('firstname')" class="error-message"> {{ validationMap.get('firstname') }} </div>
    </div>
    
    <div class="form-group">
    <label>Last Name </label> 
    <input type="text" class="form-control" v-model="secondname" placeholder="Last Name" /> 
    <div v-if="validationMap.get('secondname')" class="error-message"> {{ validationMap.get('secondname') }} </div>
    
    </div>
    
    <div class="form-group">
    <label>Address </label> 
    <input type="text" class="form-control" v-model="address" placeholder="Address" /> 
    <div v-if="validationMap.get('address')" class="error-message"> {{ validationMap.get('address') }} </div>
    
    </div>
    
    <div class="form-group" v-if="!adminEditPatient">
    <label>Insurance Number </label> 
    <input type="text" class="form-control" v-model="insuranceNumber" placeholder="Insurance Number" /> 
    <div v-if="validationMap.get('insuranceNumber')" class="error-message"> {{ validationMap.get('insuranceNumber') }} </div>
    
    </div>
    
    <div class="form-group">
    <label>Birth Date </label> 
    <input type="date" class="form-control" v-model="birthDate" placeholder="Birth Date" /> 
    <div v-if="validationMap.get('birthDate')" class="error-message"> {{ validationMap.get('birthDate') }} </div>
    </div>

    <div class="form-group" v-if="adminEditPatient">
        <label for="isUnblocked" class="form-label">Account is:</label>
        <select id="isUnblocked" class="form-select" v-model="isUnblocked">
          <option value="true">valid</option>
          <option value="false">blocked</option>
        </select>
      </div>
    
    <button class="btn btn-primary btn-block" v-if="createPatient"> Sign up</button>
    <button class="btn btn-primary btn-block" v-else-if="adminEditPatient"> Submit changes</button>
    </form>
    
    </template>
    
    <script>
    import axios from 'axios';
    export default {
        name: 'CreatePatientForm',
        data() {
            return {
                firstname: '',
                secondname: '',
                password: '',
                email: '',
                phoneNumber: '',
                address: '',
                birthDate: '',
                insuranceNumber: '',
                isUnblocked: true,
                username: '',
                 validationMap: new Map()
            }
        }, 
        props: {
            id: {
      type: Number,
      required: false
    },
    adminEditPatient: {
      type: Boolean,
      default: false
    },
    createPatient: {
      type: Boolean,
      default: false
    },
        },
         created() {
        this.validationMap = new Map()
      if(this.adminEditPatient) {
        console.log(this.adminEditPatient)
        const token = localStorage.getItem('token');
       axios.get(`/admin/patient/details/${this.id}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })
        .then(
          res => {
           console.log(res)
           const patient = res.data.patient;
           this.firstname = patient.firstname
           this.secondname = patient.secondName
           this.email = patient.account.email
           this.phoneNumber = patient.account.phoneNumber
           this.address = patient.address
           this.birthDate = patient.birthDate
           this.isUnblocked = patient.account.unblocked
           this.username = patient.account.username
          }
        ).catch(
          err => {
            console.log(err)
          }
        )
      }
    },
        methods: {
            async handleSubmit(e) {
    const data = {
        firstname: this.firstname,
        secondname: this.secondname,
        email: this.email,
        username: this.username,
        password: this.password,
        phoneNumber: this.phoneNumber,
        address: this.address,
        birthDate: this.birthDate,
        insuranceNumber: this.insuranceNumber,
        isUnblocked: this.isUnblocked
    };
    
    let url;
    let token;
    let response;
    
    try {
        if (this.createPatient) {
            url = '/auth/signUpPatient';
            response = await axios.post(url, data);
        } else if (this.adminEditPatient) {
            token = localStorage.getItem('token');
            url = '/admin/editPatientInfo/' + this.id;
            response = await axios.post(url, data, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
        }

        console.log(response);

        if (response.data.statusCode === 200) {
            this.validationMap = new Map()
            console.log(response);
            console.log('We are fine');
            if(this.createPatient) {
                this.$router.push('/login');
            } else if(this.adminEditPatient) {
                this.$router.go(-1)
            }
        
        } 
    } catch (err) {
        this.validationMap = new Map();
        console.log(err);
        if (err.response && err.response.data.statusCode === 422) {
            console.log(err.response.data.bindingResult);
            for (let validation of err.response.data.bindingResult) {
                this.validationMap.set(validation.field, validation.defaultMessage);
            }
        }
        // Log the entire error response if necessary
        // console.log(err.response);
    }
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
    border-color: #80bdff;
    outline: 0;
    box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
  }
  .form-group label{
    color: #495057;
  }
  h3 {
    color: #4d4645
  }
  .btn-block {
    width: 100%;
  }
    
    </style>