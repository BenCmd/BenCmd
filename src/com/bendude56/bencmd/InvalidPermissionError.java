package com.bendude56.bencmd;

public class InvalidPermissionError extends Error {
	private static final long	serialVersionUID	= 0L;
	private String				permission;
	private String				errorMessage;

	public InvalidPermissionError() {
		permission = "";
		errorMessage = "";
	}

	public InvalidPermissionError(String permission, String message) {
		this.permission = permission;
		errorMessage = message;
	}

	public String getPermission() {
		return permission;
	}

	public String getMessage() {
		return errorMessage;
	}
}
