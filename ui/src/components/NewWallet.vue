<template>
  <b-jumbotron>
    <template slot="header">
      Create a New Wallet
    </template>
    <hr class="my-4">
    <b-btn @click="buildWallet()" variant="primary">Create Wallet Now</b-btn>
  </b-jumbotron>
</template>
<script>
  import Wallet from '../services/Wallet'

  export default {
    name: 'new_wallet',
    methods: {
      buildWallet () {
        const wallet = new Wallet()
        this.$store.dispatch('updateWallet', wallet)
      }
    }
  }
</script>
<style>
</style>
