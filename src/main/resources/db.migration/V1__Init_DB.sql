-- Create tables
create table certificate_tasks (
                                   id bigint not null auto_increment,
                                   group_name varchar(255),
                                   is_generated bit,
                                   is_send bit,
                                   send_error varchar(255),
                                   user_id bigint,
                                   primary key (id)
) engine=InnoDB;

create table certificates (
                              id bigint not null,
                              file mediumblob not null,
                              group_name varchar(255),
                              unique_id varchar(255) not null,
                              user_id bigint,
                              primary key (id)
) engine=InnoDB;

create table certificates_seq (
                                  next_val bigint
) engine=InnoDB;

insert into certificates_seq values (1);

create table client_course (
                               client_id bigint not null,
                               course_id bigint not null,
                               primary key (client_id, course_id)
) engine=InnoDB;

create table client_emails (
                               client_id bigint not null,
                               email varchar(255),
                               primary key (client_id, email)
) engine=InnoDB;

create table client_phones (
                               client_id bigint not null,
                               phone varchar(255),
                               primary key (client_id, phone)
) engine=InnoDB;

create table clients (
                         role integer not null,
                         id bigint not null auto_increment,
                         email varchar(255) not null,
                         is_active bit,
                         is_banned bit,
                         name varchar(255) not null,
                         password varchar(255),
                         phone varchar(16) not null,
                         register_date datetime(6) not null,
                         surname varchar(255) not null,
                         telegram_chatid varchar(255),
                         unique_id varchar(255) not null,
                         primary key (id)
) engine=InnoDB;

create table invite_codes (
                              id bigint not null,
                              code varchar(255) not null,
                              destination varchar(255),
                              destination_type varchar(255),
                              expiration_date datetime(6) not null,
                              role smallint not null,
                              usage_count integer not null,
                              primary key (id)
) engine=InnoDB;

create table invite_codes_seq (
                                  next_val bigint
) engine=InnoDB;

insert into invite_codes_seq values (1);

create table lesson (
                        id bigint not null auto_increment,
                        description_url varchar(255),
                        name varchar(255),
                        sheet_number integer,
                        spreadsheetid varchar(255),
                        video_url varchar(255),
                        primary key (id)
) engine=InnoDB;

create table lesson_group (
                              lesson_id bigint not null,
                              group_id bigint not null,
                              primary key (lesson_id, group_id)
) engine=InnoDB;

create table study_groups (
                              id bigint not null auto_increment,
                              name varchar(255) not null,
                              register_date datetime(6) not null,
                              primary key (id)
) engine=InnoDB;

create table task (
                      id bigint not null auto_increment,
                      deadline date,
                      description_url varchar(255),
                      expected_result smallint,
                      is_active bit,
                      name varchar(255),
                      lesson_id bigint,
                      primary key (id)
) engine=InnoDB;

create table task_students (
                               task_id bigint not null,
                               student_id bigint not null,
                               primary key (task_id, student_id)
) engine=InnoDB;

create table task_answer (
                             id bigint not null auto_increment,
                             answer_url varchar(255),
                             course varchar(255),
                             course_id bigint,
                             is_correction bit,
                             is_passed bit,
                             is_read bit,
                             lesson_id bigint,
                             lesson_num integer,
                             message_for_correction text,
                             submitted_date datetime(6),
                             zip_answer_file mediumblob,
                             task_id bigint,
                             user_id bigint,
                             primary key (id)
) engine=InnoDB;

create table test (
                      id bigint not null auto_increment,
                      deadline date,
                      is_passed bit,
                      mandatory bit,
                      name varchar(255),
                      test_url varchar(255),
                      lesson_id bigint,
                      primary key (id)
) engine=InnoDB;

create table test_answer_correct_answers (
                                             test_question_id bigint not null,
                                             correct_answer varchar(255),
                                             primary key (test_question_id, correct_answer)
) engine=InnoDB;

create table test_answer_options (
                                     test_question_id bigint not null,
                                     options varchar(255),
                                     primary key (test_question_id, options)
) engine=InnoDB;

create table test_answer (
                             id bigint not null auto_increment,
                             attempt integer,
                             course varchar(255),
                             is_passed bit,
                             submitted_date datetime(6),
                             total_score varchar(255),
                             test_id bigint,
                             user_id bigint,
                             primary key (id)
) engine=InnoDB;

create table test_question_from_google_docs (
                                                id bigint not null auto_increment,
                                                question varchar(255),
                                                test_id bigint,
                                                primary key (id)
) engine=InnoDB;

-- Add constraints
alter table certificates add constraint UK_b9i0njv3bikopskprodkb8805 unique (unique_id);
alter table client_emails add constraint UK_r9jfi0mc0beca5lyq237qiyqh unique (email);
alter table client_phones add constraint UK_oi4dejnsv984oq1bc51iyfeli unique (phone);
alter table clients add constraint UK_srv16ica2c1csub334bxjjb59 unique (email);
alter table clients add constraint UK_e3it7h0veoeergtkfqxhos5qj unique (phone);
alter table clients add constraint UK_cvlhe64a1l9m1cownuqeh1jrd unique (unique_id);
alter table invite_codes add constraint UK_ewef0q2x39gfns3l6ats16gh unique (code);
alter table study_groups add constraint UK_27ty2g15yfxifu5wc49j7goid unique (name);

-- Add foreign key constraints
alter table certificates add constraint FK339qqw4f6o7fwy7wl5neqw2rk foreign key (user_id) references clients (id);
alter table client_course add constraint FKpsn39asfnt6chq21il9csudiu foreign key (course_id) references study_groups (id);
alter table client_course add constraint FK6qu006k50gvoadfv8kjpm4fj1 foreign key (client_id) references clients (id);
alter table client_emails add constraint FK9fprx2avgpau9tnoq11l6mcv5 foreign key (client_id) references clients (id);
alter table client_phones add constraint FKiiqlubvigw1abrsg2bc3ql2bv foreign key (client_id) references clients (id);
alter table lesson_group add constraint FK5y1t3glesqsbx83e26r141hn4 foreign key (group_id) references study_groups (id);
alter table lesson_group add constraint FK769lt252xdms1h6yhplk2n5eq foreign key (lesson_id) references lesson (id);
alter table task add constraint FK5x8hrayewoued0usmps6rhk9e foreign key (lesson_id) references lesson (id);
alter table task_students add constraint FK8qjhniapyb3pmhfsdgadpt1cp foreign key (student_id) references clients (id);
alter table task_students add constraint FK7nxmyj1w7knrgvfob4b968yo foreign key (task_id) references task (id);
alter table task_answer add constraint FKtiuuv30gw1nncf5o1467we16q foreign key (task_id) references task (id);
alter table task_answer add constraint FKgh8e9dil0of2ad605k4jbfvvx foreign key (user_id) references clients (id);
alter table test add constraint FKfevux0up13qx3a5m7woxxwwx6 foreign key (lesson_id) references lesson (id);
alter table test_answer_correct_answers add constraint FKgvyosbnuhcoa305civog0nvyj foreign key (test_question_id) references test_question_from_google_docs (id);
alter table test_answer_options add constraint FKqbr0uyi9g65ypmohl6wwoe9kf foreign key (test_question_id) references test_question_from_google_docs (id);
alter table test_answer add constraint FKeo1mweqhefkvmnecvlp4iunn foreign key (test_id) references test (id);
alter table test_answer add constraint FKr5rgvxgkdi22jev8cyfa4cjq foreign key (user_id) references clients (id);
alter table test_question_from_google_docs add constraint FKgl375cioe6t9hjurfaw4nmxuc foreign key (test_id) references test (id);