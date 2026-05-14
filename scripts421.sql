alter table student
    add constraint student_age_check check (age >= 16),
    add constraint student_unique unique (name),
    add constraint student_name_not_null check (name is not null),
    alter column age set default 20;

alter table faculty
    add constraint faculty_color_unique unique (name, color);