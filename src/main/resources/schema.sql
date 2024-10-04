
drop table if exists schedules;
drop table if exists users;

create table users
(
    created_at       datetime(6)  null,
    last_modified_at datetime(6)  null,
    user_id          bigint auto_increment
        primary key,
    password         varchar(60)  not null,
    email            varchar(255) null,
    name             varchar(255) null,
    constraint UKob8kqyqqgmefl0aco34akdtpe
        unique (email)
);

create table schedules
(
    is_public        bit          not null,
    created_at       datetime(6)  null,
    end_at           datetime(6)  null,
    last_modified_at datetime(6)  null,
    schedule_id      bigint auto_increment
        primary key,
    start_at         datetime(6)  null,
    user_id          bigint       null,
    content          varchar(255) null,
    constraint FKd4y4xekwahv9boo6lc8gfl3jv
        foreign key (user_id) references users (user_id)
);