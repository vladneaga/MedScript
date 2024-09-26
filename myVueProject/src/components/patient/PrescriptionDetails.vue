<template>
  <div v-if="(prescription && physician) || (prescription && patient) || (prescription && forAdmin)">
    <h2>Prescription Details</h2>
    <p><strong>Patient:</strong> {{ patient.firstname }} {{ patient.secondName }}</p>
    <p><strong>Medication:</strong> {{ prescription.medication }}</p>
    <p><strong>Total Grammage:</strong> {{ prescription.totalGrammage }}</p>
    <p><strong>Issued at:</strong> {{ formatDate(prescription.creationDate) }}</p>
    <p><strong>Expiration Date:</strong> {{ formatDate(prescription.expirationDate) }}</p>
    <p><strong>Issued By:</strong> Dr. {{ physician.firstname }} {{ physician.secondName }}</p>
    <button @click="downloadPrescription">Download Prescription</button>
  </div>
  <div v-else>
    <h3>Loading...</h3>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'PrescriptionDetails',
  props: {
    id: {
      type: Number,
      required: true
    },
    forPatient: {
      type: Boolean,
      default: false
    },
    forPhysician: {
      type: Boolean,
      default: false
    },
    forAdmin: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      prescription: null,
      physician: null,
      patient: null
    }
  },
  async created() {
    try {
      const token = localStorage.getItem('token');
      let response;
      let request;
      if (this.forPatient) {
        request = `patient/prescription/${this.id}`;
      } else if (this.forPhysician) {
        request = `physician/prescription/${this.id}`;
      } else if (this.forAdmin) {
        console.log(this.forAdmin)
        request = `/admin/prescription/${this.id}`;
      }
      console.log(this.forAdmin)
      response = await axios.get(request, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      
      this.physician = response.data.physicianDTO;
      this.patient = response.data.patientDTO.person;
      this.prescription = response.data.prescription; //
    } catch (error) {
      console.error('Error fetching prescription details:', error);
      this.$router.push('/login');
    }
  },
  methods: {
    formatDate(date) {
      const options = { year: 'numeric', month: 'long', day: 'numeric' };
      return new Date(date).toLocaleDateString(undefined, options);
    },
    async downloadPrescription() {
      try {
        const token = localStorage.getItem('token');
        let response;
        let request;
        if (this.forPatient) {
          request = `patient/downloadPrescription/${this.id}`;
        } else if (this.forPhysician) {
         
          request = `physician/downloadPrescription/${this.id}`;
        } else if(this.forAdmin) {
          request = `admin/downloadPrescription/${this.id}`;
        } 
        console.log(this.forAdmin)
        response = await axios.get(request, {
          headers: {
            Authorization: `Bearer ${token}`
          },
          responseType: 'blob'
        });

        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', `prescription_${this.id}.pdf`);
        document.body.appendChild(link);
        link.click();
        link.remove();
        window.URL.revokeObjectURL(url);
      } catch (error) {
        console.error('Error downloading prescription:', error);
      }
    }
  }
}
</script>

<style scoped>
h2 {
  color: #343a40;
  border-bottom: 2px solid #007bff;
  padding-bottom: 5px;
  margin-bottom: 20px;
}

p {
  margin: 10px 0;
}

button {
  display: inline-block;
  margin-top: 20px;
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
</style>