insert into users (id, username, name, surname, email, password, gender, birth_date, is_active) values
(2, 'anastasia', 'Анастасия', 'Романова', 'anastasia@mail.com', '$2a$10$XURPShQNCsLjp1ESc2laoOLHTDCW9B7UKelSiEPZaLqLKjz.baF3K', 'FEMALE', '1995-05-15', true),
(3, 'alex', 'Александр', 'Островский', 'alex@mail.com', '$2a$10$XURPShQNCsLjp1ESc2laoOLHTDCW9B7UKelSiEPZaLqLKjz.baF3K', 'MALE', '1992-08-20', true),
(4, 'ivan', 'Иван', 'Гончаров', 'ivan@mail.com', '$2a$10$XURPShQNCsLjp1ESc2laoOLHTDCW9B7UKelSiEPZaLqLKjz.baF3K', 'MALE', '1990-12-10', true);

select setval('users_id_seq', 4);

insert into projects (id, name, is_active, is_open, description) values
(1, 'Веб-сайт', true, true, 'Разработка нового корпоративного сайта'),
(2, 'Мобильное приложение', true, false, 'Внутреннее мобильное приложение для сотрудников');

select setval('projects_id_seq', 2);

insert into project_users (project_id, user_id) values
(1, 2), (1, 3), (1, 4), -- Анастасия, Александр и Иван работают над веб-сайтом
(2, 2), (2, 4); -- Анастасия и Иван работают над мобильным приложением

insert into tasks (id, title, description, comments, status, creation_time, last_update_time, due_time, reporter, project, assignee) values
(1, 'Спроектировать БД', 'Сделать схему базы данных в draw.io', '{1, 2}', 'IN_PROGRESS',
 current_timestamp - interval '2 days', current_timestamp - interval '1 hour', current_timestamp + interval '5 days',
 2, 1, 3),
(2, 'Сверстать главную страницу', 'Сделать верстку по макетам из Figma', null, 'TODO',
 current_timestamp - interval '1 day', current_timestamp - interval '1 day', current_timestamp + interval '1 day',
 2, 1, 4),
(3, 'Настроить CI/CD pipeline', 'Добавить GitHub Actions', null, 'DONE',
 current_timestamp - interval '10 days', current_timestamp - interval '2 days', current_timestamp - interval '1 day',
 2, 2, 2);

select setval('tasks_id_seq', 3);

insert into comments (id, text, creation_time, last_update_time, author, task) values
(1, 'Я начал работу над базой. Пока все идет по плану.',
 current_timestamp - interval '1 day', current_timestamp - interval '1 day', 3, 1),
(2, 'Отлично! Не забудь учесть связи многие-ко-многим.',
 current_timestamp - interval '2 hours', current_timestamp - interval '2 hours', 2, 1);

select setval('comments_id_seq', 2);

insert into task_watchers (task_id, user_id) values
(1, 2), (1, 4),
(2, 2), (2, 3),
(3, 4);