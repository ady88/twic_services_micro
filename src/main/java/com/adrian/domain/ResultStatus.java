package com.adrian.domain;

public class ResultStatus {
	private final int code;
	private final String message;
	private final ServiceType type;
	
	private ResultStatus(int code, String message, ServiceType type) {
		this.code = code;
		this.message = message;
		this.type = type;
	}
	
	public int getCode() {
		return code;
	}
	public String getMessage() {
		return message;
	}
	public ServiceType getType() {
		return type;
	}
	
	public static ResultStatus of(final int code, final String message, final ServiceType type) {
		return new ResultStatus(code, message, type);
	}
}