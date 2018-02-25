import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    wallet: {}
  },
  mutations: {
    UPDATE_WALLET (state, wallet) {
      state.wallet = wallet
    }
  },
  actions: {
    updateWallet ({commit}, wallet) {
      commit('UPDATE_WALLET', wallet)
    }
  },
  getters: {
    getWallet: state => state.wallet
  }
})
