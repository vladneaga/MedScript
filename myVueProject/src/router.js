import { createRouter, createWebHistory } from 'vue-router'
import HomePhysician from './components/physician/HomePhysician.vue'
import Login from './components/Login.vue'
import Register from './components/Register.vue'
import HomePatient from './components/patient/HomePatient.vue'
import PrescriptionDetails from './components/patient/PrescriptionDetails.vue'
import PatientDetails from './components/patientList/PatientDetails.vue'
import HomeAdmin from './components/admin/HomeAdmin.vue'
import PhysicianDetails from './components/physicianList/PhysicianDetails.vue'
import EditPhysicianForm from './components/admin/EditPhysicianForm.vue'
import EditPatientForm from './components/admin/EditPatientForm.vue'
// secure strangers from accessing patients and prescriptions
const routes = [
  { path: '/physician/home', component: HomePhysician },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/patient/home', component: HomePatient },
  { path: '/admin/home', component: HomeAdmin },
  {
    path: '/prescription/:id',
    component: PrescriptionDetails,
    name: 'prescriptionDetails',
    props: route => ({
      id: parseInt(route.params.id),
      forPhysician: route.query.forPhysician === 'true',
      forPatient: route.query.forPatient === 'true',
      forAdmin: route.query.forAdmin === 'true',
      
    })
  },
  {
    path: '/patient/:id',
    component: PatientDetails,
    name: 'PatientDetails',
    props: route => ({
      id: parseInt(route.params.id),
      forPhysician: route.query.forPhysician === 'true',
      forAdmin: route.query.forAdmin === 'true',
      physicianId: parseInt(route.query.physicianId) // Make sure to parse the physicianId
    })
  },
  {
    path: '/physician/:id',
    component: PhysicianDetails,
    name: 'PhysicianDetails',
    props: route => ({
      id: parseInt(route.params.id),
      forAdmin: route.query.forAdmin === 'true',
      forPhysician: route.query.forPhysician === 'true',
      adminId: parseInt(route.query.adminId) // Make sure to parse the adminId
      //physicianId: parseInt(route.query.physicianId) // Make sure to parse the physicianId
    })
  },
  {
    path: '/editPhysician/:id',
    component: EditPhysicianForm,
    name: 'EditPhysicianForm',
    props: route => ({
      id: parseInt(route.params.id),
    })
  },
  {
    path: '/editPatient/:id',
    component: EditPatientForm,
    name: 'EditPatientForm',
    props: route => ({
      id: parseInt(route.params.id),
    })
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router