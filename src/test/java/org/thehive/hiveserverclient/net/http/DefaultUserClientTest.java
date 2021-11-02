package org.thehive.hiveserverclient.net.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.Test;
import org.thehive.hiveserverclient.model.User;
import org.thehive.hiveserverclient.util.HeaderUtil;

class DefaultUserClientTest {

    DefaultUserClient defaultUserClient = new DefaultUserClient("http://localhost:8080", new ObjectMapper());

    @Test
    void get() throws InterruptedException {
        var h = new BasicHeader(HeaderUtil.HTTP_BASIC_AUTHORIZATION_HEADER_NAME, HeaderUtil.httpBasicAuthorizationHeaderValue("userr", "password"));
        defaultUserClient.get(new RequestCallback<User>() {
            @Override
            public void onRequest(User data) {
                System.out.println(data);
            }

            @Override
            public void onError(Error e) {
                System.out.println(e);
            }

            @Override
            public void onFail(Throwable t) {
                System.out.println(t);
            }
        }, h);


        Thread.sleep(100000);

    }

}