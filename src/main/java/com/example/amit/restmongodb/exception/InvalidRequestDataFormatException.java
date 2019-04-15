package com.example.amit.restmongodb.exception;

public class InvalidRequestDataFormatException extends Exception {

	
	/**
     * 
     */
    private static final long serialVersionUID = 6629607516353113564L;

	public InvalidRequestDataFormatException(){
		
	}
	
	public InvalidRequestDataFormatException(String message){
		super(message);
	}
	
	public InvalidRequestDataFormatException(String message, Throwable cause){
		super(message, cause);
	}
	
	public InvalidRequestDataFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
       super(message, cause, enableSuppression, writableStackTrace);
   	}
}
