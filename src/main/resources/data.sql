INSERT INTO users (username, password, role, dni, full_name)
VALUES (
        'admin',
        '{noop}admin',
        'ADMIN',
        '00000000',
        'Admin User'
    );
INSERT INTO users (username, password, role, dni, full_name)
VALUES (
        'doctor',
        '{noop}doctor',
        'DOCTOR',
        '11111111',
        'Dr. House'
    );
INSERT INTO doctors (id, specialty, cmp)
VALUES (2, 'Diagnostico', 'CMP12345');

INSERT INTO users (username, password, role, dni, full_name)
VALUES (
        'pasciente',
        '{noop}1234',
        'PATIENT',
        '22222222',
        'Paciente Prueba'
    );

INSERT INTO patients (id, birth_date, insurance_number)
VALUES (3, '2000-01-01', 'TEST-INS-001');

-- Horario de ejemplo para el doctor 2 (lunes a viernes 09:00-17:00, cada 30 minutos)
INSERT INTO doctor_schedules (doctor_id, day_of_week, start_time, end_time, slot_minutes)
VALUES (2, 'MONDAY', '09:00:00', '17:00:00', 30);

INSERT INTO doctor_schedules (doctor_id, day_of_week, start_time, end_time, slot_minutes)
VALUES (2, 'TUESDAY', '09:00:00', '17:00:00', 30);

INSERT INTO doctor_schedules (doctor_id, day_of_week, start_time, end_time, slot_minutes)
VALUES (2, 'WEDNESDAY', '09:00:00', '17:00:00', 30);

INSERT INTO doctor_schedules (doctor_id, day_of_week, start_time, end_time, slot_minutes)
VALUES (2, 'THURSDAY', '09:00:00', '17:00:00', 30);

INSERT INTO doctor_schedules (doctor_id, day_of_week, start_time, end_time, slot_minutes)
VALUES (2, 'FRIDAY', '09:00:00', '17:00:00', 30);

-- Citas de prueba para el doctor 2 y paciente 3
INSERT INTO appointments (date, time, status, doctor_id, patient_id, reason, rescheduled_date, rescheduled_time, cancellation_reason)
VALUES ('2025-01-10', '09:00:00', 'COMPLETED', 2, 3, 'Control general', NULL, NULL, NULL);

INSERT INTO appointments (date, time, status, doctor_id, patient_id, reason, rescheduled_date, rescheduled_time, cancellation_reason)
VALUES ('2025-02-15', '09:30:00', 'COMPLETED', 2, 3, 'Control de anemia', NULL, NULL, NULL);

INSERT INTO appointments (date, time, status, doctor_id, patient_id, reason, rescheduled_date, rescheduled_time, cancellation_reason)
VALUES ('2025-03-20', '10:00:00', 'COMPLETED', 2, 3, 'Control de peso', NULL, NULL, NULL);

-- Registros médicos asociados a esas citas
INSERT INTO medical_records (patient_id, doctor_id, date, diagnosis, treatment, notes,
                             hemoglobin, blood_sugar, weight, height,
                             triglycerides, cholesterol, bmi, appointment_id)
VALUES (3, 2, '2025-01-10T09:15:00', 'Chequeo general', 'Recomendaciones de estilo de vida', 'Inicio de seguimiento',
        11.5, 95.0, 70.0, 1.70,
        150.0, 190.0, 24.2, 1);

INSERT INTO medical_records (patient_id, doctor_id, date, diagnosis, treatment, notes,
                             hemoglobin, blood_sugar, weight, height,
                             triglycerides, cholesterol, bmi, appointment_id)
VALUES (3, 2, '2025-02-15T09:45:00', 'Anemia leve en mejoría', 'Continuar suplemento de hierro', 'Paciente refiere mejoría',
        12.2, 92.0, 69.0, 1.70,
        145.0, 185.0, 23.9, 2);

INSERT INTO medical_records (patient_id, doctor_id, date, diagnosis, treatment, notes,
                             hemoglobin, blood_sugar, weight, height,
                             triglycerides, cholesterol, bmi, appointment_id)
VALUES (3, 2, '2025-03-20T10:20:00', 'Control de peso y anemia', 'Mantener dieta y ejercicio', 'Peso y hemoglobina estables',
        12.8, 90.0, 68.0, 1.70,
        140.0, 180.0, 23.5, 3);

-- =============================
-- Datos adicionales de DOCTORES (usuarios id 4-12)
-- =============================

INSERT INTO users (username, password, role, dni, full_name)
VALUES ('doctor1', '{noop}1234', 'DOCTOR', '30000001', 'Dr. Carlos Pérez');

INSERT INTO users (username, password, role, dni, full_name)
VALUES ('doctor2', '{noop}1234', 'DOCTOR', '30000002', 'Dra. María López');

INSERT INTO users (username, password, role, dni, full_name)
VALUES ('doctor3', '{noop}1234', 'DOCTOR', '30000003', 'Dr. Juan García');

INSERT INTO users (username, password, role, dni, full_name)
VALUES ('doctor4', '{noop}1234', 'DOCTOR', '30000004', 'Dra. Ana Torres');

INSERT INTO users (username, password, role, dni, full_name)
VALUES ('doctor5', '{noop}1234', 'DOCTOR', '30000005', 'Dr. Luis Fernández');

INSERT INTO users (username, password, role, dni, full_name)
VALUES ('doctor6', '{noop}1234', 'DOCTOR', '30000006', 'Dra. Sofía Ramos');

INSERT INTO users (username, password, role, dni, full_name)
VALUES ('doctor7', '{noop}1234', 'DOCTOR', '30000007', 'Dr. Miguel Rojas');

INSERT INTO users (username, password, role, dni, full_name)
VALUES ('doctor8', '{noop}1234', 'DOCTOR', '30000008', 'Dra. Paula Castillo');

INSERT INTO users (username, password, role, dni, full_name)
VALUES ('doctor9', '{noop}1234', 'DOCTOR', '30000009', 'Dr. Diego Herrera');

-- Los IDs de estos usuarios serán 4-12 asumiendo auto-incremento continuo.
-- Creamos las filas correspondientes en la tabla doctors.

INSERT INTO doctors (id, specialty, cmp)
VALUES (4, 'Pediatría', 'CMP20001');

INSERT INTO doctors (id, specialty, cmp)
VALUES (5, 'Cardiología', 'CMP20002');

INSERT INTO doctors (id, specialty, cmp)
VALUES (6, 'Ginecología', 'CMP20003');

INSERT INTO doctors (id, specialty, cmp)
VALUES (7, 'Neurología', 'CMP20004');

INSERT INTO doctors (id, specialty, cmp)
VALUES (8, 'Dermatología', 'CMP20005');

INSERT INTO doctors (id, specialty, cmp)
VALUES (9, 'Endocrinología', 'CMP20006');

INSERT INTO doctors (id, specialty, cmp)
VALUES (10, 'Gastroenterología', 'CMP20007');

INSERT INTO doctors (id, specialty, cmp)
VALUES (11, 'Neumología', 'CMP20008');

INSERT INTO doctors (id, specialty, cmp)
VALUES (12, 'Oncología', 'CMP20009');

-- =============================
-- Datos adicionales de PACIENTES (usuarios id 13-21)
-- =============================

INSERT INTO users (username, password, role, dni, full_name)
VALUES ('patient2', '{noop}1234', 'PATIENT', '40000002', 'Paciente Dos');

INSERT INTO users (username, password, role, dni, full_name)
VALUES ('patient3', '{noop}1234', 'PATIENT', '40000003', 'Paciente Tres');

INSERT INTO users (username, password, role, dni, full_name)
VALUES ('patient4', '{noop}1234', 'PATIENT', '40000004', 'Paciente Cuatro');

INSERT INTO users (username, password, role, dni, full_name)
VALUES ('patient5', '{noop}1234', 'PATIENT', '40000005', 'Paciente Cinco');

INSERT INTO users (username, password, role, dni, full_name)
VALUES ('patient6', '{noop}1234', 'PATIENT', '40000006', 'Paciente Seis');

INSERT INTO users (username, password, role, dni, full_name)
VALUES ('patient7', '{noop}1234', 'PATIENT', '40000007', 'Paciente Siete');

INSERT INTO users (username, password, role, dni, full_name)
VALUES ('patient8', '{noop}1234', 'PATIENT', '40000008', 'Paciente Ocho');

INSERT INTO users (username, password, role, dni, full_name)
VALUES ('patient9', '{noop}1234', 'PATIENT', '40000009', 'Paciente Nueve');

INSERT INTO users (username, password, role, dni, full_name)
VALUES ('patient10', '{noop}1234', 'PATIENT', '40000010', 'Paciente Diez');

-- Creamos las filas correspondientes en la tabla patients.

INSERT INTO patients (id, birth_date, insurance_number)
VALUES (13, '1990-02-10', 'INS-002');

INSERT INTO patients (id, birth_date, insurance_number)
VALUES (14, '1985-03-15', 'INS-003');

INSERT INTO patients (id, birth_date, insurance_number)
VALUES (15, '1978-04-20', 'INS-004');

INSERT INTO patients (id, birth_date, insurance_number)
VALUES (16, '1995-05-05', 'INS-005');

INSERT INTO patients (id, birth_date, insurance_number)
VALUES (17, '2001-06-18', 'INS-006');

INSERT INTO patients (id, birth_date, insurance_number)
VALUES (18, '1992-07-22', 'INS-007');

INSERT INTO patients (id, birth_date, insurance_number)
VALUES (19, '1988-08-30', 'INS-008');

INSERT INTO patients (id, birth_date, insurance_number)
VALUES (20, '1975-09-12', 'INS-009');

INSERT INTO patients (id, birth_date, insurance_number)
VALUES (21, '2003-10-01', 'INS-010');

-- =============================
-- Citas adicionales en diferentes meses y con distintos doctores/pacientes
-- =============================

-- Abril 2025
INSERT INTO appointments (date, time, status, doctor_id, patient_id, reason, rescheduled_date, rescheduled_time, cancellation_reason)
VALUES ('2025-04-05', '09:00:00', 'PENDING', 4, 13, 'Chequeo pediátrico', NULL, NULL, NULL);

-- Mayo 2025
INSERT INTO appointments (date, time, status, doctor_id, patient_id, reason, rescheduled_date, rescheduled_time, cancellation_reason)
VALUES ('2025-05-12', '11:30:00', 'PENDING', 5, 14, 'Dolor torácico leve', NULL, NULL, NULL);

-- Junio 2025
INSERT INTO appointments (date, time, status, doctor_id, patient_id, reason, rescheduled_date, rescheduled_time, cancellation_reason)
VALUES ('2025-06-18', '15:00:00', 'PENDING', 6, 15, 'Control ginecológico anual', NULL, NULL, NULL);

-- Julio 2025
INSERT INTO appointments (date, time, status, doctor_id, patient_id, reason, rescheduled_date, rescheduled_time, cancellation_reason)
VALUES ('2025-07-22', '10:30:00', 'PENDING', 7, 16, 'Migrañas frecuentes', NULL, NULL, NULL);

-- Agosto 2025
INSERT INTO appointments (date, time, status, doctor_id, patient_id, reason, rescheduled_date, rescheduled_time, cancellation_reason)
VALUES ('2025-08-10', '16:00:00', 'PENDING', 8, 17, 'Erupción cutánea', NULL, NULL, NULL);

-- Septiembre 2025
INSERT INTO appointments (date, time, status, doctor_id, patient_id, reason, rescheduled_date, rescheduled_time, cancellation_reason)
VALUES ('2025-09-03', '09:30:00', 'PENDING', 9, 18, 'Control de tiroides', NULL, NULL, NULL);

-- Octubre 2025
INSERT INTO appointments (date, time, status, doctor_id, patient_id, reason, rescheduled_date, rescheduled_time, cancellation_reason)
VALUES ('2025-10-15', '14:00:00', 'PENDING', 10, 19, 'Dolor abdominal crónico', NULL, NULL, NULL);

-- Noviembre 2025
INSERT INTO appointments (date, time, status, doctor_id, patient_id, reason, rescheduled_date, rescheduled_time, cancellation_reason)
VALUES ('2025-11-20', '08:30:00', 'PENDING', 11, 20, 'Control de asma', NULL, NULL, NULL);

-- Diciembre 2025
INSERT INTO appointments (date, time, status, doctor_id, patient_id, reason, rescheduled_date, rescheduled_time, cancellation_reason)
VALUES ('2025-12-28', '13:00:00', 'PENDING', 12, 21, 'Evaluación oncológica', NULL, NULL, NULL);

-- Citas adicionales en 2026 para ver historial amplio
INSERT INTO appointments (date, time, status, doctor_id, patient_id, reason, rescheduled_date, rescheduled_time, cancellation_reason)
VALUES ('2026-01-10', '09:00:00', 'PENDING', 4, 13, 'Control de crecimiento', NULL, NULL, NULL);

INSERT INTO appointments (date, time, status, doctor_id, patient_id, reason, rescheduled_date, rescheduled_time, cancellation_reason)
VALUES ('2026-02-14', '10:00:00', 'PENDING', 5, 14, 'Chequeo cardiológico', NULL, NULL, NULL);