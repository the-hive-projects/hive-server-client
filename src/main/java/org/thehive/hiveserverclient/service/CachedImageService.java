package org.thehive.hiveserverclient.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.thehive.hiveserverclient.model.Image;
import org.thehive.hiveserverclient.net.http.ImageClient;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

@Slf4j
public class CachedImageService extends ImageServiceImpl {

    private final Cache<String, Image> cache;
    private final ExecutorService executorService;

    public CachedImageService(@NonNull ImageClient imageClient, ExecutorService executorService) {
        this(imageClient, executorService, 500);
    }

    public CachedImageService(@NonNull ImageClient imageClient, ExecutorService executorService, int maxCacheSize) {
        this(imageClient, executorService, Caffeine.newBuilder().maximumSize(maxCacheSize).build());
    }

    public CachedImageService(@NonNull ImageClient imageClient, @NonNull ExecutorService executorService, @NonNull Cache<String, Image> cache) {
        super(imageClient);
        this.cache = cache;
        this.executorService = executorService;
    }

    @SuppressWarnings({"unchecked", "OptionalGetWithoutIsPresent"})
    @Override
    public void take(@NonNull String username, @NonNull Consumer<? super AppResponse<? extends Image>> consumer) {
        var cachedImage = cache.getIfPresent(username);
        if (cachedImage != null) {
            log.info("Image is taken from cache, username: {}", username);
            executorService.execute(() -> consumer.accept(AppResponse.wrap(cachedImage)));
        } else {
            super.take(username, consumer.andThen(response -> {
                var appResponse = (AppResponse<? extends Image>) response;
                if (appResponse.status().isSuccess())
                    cache.put(username, appResponse.response().get());
            }));
        }
    }

}

