
drop table if exists schedule_user;
drop table if exists comments;
drop table if exists schedules;
drop table if exists users;

create table users
(
    created_at       datetime(6)            null,
    id               bigint auto_increment
        primary key,
    last_modified_at datetime(6)            null,
    name             varchar(12)            null,
    password         varchar(60)            not null,
    email            varchar(255)           null,
    role             enum ('ADMIN', 'USER') null,
    constraint UK6dotkott2kjsp8vw4d0m25fb7
        unique (email)
);



create table schedules
(
    is_public           bit          not null,
    created_at          datetime(6)  null,
    end_at              datetime(6)  null,
    id                  bigint auto_increment
        primary key,
    last_modified_at    datetime(6)  null,
    start_at            datetime(6)  null,
    user_id             bigint       null,
    content             varchar(512) null,
    weather_on_creation varchar(255) null,
    constraint FKd4y4xekwahv9boo6lc8gfl3jv
        foreign key (user_id) references users (id)
);



create table comments
(
    created_at       datetime(6)  null,
    id               bigint auto_increment
        primary key,
    last_modified_at datetime(6)  null,
    schedule_id      bigint       null,
    user_id          bigint       null,
    content          varchar(512) null,
    constraint FK8omq0tc18jd43bu5tjh6jvraq
        foreign key (user_id) references users (id),
    constraint FKbef7m370enopdpf7yp6nmv0oo
        foreign key (schedule_id) references schedules (id)
);

create table schedule_user
(
    created_at       datetime(6) null,
    id               bigint auto_increment
        primary key,
    last_modified_at datetime(6) null,
    schedule_id      bigint      null,
    user_id          bigint      null,
    constraint FK5mmtc1rwy49p0w68lrpdox1o
        foreign key (schedule_id) references schedules (id),
    constraint FKikussghgqxw29x5mryic21ynb
        foreign key (user_id) references users (id)
);

