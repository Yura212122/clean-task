INSERT INTO client_course (client_id, course_id)
VALUES (
           (SELECT id FROM clients WHERE email = 'admin@admin.example.com'),
           (SELECT id FROM study_groups WHERE name = 'ProgAcademy')
       );