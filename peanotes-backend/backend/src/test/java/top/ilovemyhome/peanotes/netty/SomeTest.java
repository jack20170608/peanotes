package top.ilovemyhome.peanotes.netty;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import javax.crypto.spec.PSource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class SomeTest {

    @Test
    public void testEncoding() {
        String str1 = "a";
        byte[] str1Bytes = str1.getBytes();
        System.out.println(str1Bytes.length);

        String str2 = "中";
        byte[] str2Bytes = str2.getBytes();
        System.out.println(str2Bytes.length);

        String str3 = "😊";
        byte[] str3Bytes = str3.getBytes();
        System.out.println(str3Bytes.length);
    }

    @Test
    public void testInteger() {
        System.out.println(Integer.MAX_VALUE);
        System.out.println(Integer.MIN_VALUE);
        char a = '\b';
        int av = a;
        System.out.println(av);
        System.out.println(0xAA);
    }

    @Test
    public void testDouble() {
        System.out.println(String.format("%8d%8d", 130000, 200000));
        System.out.println(String.format("%1.2f", 12345.12345));
    }

    @Test
    public void testInput() throws Exception {
        int k;
        for (k = 1, print(k);
             print(k) && k*k < 26;
              k+=2 , print(k)
        ){
            print(k);
        }
    }

    boolean print(int k){
        System.out.printf("k is %d!\n", k);
        return true;
    }

    @Test
    public void testLamanda() {
        Function<String, String> f1 = upperCase("JACK");
        System.out.println(f1.apply("good good study day day up"));

        Function<String, String> f2 = upperCase("LUCY");
        System.out.println(f2.apply("good good study day day up"));

    }

    Function<String, String> upperCase(String aaa) {
        return s -> aaa + "_" + StringUtils.upperCase(s);
    }


    //1B * 1024 = 1K
    //1K * 1024 = 1M
    //1M * 1024 = 1G
    @Test
    public void testMemoryLeak1() throws Exception {
        byte[] bigArray1 = new byte[1024 * 1024 * 1024];
        Thread.sleep(Duration.ofSeconds(10));
        byte[] bigArray2 = new byte[1024 * 1024 * 1024];
        Thread.sleep(Duration.ofSeconds(10));
        int[] bigArray3 = new int[1024 * 1024 * 1024];
        Thread.sleep(Duration.ofSeconds(10));
        Thread.sleep(Duration.ofDays(1));
    }

    //1B * 1024 = 1K
    //1K * 1024 = 1M
    //1M * 64 = 64M
    @Test
    public void testMemoryLeak2() throws Exception {
        List<byte[]> list = new ArrayList<>();
        Thread.sleep(Duration.ofSeconds(10));
        for (int i = 0; i < 32; i++) {
            list.add(new byte[1024 * 1024 * 64]);
            System.out.println("Add " + i);
            Thread.sleep(Duration.ofSeconds(1));
        }
        Thread.sleep(Duration.ofDays(1));
        System.out.println(list.size());
    }

    public static Function<String, String> genFun1(String name) {
        byte[] bigArray = new byte[1024 * 1024 * 1024];
        bigArray[counter.incrementAndGet()] = 'a';
        for (int i = 0; i < 100; i++) {
            System.out.print(bigArray[i]);
        }
        return s -> name + "_" + StringUtils.upperCase(s);
    }

    public static Function<String, String> genFun2(String name) {
        return s -> {
            byte[] bigArray = new byte[1024 * 1024 * 1024];
            return name + "_" + StringUtils.upperCase(s);
        };
    }


    @Test
    public void testMemoryLeak3() throws Exception {
        var fList = new ArrayList<Function<String, String>>();
        Thread.sleep(Duration.ofSeconds(10));
        for (int i = 0; i < 100; i++) {
//            var f = genFun1("haha" + i);
            var f = genFun2("haha" + i);
            System.out.println(f);
            fList.add(f);
            Thread.sleep(Duration.ofSeconds(1));
        }
        fList.forEach(f -> f.apply("A"));
        Thread.sleep(Duration.ofDays(1));
    }

    private static final AtomicInteger counter = new AtomicInteger(0);
}
