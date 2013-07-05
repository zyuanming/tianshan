package com.tianshan.model;

import android.content.ContentValues;
import org.json.JSONObject;

/**
 * 用户会话类
 * 
 * @author lkh
 * 
 */
public class UserSession
{
	private String auth;
	private String formhash;
	private int groupid = 7;
	private String mobile_auth;
	private String saltkey;
	private long uid = 0L;
	private String username;
	private String webviewcookies;

	public UserSession()
	{}

	public UserSession(JSONObject paramJSONObject)
	{
		_initValue(paramJSONObject);
	}

	public ContentValues _getSqlVal()
	{
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("uid", Long.valueOf(uid));
		contentvalues.put("username", username);
		contentvalues.put("groupid", Integer.valueOf(groupid));
		String s;
		if (saltkey == null)
			s = "";
		else
			s = saltkey;
		contentvalues.put("saltkey", s);
		contentvalues.put("auth", auth);
		contentvalues.put("formhash", formhash);
		contentvalues.put("mobile_auth", mobile_auth);
		contentvalues.put("webviewcookies", webviewcookies);
		return contentvalues;
	}

	public void _initValue(JSONObject paramJSONObject)
	{
		String str = paramJSONObject.optString("cookiepre");
		this.saltkey = paramJSONObject.optString("saltkey");
		if (!"".equals(this.saltkey))
			this.saltkey = (str + "saltkey=" + this.saltkey);
		this.uid = paramJSONObject.optLong("member_uid");
		this.username = paramJSONObject.optString("member_username");
		this.groupid = paramJSONObject.optInt("groupid");
		this.auth = paramJSONObject.optString("auth");
		if (!"".equals(this.auth))
			this.auth = (str + "auth=" + paramJSONObject.optString("auth"));
		this.formhash = paramJSONObject.optString("formhash");
		this.mobile_auth = ("mobile_auth=" + paramJSONObject
				.optString("mobile_auth"));
	}

	public String getAuth()
	{
		return this.auth;
	}

	public String getFormhash()
	{
		return this.formhash;
	}

	public int getGroupid()
	{
		return this.groupid;
	}

	public String getSaltkey()
	{
		return this.saltkey;
	}

	public long getUid()
	{
		return this.uid;
	}

	public String getUsername()
	{
		return this.username;
	}

	public String getWebViewCookies()
	{
		return this.webviewcookies;
	}

	public String getmobile_auth()
	{
		return this.mobile_auth;
	}

	public void setAuth(String paramString)
	{
		this.auth = paramString;
	}

	public void setFormhash(String paramString)
	{
		this.formhash = paramString;
	}

	public void setGroupid(int paramInt)
	{
		this.groupid = paramInt;
	}

	public void setSaltkey(String paramString)
	{
		this.saltkey = paramString;
	}

	public void setUid(long paramLong)
	{
		this.uid = paramLong;
	}

	public void setUsername(String paramString)
	{
		this.username = paramString;
	}

	public void setWebViewCookies(String paramString)
	{
		this.webviewcookies = paramString;
	}

	public void setmobile_auth(String paramString)
	{
		this.mobile_auth = paramString;
	}
}