package com.dfq.coeffi.visitor.Exception;

public class FileStorageException extends RuntimeException {

    public FileStorageException(String message)
    {
        super(message);
    }

    public FileStorageException(String m,Throwable c)
    {
        super(m,c);
    }

}
