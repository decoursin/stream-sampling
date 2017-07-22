package decoursin;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static com.google.common.truth.Truth.assertThat;
import static decoursin.StreamSampler.characterFrequency;
import static decoursin.StreamSampler.randomSample;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ThreadLocalRandom.class, StreamSampler.class})
public class StreamSamplerTest {

    private static final byte[] input = "asfasdfaasdf\nasdfasfasdf\naabbcccdddeeefffggg\n".getBytes(StandardCharsets.US_ASCII);
    private static final long size = 42;

    @Test
    public void testPrintRandomFromSTDIN() throws Exception {
        /**
         * I'll mock the *ThreadLocalRandom* static method call. However, in real life, probably, we'd have a DI framework
         * which would make this easier.
         */
        ThreadLocalRandom mockRandom = PowerMockito.mock(ThreadLocalRandom.class);
        PowerMockito.mockStatic(ThreadLocalRandom.class);
        PowerMockito.when(ThreadLocalRandom.current()).thenReturn(mockRandom);
        PowerMockito.when(mockRandom.nextLong(size)).thenReturn(0L, 12L, 24L, 38L, size - 1);

        // redirect STDIN and STDOUT
        final ByteArrayOutputStream redirect = new ByteArrayOutputStream();
        System.setOut(new PrintStream(redirect));
        InputStream stream = new ByteArrayInputStream(input);
        System.setIn(stream);

        // run it
        randomSample(5);

        // get STDOUT.
        String actual = redirect.toString();

        // assert
        assertThat(actual).isEqualTo("asdfg");
    }

    @Test
    public void testCountLetterOccurences() throws Exception {
        Pair<HashMap<Character, Long>, Long> pair = characterFrequency(new ByteArrayInputStream(input));

        Map<Character, Long> expected = new HashMap<>();
        expected.put('a', 9L);
        expected.put('b', 2L);
        expected.put('c', 3L);
        expected.put('d', 7L);
        expected.put('e', 3L);
        expected.put('f', 9L);
        expected.put('g', 3L);
        expected.put('s', 6L);

        assertThat(pair.getLeft()).isEqualTo(expected);

        assertThat(pair.getRight()).isEqualTo(size);
    }
}