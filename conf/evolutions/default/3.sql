# Update table candidate with password and team information

# --- !Ups

alter table candidate add column password varchar(255);
alter table candidate add column team varchar(50);

 # --- !Downs

alter table candidate drop column password;
alter table candidate drop column team;