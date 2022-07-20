delete
from sweater_user_role;
delete
from sweater_roles;
delete
from sweater_message;
delete
from sweater_user;

insert into sweater_user (id, active, password, username)
values (1, true, "$2a$12$StHXG5uhtPOK5CswFuvZNu4JqTBhFQSL/fthsMIJketj/klNuX04m", "admin");

insert into sweater_roles (id, name)
values (1, "ADMIN"),
       (2, "USER");

insert into sweater_user_role (user_id, role_id)
values (1, 1),
       (1, 2);