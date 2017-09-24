package com.manuelweiss.cec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.function.Consumer;

class CecStreamConsumer {

	private static final Logger logger = LoggerFactory.getLogger(CecStreamConsumer.class);

	private final String label;
	private final BufferedReader reader;
	private final Consumer<String> consumer;

	CecStreamConsumer(String label, InputStream stream, Consumer<String> consumer) {
		this.label = label;
		reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(stream)));
		this.consumer = consumer;
	}

	CecStreamConsumer(String label, InputStream stream) {
		this(label, stream, line -> {
			logger.error("Error: {}", line);
		});
	}

	void maybeReadStream() throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			logger.debug("CEC command from \"{}\": {}", new Object[]{label, line});
			consumer.accept(line.trim());
		}
	}
}