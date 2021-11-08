package org.thehive.hiveserverclient.net.websocket.message;

import lombok.*;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractMessage<T> implements Message<T> {

    private Map<String, Object> headers;

    @Override
    public Map<String, Object> getHeaders() {
        if (headers == null)
            this.headers = new HashMap<>();
        return headers;
    }

    public void addHeader(@NonNull String key, @NonNull Object value) {
        if (headers == null)
            this.headers = new HashMap<>();
        this.headers.put(key, value);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <V> V getHeader(@NonNull String key) {
        if (headers == null)
            return null;
        return (V) headers.get(key);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <V> V getHeader(@NonNull String key, @NonNull Class<V> type) {
        if (headers == null)
            return null;
        return (V) headers.get(key);
    }

    public boolean containsHeader(@NonNull String key) {
        if (headers == null)
            return false;
        return headers.containsKey(key);
    }

}
