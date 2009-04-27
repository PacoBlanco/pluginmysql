/*1.- Create a tablespace and user, both specific for ITECBAN PBO.*/

create tablespace itecbanPBO
datafile 'c:\oracle\10.2.0\oradata\itecban\itecbanPBO.dbf'
size 10M;

create user itecbanPBO
identified by "_itecbanPBO"
default tablespace itecbanPBO
temporary tablespace temp;

grant create session to itecbanPBO;
grant create table to itecbanPBO;
grant create view to itecbanPBO;
grant create sequence to itecbanPBO;

alter user itecbanPBO quota unlimited on itecbanPBO;

commit;


/*2.- Create a tablespace and user, for testing purposes.*/

create tablespace martaPBO
datafile '/opt/oracle/oradata/itecbanD/martaPBO.dbf'
size 10M;

create user marta
identified by "marta"
default tablespace martaPBO
temporary tablespace temp;

grant create session to marta;
grant create table to marta;
grant create view to marta;
grant create sequence to marta;

alter user marta quota unlimited on martaPBO;

commit;