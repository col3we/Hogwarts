select student.name, student.age, faculty.name from student
left join faculty on student.faculty_id = faculty.id;

select student.name, student.age from student
join avatar on student.id = avatar.student_id;