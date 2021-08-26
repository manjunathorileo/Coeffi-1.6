package com.dfq.coeffi.exception;

/**
 * @Auther : H Kapil Kumar
 * @Date : May-18
 */

import static java.lang.String.format;

public class DuplicateUserException extends AcademicException{
	private static final long serialVersionUID = 1939671486397901170L;
	public DuplicateUserException(String entityType, String duplicateText) {
        super(format("The %s with %s already exists. Please specify a new one",
                entityType, duplicateText));
    }
}