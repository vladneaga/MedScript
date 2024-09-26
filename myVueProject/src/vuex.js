import { createStore } from 'vuex';

const store = createStore({
  state: {
    user: null,
    isAuthenticated: false,
  },
  getters: {
    user: (state) => state.user,
    isLoggedIn: (state) => !!state.user, // Check if the user is logged in
  },
  actions: {
    setUser({ commit }, user) {
      commit('setUser', user);
      console.log('setUser action:', user); // Add console log
    },
    clearUser({ commit }) {
      commit('clearUser');
      console.log('clearUser action'); // Add console log
    },
  },
  mutations: {
    setUser(state, user) {
      console.log('setUser mutation:', user); // Add console log
      state.user = user;
    },
    clearUser(state) {
      console.log('clearUser mutation'); // Add console log
      state.user = null;
    },
  },
});

export default store;