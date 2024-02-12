package mohamednasser.projects.webserver;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;

public class Bytes {

    private static final int DEFAULT_INITIAL_SIZE = 64;

    private static final double DEFAULT_RESIZE_FACTOR = 2d;

    private byte[] bytes;

    private int limit;

    private int capacity;

    private double resizeFactor;

    public static Bytes newInstance() {
        return new Bytes(DEFAULT_INITIAL_SIZE);
    }

    public static Bytes newInstance(int initialSize) {
        return new Bytes(initialSize);
    }

    private Bytes(int initialSize) {
        bytes = new byte[initialSize];
        capacity = initialSize;
        limit = 0;
        resizeFactor = DEFAULT_RESIZE_FACTOR;
    }

    public  void setResizeFactor(double f) {
        this.resizeFactor = f;
    }

    public void append(ByteBuffer buf) {
        int nBytes = buf.remaining();
        ensureEnoughSize(nBytes);
        while (buf.hasRemaining())
            bytes[limit++] = buf.get();
    }

    public void append(byte[] arr) {
        int nBytes = arr.length;
        ensureEnoughSize(nBytes);
        for (byte b : arr)
            bytes[limit++] = b;
    }

    public void append(byte b) {
        ensureEnoughSize(1);
        bytes[limit++] = b;
    }

    private void ensureEnoughSize(int toAdd) {
        if (toAdd > capacity - limit) {
            resize(Math.max((int)(resizeFactor*capacity), toAdd));
        }
    }

    public String asString() {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<limit; i++) {
            sb.append((char) bytes[i]);
        }
        return sb.toString();
    }

    public Iterator<Byte> getIterator() {
        return new Iterator<Byte>() {
            int index = 0;
            @Override
            public boolean hasNext() { return index < limit; }
            @Override
            public Byte next() { return bytes[index++]; }
        };
    }

    private void resize(int newCapacity) {
        bytes = Arrays.copyOf(bytes, newCapacity);
        capacity = newCapacity;
    }

    public int size() {
        return limit;
    }
}
