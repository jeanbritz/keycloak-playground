<template>
  <v-responsive
    class="border rounded"
  >
    <v-app>
      <v-app-bar
        color="teal-darken-4"
        class="ma-0 pa-0"
        @click="drawer = !drawer"
      >
        <template #image>
          <v-img
            gradient="to top right, rgba(19,84,122,.8), rgba(128,208,199,.8)"
          />
        </template>

        <template #prepend>
          <v-app-bar-nav-icon />
        </template>

        <v-app-bar-title>
          <div class="logo-container">
            <v-img
              src="@/assets/keycloak.png"
              alt="Logo"
              class="logo"
            ></v-img>
            <span class="app-title">Playground</span>
          </div>
        </v-app-bar-title>
        <v-spacer />

        <v-menu>
          <template #activator="{ props }">
            <v-btn
              icon="mdi-account"
              variant="text"
              v-bind="props"
              to="/login"
            />
          </template>
        </v-menu>
      </v-app-bar>
      <v-navigation-drawer
        v-model="drawer"
        app
        clipped
      >
        <v-list dense>
          <v-list-item
            v-for="(item, index) in items"
            :key="index"
            :to="item.location"
            link
          >
            <v-list-item-title>{{ item.title }}</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-navigation-drawer>
      <v-main>
        <v-container>
          <router-view />
        </v-container>
      </v-main>
    </v-app>
  </v-responsive>
</template>


<script>
export default {
  data() {
    return {
      drawer: false, // Tracks whether the drawer is open or closed
      items: [
        { title: "Home", location: "/" },
        { title: "User Info", location: "/userinfo" },
        { title: "Tokens", location: "/tokens"},
        { title: "Log out", location: "/logout"}
      ],
    };
  },
};
</script>

<style>
.logo-container {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  max-width: fit-content;
}

.logo {
  height: 32px;
  width: 32px;
  margin-right: 8px; /* Adjust spacing */
}

.app-title {
  font-size: 1.25rem; /* Adjust text size */
}
</style>
