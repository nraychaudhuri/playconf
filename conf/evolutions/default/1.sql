# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table registered_user (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  twitter_id                varchar(255),
  description               varchar(255),
  picture_url               varchar(255),
  registration_date         date,
  constraint pk_registered_user primary key (id))
;

create table speaker (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  email                     varchar(255),
  bio                       varchar(1000),
  picture_url               varchar(255),
  twitter_id                varchar(255),
  constraint pk_speaker primary key (id))
;

create table submission (
  id                        bigint auto_increment not null,
  title                     varchar(255),
  proposal                  varchar(1000),
  type                      integer,
  is_approved               tinyint(1) default 0,
  keywords                  varchar(255),
  speaker_id                bigint,
  constraint ck_submission_type check (type in (0,1)),
  constraint pk_submission primary key (id))
;

alter table submission add constraint fk_submission_speaker_1 foreign key (speaker_id) references speaker (id) on delete restrict on update restrict;
create index ix_submission_speaker_1 on submission (speaker_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table registered_user;

drop table speaker;

drop table submission;

SET FOREIGN_KEY_CHECKS=1;

