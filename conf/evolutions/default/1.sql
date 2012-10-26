# Candidates schema

# --- !Ups

create table candidate (
  id serial,
  email varchar(50),
  profile varchar(30),
  mainTechnologies varchar(1000),
  otherTechnologies varchar(100),
  twitterAccount varchar(20),
  portfolio varchar(200),
  primary key (id)
 );

 # --- !Downs

 drop table candidate;