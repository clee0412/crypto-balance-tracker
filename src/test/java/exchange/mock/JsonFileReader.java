package exchange.mock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonFileReader {

	private static final String BASE_PATH = "src/test/resources/mockfiles/";

	public static String readJsonFile(final String fileName) {
		try {
			return String.join("", Files.readAllLines(Paths.get(BASE_PATH, fileName)));
		} catch (final IOException e) {
			return "";
		}
	}
}
