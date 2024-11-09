drop table if exists roles cascade;
drop table if exists users cascade;
drop table if exists orders;
drop table if exists past_orders;
drop table if exists news;
drop table if exists responses;

create table users
(
    email           varchar(50)
        constraint "Users_email_key"
            unique,
    password        varchar(70) not null,
    id              serial
        primary key
        unique,
    username        varchar(50),
    active          boolean default false,
    activation_code varchar(100),
    name            varchar(20),
    surname         varchar(20)
);

create table roles
(
    id   integer     not null
        primary key
        constraint fkoxu306cef946ub0alay6phtf9
            references users,
    role varchar(10) not null
);

create table orders
(
    id            serial                                                     not null
        primary key,
    product_name  varchar(100)     default 'Нет названия'::character varying not null,
    max_price     double precision default 0                                 not null,
    count         integer          default 0                                 not null,
    expected_date date             default CURRENT_DATE                      not null,
    description   varchar(100),
    real_price    double precision default 0,
    real_date     date             default CURRENT_DATE,
    author        varchar(50),
    is_approved   boolean          default false,
    provider      varchar(20)
);

create table past_orders
(
    id            int                                                        not null
        primary key,
    product_name  varchar(100)     default 'Нет названия'::character varying not null,
    max_price     double precision default 0                                 not null,
    count         integer          default 0                                 not null,
    expected_date date             default CURRENT_DATE                      not null,
    description   varchar(100),
    real_price    double precision default 0,
    real_date     date             default CURRENT_DATE,
    status        varchar          default 'finished'::character varying     not null,
    author        varchar(50),
    provider      varchar(20)
);

create table news
(
    author    varchar(30),
    text      varchar(300),
    pub_date  date,
    title     varchar(50),
    id        integer,
    new_image varchar(100)
);

create table responses
(
    id serial primary key,
    id_supp int references users(id) on delete cascade on update cascade,
    id_order int references orders(id) on delete cascade,
    id_past_order int references past_orders(id) on delete cascade
);