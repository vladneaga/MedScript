<template>
  <div class="controls" >
            <div class="sort-controls" >
              <label for="sortField" class="sort-label">Sort by:</label>
              <select id="sortField" v-model="sortField" @change="fetchPrescriptions" class="sort-select">
                <option value="creationDate">Creation Date</option>
                <option value="expirationDate">Expiration Date</option>
                <option value="medication">Medication</option>
              </select>
            </div>
            <div class="sort-controls" >
              <label for="pageNumber" class="sort-label">Items per Page:</label>
              <select id="pageNumber" v-model="itemsPerPage" @change="fetchPrescriptions" class="sort-select">
                <option value="2">2</option>
                <option value="5">5</option>
                <option value="10">10</option>
                <option value="25">25</option>
                <option value="100">100</option>
              </select>
            </div>
          </div>
  
          <ul>
            <li v-for="prescription in prescriptions" :key="prescription.id">
              <div class="prescriptiontext">
                {{ prescription.medication }}. {{ prescription.totalGrammage }} 
                <router-link :to="{ name: 'prescriptionDetails', params: { id: prescription.id},  query: { forPatient: forPatient, forPhysician: forPhysician || physicianInPatientDetails, 
                forAdmin: forAdmin || forAdminInPhysician, 
                 } }">
                  View Details
                </router-link>
              </div>
              <span v-if="dateIsLater(prescription.expirationDate)">
                <div class="valid">Valid</div>
              </span>
              <span v-else>
                <div class="expired">Expired</div>
              </span>
              <button @click="downloadPrescription(prescription.id)">Download Prescription</button>
            </li>
          </ul>
  
<div class="pagesList">
  <ul>
    <li v-for="(page, index) in pagesToDisplay" :key="page">
      <button 
        class="pagination-button"
        :class="{ 'active': currentPage === page, 'right': index === pagesToDisplay.length - 1 }"
        @click="currentPage = page; fetchPrescriptions()"
      >
        {{ page + 1 }}
      </button>
    </li>
  </ul>
</div>
</template>

<script>
  import axios from 'axios';
export default {
  name: "PrescriptionTable",
  props: {
    forPatient: false,
    forPhysician: false,
    physicianInPatientDetails: false,
    forAdmin: false,
    forAdminInPhysician: false,
    patientId: null,
    physicianId: null,
    sortFieldOptional: null,
    
    




  },
  data() {
      return {
        sortField: 'expirationDate',
        itemsPerPage: 5,
        currentPage: 0,
        pagesToDisplay: [],
        totalPrescriptionPages: 0,
        prescriptions: [],
       

        
      };
    },
    watch: {
    sortFieldOptional: 'fetchPrescriptions'
  },
    async created() {
    
     this.fetchPrescriptions();

    },
    methods: {
     

      formatDate(date) {
        const options = { year: 'numeric', month: 'long', day: 'numeric' };
        return new Date(date).toLocaleDateString(undefined, options);
      },
      dateIsLater(date1, date2 = new Date()) {
        const d1 = new Date(date1);
        const d2 = new Date(date2);
        return d1 > d2;
      },
      async downloadPrescription(prescriptionId) {
  console.log('Downloading prescription with ID:', prescriptionId); // Confirm ID

  try {
    const token = localStorage.getItem('token');
    let response;
    let url;

    // Debug which condition is being met
    if (this.forPatient) {
      console.log('For Patient: Setting URL for patient');
      url = `/patient/downloadPrescription/${prescriptionId}`;
    } else if (this.forPhysician || this.physicianInPatientDetails) {
      console.log('For Physician: Setting URL for physician');
      url = `/physician/downloadPrescription/${prescriptionId}`;
    }  else if (this.forAdmin) {
      console.log('For Admin: Setting URL for admin');
      url = `/admin/downloadPrescription/${prescriptionId}`;
    } else if(this.forAdminInPhysician) {
      url = `/admin/downloadPrescription/${prescriptionId}`;
    } else {
      console.error('No valid condition for URL');
    }

    console.log('Request URL:', url); // Verify URL

    // Check if URL is defined before making the request
    if (!url) {
      throw new Error('URL is not defined');
    }

    response = await axios.get(url, {
      headers: {
        Authorization: `Bearer ${token}`
      },
      responseType: 'blob'
    });

    console.log('Response:', response); // Log response

    // Create a link element, use it to download the file
    const blob = new Blob([response.data], { type: response.headers['content-type'] });
    const urlBlob = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = urlBlob;
    link.setAttribute('download', `prescription_${prescriptionId}.pdf`); // or any other extension
    document.body.appendChild(link);
    link.click();

    // Clean up and remove the link
    document.body.removeChild(link);
    window.URL.revokeObjectURL(urlBlob);
  } catch (error) {
    console.error('Error downloading prescription:', error);
  }
},
      async fetchPrescriptions() {
        if(this.sortFieldOptional && (this.sortFieldOptional === 'creationDate' || this.sortFieldOptional === 'expirationnDate'
            || this.sortFieldOptional === 'medication')) {
            this.sortField = this.sortFieldOptional
        }
      try {
        const token = localStorage.getItem('token');
        let response;
        if(this.forPatient) {
        response = await axios.get('/patient/prescriptionsSorted?sortField=' + this.sortField
        + '&size=' + this.itemsPerPage + '&page=' + this.currentPage
        ,  {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
    }   else if(this.forPhysician) {
        response = await axios.get('/physician/prescriptionsSorted?sortField=' + this.sortField
        + '&size=' + this.itemsPerPage + '&page=' + this.currentPage
        ,  {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
    } else if(this.physicianInPatientDetails) {
        const url = `/physician/allPrescriptions/${this.patientId}?sortField=${this.sortField}&size=${this.itemsPerPage}&page=${this.currentPage}`;
        console.log(url)
             response = await axios.get(url, {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            });
    } else if(this.forAdmin) {
      console.log('admin in fetch data in prescription table for patients')
      response = await axios.get('/admin/patient/prescriptionsSorted/' + this.patientId + '?sortField=' + this.sortField
        + '&size=' + this.itemsPerPage + '&page=' + this.currentPage
        ,  {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }); 
    } else if(this.forAdminInPhysician) {
      response = await axios.get('/admin/physician/prescriptionsSorted/' + this.physicianId + '?sortField=' + this.sortField
        + '&size=' + this.itemsPerPage + '&page=' + this.currentPage
        ,  {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }); 
    }
        console.log(this.forAdminInPhysician)
        console.log(this.itemsPerPage)
        this.prescriptions = response.data.pagedAndSortedPatientPrescriptions
        this.totalPrescriptionPages = response.data.prescriptionPagesSize
        console.log('Total pages:' + this.totalPrescriptionPages)
        this.pagesToDisplay = this.getPagesToDisplay(this.currentPage, this.totalPrescriptionPages)
        console.log('Pages to display:' + this.pagesToDisplay)
    } catch(error) {
        console.error('Error fetching patient details:', error);
        this.$router.push('/login');
      
    }
   
    this.pagesToDiplay = this.getPagesToDisplay(this.currentPage, this.totalPrescriptionPages)
      },
       getPagesToDisplay(currentPage, totalPages) {
    let pagesToDisplay = [];

    if (currentPage === 0) {
        if (totalPages < 5) {
            for (let i = 0; i < totalPages; i++) {
                pagesToDisplay.push(i);
            }
        } else {
            for (let i = 0; i < 3; i++) {
                pagesToDisplay.push(i);
            }
            pagesToDisplay.push(totalPages - 1);
        }
    } else if (currentPage === totalPages - 1) {
        pagesToDisplay.push(0);
        if (totalPages - 2 > 0 && !pagesToDisplay.includes(totalPages - 2)) {
            pagesToDisplay.push(totalPages - 2);
        }
        if (totalPages - 1 > 0 && !pagesToDisplay.includes(totalPages - 1)) {
            pagesToDisplay.push(totalPages - 1);
        }
    } else if (currentPage >= totalPages - 3) {
        for (let i = currentPage; i < totalPages; i++) {
            pagesToDisplay.push(i);
        }
        if (!pagesToDisplay.includes(currentPage - 1) && currentPage - 1 >= 0) {
            pagesToDisplay.push(currentPage - 1);
        }
        if (!pagesToDisplay.includes(0)) {
            pagesToDisplay.push(0);
        }
    } else {
        if (currentPage - 1 >= 0) {
            pagesToDisplay.push(currentPage - 1);
        }
        pagesToDisplay.push(currentPage);
        if (currentPage + 1 < totalPages) {
            pagesToDisplay.push(currentPage + 1);
        }
        if (!pagesToDisplay.includes(0)) {
            pagesToDisplay.push(0);
        }
        if (!pagesToDisplay.includes(totalPages - 1)) {
            pagesToDisplay.push(totalPages - 1);
        }
    }

    return pagesToDisplay.sort((a, b) => a - b);
}
    }
}
</script>

<style scoped>
.valid {
  display: inline-block;
  padding: 0.25em 0.5em;
  background-color: #28a745;
  color: white;
  border-radius: 0.25rem;
  font-weight: bold;
}

.expired {
  display: inline-block;
  padding: 0.25em 0.5em;
  background-color: #dc3545;
  color: white;
  border-radius: 0.25rem;
  font-weight: bold;
}

.prescriptiontext {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: rgb(75, 65, 65);
}

.prescriptiontext a {
  margin-left: 10px;
  color: #007bff;
  text-decoration: none;
}

.prescriptiontext a:hover {
  text-decoration: underline;
}

button {
  display: inline-block;
  margin-top: 10px;
  padding: 10px 20px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}

.controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.sort-controls {
  display: flex;
  align-items: center;
}

.sort-label {
  margin-right: 10px;
  font-size: 16px;
  color: #007bff;
}

.sort-select {
  padding: 8px 12px;
  font-size: 16px;
  border: 1px solid #007bff;
  border-radius: 4px;
  background-color: #fff;
  cursor: pointer;
}

.sort-select:focus {
  border-color: #0056b3;
  outline: none;
}

/* Pagination button styles */
.pagesList {
  margin-top: 20px;
}

.pagesList ul {
  display: flex;
  padding: 0;
  list-style: none;
  width: 100%;
}

.pagesList ul li {
  margin: 0 5px;
  background: none !important; /* Ensure transparency */
  padding: 0; /* Remove any padding */
  border: none; /* Remove any border */
}

.pagination-button {
  padding: 5px 10px;
  font-size: 12px;
  background-color: #0e20bb;
  color: #ddd;
  border: 1px solid #3b5998;
  border-radius: 4px;
  cursor: pointer;
}

.pagination-button:hover {
  background-color: #0056b3;
  color: #fff;
}

.pagination-button.active {
  background-color: #28a745;
  color: #fff;
}

.pagination-button.right {
  margin-left: auto;
}

.pagination-button:focus {
  outline: none;
}
</style>