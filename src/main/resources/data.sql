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