package mohamednasser.projects.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static mohamednasser.projects.http.HttpStatusCode.CLIENT_ERROR_400;


public final class HttpParser {

    private static final HttpParser HTTP_PARSER = new HttpParser();

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpParser.class);

    private static final int SP = 0x20;

    private static final int CR = 0x0D;

    private static final int LF = 0x0A;

    private static final Set<Integer> WHITESPACES = Set.of(SP, 0x09, 0x0B, 0x0C);

    private HttpParser() {
    }

    public static HttpParser getInstance() {
        return HTTP_PARSER;
    }


    public HttpRequest parseHttpRequest(InputStream in) throws IOException, HttpException {
        InputStreamReader isr = new InputStreamReader(in, StandardCharsets.US_ASCII);
        HttpRequest.Builder builder = new HttpRequest.Builder();
        visitRequestLine(isr, builder);
        visitHeaderSection(isr, builder);
        visitBody(isr, builder);
        return builder.build();
    }

    private void visitRequestLine(InputStreamReader isr, HttpRequest.Builder builder) throws IOException, HttpException {
        visitMethod(isr, builder);
        visitTargetUri(isr, builder);
        visitVersion(isr, builder);
    }

    private void visitMethod(InputStreamReader isr, HttpRequest.Builder builder) throws IOException, HttpException {
        int c = consumePrecedingCrlf(isr);
        while (WHITESPACES.contains(c))
            c = isr.read();
        if (c == CR || c == LF)
            throw new HttpException(CLIENT_ERROR_400);
        StringBuilder buffer = new StringBuilder();
        buffer.append((char) c);
        while ((c = isr.read()) != -1 && !WHITESPACES.contains(c) && c != CR && c != LF) {
            buffer.append((char) c);
        }
        builder.setMethod(buffer.toString());
    }

    private void visitTargetUri(InputStreamReader isr, HttpRequest.Builder builder) throws IOException, HttpException {
        int c = isr.read();
        while (WHITESPACES.contains(c))
            c = isr.read();
        if (c == CR || c == LF)
            throw new HttpException(CLIENT_ERROR_400);
        StringBuilder buffer = new StringBuilder();
        buffer.append((char) c);
        while ((c = isr.read()) != -1 && c != CR && c != LF && !WHITESPACES.contains(c)) {
            buffer.append((char) c);
        }
        builder.setTarget(buffer.toString());
    }

    private void visitVersion(InputStreamReader isr, HttpRequest.Builder builder) throws IOException, HttpException {
        int c = isr.read();
        while (WHITESPACES.contains(c))
            c = isr.read();
        if (c == CR || c == LF)
            throw new HttpException(CLIENT_ERROR_400);
        StringBuilder buffer = new StringBuilder();
        buffer.append((char) c);
        while ((c = isr.read()) != -1 && c != CR && c != LF && !WHITESPACES.contains(c)) {
            buffer.append((char) c);
        }
        builder.setVersion(buffer.toString());
        while (WHITESPACES.contains(c))
            c = isr.read();
        if (c != CR || isr.read() != LF)
            throw new HttpException(CLIENT_ERROR_400);
    }


    // consumes all trailing CRLF sequences and returns first nont CR char
    private int consumePrecedingCrlf(InputStreamReader isr) throws IOException, HttpException {
        int c;
        while ((c = isr.read()) == CR) {
            if (isr.read() != LF)
                throw new HttpException(CLIENT_ERROR_400);
        }
        return c;
    }

    private void visitBody(InputStreamReader isr, HttpRequest.Builder builder) {
    }

    private void visitHeaderSection(InputStreamReader isr, HttpRequest.Builder builder) throws IOException, HttpException {
        Map<String, String> headers = new HashMap<>();
        boolean endOfHeaderSection = false;
        while (!endOfHeaderSection) {
            try {
                LOGGER.info("Parsing next header..");
                visitHeader(isr, headers);
            } catch (IllegalStateException ignored) {
                endOfHeaderSection = true;
            }
        }
        LOGGER.info("Finished parsing header section..");
        headers.forEach(builder::addHeader);
    }

     private void visitHeader(InputStreamReader isr, Map<String, String> headers) throws IOException, HttpException {
       int c = isr.read();
       if (c == -1 || c == LF || c == CR && (c = isr.read()) != LF)
           throw new HttpException(CLIENT_ERROR_400);
       if (c == LF)
           throw new IllegalStateException("Unexpected CRLF sequence");
       if (WHITESPACES.contains(c)) {
           consumeLine(isr); // any header field line with trailing whitespace is ignored
           return;
       }
       StringBuilder headerName = new StringBuilder();
       headerName.append((char) c);
       readHeaderName(isr, headerName);
       StringBuilder headerValue = new StringBuilder();
       readHeaderValue(isr, headerValue);
       headers.put(headerName.toString(), headerValue.toString());
       LOGGER.info("Parsed header field = " + headerName.toString() + " : " + headerValue.toString());
    }

    private void readHeaderValue(InputStreamReader isr, StringBuilder headerValue) throws IOException, HttpException {
        int c = isr.read();
        if (c != SP)
            headerValue.append((char) c);
        int prev = c;
        while ((c = isr.read()) != LF && c != CR) {
            if (!WHITESPACES.contains(c) || !WHITESPACES.contains(prev)) {
                headerValue.append((char) c);
                prev = c;
            }
        }
        if (c == LF || isr.read() != LF)
            throw new HttpException(CLIENT_ERROR_400);
    }

    private void readHeaderName(InputStreamReader isr, StringBuilder headerName) throws IOException, HttpException {
        int c;
        while ((c = isr.read()) != ':' && c != LF &&  c != CR && !WHITESPACES.contains(c)) {
            headerName.append((char) c);
        }
        if (c != ':')
            throw new HttpException(CLIENT_ERROR_400);
    }

    private void consumeLine(InputStreamReader isr) throws IOException {
        int c = isr.read();
        while (c != LF) c = isr.read();
    }
}
