package fundamental;

import java.text.Normalizer;

public class Unicode2AsciiConverter {
	public static String convert(String unicodeString) {
		// Use Canonical decomposition
		String normalized = Normalizer.normalize(unicodeString,
				Normalizer.Form.NFD);
		return normalized;
	}
}
