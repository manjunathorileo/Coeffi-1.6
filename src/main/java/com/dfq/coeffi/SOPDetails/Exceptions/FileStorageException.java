package com.dfq.coeffi.SOPDetails.Exceptions;

public class FileStorageException extends RuntimeException
{
    public FileStorageException(String messaeg)
    {
        super(messaeg);
    }

    public FileStorageException(String m,Throwable c)
    {
        super(m,c);
    }
}
