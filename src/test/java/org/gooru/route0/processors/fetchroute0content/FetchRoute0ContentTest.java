package org.gooru.route0.processors.fetchroute0content;

import java.util.UUID;

import org.gooru.route0.DBITestHelper;
import org.gooru.route0.infra.constants.Constants;
import org.gooru.route0.infra.data.EventBusMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.impl.MessageImpl;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author ashish.
 */
public class FetchRoute0ContentTest {

    public static void main(String[] args) {
        FetchRoute0ContentTest fetchRoute0ContentTest = new FetchRoute0ContentTest();
        fetchRoute0ContentTest.testUserIsTeacherOrCoTeacherForClass();
        fetchRoute0ContentTest.testFetchRoute0ContentService();
    }

    private void testFetchRoute0ContentService() {
        Message<JsonObject> message = createMessage();
        EventBusMessage ebMessage = EventBusMessage.eventBusMessageBuilder(message);
        FetchRoute0ContentCommand command = FetchRoute0ContentCommand.builder(ebMessage);
        FetchRoute0ContentResponse result = new DBITestHelper().getDBI().onDemand(FetchRoute0ContentDao.class)
            .fetchRoute0ContentForUserInIL(command.asBean());

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("Result: ");
        System.out.println(result.asJson().toString());
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    private void testUserIsTeacherOrCoTeacherForClass() {
        FetchRoute0ContentCommand.FetchRoute0ContentCommandBean bean =
            new FetchRoute0ContentCommand.FetchRoute0ContentCommandBean();
        bean.setTeacherId(UUID.fromString(UUID.randomUUID().toString()));
        bean.setUserId(UUID.randomUUID());
        bean.setClassId(UUID.fromString("21d4dd82-5369-459b-9b2b-cd87734cdc5e"));
        bean.setCourseId(UUID.randomUUID());

        FetchRoute0ContentDao dao = new DBITestHelper().getDBI().onDemand(FetchRoute0ContentDao.class);

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
        if (dao.isUserTeacherOrCollaboratorForClass(bean)) {
            System.out.println("User is teacher or collaborator");
        } else {
            System.out.println("User is not teacher or collaborator");
        }
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    private Message<JsonObject> createMessage() {

        return new MessageImpl<Object, JsonObject>() {

            @Override
            public MultiMap headers() {
                return DataProvider.getMessageHeader();
            }

            @Override
            public JsonObject body() {
                return DataProvider.getMessageBody();
            }
        };
    }

    private static class DataProvider {
        private static JsonObject getMessageBody() {
            return new JsonObject().put(Constants.Message.MSG_USER_ID, "6b17af6e-c2d9-4241-b9b0-2ace03a44823")
                .put(Constants.Message.MSG_KEY_SESSION, new JsonObject().put("email_id", "dummy@example.com"))
                .put(Constants.Message.MSG_SESSION_TOKEN, "session-token")
                .put(Constants.Message.MSG_HTTP_BODY, getHttpBodyForFetchRoute0Content());
        }

        private static JsonObject getHttpBodyForFetchRoute0Content() {
            JsonObject jsonObject = new JsonObject();
/*
            jsonObject.put(FetchRoute0ContentCommand.CommandAttributes.CLASS_ID,
                new JsonArray().add("6b17af6e-c2d9-4241-b9b0-2ace03a44823"));
*/
            jsonObject.put(FetchRoute0ContentCommand.CommandAttributes.COURSE_ID,
                new JsonArray().add("c3c5a610-0c97-4ecc-852c-8586a7ba3b52"));
            return jsonObject;
        }

        private static MultiMap getMessageHeader() {
            return new CaseInsensitiveHeaders().add(Constants.Message.MSG_OP, Constants.Message.MSG_OP_ROUTE0_GET);
        }
    }
}
