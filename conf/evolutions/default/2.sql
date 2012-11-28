# Update twitter account max lenght to 100

# --- !Ups

alter table candidate alter column twitteraccount type varchar(100);

 # --- !Downs

alter table candidate alter column twitteraccount type varchar(20);