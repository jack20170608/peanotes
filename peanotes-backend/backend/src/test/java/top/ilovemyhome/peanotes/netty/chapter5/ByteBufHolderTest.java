package top.ilovemyhome.peanotes.netty.chapter5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

public class ByteBufHolderTest {

    @Test
    public void testDuplicate(){
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();


        System.out.println(System.getProperty("io.netty.allocator.type"));
    }
}
