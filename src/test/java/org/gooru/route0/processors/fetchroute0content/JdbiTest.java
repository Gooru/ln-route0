package org.gooru.route0.processors.fetchroute0content;

import java.util.UUID;

import org.gooru.route0.DBITestHelper;

/**
 * @author ashish.
 */
public class JdbiTest {

    public static void main(String[] args) {
        new JdbiTest().testUsesIsTeacherOrCoTeacherForClass();
    }

    private void testUsesIsTeacherOrCoTeacherForClass() {
        FetchRoute0ContentCommand.FetchRoute0ContentCommandBean bean =
            new FetchRoute0ContentCommand.FetchRoute0ContentCommandBean();
        bean.setTeacherId(UUID.fromString(UUID.randomUUID().toString()));
        bean.setUserId(UUID.randomUUID());
        bean.setClassId(UUID.fromString("21d4dd82-5369-459b-9b2b-cd87734cdc5e"));
        bean.setCourseId(UUID.randomUUID());

        FetchRoute0ContentDao dao = new DBITestHelper().getDBI().onDemand(FetchRoute0ContentDao.class);

        if (dao.isUserTeacherOrCollaboratorForClass(bean)) {
            System.out.println("User is teacher or collaborator");
        } else {
            System.out.println("User is not teacher or collaborator");
        }
    }
}
