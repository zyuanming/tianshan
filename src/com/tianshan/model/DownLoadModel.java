package com.tianshan.model;

public class DownLoadModel
{
	private byte[] content;
	private String name;
	private String status;
	private String type;
	private String url;

	public byte[] getContent()
	{
		return this.content;
	}

	public String getName()
	{
		return this.name;
	}

	public String getStatus()
	{
		return this.status;
	}

	public String getType()
	{
		return this.type;
	}

	public String getUrl()
	{
		return this.url;
	}

	public void setContent(byte[] paramArrayOfByte)
	{
		this.content = paramArrayOfByte;
	}

	public void setName(String paramString)
	{
		this.name = paramString;
	}

	public void setStatus(String paramString)
	{
		this.status = paramString;
	}

	public void setType(String paramString)
	{
		this.type = paramString;
	}

	public void setUrl(String paramString)
	{
		this.url = paramString;
	}
}