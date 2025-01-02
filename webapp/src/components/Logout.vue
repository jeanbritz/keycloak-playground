<template>
  <v-card class="mx-auto ">
    <v-btn
      block
      size="x-large"
      @click="logout"
    >
      Logout from OIDC
    </v-btn>
  </v-card>
</template>

<script>

import axios from "axios";
import router from "@/router/index.js";

export default {
  data() {

  },

  methods: {
    logout() {
      const axiosInstance = axios.create({
        baseURL: 'http://localhost:8080',
        withCredentials: true
      })
      axiosInstance.get('/oidc/logout')
        .then(response => {
          router.go(-1);
          console.log(response.data);
        })
        .catch((error) => {
          if(error.response && error.response.status === 401) {
            router.push({ path: 'login' });
            console.log("You need to login again")
          } else {
            console.error(error);
          }

        })
    }
  }
}

</script>

<style scoped lang="sass">

</style>
