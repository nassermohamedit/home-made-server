package mohamednasser.projects.webserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;



public class Server {

    private static final int DEFAULT_PORT = 9812;

    private final ServerSocketChannel socket;

    private final Selector selector = Selector.open();

    private final Set<SocketChannel> cSockets = new HashSet<>();

    private final ExecutorService exec;

    private boolean running = false;

    private boolean closed = false;

    private Server(SocketAddress address, ThreadFactory tf) throws IOException {
        socket = initServerSocket(address);
        exec = getExecutorServiceInstance(tf);
    }

    public static Server createServerInstance(SocketAddress address, ThreadFactory tf) throws IOException {
        return new Server(address, tf);
    }

    public static Server createServerInstance(SocketAddress address) throws IOException {
        ThreadFactory tf = getDefaultThreadFactory();
        return new Server(address, tf);
    }

    public static Server createServerInstance(String host) throws IOException {
        InetSocketAddress address = new InetSocketAddress(host, DEFAULT_PORT);
        ThreadFactory tf = getDefaultThreadFactory();
        return new Server(address, tf);
    }

    public static Server createServerInstance(ThreadFactory tf) throws IOException {
        InetSocketAddress address = new InetSocketAddress(DEFAULT_PORT);
        return new Server(address, tf);
    }

    public static Server createServerInstance() throws IOException {
        InetSocketAddress address = new InetSocketAddress(DEFAULT_PORT);
        ThreadFactory tf = getDefaultThreadFactory();
        return new Server(address, tf);
    }

    private static ThreadFactory getDefaultThreadFactory() {
        return Thread.ofVirtual().factory();
    }

    private ExecutorService getExecutorServiceInstance(ThreadFactory tf) {
        return Executors.newCachedThreadPool(tf);
    }

    private ServerSocketChannel initServerSocket(SocketAddress address) throws IOException {
        final ServerSocketChannel socket;
        socket = ServerSocketChannel.open();
        socket.bind(address);
        socket.configureBlocking(false);
        socket.register(selector, SelectionKey.OP_ACCEPT);
        return socket;
    }

    public void start() throws IOException {
        checkRunningState();
        running = true;
        while (running) {
            Iterator<SelectionKey> iter = selectReadySockets();
            processKeys(iter);
        }
    }

    private void checkRunningState() {
        if (running) {
            throw new IllegalStateException("Server is already running.");
        }
    }

    private void processKeys(Iterator<SelectionKey> iter) throws IOException {
        while (iter.hasNext()) {
            processKey(iter.next());
            iter.remove();
        }
    }

    private Iterator<SelectionKey> selectReadySockets() throws IOException {
        selector.select();
        return selector.selectedKeys().iterator();
    }

    private void processKey(SelectionKey key) throws IOException {
        if (key.isAcceptable()) acceptAndRegister();
        else handleReadyClientSocket(key);
    }

    private void acceptAndRegister() throws IOException {
        SocketChannel cSocket = socket.accept();
        cSocket.configureBlocking(false);
        cSocket.register(selector, SelectionKey.OP_READ);
        selector.wakeup();
        cSockets.add(cSocket);
    }

    private void handleReadyClientSocket(SelectionKey key) {
        var cSocket = (SocketChannel) key.channel();
        key.cancel();
        ReadySocketHandler handler = new ReadyClientSocketTask(
                cSocket,
                new DefaultReadySocketHandler(cSocket, null)
        );
        exec.execute(handler::handle);
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isClosed() {
        return closed;
    }

    public void stop() throws IOException {
        closed = true;
        socket.close();
        selector.close();
        exec.shutdown();
        for (var cs : cSockets) cs.close();
        running = false;
    }


    private class ReadyClientSocketTask implements ReadySocketHandler {

        private final ReadySocketHandler handler;

        private final SocketChannel cSocket;

        private ReadyClientSocketTask(SocketChannel s, ReadySocketHandler handler) {
            this.handler = handler;
            cSocket = s;
        }
        @Override
        public void handle() {
            try {
                handler.handle();
                cSocket.register(selector, SelectionKey.OP_READ);
                selector.wakeup();
            } catch (ClosedChannelException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
