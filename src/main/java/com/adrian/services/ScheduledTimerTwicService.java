package com.adrian.services;

import java.util.Date;
import java.util.logging.Logger;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import com.adrian.constants.Constants;
import com.adrian.domain.ResultStatus;

@Singleton
@Startup
public class ScheduledTimerTwicService {
	private static final String DOWNLOAD_STARTED_MESSAGE = "Twic download timer triggered at ";

	private static final String DOWNLOAD_FINISHED_MESSAGE = "Scheduled Twic download finished at ";

	private static final Logger LOG = Logger.getLogger(ScheduledTimerTwicService.class.getName());

	/**
	 * This is the minimum number of failures that should occur in order for the
	 * twic download process to stop.
	 */
	private static final int MIN_TWIC_BREAKER = 3;

	@Inject
	private TwicRetrievalService twicService;

	@Schedule(dayOfWeek = "Fri", hour = "19")
	public void run() {
		LOG.info(DOWNLOAD_STARTED_MESSAGE + new Date());

		int currentDownloadFailures = 0;
		int currentFileToProcess = Constants.OLDEST_TWIC_ENTRY;
		int succesfullyDownloadedFiles = 0;

		while (currentDownloadFailures < MIN_TWIC_BREAKER) {
			ResultStatus status = twicService.retrieveAndStore(currentFileToProcess);
			if (status.getCode() != Constants.OK_CODE && status.getCode() != Constants.STORAGE_ENTRY_EXISTS_CODE) {
				LOG.info(String.format("Failed to download file with number '%d'.", currentFileToProcess));
				currentDownloadFailures++;
			} else {
				if (status.getCode() == Constants.OK_CODE) {
					succesfullyDownloadedFiles++;
				}
				currentDownloadFailures = 0;
			}
			currentFileToProcess++;
		}
		LOG.info(String.format("'%d' files have been downloaded.", succesfullyDownloadedFiles));
		LOG.info(DOWNLOAD_FINISHED_MESSAGE + new Date());
	}
}
