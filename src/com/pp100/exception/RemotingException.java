package com.pp100.exception;

public class RemotingException extends Exception
{
	private static final long serialVersionUID = 1L;

	public RemotingException()
	{
	}

	public RemotingException(String message)
	{
		super(message);
	}

	public RemotingException(String message, Throwable cause)
	{
		super(message, cause);
	}

	@Override
	public String getMessage()
	{
		return super.getMessage();
	}

}
