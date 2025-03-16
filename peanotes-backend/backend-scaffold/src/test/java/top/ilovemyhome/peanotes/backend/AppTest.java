package top.ilovemyhome.peanotes.backend;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.failBecauseExceptionWasNotThrown;

public class AppTest {

    @Test
    public void testUnitTest(){
        assertThat(true).isTrue();
    }

    @Test
    public void testFunction(){
        A a1 = new A("a1");
        A a2 = new A("a2");

        System.out.println(a1.getF());
        System.out.println(a2.getF());
        assertThat(a1.getF()).isNotEqualTo(a2.getF());

        assertThat(a1.upperCase).isEqualTo(a2.upperCase);
    }
}


class A {

    A(String name) {
        this.name = name;
    }
    private final Runnable f = () -> {
        System.out.println("My name is " + this.getName()+ " hahaha");
    };

    public static final Function<String, String> upperCase = StringUtils::upperCase;

    public Runnable getF() {
        return f;
    }

    public String getName() {
        return name;
    }

    private final String name;
}
