MedScript is a medical prescription management system designed to streamline interactions between patients, physicians, and administrators. The application offers a secure, user-friendly interface for handling patient registrations, managing physician-patient relationships, and creating and managing prescriptions.


**** Before running the application please send a GET request to the http://localhost:8081/auth/startapp (host can be chnaged in resources/application.properties) ****
MedScript provides three main roles:

  Patient
  Physician
  Admin
  
Each role has specific functionalities and restrictions, ensuring secure access to sensitive medical information and an efficient workflow.

Features by Role
1. Patient
  Registration:
  Patients can only register with an existing insurance number that is stored in the database. For real-world use, these insurance numbers would ideally be provided by the Ministry of Health.
  For testing purposes, mock insurance numbers have been created and stored.
  During registration, the patient must provide a unique login, email, and phone number.
  The application is error-friendly and warns the patient if the chosen login, email, or phone number already exists.
  All required fields for registration are thoroughly checked, and feedback is provided on whether the data is accepted or rejected.
  Access to Prescriptions:
  Once registered, patients can log in to view and download their prescriptions.
  The prescription list is displayed in a beautiful, user-friendly frame that allows patients to sort prescriptions by:
  Creation date
  Expiration date
  Alphabetically by medication name
  Patients can also choose the page size for easy navigation through their prescriptions.
  Password Recovery:
  In case a patient forgets their password, they must contact the institution's admin for assistance.
2. Physician
  Patient Search & Management:
  Physicians can search for patients registered in the system and assign or remove them from their list of managed patients.
  Prescriptions:
  Physicians can create and prescribe medical prescriptions for their patients.
  They have the option to either:
  Manually sign prescriptions after printing them.
  Create a digital signature that will automatically be included on all prescriptions.
  Prescription Management:
  Physicians can view the presctions created by them as well as their assigned patients and manage their created prescriptions within a user-friendly interface.
  Prescriptions can be sorted and the page size can be customized for easy viewing.
3. Admin
  Account Creation & Management:
  Admins can create physician accounts and add new admins to the institution.
  Admins also have the ability to edit physician and patient accounts.
  The system ensures that login, email, and phone number information remains unique.
  Also thorough validation for other related data during the registration/editing process is present.
  Admins can change the password for any user if needed.
  Access to Physician Information:
  Admins can view information about physicians, including their assigned patients and the prescriptions they have created.
  The admin interface allows for searching through assigned patients as well as seeing their details and prescriptions.
  Access to Patient Information:
  Admins can view patient information, including prescriptions.
  This information is displayed in a user-friendly interface that allows for sorting and customized page sizes.
Security
  Password Encryption:
    All passwords are encoded for security purposes. No one, not even administrators, has access to a user’s password.     Only the user knows their password.
API Configuration
  The API configurations, including database settings, can be found and modified in the resources/application.properties file.
Prescription Storage
  All prescriptions are stored securely on the server, allowing patients and physicians to access them as needed.

**** Before running the application please send a GET request to the http://localhost:8081/auth/startapp (host can be chnaged in resources/application.properties) ****

Starting the application: 
1) run the src/main/java/group11/medScriptAPI/MedScriptApiApplication.java of the MedScriptAPI
2) cd into myVueProject and run: npm run dev
