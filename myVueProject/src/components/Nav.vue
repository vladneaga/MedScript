<template>
  <nav class='navbar navbar-expand navbar-light fixed-top'>
    <div class="container">
      <a href="#" class="navbar-brand">MedScript</a>
      <div class="collapse navbar-collapse">
        <ul class="nav">
          <li class="nav-item" v-if="!isLoggedIn">
            <router-link to="/login" class="nav-link">Login</router-link>
          </li>
          <li class="nav-item" v-if="!isLoggedIn">
            <router-link to="/register" class="nav-link">Sign up</router-link>
          </li>
          <li class="nav-item" v-if="isLoggedIn">
            <a href="javascript:void(0)" @click="handleLogout" class="nav-link">Logout</a>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script>
export default {
  name: 'Nav',
  data() {
    return {
      isLoggedIn: false,
    };
  },
  mounted() {
    this.checkLoginStatus();
    this.$router.afterEach(() => {
      this.checkLoginStatus(); // Check login status after each route change
    });
  },
  methods: {
    checkLoginStatus() {
      this.isLoggedIn = localStorage.getItem('token') !== null; // Update the login status
    },
    handleLogout() {
      localStorage.removeItem('token'); // Remove token from localStorage
      this.checkLoginStatus(); // Update the login status
      this.$router.push('/login'); // Redirect to login page
    },
  },
};
</script>