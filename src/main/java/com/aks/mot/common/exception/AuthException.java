package com.aks.mot.common.exception;

public class AuthException extends AksException {

	private static final long serialVersionUID = 1L;

	public AuthException(String errCd) {
		super(errCd);
	}

	public AuthException(String errCd, String[] arg) {
		super(errCd, arg);
	}

	public AuthException(String errCd, Throwable th) {
		super(errCd, th);
	}

}
