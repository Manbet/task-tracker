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
    birth_date date,
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

create table spring_session
(
    primary_id            char(36) not null,
    session_id            char(36) not null,
    creation_time         bigint   not null,
    last_access_time      bigint   not null,
    max_inactive_interval int      not null,
    expiry_time           bigint   not null,
    principal_name        varchar(100),
    constraint spring_session_PK primary key (primary_id)
);

create unique index spring_session_ix1 on spring_session (session_id);
create index spring_session_ix2 on spring_session (expiry_time);
create index spring_session_ix3 on spring_session (principal_name);

create table spring_session_attributes
(
    session_primary_id char(36)     not null,
    attribute_name     varchar(200) not null,
    attribute_bytes    bytea        not null,
    constraint spring_session_attributes_PK primary key (session_primary_id, attribute_name),
    constraint spring_session_attributes_FK foreign key (session_primary_id) references spring_session (primary_id) on delete cascade
);

insert into users (username, gender, password, email, is_active)
values ('bot', 'SYSTEM', '$2a$10$XURPShQNCsLjp1ESc2laoObo9QZDhxz73hJPaEv7/cBha4pk0AgP.', 'bot@mail.com', true)
returning id;