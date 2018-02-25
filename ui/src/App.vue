<template>
  <div class="App">
    <h1>{{ appSummary }}</h1>
    <router-view></router-view>

    <!-- FOR DEVELOPMENT -->
    <b-form-textarea id="textarea1"
                     placeholder="Enter something"
                     :rows="3"
                     readonly
                     :value="wallet">
    </b-form-textarea>
  </div>
</template>
<script>
  export default {
    mounted () {
      this.getSummary(summary => this.appSummary = summary.content)
    },
    computed: {
      wallet () {
        return JSON.stringify(this.$store.getters.getWallet, null, 2)
      }
    },
    data () {
      return {
        appSummary: ''
      }
    },
    methods: {
      getSummary (cb) {
        this.axios.get(`/v1/summary`)
          .then(res => res.data)
          .then(cb)
          .catch(console.error)
      }
    }
  }
</script>
<style>
</style>
