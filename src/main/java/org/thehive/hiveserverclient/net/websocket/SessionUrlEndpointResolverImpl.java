package org.thehive.hiveserverclient.net.websocket;

import lombok.NonNull;
import org.thehive.hiveserverclient.payload.Payload;

import java.util.HashMap;
import java.util.Map;

public class SessionUrlEndpointResolverImpl implements SessionUrlEndpointResolver {

    private final String subscriptionUrlEndpoint;
    private final Map<Class<? extends Payload>, String> payloadTypeDestinationUrlEndpointMap;

    public SessionUrlEndpointResolverImpl(@NonNull String subscriptionUrlEndpoint) {
        this.subscriptionUrlEndpoint = subscriptionUrlEndpoint;
        this.payloadTypeDestinationUrlEndpointMap = new HashMap<>();
    }

    @Override
    public String resolveSubscriptionUrlEndpoint(@NonNull String id) {
        return injectId(subscriptionUrlEndpoint, id);
    }

    @Override
    public void setDestinationUrlEndpoint(@NonNull Class<? extends Payload> payloadType, @NonNull String destinationUrlEndpoint) {
        payloadTypeDestinationUrlEndpointMap.put(payloadType, subscriptionUrlEndpoint);
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
