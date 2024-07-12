package top.ilovemyhome.peanotes.backend.common.io;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FileHelperTest {

    @Test
    public void testSearchWithWildcard() throws IOException {
        assertThat(FileHelper.searchWithWildcard(Paths.get("src/test/resources/"), "glob:*.{txt,docx,json}").size())
            .isEqualTo(1);
    }
}
