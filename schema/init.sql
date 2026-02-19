create table if not exists tasks (
    id bigint primary key,
    title varchar(255) not null,
    description text not null,
    comment varchar(255) not null
);

create sequence if not exists id_seq start with 1 increment by 1;