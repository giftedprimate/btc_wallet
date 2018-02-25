import Router from 'vue-router'

import NewWallet from '../components/NewWallet'

export default new Router({
  routes: [
    {
      path: '/',
      name: 'NewWallet',
      component: NewWallet
    },
    {
      path: '*',
      redirect: '/'
    }
  ]
})
