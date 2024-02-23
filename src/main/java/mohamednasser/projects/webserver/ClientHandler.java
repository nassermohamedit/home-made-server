package mohamednasser.projects.webserver;

import mohamednasser.projects.http.HttpException;
import mohamednasser.projects.http.HttpParser;
import mohamednasser.projects.http.HttpRequest;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            HttpParser parser = HttpParser.getInstance();
            HttpRequest request = parser.parseHttpRequest(clientSocket.getInputStream());
            System.out.println(request);
            String body = request.toString();
            final String CLRF = "\n\r";
            String answer = "HTTP/1.1 200 OK" + CLRF +
                    "Content-Length: " + body.getBytes().length + CLRF +
                    CLRF +
                    body +
                    CLRF + CLRF;
            clientSocket.getOutputStream().write(answer.getBytes());
            clientSocket.close();
        } catch (IOException | HttpException e) {
            throw new RuntimeException(e);
        }
    }
}
