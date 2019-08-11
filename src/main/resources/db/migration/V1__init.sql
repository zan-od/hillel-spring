create table pet (
    id serial primary key,
    kind varchar(255),
    name varchar(255)
);

create table doctor (
    id serial primary key,
    name varchar(255)
);

create table doctor_specializations (
    doctor_id integer,
    specializations varchar(255),

    foreign key (doctor_id) references doctor(id)
);

create table doctor_record (
    id serial primary key,
    doctor_id integer,
    pet_id integer,
    start_date timestamp
);