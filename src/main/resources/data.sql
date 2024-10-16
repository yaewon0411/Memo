INSERT INTO users (id, created_at, last_modified_at, password, email, name, role)
VALUES (1, NOW(), NOW(), '$2a$10$lM62v3cud7yPsgSe9GyZWOu4p5.sDCbvJdkkesiM3.9uWrgZhzCBG', 'root1234@naver.com', 'root',
        'ADMIN');


INSERT INTO schedules (is_public, created_at, end_at, last_modified_at, start_at, user_id, content)
VALUES (TRUE, now(), DATEADD('DAY', 2, NOW()), now(), DATEADD('MINUTE', 50, NOW()), 1, '과제 마무리 하기');
