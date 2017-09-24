package com.manuelweiss.cec;

import com.manuelweiss.cec.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.*;
import java.util.Optional;

import static java.lang.Thread.sleep;

@Component
public class CecConnection implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(CecConnection.class);

	private static final String COMMAND = "/usr/bin/cec-client -d 8 -t r -o RaspberryPi";
	private static final int SLEEP_BETWEEN_RETRIES = 1000;
	private static final int EMPTY_STREAM_DELAY = 500;
	private static final int MAX_RETRY_COUNT = 10;

	@Autowired
	private ApplicationEventPublisher publisher;

	private Process process;
	private InputStream inputStream;
	private InputStream errorStream;
	private OutputStream outputStream;

	@Override
	public void run() {
		int retryCount = 1;
		do {
			try {
				logger.info("Starting cec-connection command line. Attempt {}", retryCount);
				openStreams();
				CecStreamConsumer is = new CecStreamConsumer(
						"InputStream", inputStream, line -> fireEvent(line));
				CecStreamConsumer es = new CecStreamConsumer(
						"ErrorStream", errorStream);
				do {
					is.maybeReadStream();
					es.maybeReadStream();
					sleep(EMPTY_STREAM_DELAY); // don't hammer the poor raspi down
				} while (true);

			} catch (IOException ex) {
				logger.warn("Error reading CEC stream from commandline!", ex);
			} catch (InterruptedException ex) {
				logger.error("Thread interrupted", ex);
			} finally {
				close();
			}

			// set asleep after an error. Maybe the call is simply invalid. In this case I want to
			// avoid going into an infinite loop
			try {
				sleep(SLEEP_BETWEEN_RETRIES);
			} catch (InterruptedException ex) {
				logger.info("Interrupted CecConnection shutting down");
				return;
			}
		} while (retryCount++ < MAX_RETRY_COUNT);
	}

	@PreDestroy
	public void close() {
		if (process != null) {
			process.destroy();
		}
		closeStream(inputStream);
		closeStream(errorStream);
		closeStream(outputStream);
	}

	public void sendMessage(Message message) {
		String rawMessage = /*"tx "+ */CecUtils.createRawMessage(message);

		logger.info("Sending Message: {}, RAW: \"{}\"", message, rawMessage);

		PrintWriter pw = new PrintWriter(outputStream, true);
		pw.println(rawMessage);
	}

	private void openStreams() throws IOException {
		logger.info("Open streams: {}", COMMAND);

		ProcessBuilder pb = new ProcessBuilder(COMMAND.split(" "));
		process = pb.start();

		inputStream = process.getInputStream();
		errorStream = process.getErrorStream();
		outputStream = process.getOutputStream();

		logger.info("Opening streams done");
	}

	private void closeStream(Closeable is) {
		try {
			if (is != null) {
				is.close();
			}
		} catch (IOException ex) {
			logger.error(null, ex);
		}
	}

	@Async
	private void fireEvent(String line) {
		Optional<Message> optionalMessage = CecUtils.parseMessage(line);
		if (optionalMessage.isPresent()) {
			Message message = optionalMessage.get();
			logger.info("Event fired: {}", message);
			publisher.publishEvent(message);
		} else {
			logger.warn("Could not parse: {}", line);
		}
	}
	
}
