package mohamednasser.projects.http;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;

public class HttpParserTest {

    private HttpParser parser = HttpParser.getInstance();

    private static HttpRequest testHttpRequest;

    private static String testHttpRequestString = "GET /photos/restapi/cats/minouch HTTP/1.1\r\n" +
            "Accept: application/json\r\n" +
            "Authorization: Basic dGVzdHVzZXIwMTpuZXRjb29s\r\n" +
            "Host: localhost\r\n" +
            "Connection: keep-alive\r\n\r\n";
    @BeforeAll
    public static void setup() {

        testHttpRequest = new HttpRequest();
        testHttpRequest.setTarget("/photos/restapi/cats/minouch");
        testHttpRequest.setMethod(HttpMethod.valueOf("GET"));
        testHttpRequest.setVersion("HTTP/1.1");
        testHttpRequest.addHeader("Accept", "application/json");
        testHttpRequest.addHeader("Authorization", "Basic dGVzdHVzZXIwMTpuZXRjb29s");
        testHttpRequest.addHeader("Host", "localhost");
        testHttpRequest.addHeader("Connection", "keep-alive");
    }


    @Test
    void httpRequestParsingTest() throws HttpException, IOException {
        InputStream in = new ByteArrayInputStream(testHttpRequestString.getBytes());
        HttpRequest request = parser.parseHttpRequest(in);
        Assertions.assertEquals(request, testHttpRequest);
    }

    @Test
    void ignoreConsecutiveWhitespaceTest() throws HttpException, IOException {
        String httpRequest = "        GET          /photos/restapi/cats/minouch          HTTP/1.1            \r\n\r\n";
        InputStream in = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest request = parser.parseHttpRequest(in);
        Assertions.assertEquals(testHttpRequest.getMethod(), request.getMethod());
        Assertions.assertEquals(testHttpRequest.getTarget(), request.getTarget());
        Assertions.assertEquals(testHttpRequest.getVersion(), request.getVersion());
    }

    @Test
    void unimplementedMethodTest() throws HttpException, IOException {
        String httpRequest = "EAT /photos/restapi/cats/minouch HTTP/1.1\r\n\r\n";
        InputStream in = new ByteArrayInputStream(httpRequest.getBytes());
        Assertions.assertThrows(
                HttpException.class,
                () -> parser.parseHttpRequest(in),
                HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED.message
        );
    }

    @Test
    void missingMethodTest() {
        String httpRequest = "/photos/restapi/cats/minouch HTTP/1.1\r\n\r\n";
        InputStream in = new ByteArrayInputStream(httpRequest.getBytes());
        Assertions.assertThrows(
                HttpException.class,
                () -> parser.parseHttpRequest(in),
                HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED.message
        );
    }


    @Test
    void spaceInTargetUriTest() throws HttpException, IOException {
        String httpRequest = "EAT /photos/restapi/black cats/minouch HTTP/1.1\r\n\r\n";
        InputStream in = new ByteArrayInputStream(httpRequest.getBytes());
        Assertions.assertThrows(
                HttpException.class,
                () -> parser.parseHttpRequest(in),
                HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST.message
        );
    }

    @Test
    void longTargetUriTest() throws HttpException, IOException {
        byte[] bytes = new byte[8000];
        Arrays.fill(bytes, (byte) 'm');
        String longTargetUri = "/" + new String(bytes, StandardCharsets.US_ASCII);

        String httpRequest = "GET " + longTargetUri + " HTTP/1.1\r\n\r\n";
        InputStream in = new ByteArrayInputStream(httpRequest.getBytes());
        Assertions.assertThrows(
                HttpException.class,
                () -> parser.parseHttpRequest(in),
                HttpStatusCode.CLIENT_ERROR_414_BAD_REQUEST.message
        );
    }

}
