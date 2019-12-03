package com.aks.mot.com.exception;


@SuppressWarnings("serial")
public class AksException extends RuntimeException {

	private final String errCd;

	private final String errMsg;

	public AksException(String errCd) {
		super(getMessage(errCd));
		this.errCd = errCd;
		this.errMsg = getMessage(errCd);
	}

	public AksException(String errCd, String[] arg) {
		super(getMessage(errCd, arg));
		this.errCd = errCd;
		this.errMsg = getMessage(errCd);
	}

	public AksException(String errCd, Throwable th) {
		// WAS - DB 세션 타임아웃시 다르게 표시되도록 예외처리
		//super(getMessage(th.toString().indexOf("SQLTimeoutException") > -1 ? "gsi.cm.err.sqlTimeout" : errCd), th);
		super(getMessage(errCd), th);
		this.errCd = errCd;
		this.errMsg = getMessage(errCd);
	}

	/**
	 * 에러메세지 리턴
	 * @Method Name : getErrorMessage
	 * @return
	 */
	public String getErrCd() {
		return errCd;
	}

	public String getErrMsg() {
		return errMsg;
	}
	
	@SuppressWarnings("unused")
	public static String getMessage(String errCd) {
		if(errCd != null && errCd.indexOf(";;")> 0) {
	        String[] arrMsg =  errCd.split(";;");
        	String[] param =  {};
        	if(arrMsg.length > 1 && arrMsg[1] != null ) {
        		param = arrMsg[1].split("##");
        	}
    		//return MessageUtil.getMessage(arrMsg[0], param);
        	return "MessageUtil Not found";
		} else {
    		//return MessageUtil.getMessage(errCd);
			return "MessageUtil Not found";
        }
	}

	public static String getMessage(String errCd, String[] arg) {
		//return MessageUtil.getMessage(errCd, arg);
		return "MessageUtil Not found";
	}
}
