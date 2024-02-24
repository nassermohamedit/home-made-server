package mohamednasser.projects.http;

import java.util.HashMap;
import java.util.Map;

public class ContentType {

    private static final Map<String, String> EXTENSION_CONTENT_TYPE_MAP;

    static {
        EXTENSION_CONTENT_TYPE_MAP = new HashMap<>();
        EXTENSION_CONTENT_TYPE_MAP.put("html", "text/html");
        EXTENSION_CONTENT_TYPE_MAP.put("css", "text/css");
        EXTENSION_CONTENT_TYPE_MAP.put("js", "application/javascript");
        EXTENSION_CONTENT_TYPE_MAP.put("jpg", "image/jpeg");
        EXTENSION_CONTENT_TYPE_MAP.put("png", "image/png");
        EXTENSION_CONTENT_TYPE_MAP.put("json", "application/json");
        EXTENSION_CONTENT_TYPE_MAP.put("xml", "application/xml");

    }

    public static String inferContentType(String resourceName) {
        String ext = resourceName.substring(resourceName.lastIndexOf('.') + 1);
       return EXTENSION_CONTENT_TYPE_MAP.get(ext);
    }
}
