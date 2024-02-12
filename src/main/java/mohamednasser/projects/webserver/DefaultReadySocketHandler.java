package mohamednasser.projects.webserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;

public class DefaultReadySocketHandler implements ReadySocketHandler {


    private final SocketChannel socket;

    private final Map<String, Object> context;

    public DefaultReadySocketHandler(SocketChannel socket, Map<String, Object> context) {
        this.socket = socket;
        this.context = context;
    }


    private Bytes readReceivedBytes() throws IOException {
        Bytes bytes = Bytes.newInstance();
        ByteBuffer buf = ByteBuffer.allocate(64);
        while (socket.read(buf) > 0) {
            buf.flip();
            bytes.append(buf);
            buf.clear();
        }
        return bytes;
    }

    @Override
    public void handle() {
        try {
            Bytes bytes = readReceivedBytes();
            if (bytes.size() < 2) return;
            System.out.println("received a message: " + bytes.asString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
