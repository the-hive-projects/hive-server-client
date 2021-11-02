package org.thehive.hiveserverclient.net.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.thehive.hiveserverclient.model.User;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class DefaultUserClient implements UserClient {

    protected final String serverBaseUrl;
    protected final ThreadPoolExecutor executor;
    protected final ObjectMapper objectMapper;
    protected final CloseableHttpClient httpClient;

    public DefaultUserClient(String serverBaseUrl, ObjectMapper objectMapper) {
        this.serverBaseUrl = serverBaseUrl;
        this.objectMapper = objectMapper;
        this.executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        this.httpClient=HttpClients.createSystem();
    }

    @Override
    public void get(RequestCallback<User> callback, Header... headers) {
        executor.execute(() -> {
            var req = new HttpGet(serverBaseUrl.concat(UrlEndpointConstants.USER));
            for (var h : headers)
                req.addHeader(h);
            try {
                try (var response = httpClient.execute(req)) {
                    var responseBody = EntityUtils.toString(response.getEntity());
                    if (response.getStatusLine().getStatusCode() % 100 == 2)
                        callback.onRequest(objectMapper.readValue(responseBody, User.class));
                    else
                        callback.onError(objectMapper.readValue(responseBody, Error.class));
                }
            } catch (Throwable t) {
                callback.onFail(t);
            }
        });
    }

}
