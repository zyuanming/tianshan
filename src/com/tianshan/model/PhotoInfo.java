package com.tianshan.model;

import android.graphics.Bitmap;
import org.json.JSONException;
import org.json.JSONObject;

public class PhotoInfo
{
	private Bitmap bit;
	private String id;
	private String imgurl;
	private String title;

	public PhotoInfo()
	{}

	public PhotoInfo(String paramString) throws JSONException
	{
		constructPhotoInfo(new JSONObject(paramString));
	}

	public PhotoInfo(JSONObject paramJSONObject) throws JSONException
	{
		constructPhotoInfo(paramJSONObject);
	}

	private void constructPhotoInfo(JSONObject paramJSONObject)
			throws JSONException
	{
		this.id = paramJSONObject.optString("id");
		this.title = paramJSONObject.optString("title");
		this.imgurl = paramJSONObject.optString("imgurl");
	}

	public Bitmap getBit()
	{
		return this.bit;
	}

	public String getId()
	{
		return this.id;
	}

	public String getImgurl()
	{
		return this.imgurl;
	}

	public String getTitle()
	{
		return this.title;
	}

	public void setBit(Bitmap paramBitmap)
	{
		this.bit = paramBitmap;
	}

	public void setId(String paramString)
	{
		this.id = paramString;
	}

	public void setImgurl(String paramString)
	{
		this.imgurl = paramString;
	}

	public void setTitle(String paramString)
	{
		this.title = paramString;
	}
}