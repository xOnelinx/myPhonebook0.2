# --- !Ups

create table "PEOPLE" (
  "ID" bigint generated by default as identity(start with 1) not null primary key,
  "NAME" varchar not null,
  "PHONE" varchar not null
);
INSERT INTO PEOPLE (ID, NAME, PHONE) VALUES (1,'GadkiKostya','+79788216710');
INSERT INTO PEOPLE (ID, NAME, PHONE) VALUES (2,'Denis','+79788222521');
# --- !Downs

drop table "PEOPLE" if exists;
