
-- 创建数据库
-- create database if not exists my_everything_plus;

-- 创建数据库表
drop table if exists file_index;
create table if not exists file_index
(
  name varchar(256) not null comment '文件名称',
  path varchar(1024) not null comment '文件路径',
  depath int not null comment '文件路径深度',
  file_type varchar(32) not null comment '文件类型'
);

-- 创建索引
-- create index file_name from file_index(name);
