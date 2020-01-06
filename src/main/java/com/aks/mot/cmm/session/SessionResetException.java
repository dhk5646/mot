package com.aks.mot.cmm.session;

public class SessionResetException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SessionResetException( String message ) {
        super( message );
    }

    public SessionResetException( String message, Throwable th ) {
        super( message, th );
    }

    public SessionResetException( Throwable th ) {
        super( th );
    }
}
