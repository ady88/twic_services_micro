package com.adrian.constants;

public class Constants {
	
	// Path to google storage api key file
	public static final String GOOGLE_STORAGE_API_KEY_PATH = "/Twic project-30699c4e695d.json";
	
	// Template url path to twic archive files 
	public static final String URL_TO_TWIC_FILE = "https://theweekinchess.com/zips/twic%dg.zip";
	
	// Number of the oldest twic archive entry
	public static final int OLDEST_TWIC_ENTRY = 920;
	
	// Number of the oldest twic archive entry
	public static final String STORAGE_ENTRY_TEMPLATE = "twic_%d"; 
	
	/**
	 * ---------------------------------------------
	 * Status codes and messages
	 * ---------------------------------------------
	 */
	
	public static final int OK_CODE = 200;
	
	public static final String OK_MESSAGE = "OK";
	
	public static final int MALFORMED_URL_CODE = 10002;
	
	public static final String MALFORMED_URL_MESSAGE = "Given path is not a valid URL.";
	
	public static final int DOWNLOAD_FAILED_CODE = 10001;
	
	public static final String DOWNLOAD_FAILED_MESSAGE = "Download failed for pgn file with number '%d'";
	
	public static final int STORAGE_FAILED_CODE = 10003;
	
	public static final String STORAGE_FAILED_MESSAGE = "Storage failed for file with id '%d'";
	
	public static final int STORAGE_ENTRY_EXISTS_CODE = 10004;
	
	public static final String STORAGE_ENTRY_EXISTS_MESSAGE = "Storage entry with number '%d' already exists";
}
