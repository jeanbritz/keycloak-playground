<template>
  <v-container class="py-4">
    <v-card outlined>
      <v-card-title>Tokens Information</v-card-title>
      <v-card-text>
        <v-list dense>
          <v-list-item
            v-for="(value, key) in tokens"
            :key="key"
          >
            <v-card>
              <v-card-title class="text-h5 bg-grey-lighten-3">
                {{ key }}
              </v-card-title>
              <v-divider />
              <v-card-text>
                <div>
                  <h4>JWT Header</h4>
                  <pre>{{ parseJwtHeader(value) }}</pre>
                </div>
                <div>
                  <h4>JWT Claims</h4>
                  <pre>{{ parseJwtClaims(value) }}</pre>
                </div>
              </v-card-text>
            </v-card>
          </v-list-item>
        </v-list>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script>
import axios from "axios";
import router from "@/router/index.js";
import {jwtDecode} from "jwt-decode";

export default {

  data() {
    return {
      tokens: {},
    }
  },

  created() {
    const axiosInstance = axios.create({
      baseURL: 'http://localhost:8080',
      withCredentials: true
    })
    axiosInstance.get('/oidc/debug/tokens')
      .then(response => {
        this.tokens = response.data;
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
  },

  methods: {
    parseJwtHeader(token) {
      try {
        try {
          const decodedHeader = jwtDecode(token , {header: true});
          return JSON.stringify(decodedHeader, null, 2); // Pretty print with 2 spaces indentation
        } catch (error) {
          console.error('Invalid JWT header:', error);
          return 'Invalid JWT header';
        }
      } catch (error) {
        console.error('Invalid JWT header:', error);
        return null;
      }
    },
    parseJwtClaims(token) {
      try {
        try {
          const decodedClaims = jwtDecode(token);
          return JSON.stringify(decodedClaims, null, 2); // Pretty print with 2 spaces indentation
        } catch (error) {
          console.error('Invalid JWT token:', error);
          return 'Invalid JWT token';
        }
      } catch (error) {
        console.error('Invalid JWT token:', error);
        return null;
      }
    }
  }
}
</script>



<style scoped lang="sass">

</style>
