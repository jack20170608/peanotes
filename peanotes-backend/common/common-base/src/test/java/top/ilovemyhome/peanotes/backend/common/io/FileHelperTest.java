package top.ilovemyhome.peanotes.backend.common.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FileHelperTest {

    @Test
    public void testSearchWithWildcard() throws IOException {
        assertThat(FileHelper.searchWithWildcard(Paths.get("src/test/resources/"), "glob:*.{txt,docx,json}").size())
            .isEqualTo(1);
    }

    @Test
    public void testPath() throws IOException {
        Path file123 = Files.createFile(tempPath.resolve("foo123.log"));
        Path file456 = Files.createFile(tempPath.resolve("foo456.log"));
        Path file789 = Files.createFile(tempPath.resolve("foo789.txt"));

        assertThat(file123.getFileName().toString()).isEqualTo("foo123.log");
        assertThat(file456.getFileName().toString()).isEqualTo("foo456.log");
        assertThat(file789.getFileName().toString()).isEqualTo("foo789.txt");

        Supplier<Stream<Path>> failCallbackFileStreamSupplier = () -> {
            try {
                return Files.list(tempPath).filter(p -> {
                    String fileName = p.getFileName().toString();
                    return fileName.endsWith(".log");
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        long count = failCallbackFileStreamSupplier.get().count();
        assertThat(count).isEqualTo(2);
        while (count > 0) {
            failCallbackFileStreamSupplier.get().forEach(p -> {
                try {
                    System.out.println(p.getFileName().toString());
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            count = failCallbackFileStreamSupplier.get().count();
        }
    }

    @TempDir
    static Path tempPath;
}
