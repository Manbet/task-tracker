create table if not exists tasks (
    id bigserial primary key,
    title varchar(255) not null,
    description text,
    comment varchar(255),
    is_active boolean,
    creation_time timestamp without time zone not null,
    last_update_time timestamp without time zone not null,
    due_time         timestamp without time zone not null,
    reporter         bigint not null,
    assignee         bigint
);

create table if not exists users
(
    id   bigserial primary key,
    name varchar(255) not null,
    surname varchar(255) not null,
    gender varchar(255) not null,
    birth_date timestamp without time zone not null
);

create table if not exists task_watchers
(
    task_id bigint not null,
    user_id bigint not null,
    primary key (task_id, user_id)
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

select * from tasks t join users u on t.reporter = u.id where u.name = 'Павел';
select * from tasks t join users u on t.assignee = u.id where u.name = 'Петр';

--many to many
--task_id | user_id
--5       | 1
--5       | 2
--5       | 3
--5       | 4
--6       | 1
--6       | 4

select t.* from tasks t
    join task_watchers tw on t.id = tw.task_id
    join users u on tw.user_id = u.id
where u.name = 'Эмин';

select u.* from tasks t
    join task_watchers tw on t.id = tw.task_id
    join users u on tw.user_id = u.id
where t.title = 'разбор ошибок';
