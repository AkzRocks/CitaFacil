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