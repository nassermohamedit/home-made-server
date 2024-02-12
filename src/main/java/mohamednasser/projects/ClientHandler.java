package mohamednasser.projects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientHandler implements Runnable {

    private final SocketChannel clientSocket;

    public ClientHandler(SocketChannel clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            String body = "<html><head><title>Home Made Server</title></head><body>request received</body></html>";
            final String CLRF = "\n\r";
            String answer = "HTTP/1.1 200 OK" + CLRF +
                    "Content-Length: " + body.getBytes().length + CLRF +
                    CLRF +
                    body +
                    CLRF + CLRF;
            clientSocket.write(ByteBuffer.wrap(answer.getBytes()));
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
