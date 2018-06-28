-- drop table user_route0_content
-- drop table route0_queue

create table user_route0_content (
    id bigserial NOT NULL,
    user_id uuid NOT NULL,
    course_id uuid NOT NULL,
    class_id uuid,
    status text NOT NULL DEFAULT 'pending' CHECK (status::text = ANY(ARRAY['pending'::text, 'accepted'::text, 'rejected'::text, 'na'::text])),
    route0_content jsonb NOT NULL,
    created_at timestamp without time zone DEFAULT timezone('UTC'::text, now()) NOT NULL,
    CONSTRAINT ur0c_pkey PRIMARY KEY (id)
);

ALTER TABLE user_route0_content OWNER TO nucleus;

CREATE UNIQUE INDEX ur0c_ucc_null_unq_idx
    ON user_route0_content (user_id, course_id, class_id)
    where class_id is not null;

CREATE UNIQUE INDEX ur0c_ucc_unq_idx
    ON user_route0_content (user_id, course_id)
    where class_id is null;

COMMENT on table user_route0_content IS 'Store the route0 content for user/course/class combination as a cache to provide complete JSON packet';

create table route0_queue (
    id bigserial NOT NULL,
    user_id uuid NOT NULL,
    course_id uuid NOT NULL,
    class_id uuid,
    priority int check (priority::int = ANY(ARRAY[1::int, 2::int, 3::int, 4::int])),
    status int NOT NULL DEFAULT 0 CHECK (status::int = ANY (ARRAY[0::int, 1::int, 2::int])),
    created_at timestamp without time zone DEFAULT timezone('UTC'::text, now()) NOT NULL,
    updated_at timestamp without time zone DEFAULT timezone('UTC'::text, now()) NOT NULL,
    CONSTRAINT r0q_pkey PRIMARY KEY (id)
);

ALTER TABLE route0_queue OWNER TO nucleus;

CREATE UNIQUE INDEX r0q_ucc_unq_idx
    ON route0_queue (user_id, course_id, class_id)
    where class_id is not null;

CREATE UNIQUE INDEX r0q_ucc_null_unq_idx
    ON route0_queue (user_id, course_id)
    where class_id is null;

COMMENT on TABLE route0_queue IS 'Persistent queue for route0 tasks';
COMMENT on COLUMN route0_queue.status IS '0 means queued, 1 means dispatched for processing, 2 means in process';
COMMENT on COLUMN route0_queue.priority IS '1 means route0 setting changed in class, 2 means course assigned to class, 3 means users joining class and 4 means OOB request for user accessing the route0 content';


create table user_route0_content_detail (
    id bigserial NOT NULL,
    user_route0_content_id bigint NOT NULL,
    unit_id uuid NOT NULL,
    unit_title text NOT NULL,
    unit_sequence int NOT NULL,
    lesson_id uuid NOT NULL,
    lesson_title text NOT NULL,
    lesson_sequence int NOT NULL,
    collection_id uuid NOT NULL,
    collection_type text NOT NULL CHECK (collection_type::text = ANY (ARRAY['collection'::text, 'collection-external'::text, 'assessment'::text, 'assessment-external'::text])),
    collection_sequence int NOT NULL,
    route0_sequence int NOT NULL,
    CONSTRAINT ur0cd_pkey PRIMARY KEY (id),
    CONSTRAINT ur0cd_ur0c_r0seq_unq UNIQUE (user_route0_content_id, route0_sequence),
    CONSTRAINT ur0ci_fkey FOREIGN KEY(user_route0_content_id) references user_route0_content(id),
    CONSTRAINT ur0cd_ur0c_coll_unq UNIQUE (user_route0_content_id, collection_id)
);

ALTER TABLE user_route0_content_detail OWNER TO nucleus;

CREATE INDEX ur0cd_ulcs_idx ON user_route0_content_detail USING BTREE (unit_id, lesson_id, collection_id, route0_sequence ASC);


-- competency_content_map (LM)
