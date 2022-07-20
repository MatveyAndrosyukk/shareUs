create table sweater_message
(
    id       bigint       not null auto_increment,
    filename varchar(255),
    tag      varchar(255),
    text     varchar(255) not null,
    user_id  bigint,
    primary key (id)
);

create table sweater_roles
(
    id   bigint not null auto_increment,
    name varchar(255),
    primary key (id)
);

create table sweater_user
(
    id              bigint       not null auto_increment,
    activation_code varchar(255),
    active          bit          not null,
    email           varchar(255),
    password        varchar(255) not null,
    username        varchar(255) not null,
    image_filename        varchar(255),
    primary key (id)
);

create table sweater_user_role
(
    user_id bigint not null,
    role_id bigint not null
);

create table sweater_user_subscriptions
(
    subscriber_id    bigint not null,
    channel_id bigint not null,
    primary key (subscriber_id, channel_id)
);

alter table sweater_message
    add constraint message_user_fk
        foreign key (user_id) references sweater_user (id);


alter table sweater_user_role
    add constraint user_role_role_fk
        foreign key (role_id) references sweater_roles (id);

alter table sweater_user_role
    add constraint user_role_user_fk
        foreign key (user_id) references sweater_user (id);

alter table sweater_user_subscriptions
    add constraint user_role_subscriptions_channel_fk
        foreign key (channel_id) references sweater_user (id);

alter table sweater_user_subscriptions
    add constraint user_role_subscriptions_subscriber_fk
        foreign key (subscriber_id) references sweater_user (id);