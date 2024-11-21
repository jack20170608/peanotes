package top.ilovemyhome.peanotes.backend.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class FileHelper {

    public static void deleteWithPredicate(Path sourcePath, Predicate<String> predicate){
        boolean pathExists = Files.exists(sourcePath) && Files.isDirectory(sourcePath);
        if (pathExists){
            try (Stream<Path> stream = Files.list(sourcePath)) {
                stream.map(p -> p.getFileName().toString())
                    .filter(predicate)
                    .forEach(f -> {
                        try {
                            Files.deleteIfExists(sourcePath.resolve(f));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            } catch (IOException e) {
                LOGGER.warn("File list failure.", e);
                throw new RuntimeException(e);
            }
        }else {
            throw new IllegalStateException("Source path is not exists or not a directory.");
        }
    }

    private FileHelper() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FileHelper.class);
}
