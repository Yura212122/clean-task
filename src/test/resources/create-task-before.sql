DELETE FROM task_students;
DELETE FROM task;

INSERT INTO task (id, deadline, description_url, expected_result, is_active, name, lesson_id)
VALUES (2, '2024-12-31', 'http://example.com/task-description', 1, 1, 'Test Task', NULL);

INSERT INTO task_students (task_id, student_id)
VALUES (2, 1);