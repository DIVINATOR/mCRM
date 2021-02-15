create table if not exists "t_catalog_subtype"
(
    id   identity,
    "name" varchar
);

create table if not exists "t_catalog_details"
(
    id  identity,
    "name"       varchar,
    "subtype_id" long auto_increment,
    "details_id" long auto_increment,
    constraint "subtype_id___fk"
        foreign key ("subtype_id")
            references "t_catalog_subtype" (id)
);

create table if not exists "t_call_history"
(
    id identity,
    "timestamp" long,
    "subtype" varchar,
    "details" varchar,
    "tid" varchar,
    "title" varchar
);

create table if not exists "t_users"
(
    id identity,
    "login" varchar,
    "firstname" varchar,
    "surname" varchar,
    "patronymic" varchar
)