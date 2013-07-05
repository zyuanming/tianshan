package com.tianshan.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Advertisement
{
	private int adId;
	private long endTime;
	private long startTime;
	private long updateTime;
	private String url;

	public Advertisement()
	{}

	public Advertisement(String paramString) throws JSONException
	{
		constructAdvertisement(new JSONObject(paramString));
	}

	public Advertisement(JSONObject paramJSONObject) throws JSONException
	{
		constructAdvertisement(paramJSONObject);
	}

	private void constructAdvertisement(JSONObject paramJSONObject)
			throws JSONException
	{
		this.adId = paramJSONObject.optInt("adid");
		this.startTime = paramJSONObject.optLong("starttime");
		this.endTime = paramJSONObject.optLong("endtime");
		this.updateTime = paramJSONObject.optLong("updatetime");
		this.url = paramJSONObject.optString("url");
	}

	public int getAdId()
	{
		return this.adId;
	}

	public long getEndTime()
	{
		return this.endTime;
	}

	public long getStartTime()
	{
		return this.startTime;
	}

	public long getUpdateTime()
	{
		return this.updateTime;
	}

	public String getUrl()
	{
		return this.url;
	}

	public void setAdId(int paramInt)
	{
		this.adId = paramInt;
	}

	public void setEndTime(long paramLong)
	{
		this.endTime = paramLong;
	}

	public void setStartTime(long paramLong)
	{
		this.startTime = paramLong;
	}

	public void setUpdateTime(long paramLong)
	{
		this.updateTime = paramLong;
	}

	public void setUrl(String paramString)
	{
		this.url = paramString;
	}
}