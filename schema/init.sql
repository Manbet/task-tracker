create table if not exists tasks
(
    id               bigserial primary key,
    title            varchar(255)                not null,
    description      text,
    comments         bigint[],
    status           text,
    creation_time    timestamp without time zone not null,
    last_update_time timestamp without time zone not null,
    due_time         timestamp without time zone not null,
    reporter         bigint                      not null,
    project          bigint                      not null,
    assignee         bigint
);

create table if not exists users
(
    id         bigserial primary key,
    username   varchar(255) not null,
    name       varchar(255),
    surname    varchar(255),
    email      varchar(255) not null,
    password   varchar(255) not null,
    gender     varchar(255) not null,
    birth_date timestamp without time zone,
--     role       varchar(255) not null,
    is_active  boolean
);

create table if not exists projects
(
    id          bigserial primary key,
    name        varchar(255) not null,
    is_active   boolean      not null,
    is_open     boolean      not null,
    description text
);

create table if not exists comments
(
    id               bigserial primary key,
    text             text                        not null,
    creation_time    timestamp without time zone not null,
    last_update_time timestamp without time zone not null,
    author           bigint,
    task             bigint                      not null
);

create table if not exists task_watchers
(
    task_id bigint not null,
    user_id bigint not null,
    primary key (task_id, user_id)
);

create table if not exists project_users
(
    project_id bigint not null,
    user_id    bigint not null,
    primary key (project_id, user_id)
);

create table if not exists user_roles
(
    user_id bigint       not null primary key,
    role    varchar(255) not null
);

--1, 2, 3, 4
--5, 6

--one to
--table task
--reporter | assignee
--1        | 4
--2        | 1
--3        | 4
--4        | 3
--1        | 2
--4        | 1

select *
from tasks t
         join users u on t.reporter = u.id
where u.name = 'Павел';
select *
from tasks t
         join users u on t.assignee = u.id
where u.name = 'Петр';

--many to many
--task_id | user_id
--5       | 1
--5       | 2
--5       | 3
--5       | 4
--6       | 1
--6       | 4

select t.*
from tasks t
         join task_watchers tw on t.id = tw.task_id
         join users u on tw.user_id = u.id
where u.name = 'Эмин';

select u.*
from tasks t
         join task_watchers tw on t.id = tw.task_id
         join users u on tw.user_id = u.id
where t.title = 'разбор ошибок';

insert into users (username, gender, password, email, is_active)
values ('bot', 'SYSTEM', 'admin', 'bot@example.com', true)
returning id;