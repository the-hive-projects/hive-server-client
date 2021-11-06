package org.thehive.hiveserverclient.net.http;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.Header;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.thehive.hiveserverclient.model.Error;
import org.thehive.hiveserverclient.model.Image;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

@RequiredArgsConstructor
public class ImageClientImpl implements ImageClient {

    protected final String url;
    protected final CloseableHttpClient httpClient;
    protected final ObjectMapper objectMapper;
    protected final ThreadPoolExecutor executor;

    @Override
    public void get(String username, RequestCallback<? super Image> callback, Header... headers) {
        var reqUrl = RequestUtils.concatUrlVariables(url, username);
        var req = RequestUtils.getRequestOf(reqUrl, headers);
        executor.execute(() -> {
            var executeFail = true;
            try {
                try (var response = httpClient.execute(req)) {
                    var responseBody = EntityUtils.toString(response.getEntity());
                    executeFail = false;
                    if (response.getStatusLine().getStatusCode() / 100 == 2)
                        callback.onRequest(objectMapper.readValue(responseBody, Image.class));
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
