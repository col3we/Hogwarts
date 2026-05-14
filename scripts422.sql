create table car (
    id bigint primary key generated always as identity,
    brand varchar(50) not null,
    model varchar(50) not null,
    price decimal(10,2) not null
);

create table person (
    id bigint primary key generated always as identity,
    name varchar(100) not null,
    age integer not null,
    license boolean not null default false
);

create table person_car (
    person_id bigint references person(id),
    car_id bigint references car(id),
    primary key (person_id, car_id)
);