DELETE FROM clients;

INSERT INTO clients (id, role, email, is_active, is_banned, name, password, phone, register_date, surname, telegram_chatid, unique_id)
VALUES (1, 2, 'test@example.com', 1, 0, 'Test', '$2a$08$T9sIiXNE1XLFXresuxySzOIobEVulOm4heQapih4zV8EcvOSgpJga',
        '+380123456789', NOW(), 'Test', NULL, 'F92F59CDECA1E6287539B8447E563D5487963BAFA325D6C5097E904792A41234');

INSERT INTO clients (id, role, email, is_active, is_banned, name, password, phone, register_date, surname, telegram_chatid, unique_id)
VALUES (2, 0, 'test2@example.com', 1, 0, 'Test2', '$2a$08$T9sIiXNE1XLFXresuxySzOIobEVulOm4heQapih4zV8EcvOSgpJga',
        '+380123456700', NOW(), 'Test2', '1234567890', 'F92F59CDECA1E6287539B8447E563D5487963BAFA325D6C5097E904792A41235');


