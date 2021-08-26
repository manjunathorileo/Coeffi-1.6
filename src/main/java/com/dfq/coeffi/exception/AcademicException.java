package com.dfq.coeffi.exception;
/**
 * @Auther : H Kapil Kumar
 * @Date : May-18
 */
public class AcademicException extends RuntimeException
{
	private static final long serialVersionUID = -7170697018482052890L;
	
	public AcademicException(String message) {
        super(message);
    }

    public AcademicException() {
        super("Something went wrong on academic application. Please contact");
    }

}


