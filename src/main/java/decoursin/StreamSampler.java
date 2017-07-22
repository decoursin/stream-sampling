package decoursin;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.RandomStringGenerator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.System.exit;

public class StreamSampler {
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();
    private static final RandomStringGenerator generator = new RandomStringGenerator.Builder()
        .withinRange('a', 'z')
        .usingRandom(random::nextInt) // uses Java 8 syntax
        .build();

    public static void main(String[] args) {
        if (args.length != 1) {
            printUsageAndExit();
        }
        // Only positive number or zero.
        else if (!args[0].matches("^\\d+$")) {
            printUsageAndExit();
        }

        long sampleSize = Integer.parseInt(args[0]);

        randomSample(sampleSize);
    }

    static void randomSample(long sampleSize) {
        try {
            Pair<HashMap<Character, Long>, Long> pair = characterFrequency(System.in);

            // if there was no STDIN, generate
            // our own random characters.
            if (pair.getRight() == 0) {
                pair = characterFrequency(generateInputStream());
            }

            printRandomSample(pair.getLeft(), sampleSize, pair.getRight());
        } catch (java.io.IOException e) {
            e.printStackTrace();

            exit(1);
        }
    }

    static Pair<HashMap<Character, Long>, Long> characterFrequency(InputStream input) throws java.io.IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        HashMap<Character, Long> frequency = new HashMap<>();
        String line;
        long size = 0;

        while ((line = reader.readLine()) != null) {
            size += line.length();
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                Long val = frequency.get(c);
                if (val != null) {
                    frequency.put(c, val + 1);
                } else {
                    frequency.put(c, (long) 1);
                }
            }
        }

        return Pair.of(frequency, size);
    }

    static void printRandomSample(HashMap<Character, Long> frequency, long sampleSize, long populationSize) {
        // start with -1, to account
        // for the zero index.
        long count = -1;
        NavigableMap<Long, Character> tree = new TreeMap<>();

        for (Map.Entry<Character, Long> entry : frequency.entrySet()) {
            count = count + entry.getValue();
            tree.put(count, entry.getKey());
        }

        for (int i = 0; i < sampleSize; i++) {
            long r = random.nextLong(populationSize);

            System.out.print(tree.ceilingEntry(r).getValue());
        }
    }

    static InputStream generateInputStream() {
        byte[] input = generator.generate(random.nextInt(100000)).getBytes(StandardCharsets.US_ASCII);
        return new ByteArrayInputStream(input);
    }

    private static void printUsageAndExit() {
        System.out.println("Usage: `basename $0` number < FILE");
        exit(1);
    }
}
