package mohamednasser.projects.webserver;

import mohamednasser.projects.http.HttpException;
import mohamednasser.projects.http.HttpParser;
import mohamednasser.projects.http.HttpRequest;
import mohamednasser.projects.http.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static mohamednasser.projects.http.HttpStatusCode.CLIENT_ERROR_404;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;

    private final HttpServer server;

    private final Map<Path, Map<String, Object>> resources;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

    public ClientHandler(Socket clientSocket, Map<Path, Map<String, Object>> resources, HttpServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.resources = resources;
    }

    @Override
    public void run() {
        try {
            HttpParser parser = HttpParser.getInstance();
            HttpRequest request = parser.parseHttpRequest(clientSocket.getInputStream());
            Path uri = Paths.get("/").relativize(Paths.get(request.getTarget()));
            if (!resources.containsKey(uri)) {
                throw new HttpException(CLIENT_ERROR_404);
            }
            Path localUri = server.getWebroot().resolve(uri);
            long size = (Long) resources.get(uri).get("size");
            String contentType = (String) resources.get(uri).get("Content-Type");
            LOGGER.info(localUri.toString());
            InputStream in  = Files.newInputStream(localUri);
            OutputStream out = clientSocket.getOutputStream();
            System.out.println("Streams are open");
            String answer = "HTTP/1.1 200 OK" + "\n\r" +
                    "Content-Length: " + size + "\n\r" +
                    "Content-Type: " + contentType + "\n\r" +
                    "\n\r";
            out.write(answer.getBytes());
            int i;
            while ((i = in.read()) != -1) {
                out.write(i);
            }
            out.write(("\n\r\n\r").getBytes());
            in.close();
            clientSocket.close();
        } catch (IOException | HttpException e) {
            throw new RuntimeException(e);
        }
    }
}
