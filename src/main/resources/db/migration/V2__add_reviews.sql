create table review (
    id serial primary key,
    doctor_record_id integer,
    review_date timestamp,
    service_rating smallint,
    equipment_rating smallint,
    qualification_rating smallint,
    treatment_rating smallint,
    total_rating smallint,
    review_comment varchar(255)
);