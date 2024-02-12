package mohamednasser.projects.webserver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.ByteBuffer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


class BytesTest {

    @ParameterizedTest
    @MethodSource("testStringProvider")
    public void appendArrayTest(String input) {
        Bytes bytes = Bytes.newInstance(10);
        bytes.append(input.getBytes());
        assertEquals(input, bytes.asString());
    }

    @ParameterizedTest
    @MethodSource("testStringProvider")
    public void appendByteBufferTest(String input) {
        ByteBuffer buf = ByteBuffer.allocate(64);
        buf.put(input.getBytes());
        buf.flip();
        Bytes bytes = Bytes.newInstance(10);
        bytes.append(buf);
        assertEquals(input, bytes.asString());

    }


    private static Stream<String> testStringProvider() {
        return Stream.of("Hello", "Tea is surely better than coffee");
    }
}