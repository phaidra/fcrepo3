package org.fcrepo.server.hooks;

import org.fcrepo.server.errors.ServerException;

public class APIHookException extends ServerException {

	public APIHookException(String bundleName, String code, String[] values,
			String[] details, Throwable cause) {
		super(bundleName, code, values, details, cause);
		// TODO Auto-generated constructor stub
	}
	
    public APIHookException(String message) {
        super(null, message, null, null, null);
    }
    
    public APIHookException(String message, Throwable cause) {
        super(null, message, null, null, cause);
    }
}
