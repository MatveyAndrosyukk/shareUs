insert into sweater_user (id, active, password, username, email)
values (1, true, '$2a$12$StHXG5uhtPOK5CswFuvZNu4JqTBhFQSL/fthsMIJketj/klNuX04m', 'admin', 'administrator@mail.ru'),
       (2, true, '$2a$12$145m9lMUWTl/k7mMs4t5c.rjI50hiaxqU2CN4FTLv5Vp.OGannJam', 'user', 'user@mail.ru');

insert into sweater_roles (id, name)
values (1, 'ADMIN'), (2, 'USER');

insert into sweater_user_role (user_id, role_id)
values (1, 1), (1, 2), (2, 2);

