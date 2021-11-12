package org.thehive.hiveserverclient.net.http;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.thehive.hiveserverclient.model.Error;
import org.thehive.hiveserverclient.model.Session;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@RequiredArgsConstructor
public class SessionClientImpl implements SessionClient {

    protected final String url;
    protected final CloseableHttpClient httpClient;
    protected final ObjectMapper objectMapper;
    protected final ExecutorService executorService;

    @Override
    public void get(String id, RequestCallback<? super Session> callback, Header... headers) {
        var reqUrl = RequestUtils.concatUrlVariables(url, id);
        var req = RequestUtils.getRequestOf(reqUrl, headers);
        executeRequest(req, callback);
    }

    @SneakyThrows
    @Override
    public void save(Session session, RequestCallback<? super Session> callback, Header... headers) {
        var userStr = objectMapper.writeValueAsString(callback);
        var req = RequestUtils.postRequestOf(url, userStr, headers);
        executeRequest(req, callback);
    }

    private void executeRequest(@NonNull HttpRequestBase request, @NonNull RequestCallback<? super Session> callback) {
        executorService.execute(() -> {
            boolean executeFail = true;
            try {
                try (var response = httpClient.execute(request)) {
                    var responseBody = EntityUtils.toString(response.getEntity());
                    executeFail = false;
                    if (response.getStatusLine().getStatusCode() / 100 == 2)
                        callback.onRequest(objectMapper.readValue(responseBody, Session.class));
                    else
                        callback.onError(objectMapper.readValue(responseBody, Error.class));
                }
            } catch (JacksonException e) {
                e.printStackTrace();
            } catch (IOException e) {
                if (executeFail)
                    callback.onFail(e);
                else
                    e.printStackTrace();
            }
        });
    }

}
