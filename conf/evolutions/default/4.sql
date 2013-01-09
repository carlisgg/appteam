# Ideas table

# --- !Ups

create table idea (
  id serial,
  candidate_email varchar(50),
  team varchar(50),
  title varchar(255),
  description varchar(4000),
  rating int,
  shared boolean,
  primary key (id)
 );

 # --- !Downs

 drop table idea;