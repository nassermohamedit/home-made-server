package mohamednasser.projects.webserver;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientSession {

    private final SocketChannel socket;

    private final Map<String, Object> context = new ConcurrentHashMap<>();

    public ClientSession(SocketChannel s) {
        socket = s;
    }

    public Map<String, Object> getContext() {
        return this.context;
    }
}
