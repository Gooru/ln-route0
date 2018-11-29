
ALTER TABLE user_route0_content_detail
DROP CONSTRAINT ur0ci_fkey,
ADD CONSTRAINT ur0ci_fkey
   FOREIGN KEY (user_route0_content_id)
   REFERENCES user_route0_content(id)
   ON DELETE CASCADE;

CREATE INDEX ur0ci_idx ON user_route0_content_detail(user_route0_content_id);
