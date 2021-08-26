package com.dfq.coeffi.SOPDetails.Exceptions;

public class MyFileNotFoundException extends RuntimeException
{
    public MyFileNotFoundException(String s)
    {

    }
    public MyFileNotFoundException(String s,Throwable cause)
    {
        super(s,cause);

    }
}
