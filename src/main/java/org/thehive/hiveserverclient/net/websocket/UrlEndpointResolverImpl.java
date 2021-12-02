package org.thehive.hiveserverclient.net.websocket;

import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.thehive.hiveserverclient.payload.Payload;

import java.util.HashMap;
import java.util.Map;

public class UrlEndpointResolverImpl implements UrlEndpointResolver {

    private final String subscriptionUrlEndpoint;
    private final String destinationPrefix;
    private final Map<Class<? extends Payload>, String> payloadTypeDestinationUrlEndpointMap;

    public UrlEndpointResolverImpl(@NonNull String subscriptionUrlEndpoint, @Nullable String destinationPrefix) {
        this.subscriptionUrlEndpoint = subscriptionUrlEndpoint;
        this.destinationPrefix = destinationPrefix;
        this.payloadTypeDestinationUrlEndpointMap = new HashMap<>();
    }

    public UrlEndpointResolverImpl(@NonNull String subscriptionUrlEndpoint) {
        this(subscriptionUrlEndpoint, null);
    }

    @Override
    public String resolveSubscriptionUrlEndpoint(@NonNull String id) {
        return injectId(subscriptionUrlEndpoint, id);
    }

    @Override
    public void addDestinationUrlEndpoint(@NonNull Class<? extends Payload> payloadType, @NonNull String destinationUrlEndpoint) {
        if (destinationPrefix != null)
            destinationUrlEndpoint = destinationPrefix.concat(destinationUrlEndpoint);
        payloadTypeDestinationUrlEndpointMap.put(payloadType, destinationUrlEndpoint);
    }

    @Override
    public boolean containsDestinationUrlEndpoint(@NonNull Class<? extends Payload> payloadType) {
        return payloadTypeDestinationUrlEndpointMap.containsKey(payloadType);
    }

    @Override
    public String resolveDestinationUrlEndpoint(@NonNull Class<? extends Payload> payloadType, @NonNull String id) {
        return injectId(payloadTypeDestinationUrlEndpointMap.get(payloadType), id);
    }

    private String injectId(@NonNull String urlEndpoint, @NonNull String id) {
        if (urlEndpoint.contains("{id}"))
            return urlEndpoint.replace("{id}", id);
        return urlEndpoint;
    }

}
