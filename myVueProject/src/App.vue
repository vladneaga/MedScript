<script>
import Nav from './components/Nav.vue';
import axios from 'axios';
import { mapGetters, mapActions } from 'vuex';

export default {
  name: 'App',
  components: {
    Nav,
  },
  computed: {
    ...mapGetters(['isLoggedIn', 'user']),
  },
  methods: {
    ...mapActions(['setUser', 'clearUser']),
  },
  async created() {
    try {
      // const token = localStorage.getItem('token');
      // if (token) {
      //   const response = await axios.get('/physician/getDetails', {
      //     headers: {
      //       Authorization: `Bearer ${token}`,
      //     },
      //   });
      //   this.setUser(response.data.person); // Dispatch the setUser action
      //   console.log('User set in store:', response.data.person);
      // }
    } catch (error) {
      console.error('Error fetching  details:', error);
      this.clearUser();
      this.$router.push('/login');
    }
  },
};
</script>
//,

// async created() {
//   try {
//     const token = localStorage.getItem('token');
//     const response = await axios.get('/physician/getDetails', {
//       headers: {
//         Authorization: `Bearer ${token}`
//       }
//     });
//     this.$store.dispatch('setUserAction', response.data);
//     //this.user = response.data.physician;

//     console.log(this.user);
//     //console.log(response);
//   } catch (error) {
//     console.error('Error fetching physician details:', error);
//     // Handle unauthorized access, e.g., redirect to login
//     this.$router.push('/login');
//   }
// }
<template>
  <div id="app">
    <Nav /> 
    <div>
      <div class="auth-inner">
        <router-view />
      </div>
    </div>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css?family=Fira+Sans:400,500,600,700,800');

* {
  box-sizing: border-box;
}

body {
  
  background: #1C8EF9 !important;
  min-height: 100vh;
  display: flex;
  font-weight: 400;
  font-family: 'Fira Sans', sans-serif;
}
h1, h2, h3, h4, h5 ,h6, label, span {
  font-weight: 500;
  font-family: 'Fira Sans', sans-serif;
}
body, html, #app, #root, .auth-wrapper {
  width: 100%;
  height: 100%;
}
#app {
  text-align: center;
}
.navbar-light {
  background-color: #ffffff;
  box-shadow: 0px 14px 80px rgba(34, 35, 58, 0.2);
}
.forgot-password,
.forgot-password a {
  text-align: right;
  font-size: 13px;
  padding-top: 10px;
  color: #7f7d7d;
  margin: 0;
}
.forgot-password a {
  color: #167bff;
}
</style>