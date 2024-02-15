package mohamednasser.projects.http;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
public class HttpParserTest {

    private HttpParser parser = HttpParser.getInstance();

    @Test
    void requestLineParser() throws HttpException, IOException {
        InputStream in = HttpTestUtil.getHttpRequestInputStream();
        HttpRequest request = parser.parseHttpRequest(in);
        HttpRequest expectedRequest = HttpTestUtil.getHttpRequest();

        Assertions.assertEquals(request.getMethod(), expectedRequest.getMethod());
        Assertions.assertEquals(request.getTarget(), expectedRequest.getTarget());
        Assertions.assertEquals(request.getVersion(), expectedRequest.getVersion());
    }

    @Test
    void headersParsingTest() throws HttpException, IOException {
        InputStream in = HttpTestUtil.getHeaders();
        HttpRequest request = new HttpRequest();
        HttpRequest expected = HttpTestUtil.getHttpRequest();
        parser.visitHeaderSection(new InputStreamReader(in, StandardCharsets.US_ASCII), request);
        Assertions.assertEquals(request.getHeaders(), expected.getHeaders());
    }

    @Test
    void noHeadersHttpRequestTest() throws HttpException, IOException {
        String httpRequest = " GET /photos/restapi/cats/minouch HTTP/1.1\r\n";
        InputStream in = new ByteArrayInputStream(httpRequest.getBytes());
        Assertions.assertThrows(
                HttpException.class,
                () -> parser.parseHttpRequest(in),
                HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST.message);
    }


    @Test
    void ignoreConsecutiveWhitespaceTest() throws HttpException, IOException {
        String httpRequest = "        GET          /photos/restapi/cats/minouch          HTTP/1.1            \r\n\r\n";
        InputStream in = new ByteArrayInputStream(httpRequest.getBytes());
        HttpRequest expected = HttpTestUtil.getHttpRequest();
        HttpRequest request = parser.parseHttpRequest(in);
        Assertions.assertEquals(expected.getMethod(), request.getMethod());
        Assertions.assertEquals(expected.getTarget(), request.getTarget());
        Assertions.assertEquals(request.getVersion(), expected.getVersion());
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
    void missingMethodTest() throws HttpException, IOException {
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
