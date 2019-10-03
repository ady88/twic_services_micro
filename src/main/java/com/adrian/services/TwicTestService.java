package com.adrian.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.Lists;

@RequestScoped
@Path("/twic")
public class TwicTestService {

	private static final String FIRST_DATA = "first_data";
	private static final String SECOND_DATA = "second_data";
	private static final String TEST_BUCKET = "test_bucket_30699c4e695d";
	@Inject
	@ConfigProperty(name = "application.username", defaultValue = "Adrian")
	private String name;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "/hello")
	public Response sayHello() {
		var message = "Hello, %s.";

		Jsonb jsonb = JsonbBuilder.create();
		message = jsonb.toJson(message);
		return Response.ok(String.format(message, name), MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "/downloadFromTwic")
	public Response downloadFromTwic() throws FileNotFoundException, IOException {

		InputStream inputStream = TwicTestService.class.getResourceAsStream("/Twic project-30699c4e695d.json");
		
		System.out.println(inputStream);
		
		// You can specify a credential file by providing a path to GoogleCredentials.
		// Otherwise credentials are read from the GOOGLE_APPLICATION_CREDENTIALS
		// environment variable.
		GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream)
				.createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

		// Instantiates a client
		Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
		// The name for the new bucket

		// Creates the new bucket
		Bucket bucket = storage.create(BucketInfo.of(TEST_BUCKET));

		System.out.printf("Bucket %s created.%n", bucket.getName());

		BlobId blobId = BlobId.of(TEST_BUCKET, FIRST_DATA);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
		Blob blob = storage.create(blobInfo, "Hello, Cloud Storage!".getBytes(Charset.forName("UTF-8")));
		var blobExists = blob.exists();
		System.out.println(blobExists);

		BlobId blobId2 = BlobId.of(TEST_BUCKET, SECOND_DATA);
		BlobInfo blobInfo2 = BlobInfo.newBuilder(blobId2).setContentType("text/plain").build();
		Blob blob2 = storage.create(blobInfo2, "Hi again!".getBytes(Charset.forName("UTF-8")));
		var blob2Exists = blob2.exists();
		System.out.println(blob2Exists);
		return Response.ok(JsonbBuilder.create().toJson("TEST files created."), MediaType.APPLICATION_JSON).build();
	}
}