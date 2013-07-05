package com.tianshan.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Subnavisement
{
	private int displayorder;
	private int fup;
	private int id;
	private int isset;
	private String name;

	public Subnavisement()
	{}

	public Subnavisement(String paramString) throws JSONException
	{
		constructSubnavisement(new JSONObject(paramString));
	}

	public Subnavisement(JSONObject paramJSONObject) throws JSONException
	{
		constructSubnavisement(paramJSONObject);
	}

	private void constructSubnavisement(JSONObject paramJSONObject)
			throws JSONException
	{
		this.id = paramJSONObject.optInt("id");
		this.fup = paramJSONObject.optInt("fup");
		this.name = paramJSONObject.optString("name");
		this.displayorder = paramJSONObject.optInt("displayorder");
		this.isset = paramJSONObject.optInt("isset");
	}

	public int getDisplayorder()
	{
		return this.displayorder;
	}

	public int getFup()
	{
		return this.fup;
	}

	public int getId()
	{
		return this.id;
	}

	public int getIsset()
	{
		return this.isset;
	}

	public String getName()
	{
		return this.name;
	}

	public void setDisplayorder(int paramInt)
	{
		this.displayorder = paramInt;
	}

	public void setFup(int paramInt)
	{
		this.fup = paramInt;
	}

	public void setId(int paramInt)
	{
		this.id = paramInt;
	}

	public void setIsset(int paramInt)
	{
		this.isset = paramInt;
	}

	public void setName(String paramString)
	{
		this.name = paramString;
	}
}