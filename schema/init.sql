create table if not exists tasks (
    id bigint primary key,
    title varchar(255) not null,
    description text not null,
    comment varchar(255) not null,
    creation_time timestamp without time zone not null,
    last_update_time timestamp without time zone not null,
    due_time timestamp without time zone not null
);

create sequence if not exists id_seq start with 1 increment by 1;