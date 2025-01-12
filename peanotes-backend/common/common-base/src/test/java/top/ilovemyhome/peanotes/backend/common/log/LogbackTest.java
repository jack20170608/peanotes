package top.ilovemyhome.peanotes.backend.common.log;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LogbackTest {

    @Test
    public void testPrintLog(){
        LOGGER.info("Hello World");
        LOGGER2.info("Hello World");
        assertThat(LOGGER2).isEqualTo(LOGGER);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LogbackTest.class);
    private static final Logger LOGGER2 = LoggerFactory.getLogger(LogbackTest.class.getCanonicalName());
}
