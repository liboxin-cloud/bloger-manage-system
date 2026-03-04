-- 创建数据库
create database big_event;

-- 使用数据库
use big_event;

-- 用户表
create table user (
                      id int unsigned primary key auto_increment comment 'ID',
                      username varchar(20) not null unique comment '用户名',
                      password varchar(60) comment '密码',
                      nickname varchar(10) default '' comment '昵称',
                      email varchar(128) default '' comment '邮箱',
                      user_pic varchar(128) default '' comment '头像',
                      status tinyint default 1 comment '状态：1正常，0禁用',
                      create_time datetime not null comment '创建时间',
                      update_time datetime not null comment '修改时间'
) comment '用户表';

-- 分类表
create table category(
                         id int unsigned primary key auto_increment comment 'ID',
                         category_name varchar(32) not null comment '分类名称',
                         category_alias varchar(32) not null comment '分类别名',
                         create_user int unsigned not null comment '创建人ID',
                         create_time datetime not null comment '创建时间',
                         update_time datetime not null comment '修改时间',
                         constraint fk_category_user foreign key (create_user) references user(id)
) comment '分类表';

-- 文章表
create table article(
                        id int unsigned primary key auto_increment comment 'ID',
                        title varchar(30) not null comment '文章标题',
                        content varchar(10000) not null comment '文章内容',
                        cover_img varchar(128) not null comment '文章封面',
                        state varchar(10) default '草稿' comment '文章状态: 已发布/草稿',
                        category_id int unsigned comment '文章分类ID',
                        create_user int unsigned not null comment '创建人ID',
                        creator_type tinyint default 0 comment '创建者身份：0普通用户，1管理员',
                        create_time datetime not null comment '创建时间',
                        update_time datetime not null comment '修改时间',

    -- 违规相关字段（新增）
                        is_violation boolean default false comment '是否违规：true违规，false正常',
                        violation_reason varchar(255) comment '违规原因',
                        check_time datetime comment '审核时间',
                        check_admin int comment '审核管理员ID',

    -- 热度字段（新增）
                        popularity int default 0 comment '文章热度(0-10)',

                        constraint fk_article_category foreign key (category_id) references category(id),
                        constraint fk_article_user foreign key (create_user) references user(id)
) comment '文章表';

-- 管理员表（新增）
create table admin_user(
                           id bigint primary key auto_increment comment '管理员ID',
                           username varchar(50) not null unique comment '用户名',
                           password varchar(100) not null comment '密码',
                           nickname varchar(50) comment '昵称',
                           email varchar(100) comment '邮箱',
                           role tinyint default 0 comment '角色：0普通管理员，1超级管理员',
                           status tinyint default 1 comment '状态：1正常，0禁用，2锁定',
                           login_attempts int default 0 comment '登录失败次数',
                           last_failed_login_time datetime comment '最后一次失败登录时间',
                           create_time datetime comment '创建时间',
                           update_time datetime comment '更新时间',
                           index idx_username (username),
                           index idx_email (email)
) comment '管理员表';

-- 验证码存储表（可选，用于忘记密码功能）
create table verification_code(
                                  id bigint primary key auto_increment comment 'ID',
                                  email varchar(100) not null comment '邮箱',
                                  code varchar(6) not null comment '验证码',
                                  type varchar(20) comment '类型：reset_password/register',
                                  expire_time datetime not null comment '过期时间',
                                  create_time datetime default current_timestamp comment '创建时间',
                                  used tinyint default 0 comment '是否已使用：0未使用，1已使用',
                                  index idx_email (email),
                                  index idx_code (code)
) comment '验证码表';