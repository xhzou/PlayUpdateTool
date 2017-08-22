package com.pp100.models;

public class WorkFlowItemInfo
{
	private String runType;
	private String resName;
	private boolean fatalError;
	private String value;

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getRunType()
	{
		return runType;
	}

	public void setRunType(String runType)
	{
		this.runType = runType;
	}

	public String getResName()
	{
		return resName;
	}

	public void setResName(String resName)
	{
		this.resName = resName;
	}

	public boolean isFatalError()
	{
		return fatalError;
	}

	public void setFatalError(boolean fatalError)
	{
		this.fatalError = fatalError;
	}

}
