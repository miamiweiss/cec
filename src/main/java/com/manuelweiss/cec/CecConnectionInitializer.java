package com.manuelweiss.cec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CecConnectionInitializer {

    private static final Logger logger = LoggerFactory.getLogger(CecConnectionInitializer.class);

    @Autowired
	private CecConnection connection;

	@PostConstruct
	void startUp() {
        logger.debug("Starting up cec-connector thread.");
        TaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
        simpleAsyncTaskExecutor.execute(connection);

        // Add shutdown hook to close the connection properly
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                logger.info("Shutting down cec-connector thread.");
                connection.close();
            }
        });
    }

}
