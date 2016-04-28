package com.taeschma.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.taeschma.service.DropboxService;

@Component
public class ScheduledTasks {
	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	private DropboxService dropboxService;
	
	public ScheduledTasks(DropboxService dropboxService) {
		super();
		this.dropboxService = dropboxService;
	}

	@Scheduled(fixedRate = 120000)
	public void syncImages() {
		log.info("Start sync...");
		dropboxService.syncFiles();
		log.info("End sync...");
	}
}
