Below is a clean, professional README you can copy-paste directly into your project.
This is written at a capstone / final project level (not too basic, not over-engineered).


---

🦷 Dental Clinic Management System

A Java Swing–based Dental Clinic Management System designed to manage clinics, dentists, patients, services, appointments, and users with role-based access control (RBAC) and multi-branch support.


---

📌 Project Overview

This system helps dental clinics manage daily operations such as:

Patient registration

Dentist and service management

Appointment scheduling with conflict prevention

Multi-clinic (branch) handling

Secure user authentication and authorization


The application follows a layered architecture using DAO, Service, and UI layers, ensuring clean separation of concerns and maintainability.


---

✨ Features

🔐 Authentication & Authorization

Secure login using BCrypt password hashing

Role-Based Access Control (ADMIN / STAFF)

Panels are hidden and blocked if the user has no permission

Admin safety: cannot delete the last admin user

Users can be enabled / disabled



---

🏥 Clinic Management

Create, edit, and deactivate clinics

Clinics act as branches

Services and dentists are linked to a clinic

Disabled clinics cannot accept new appointments



---

🦷 Dentist Management

Dentists are assigned to a specific clinic

Prevents duplicate dentists within the same clinic

Validates names and specialties (string-only, no invalid input)



---

👤 Patient Management

Add and edit patients with validation

Prevents duplicate patient records

Form does not close when validation errors occur



---

🧾 Service Management

Services belong to a clinic

Clinic selection via dropdown (no free-text errors)

Prevents duplicate services per clinic

Services can be activated/deactivated



---

📅 Appointment Management

Clinic-based appointment scheduling

Dentist and services dropdown filtered by selected clinic

Appointment status selection:

BOOKED

COMPLETED

CANCELLED


Prevents:

Past appointments

Duplicate appointments

Double-booking dentists


Search appointments by patient name or ID



---

📊 Dashboard

Real-time statistics:

Total Clinics

Total Dentists

Total Patients

Total Services

Appointments Today


Appointment status overview

Role-safe (works for Admin and Staff)



---

🧱 Architecture

UI (Swing Panels)
   ↓
Service Layer (Business Logic & Validation)
   ↓
DAO Layer (JDBC + HikariCP)
   ↓
MySQL Database

Key Technologies

Java (Swing)

MySQL

JDBC

HikariCP (connection pooling)

BCrypt (password hashing)

SLF4J (logging)



---

🗂 Project Structure

src/main/java/com/dentalclinic/dental
│
├── UI              # Swing panels & dialogs
├── Service         # Interfaces
├── Service/impl    # Business logic
├── daos            # DAO interfaces
├── daoimpl         # JDBC implementations
├── model           # Entity models
├── security        # RBAC & AccessControl
├── util            # DB pool, FormUtils, helpers
└── main            # AppLauncher


---

🧪 Validation Rules Implemented

No past appointments

No duplicate appointments

No double-booked dentists

Clinic-specific dentists and services only

No duplicate patients or dentists

Input validation on all forms



---

🚀 How to Run

1. Import project into IntelliJ IDEA or NetBeans


2. Add required libraries:

HikariCP

MySQL Connector

jBCrypt

SLF4J API & Simple



3. Configure database connection in application.properties


4. Run SQL scripts:

create_tables.sql

seed_roles.sql

seed_users.sql



5. Run AppLauncher




---

👥 Default Roles

Role	Permissions

ADMIN	Full system access
STAFF	Patients & Appointments only



👨‍💻 Author

Dental Clinic Management System
Developed as a Java Swing desktop application with clean architecture and RBAC.


Just tell me 👍
