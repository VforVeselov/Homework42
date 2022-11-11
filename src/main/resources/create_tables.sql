create table if not exists users (id serial primary key, name varchar not null unique, password varchar not null, created_at timestamp DEFAULT Now());
create table if not exists post (id serial primary key, text text not null, created_at timestamp DEFAULT Now(), user_id int references users(id));
create table if not exists comment (id serial primary key, text text not null, user_id int not null references users(id), created_at timestamp DEFAULT Now());
create table if not exists likes (id serial primary key, user_id int references users(id), post_id int references post(id), comment_id int references comment(id));
