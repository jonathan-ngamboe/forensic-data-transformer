package loader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class CsvLoader implements FileLoader<String> {

    @Override
    public Stream<String> load(Path filePath) throws IOException {
        return Files.lines(filePath);
    }
}
