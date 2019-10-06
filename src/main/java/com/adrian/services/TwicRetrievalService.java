package com.adrian.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.adrian.constants.Constants;
import com.adrian.domain.ResultStatus;
import com.adrian.domain.ServiceType;

/**
 * Manages the retrieval of twic documents from
 * 'https://theweekinchess.com/twic' and storing of the resulting document in
 * google storage.
 */
@Singleton
public class TwicRetrievalService {

	@Inject
	private GoogleStorageService storageService;

	/**
	 * Retrieves the document with the given number from twic website and stores
	 * this document in google storage.
	 * 
	 * @param pgnNumber
	 */
	public ResultStatus retrieveAndStore(final int pgnNumber) {
		if (storageService.exists(String.format(Constants.STORAGE_ENTRY_TEMPLATE, pgnNumber))) {
			return ResultStatus.of(Constants.STORAGE_ENTRY_EXISTS_CODE,
					String.format(Constants.STORAGE_ENTRY_EXISTS_MESSAGE, pgnNumber), ServiceType.TWIC_DOWNLOAD);
		}

		URL urlToArchive;
		try {
			urlToArchive = new URL(String.format(Constants.URL_TO_TWIC_FILE, pgnNumber));
		} catch (MalformedURLException e) {
			return ResultStatus.of(Constants.MALFORMED_URL_CODE, Constants.MALFORMED_URL_MESSAGE,
					ServiceType.TWIC_DOWNLOAD);
		}

		byte[] twicBytes;

		try (ZipInputStream zin = new ZipInputStream(urlToArchive.openStream())) {
			ZipEntry ze = zin.getNextEntry();

			if (ze == null) {
				return ResultStatus.of(Constants.DOWNLOAD_FAILED_CODE,
						String.format(Constants.DOWNLOAD_FAILED_MESSAGE, pgnNumber), ServiceType.TWIC_DOWNLOAD);
			}

			byte[] buffer = new byte[1024];
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			int len;
			while ((len = zin.read(buffer)) > 0) {
				outputStream.write(buffer, 0, len);
			}
			twicBytes = outputStream.toByteArray();

		} catch (IOException e) {
			return ResultStatus.of(Constants.DOWNLOAD_FAILED_CODE,
					String.format(Constants.DOWNLOAD_FAILED_MESSAGE, pgnNumber), ServiceType.TWIC_DOWNLOAD);
		}

		return storageService.storeData(twicBytes, String.format(Constants.STORAGE_ENTRY_TEMPLATE, pgnNumber));
	}
}
