<template>
  <form @submit.prevent="handleSubmit">
    <div class="form-group">
      <label>Email</label>
      <input type="text" v-model="username" class="form-control" placeholder="Username, email or phone number"/>
    </div>

    <div class="form-group">
      <label>Password</label>
      <input type="password" v-model="password" class="form-control" placeholder="Password"/>
      <div v-if="wrongCredentials" class="error-message">{{usernameErrorPhrase}}  </div>
    </div>

    <button class="btn btn-primary btn-block">Login</button>
  </form>
</template>

<script>
import axios from 'axios';
import { mapActions } from 'vuex';

export default {
  name: 'Login',
  data() {
    return {
      username: '',
      password: '',
      wrongCredentials: false,
      usernameErrorPhrase: ''
    };
  },
  methods: {
    ...mapActions(['setUser']), // Map the setUser action from Vuex
    async handleSubmit() {
      const data = {
        username: this.username,
        password: this.password
      };

      try {
        const res = await axios.post('/auth/signin', data);
        console.log(res);
        localStorage.setItem('token', res.data.token);
        let link;
        if(res.data.statusCode === 200) {

          this.wrongCredentials = false

          if(res.data.account.role === 'ROLE_PHYSICIAN') {
            link = '/physician/getDetails';
          }
          if(res.data.account.role === 'ROLE_ADMIN') {
            link = '/admin/getDetails';
          }
          if(res.data.account.role === 'ROLE_PATIENT') {
            link = '/patient/getDetails';
          }

          // Fetch user details after login
          const userResponse = await axios.get(link, {
            headers: {
              Authorization: `Bearer ${res.data.token}`
            }
          });

          // Set the user in Vuex store
          this.setUser('userResponse ' + userResponse.data.person);

          // Redirect to home
          if(res.data.account.role === 'ROLE_PHYSICIAN') {
            this.$router.push('/physician/home');
          }
          if(res.data.account.role === 'ROLE_ADMIN') {
            this.$router.push('/admin/home');
          }
          if(res.data.account.role === 'ROLE_PATIENT') {
            this.$router.push('/patient/home');
          }
        } else if(res.data.statusCode === 500) {

          this.wrongCredentials = true;
          this.usernameErrorPhrase = 'Wrong '
          if(this.username.startsWith('+49')) {
            this.usernameErrorPhrase += 'phone number'
          } else if(this.username.includes('@')) {
            this.usernameErrorPhrase += 'email'
          } else {
            this.usernameErrorPhrase += 'login'
          }
          this.usernameErrorPhrase += ' or password'
          console.log('false credentials');
        }
      } catch (error) {
        console.error('Error during login:', error);
      }
    }
  }
};
</script>

<style scoped>
.form-group {
  margin-bottom: 15px;
}

.form-control {
  width: 100%;
  padding: 10px;
  margin: 5px 0 10px;
  box-sizing: border-box;
}

.btn {
  display: inline-block;
  font-weight: 400;
  color: #212529;
  text-align: center;
  vertical-align: middle;
  user-select: none;
  background-color: #007bff;
  border: 1px solid transparent;
  padding: 0.375rem 0.75rem;
  font-size: 1rem;
  line-height: 1.5;
  border-radius: 0.25rem;
  transition: color 0.15s ease-in-out, background-color 0.15s ease-in-out, border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
  cursor: pointer;
}

.btn:hover {
  background-color: #0056b3;
  border-color: #004085;
}

.btn-block {
  display: block;
  width: 100%;
}

.error-message {
  color: #ff0000;
  font-weight: bold;
  margin-top: 10px;
}
</style>