package com.alphasense.backend.client.core.rest.http;

import java.nio.charset.StandardCharsets;

/**
 * Used for storing content type constants for requests.
 */

public final class ContentType {

    public static final String IMAGE_JPG = "image/jpeg";
    public static final String VIDEO_MPEG = "video/mpeg";
    public static final String JSON = "application/json";
    public static final String TEXT = "text/plain";

    public static final org.apache.http.entity.ContentType CONTENT_TYPE_JSON =
            org.apache.http.entity.ContentType.create(JSON, StandardCharsets.UTF_8);
}
