import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const store = new Vuex.Store({
  state: {
    userVo: null
  },
  mutations: {
    set_userVo(state, userVo) {
      state.userVo = userVo;
    }
  },
  actions: {
    set_userVo(context, userVo) {
      context.commit('set_userVo', userVo);
    }
  },
  getters: {
    get_userVo: (state) => {
      return state.userVo;
    },
  },
})

export default store