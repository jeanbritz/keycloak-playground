<template>
  <v-container class="py-4">
    <v-card outlined>
      <v-card-title>User Information</v-card-title>
      <v-card-text>
        <v-list dense>
          <v-list-item v-for="(value, key) in userinfo" :key="key">
              <v-list-item-title class="font-weight-bold">{{ key }}</v-list-item-title>
              <v-list-item-subtitle>{{ value }}</v-list-item-subtitle>
          </v-list-item>
        </v-list>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script>
  import axios from "axios";
  import router from '@/router';

  export default {

    data() {
      return {
         userinfo: {},
      }
    },

    created() {
      const axiosInstance = axios.create({
        baseURL: 'http://localhost:8080',
        withCredentials: true
      })
      axiosInstance.get('/oidc/userinfo')
          .then(response => {
            this.userinfo = response.data;
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
</script>

<style scoped>
.v-list-item-title {
  color: #3f51b5;
}
</style>
