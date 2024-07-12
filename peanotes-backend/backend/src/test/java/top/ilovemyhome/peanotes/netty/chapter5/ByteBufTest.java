package top.ilovemyhome.peanotes.netty.chapter5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.ThreadUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Random;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Disabled
public class ByteBufTest {

    @Test
    public void testAllocateInHeap() throws Exception {
        ByteBuf[] bufs = new ByteBuf[10];
        IntStream.range(1,10).boxed().forEach(i -> {
            try {
                //allocate 1G buffer in the heap
                bufs[i] = Unpooled.buffer(1024 * 1024 * 1024);
                ThreadUtils.sleep(Duration.ofSeconds(1));
            }catch (Throwable e){
                LOGGER.error("Error", e);
            }
        });
        ThreadUtils.sleep(Duration.ofSeconds(100));
    }

    @Test
    public void testAllocateInDirect() throws Exception {
        ByteBuf[] bufs = new ByteBuf[10];
        IntStream.range(1,10).boxed().forEach(i -> {
            try {
                //allocate 1G buffer in the heap
                bufs[i] = Unpooled.directBuffer(1024 * 1024 * 1024);
                ThreadUtils.sleep(Duration.ofSeconds(1));
            }catch (Throwable e){
                LOGGER.error("Error", e);
            }
        });
        ThreadUtils.sleep(Duration.ofSeconds(100));
    }

    @Test
    public void testCompositeByteBuf() throws Exception {
        CompositeByteBuf[] bufs = new CompositeByteBuf[10];
        IntStream.range(1,10).boxed().forEach(i -> {
            try {
                //allocate 1G buffer in the heap
                CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer();
                bufs[i] = compositeByteBuf;

                ByteBuf headerBuf = Unpooled.buffer(1024 * 1024);
                ByteBuf bodyBuf = Unpooled.directBuffer(1024 * 1024 * 1024);
                compositeByteBuf.addComponents(headerBuf, bodyBuf);

                ThreadUtils.sleep(Duration.ofSeconds(1));
            }catch (Throwable e){
                LOGGER.error("Error", e);
            }
        });
        ThreadUtils.sleep(Duration.ofSeconds(100));
    }

    @Test
    public void testByteBufFeature() throws Exception {
        ByteBuf byteBuf = Unpooled.buffer(32);
        LOGGER.info("ByteBuf.hasArray() is {}",byteBuf.hasArray());
        LOGGER.info("ByteBuf.readableBytes() is {}",byteBuf.readableBytes());
        LOGGER.info("ByteBuf.capacity() is {}",byteBuf.capacity());
        LOGGER.info("ByteBuf.writeableBytes() is {}",byteBuf.writableBytes());
        byteBuf.writeInt(Integer.MAX_VALUE); //4 bytes
        byteBuf.writeLong(Long.MAX_VALUE); //8 bytes
        byteBuf.writeByte('a'); //1 byte
        //13,19
        LOGGER.info("ByteBuf.readableBytes() is {}",byteBuf.readableBytes());
        LOGGER.info("ByteBuf.capacity() is {}",byteBuf.capacity());
        LOGGER.info("ByteBuf.readerIndex() is {}",byteBuf.readerIndex());
        LOGGER.info("ByteBuf.writerIndex() is {}",byteBuf.writerIndex());
        LOGGER.info("ByteBuf.writeableBytes() is {}",byteBuf.writableBytes());

        LOGGER.info("ByteBuf.readInt() is {}",byteBuf.readInt());
        LOGGER.info("ByteBuf.readLong() is {}",byteBuf.readLong());
        LOGGER.info("ByteBuf.readByte() is {}",(char)byteBuf.readByte());

        //0, 32
        LOGGER.info("ByteBuf.readableBytes() is {}",byteBuf.readableBytes());
        LOGGER.info("ByteBuf.writeableBytes() is {}",byteBuf.writableBytes());



    }


    @Test
    public void testByteBufChar1() throws Exception {
        ByteBuf byteBuf = Unpooled.buffer(64);
        byteBuf.writeChar('a');
        byteBuf.writeChar('b');
        byteBuf.writeChar('c');
        byteBuf.writeChar('中');

        //Unicode encoding, cannot use utf-8 decoding
        byte [] target = new byte[64];
        int i = 0;
        while (byteBuf.isReadable()){
            target[i++] = byteBuf.readByte();
        }
        System.out.println(new String(target, StandardCharsets.UTF_8));
        System.out.println(new String(target, StandardCharsets.UTF_16));
    }

    @Test
    public void testByteBufChar2() throws Exception {
        ByteBuf byteBuf = Unpooled.buffer(64);
        byteBuf.writeChar('a');
        byteBuf.writeChar('1');
        byteBuf.writeChar('中');

        //Unicode encoding, cannot use utf-8 decoding
        char [] target = new char[64];
        int i = 0;
        while (byteBuf.isReadable()){
            target[i++] = byteBuf.readChar();
        }
        System.out.println(String.valueOf(target));
    }


    @Test
    public void testByteBufWrite1() throws Exception {
        ByteBuf byteBuf = Unpooled.buffer(102);
        Random random = new Random();
        int total =0 ;
        while (byteBuf.isWritable(4)){
            int data = random.nextInt(100);
            LOGGER.info("Write {}.", data);
            byteBuf.writeInt(data);
            total++;
        }
        LOGGER.info("Write {} integers to the buf.", total);
        total =0;
        while (byteBuf.isReadable(4)){
            LOGGER.info("Read {}.", byteBuf.readInt());
            total++;
        }
        LOGGER.info("Read {} integer from the buf.", total);
    }

    @Test
    public void testByteBufDerived() throws IOException {
        ByteBuf byteBuf = Unpooled.copiedBuffer("Netty in Action rocks!中国！", StandardCharsets.UTF_8);
        ByteBuf sliced = byteBuf.slice(0, 14);
        LOGGER.info(sliced.toString(StandardCharsets.UTF_8));
        byteBuf.setByte(0, 'J');
        LOGGER.info("{}",byteBuf.readableBytes());
        LOGGER.info("{}", (char)byteBuf.getByte(0));
        LOGGER.info("{}", (char)sliced.getByte(0));
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ByteBufTest.class);
}
