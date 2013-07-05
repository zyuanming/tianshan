package com.tianshan.model;

public class App
{
	private String IconUrl;
	private String desc;
	private String downloadUrl;
	private String name;

	public String getDesc()
	{
		return this.desc;
	}

	public String getDownloadUrl()
	{
		return this.downloadUrl;
	}

	public String getIconUrl()
	{
		return this.IconUrl;
	}

	public String getName()
	{
		return this.name;
	}

	public void setDesc(String paramString)
	{
		this.desc = paramString;
	}

	public void setDownloadUrl(String paramString)
	{
		this.downloadUrl = paramString;
	}

	public void setIconUrl(String paramString)
	{
		this.IconUrl = paramString;
	}

	public void setName(String paramString)
	{
		this.name = paramString;
	}
}