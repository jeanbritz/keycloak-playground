<template>
  <v-container>
    <div id="swagger-ui-wrapper" class="swagger-light">
      <div id="swagger-ui"></div>
    </div>
  </v-container>
</template>

<script setup>
import { onMounted } from 'vue';
import 'swagger-ui-dist/swagger-ui.css'; // Import Swagger UI CSS
import SwaggerUIBundle from 'swagger-ui-dist/swagger-ui-bundle';

onMounted(() => {
  SwaggerUIBundle({
    url: 'http://localhost:8080/api/openapi.json',
    dom_id: '#swagger-ui',
  });
});
</script>

<style scoped>
/* Force Swagger UI to use a light background */
.swagger-light {
  background-color: white; /* Set white background */
  color: black; /* Adjust text color */
}

/* Optional: Ensure nested Swagger UI elements inherit light theme */
.swagger-light #swagger-ui {
  background-color: white !important;
  color: black !important;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

/* Override Vuetify dark theme styles for Swagger UI */
.swagger-light .swagger-ui,
.swagger-light .swagger-ui .info,
.swagger-light .swagger-ui .opblock {
  background-color: white !important;
  color: black !important;
}
</style>
