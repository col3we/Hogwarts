-- liquibase formatted sql

-- changeset col3we:1
create table faculty (
                         id bigserial primary key,
                         name varchar(255),
                         color varchar(255)
);

-- changeset col3we:2
create table student (
                         id bigserial primary key,
                         name varchar(255),
                         age integer not null,
                         faculty_id bigint,
                         constraint fk_student_faculty
                             foreign key (faculty_id)
                                 references faculty(id)
                                 on delete set null
);

-- changeset col3we:3
create index students_name_index on student (name);

-- changeset col3we:4
create index faculty_name_and_color on faculty (name, color);