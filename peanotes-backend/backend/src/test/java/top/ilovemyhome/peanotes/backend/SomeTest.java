package top.ilovemyhome.peanotes.backend;

import org.junit.jupiter.api.Test;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.IntStream;

public class SomeTest {
    @Test
    public void testRandomFeature(){
        RandomGenerator randomGenerator = RandomGeneratorFactory.of("Random").create();
        int size = 100;
        IntStream dateDeltas = randomGenerator.ints(size, -365*3, 30);
        dateDeltas.forEach(System.out::println);
    }
}
