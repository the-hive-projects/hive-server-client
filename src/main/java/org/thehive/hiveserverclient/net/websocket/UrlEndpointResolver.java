package org.thehive.hiveserverclient.net.websocket;

import org.thehive.hiveserverclient.payload.Payload;

public interface UrlEndpointResolver {

    String resolveSubscriptionUrlEndpoint(String id);

    void addDestinationUrlEndpoint(Class<? extends Payload> payloadType, String destinationUrlEndpoint);

    boolean containsDestinationUrlEndpoint(Class<? extends Payload> payloadType);

    String resolveDestinationUrlEndpoint(Class<? extends Payload> payloadType, String id);

}
