# Ratings table

# --- !Ups

create table rating (
  id serial,
  id_idea NUMERIC,
  candidate_email varchar(50),
  rating NUMERIC,
  primary key (id)
 );

 # --- !Downs

 drop table rating;