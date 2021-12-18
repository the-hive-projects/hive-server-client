package org.thehive.hiveserverclient.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.thehive.hiveserverclient.Authentication;
import org.thehive.hiveserverclient.model.Error;
import org.thehive.hiveserverclient.model.Image;
import org.thehive.hiveserverclient.net.http.ImageClient;
import org.thehive.hiveserverclient.net.http.RequestCallback;
import org.thehive.hiveserverclient.util.HeaderUtils;

import java.util.function.Consumer;

@Slf4j
public class ImageServiceImpl implements ImageService {

    protected final ImageClient imageClient;

    public ImageServiceImpl(@NonNull ImageClient imageClient) {
        this.imageClient = imageClient;
    }

    @Override
    public void take(@NonNull String username, @NonNull Consumer<? super AppResponse<? extends Image>> consumer) {
        log.info("#take username: {}", username);
        if (!Authentication.INSTANCE.isAuthenticated())
            throw new IllegalStateException("Authentication instance has not been authenticated");
        imageClient.get(username, new RequestCallback<>() {
            @Override
            public void onResponse(Image responseBody) {
                var response = AppResponse.of(responseBody);
                log.info("#take username: {}, status: {}", username, response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onError(Error error) {
                var response = AppResponse.<Image>of(error.getMessage());
                log.info("#take username: {}, status: {}", username, response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onFail(Throwable t) {
                var response = AppResponse.<Image>of(t);
                log.info("#take username: {}, status: {}", username, response.status().name());
                consumer.accept(response);
            }
        }, HeaderUtils.httpBasicAuthenticationHeader(Authentication.INSTANCE.getToken()));
    }

}
