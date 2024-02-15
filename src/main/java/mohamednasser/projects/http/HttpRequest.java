package mohamednasser.projects.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class HttpRequest {

    public static final int MAX_URI_LENGTH = 8000;

    private HttpMethod method;

    private String target;

    private String version;

    private final Map<String, String> headers = new HashMap<>();

    public Set<Map.Entry<String, String>> getHeaders() {
        return headers.entrySet();
    }

    public void addHeader(String header, String value) {
        headers.put(header, value);
    }


    public String getVersion() {
        return version;
    }

    void setVersion(String version) {
        this.version = version;
    }

    public String getTarget() {
        return target;
    }

    void setTarget(String target) {
        if (target.length() > MAX_URI_LENGTH)
            throw new IllegalArgumentException("URI Too Long");
        this.target = target;
    }


    public HttpMethod getMethod() {
        return method;
    }

    void setMethod(HttpMethod method) {
        this.method = method;
    }

    void setMethod(String methodName) {
        setMethod(HttpMethod.valueOf(methodName));
    }
}
