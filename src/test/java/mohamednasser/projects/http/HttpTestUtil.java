package mohamednasser.projects.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class HttpTestUtil {

    static InputStream getHttpRequestInputStream() {
        String httpRequest = "       GET        /photos/restapi/cats/minouch            HTTP/1.1\r\n" +
                "Accept: application/json\r\n" +
                "Authorization: Basic dGVzdHVzZXIwMTpuZXRjb29s\r\n" +
                "Host: localhost\r\n" +
                "Connection: keep-alive\r\n\r\n";
        return new ByteArrayInputStream(httpRequest.getBytes());
    }

    static HttpRequest getHttpRequest() {
        HttpRequest request = new HttpRequest();
        request.setTarget("/photos/restapi/cats/minouch");
        request.setMethod(HttpMethod.valueOf("GET"));
        request.setVersion("HTTP/1.1");
        request.addHeader("Accept", "application/json");
        request.addHeader("Authorization", "Basic dGVzdHVzZXIwMTpuZXRjb29s");
        request.addHeader("Host", "localhost");
        request.addHeader("Connection", "keep-alive");
        return request;
    }

    static InputStream getHeaders() {
       String headers =
               "Accept: application/json\r\n" +
                       "Authorization: Basic dGVzdHVzZXIwMTpuZXRjb29s\r\n" +
                       "Host: localhost\r\n" +
                       "Connection: keep-alive\r\n\r\n";
        return new ByteArrayInputStream(headers.getBytes());
    }
}
