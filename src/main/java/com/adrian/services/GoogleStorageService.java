package com.adrian.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

import com.adrian.constants.Constants;
import com.adrian.domain.ResultStatus;
import com.adrian.domain.ServiceType;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.Lists;

/**
 * Handles the storing and retrieving of documents from google storage.
 */
@Singleton
public class GoogleStorageService {

	private static final Logger LOG = Logger.getLogger(GoogleStorageService.class.getName());

	private static final String GOOGLE_AUTH_API_PATH = "https://www.googleapis.com/auth/cloud-platform";

	private static final String TWIC_BUCKET = "twic_bucket_30699c4e695d";

	private Storage storage;

	private Bucket bucket;

	/**
	 * Initialize the google storage interface and create the twic bucket if it's
	 * not already created.
	 */
	@PostConstruct
	public void init() {
		InputStream inputStream = TwicTestService.class.getResourceAsStream(Constants.GOOGLE_STORAGE_API_KEY_PATH);

		System.out.println(inputStream);

		// You can specify a credential file by providing a path to GoogleCredentials.
		// Otherwise credentials are read from the GOOGLE_APPLICATION_CREDENTIALS
		// environment variable.
		GoogleCredentials credentials;
		try {
			credentials = GoogleCredentials.fromStream(inputStream)
					.createScoped(Lists.newArrayList(GOOGLE_AUTH_API_PATH));
		} catch (IOException e) {
			LOG.severe("Unexpected exception occured on google storage credential handling.");
			return;
		}

		// Instantiates a client
		storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
		bucket = storage.get(TWIC_BUCKET);

		if (bucket == null) {
			// Creates the new bucket
			bucket = storage.create(BucketInfo.of(TWIC_BUCKET));
		}
	}

	/**
	 * Store the given data at the specified if no data exists at that id already.
	 * 
	 * @param data
	 * @param blobId
	 * @return
	 */
	public ResultStatus storeData(final byte[] data, final String blobId) {
		final BlobId blob = BlobId.of(TWIC_BUCKET, blobId);

		if (storage.get(blob) == null) {
			final BlobInfo blobInfo = BlobInfo.newBuilder(blob).setContentType(MediaType.TEXT_PLAIN).build();

			try {
				storage.create(blobInfo, data);
			} catch (StorageException e) {
				return ResultStatus.of(Constants.STORAGE_FAILED_CODE,
						String.format(Constants.STORAGE_FAILED_MESSAGE, blobId), ServiceType.TWIC_STORE);
			}
		}

		return ResultStatus.of(Constants.OK_CODE, Constants.OK_MESSAGE, ServiceType.TWIC_STORE);
	}
	
	
	/**
	 * Check if data exists in google storage at the specified id.
	 * 
	 * @param blobId
	 * @return true if data already exists for the specified id, false otherwise
	 */
	public boolean exists(final String blobId) {
		final BlobId blob = BlobId.of(TWIC_BUCKET, blobId);
		return storage.get(blob) != null;
	}
}
