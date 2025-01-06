<template>
  <v-data-table
    :headers="headers"
    :items="movies"
  >
    <template #top>
      <v-toolbar
        flat
      >
        <v-toolbar-title>My Movie Watchlist</v-toolbar-title>
        <v-divider
          class="mx-4"
          inset
          vertical
        />
        <v-spacer />
        <v-dialog
          v-model="dialog"
          max-width="500px"
        >
          <template #activator="{ props }">
            <v-btn
              class="mb-2"
              color="primary"
              dark
              v-bind="props"
            >
              New Movie
            </v-btn>
          </template>
          <v-card>
            <v-card-title>
              <span class="text-h5">{{ formTitle }}</span>
            </v-card-title>

            <v-card-text>
              <v-container>
                <v-row>
                  <v-col
                    cols="12"
                    md="4"
                    sm="6"
                  >
                    <v-text-field
                      v-model="editedItem.title"
                      label="Title"
                    />
                  </v-col>
                  <v-col
                    cols="12"
                    md="4"
                    sm="6"
                  >
                    <v-text-field
                      v-model="editedItem.director"
                      label="Director"
                    />
                  </v-col>
                  <v-col
                    cols="12"
                    md="4"
                    sm="6"
                  >
                    <v-text-field
                      v-model="editedItem.year"
                      label="Year"
                    />
                  </v-col>
                </v-row>
              </v-container>
            </v-card-text>

            <v-card-actions>
              <v-spacer />
              <v-btn
                color="blue-darken-1"
                variant="text"
                @click="close"
              >
                Cancel
              </v-btn>
              <v-btn
                color="blue-darken-1"
                variant="text"
                @click="save"
              >
                Save
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
        <v-dialog
          v-model="dialogDelete"
          max-width="500px"
        >
          <v-card>
            <v-card-title class="text-h5">
              Are you sure you want to delete this item?
            </v-card-title>
            <v-card-actions>
              <v-spacer />
              <v-btn
                color="blue-darken-1"
                variant="text"
                @click="closeDelete"
              >
                Cancel
              </v-btn>
              <v-btn
                color="blue-darken-1"
                variant="text"
                @click="deleteItemConfirm"
              >
                OK
              </v-btn>
              <v-spacer />
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-toolbar>
    </template>
    <template #item.actions="{ item }">
      <v-icon
        class="me-2"
        size="small"
        @click="editItem(item)"
      >
        mdi-pencil
      </v-icon>
      <v-icon
        class="me-2"
        size="small"
        @click="deleteItem(item)"
      >
        mdi-delete
      </v-icon>
      <v-tooltip
        text="I have seen this"
        location="top"
      >
        <template #activator="{ props }">
          <v-icon
            class="me-2"
            size="small"
            :v-bind="props"
            @click="setWatched(item.id)"
          >
            mdi-eye-check
          </v-icon>
        </template>
      </v-tooltip>
    </template>
    <template #no-data>
      <v-btn
        color="primary"
        @click="initialize"
      >
        Refresh
      </v-btn>
    </template>
  </v-data-table>
</template>

<script>
import axios from "axios";

export default {
  data: () => ({
    dialog: false,
    dialogDelete: false,
    headers: [
      {
        title: 'Title',
        align: 'start',
        sortable: false,
        key: 'title',
      },
      { title: 'Director', key: 'director' },
      { title: 'Year', key: 'year', sortable: true },
      { title: 'Have Watched?', key: 'watched', sortable: true },
      { title: 'Actions', key: 'actions', sortable: false },
    ],
    movies: [],
    editedIndex: -1,
    editedItem: {
      title: '',
      director: '',
      year: 0,
    },
    defaultItem: {
      title: '',
      director: '',
      year: 0,
    },
  }),

  computed: {
    formTitle () {
      return this.editedIndex === -1 ? 'New Movie' : 'Edit Movie'
    },
  },

  watch: {
    dialog (val) {
      val || this.close()
    },
    dialogDelete (val) {
      val || this.closeDelete()
    },
  },

  created () {
    this.initialize()
  },

  methods: {
    initialize () {
      const axiosInstance = axios.create({
        baseURL: 'http://localhost:8080',
        withCredentials: true
      })
      axiosInstance.get('/api/movies')
        .then(response => {
          this.movies = response.data;
        })
        .catch((error) => {
            console.error(error);
        })
    },

    editItem (item) {
      this.editedIndex = this.movies.indexOf(item)
      this.editedItem = Object.assign({}, item)
      this.dialog = true
    },

    deleteItem (item) {
      this.editedIndex = this.movies.indexOf(item)
      this.editedItem = Object.assign({}, item)
      this.dialogDelete = true
    },

    deleteItemConfirm () {
      // this.movies.splice(this.editedIndex, 1)
      this.deleteMovie(this.editedItem.id);
      this.closeDelete()
    },

    close () {
      this.dialog = false
      this.$nextTick(() => {
        this.editedItem = Object.assign({}, this.defaultItem)
        this.editedIndex = -1
      })
    },

    closeDelete () {
      this.dialogDelete = false
      this.$nextTick(() => {
        this.editedItem = Object.assign({}, this.defaultItem)
        this.editedIndex = -1
      })
    },

    setWatched(id) {
      const axiosInstance = axios.create({
        baseURL: 'http://localhost:8080',
        withCredentials: true
      })
      axiosInstance.put(`/api/movies/${id}/status?watched=true`)
        .then(response => {
          if (response.status === 200) {
            this.initialize();
          }
        })
        .catch((error) => {
          console.error(error);
        })
    },

    save () {
      if (this.editedIndex > -1) {
        this.updateMovie(this.editedItem['id'], this.editedItem);
      } else {
        this.createMovie(this.editedItem)
      }
      this.close()
    },

    createMovie(item) {
      const axiosInstance = axios.create({
        baseURL: 'http://localhost:8080',
        withCredentials: true
      })
      const movie = {
        title: item.title,
        director: item.director,
        year: item.year,
      }
      axiosInstance.post('/api/movies', movie)
        .then(response => {
          if (response.status === 201) {
            this.initialize();
          }
        })
        .catch((error) => {
          console.error(error);
        })
    },

    updateMovie(id, item) {
      console.log('updateMovie, id', item);
      const axiosInstance = axios.create({
        baseURL: 'http://localhost:8080',
        withCredentials: true
      })
      const movie = {
        title: item.title,
        director: item.director,
        year: item.year,
      }
      axiosInstance.put(`/api/movies/${id}`, movie)
        .then(response => {
          if (response.status === 200) {
            this.initialize();
          }
        })
        .catch((error) => {
          console.error(error);
        })
    },

    deleteMovie(id) {
      const axiosInstance = axios.create({
        baseURL: 'http://localhost:8080',
        withCredentials: true
      })
      axiosInstance.delete(`/api/movies/${id}`)
        .then(response => {
          if (response.status === 204) {
            this.initialize();
          }
        })
        .catch((error) => {
          console.error(error);
        })
    },
  },
}
</script>

