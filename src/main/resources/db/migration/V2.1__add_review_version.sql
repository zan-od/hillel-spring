alter table review
    add column version integer;

update review set version=1;