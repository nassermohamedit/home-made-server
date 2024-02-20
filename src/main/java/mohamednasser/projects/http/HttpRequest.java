package mohamednasser.projects.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static mohamednasser.projects.http.HttpStatusCode.*;


public class HttpRequest extends HttpMessage{

    public static final int MAX_URI_LENGTH = 8000;

    private HttpMethod method;

    private String target;

    private final Map<String, String> headers = new HashMap<>();

    private HttpRequest() {
    }

    public HttpMethod getMethod() {
        return method;
    }

    private void setMethod(String method) throws HttpException {
        try {
            this.method = HttpMethod.valueOf(method);
        } catch (Exception e) {
            throw new HttpException(SERVER_ERROR_501);
        }
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) throws HttpException {
        if (target.length() > MAX_URI_LENGTH)
            throw new HttpException(CLIENT_ERROR_414);
        this.target = target;
    }

    private void setVersion(String version) throws HttpException {
        if (!isSupported(version))
            throw new HttpException(SERVER_ERROR_505);
        this.version = version;
    }

    private void addHeader(String header, String value) {
        this.headers.put(header, value);
    }

    public Set<Map.Entry<String, String>> getHeaders() {
        return headers.entrySet();
    }

    private void validate() throws HttpException {
        if (!headers.containsKey("Host"))
            throw new HttpException(CLIENT_ERROR_400);
    }

    @Override
    public String toString() {
        return method + " " + target + " " + version + "\r\n" +
                headers.entrySet()
                        .stream()
                        .map(e -> e.getKey() + ": " + e.getValue() + "\r\n")
                        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append) +
                "\r\n";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HttpRequest r)) return false;
        if (!this.method.equals(r.getMethod())) return false;
        if (!this.target.equals(r.getTarget())) return false;
        if (!this.version.equals(r.getVersion())) return false;
        Map<String, String> headers = new HashMap<>();
        r.getHeaders().forEach(e -> headers.put(e.getKey(), e.getValue()));
        return this.headers.equals(headers);
    }

    public static class Builder {

        private final HttpRequest request;

        public Builder() {
            this.request = new HttpRequest();
        }

        public Builder setMethod(String method) throws HttpException {
            request.setMethod(method);
            return this;
        }

        public Builder setTarget(String target) throws HttpException {
            request.setTarget(target);
            return this;
        }

        public Builder setVersion(String version) throws HttpException {
            request.setVersion(version);
            return this;
        }

        public Builder addHeader(String header, String value) {
            request.addHeader(header, value);
            return this;
        }

        public HttpRequest build() throws HttpException {
            request.validate();
            return request;
        }
    }
}
