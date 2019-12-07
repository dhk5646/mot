package com.aks.mot.cmn.exception;

public class MvcException extends AksException {
	
	private static final long serialVersionUID = 1L;
	
	public MvcException(String errCd) {
		super(errCd);
	}
	
	public MvcException(String errCd, String[] arg) {
		super(errCd, arg);
	}
	
	public MvcException(String errCd, Throwable th) {
		super(errCd, th);
	}
}
