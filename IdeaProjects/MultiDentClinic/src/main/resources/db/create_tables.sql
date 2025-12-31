-- ==========================================================
--  DENTAL CLINIC MANAGEMENT SYSTEM - DATABASE STRUCTURE
-- ==========================================================

-- ==========================================================
CREATE TABLE patients (
                          id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                          first_name  VARCHAR(100) NOT NULL,
                          last_name   VARCHAR(100) NOT NULL,
                          contact     VARCHAR(50),
                          address     VARCHAR(255),
                          created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================================
--  DENTISTS (now linked to clinic)
-- ==========================================================
CREATE TABLE dentists (
                          id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                          first_name  VARCHAR(100) NOT NULL,
                          last_name   VARCHAR(100) NOT NULL,
                          specialty   VARCHAR(100),
                          clinic_id   BIGINT,
                          created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT fk_dentist_clinic FOREIGN KEY (clinic_id)
                              REFERENCES clinics(id) ON DELETE SET NULL
);

-- ==========================================================
--  SERVICES (linked to clinic)
-- ==========================================================
CREATE TABLE services (
                          id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name        VARCHAR(255) NOT NULL,
                          price       DECIMAL(10,2) NOT NULL,
                          clinic_id   BIGINT,
                          created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT fk_service_clinic FOREIGN KEY (clinic_id)
                              REFERENCES clinics(id) ON DELETE SET NULL
);

-- ==========================================================
--  APPOINTMENTS (patient + dentist + clinic)
-- ==========================================================
CREATE TABLE appointments (
                              id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                              patient_id    BIGINT NOT NULL,
                              dentist_id    BIGINT NOT NULL,
                              clinic_id     BIGINT,
                              scheduled_at  DATETIME NOT NULL,
                              status        VARCHAR(50) DEFAULT 'SCHEDULED',
                              created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                              CONSTRAINT fk_appt_patient FOREIGN KEY (patient_id)
                                  REFERENCES patients(id) ON DELETE CASCADE,

                              CONSTRAINT fk_appt_dentist FOREIGN KEY (dentist_id)
                                  REFERENCES dentists(id) ON DELETE CASCADE,

                              CONSTRAINT fk_appt_clinic FOREIGN KEY (clinic_id)
                                  REFERENCES clinics(id) ON DELETE SET NULL
);

-- ==========================================================
--  ROLES (RBAC)
-- ==========================================================
CREATE TABLE roles (
                       id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name        VARCHAR(50) NOT NULL UNIQUE
);

-- ==========================================================
--  USERS (linked to roles)
-- ==========================================================
CREATE TABLE users (
                       id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username    VARCHAR(100) NOT NULL UNIQUE,
                       password    VARCHAR(255) NOT NULL,
                       role_id     BIGINT NOT NULL,
                       created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT fk_user_role FOREIGN KEY (role_id)
                           REFERENCES roles(id) ON DELETE RESTRICT
);

-- ==========================================================
-- OPTIONAL INDEXES FOR FASTER SEARCH
-- ==========================================================
CREATE INDEX idx_appt_patient ON appointments(patient_id);
CREATE INDEX idx_appt_dentist ON appointments(dentist_id);
CREATE INDEX idx_appt_clinic  ON appointments(clinic_id);

CREATE INDEX idx_dentist_clinic ON dentists(clinic_id);
CREATE INDEX idx_service_clinic ON services(clinic_id);
