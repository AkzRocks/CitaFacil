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