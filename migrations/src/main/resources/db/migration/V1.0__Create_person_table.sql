create table USER (
    ID bigint not null auto_increment,
    NAME varchar(100) not null,
    PRIMARY KEY (ID)
);

create table ROLE (
  ID bigint not null auto_increment,
  description varchar(200) not null,
  extends varchar(2000),
  create_at timestamp default current_timestamp ,
  enabled tinyint default 0,
  PRIMARY KEY (ID)
)