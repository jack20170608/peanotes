package top.ilovemyhome.peanotes.backend.common.text;

import org.junit.jupiter.api.Test;

public class StringPatternMatchTest {



    @Test
    public void testStringPatternMatch() {
        String pattern = "user:add people:add";
        String string = "useradd";

        System.out.println(StrUtils.vagueMatchMethod(pattern, string));
    }

}
