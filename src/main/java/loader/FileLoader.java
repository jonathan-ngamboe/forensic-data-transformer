package loader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileLoader<T> {
    /**
     * Reads the file content lazily.
     *
     * @param filePath The absolute path to the input file.
     * @return A Stream of data (T) representing the content.
     * @throws IOException If the file does not exist or cannot be read.
     */
    Stream<T> load(Path filePath) throws IOException;
}