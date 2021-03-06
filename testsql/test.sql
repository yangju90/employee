create table if not exists `student`(
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(20)  DEFAULT NULL COMMENT '姓名',
    `subject` VARCHAR(20)  DEFAULT NULL COMMENT '班级',
    `class` VARCHAR(20)  DEFAULT NULL COMMENT '学科',
    `score` INT  DEFAULT NULL COMMENT '分数',
    PRIMARY KEY (`id`)
);

insert into student (name,class,subject, score) values ('mat','01', '数学', FLOOR(1 + (RAND() * 101)));
insert into student (name,class,subject, score) values ('li','01', '英语', 60);
insert into student (name,class,subject, score) values ('wang','01', '语文', 40);

insert into student (name,class,subject, score) values ('xi','01', '数学', 99);
insert into student (name,class,subject, score) values ('qin','01', '英语', 77);
insert into student (name,class,subject, score) values ('mat','01', '语文', 50);

insert into student (name,class,subject, score) values ('vf','01', '数学', 12);
insert into student (name,class,subject, score) values ('mat','01', '英语', 34);
insert into student (name,class,subject, score) values ('zz','01', '语文', 90);