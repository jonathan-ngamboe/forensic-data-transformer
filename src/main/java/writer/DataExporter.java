package writer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface DataExporter<T> {

    /**
     * Writes a stream of ChatMessage objects to the specified destination file.
     * <p>
     * This method consumes the input stream.
     *
     * @param input   The lazy stream of input objects to be written.
     * @param outputPath The absolute path where the report file will be created or overwritten.
     * @throws IOException If an I/O error occurs while opening or writing to the file.
     */
    void write(Stream<T> input, Path outputPath) throws IOException;
}