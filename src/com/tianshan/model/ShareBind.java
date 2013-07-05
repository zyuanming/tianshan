package com.tianshan.model;

import android.text.TextUtils;

public class ShareBind implements Cloneable
{
	private String access_token;
	private String expires_in;
	private int id;
	private String keyword1;
	private String keyword2;
	private String keyword3;
	private String keyword4;
	private String keyword5;
	private String open_id;
	private String typename;

	public ShareBind clone()
	{
		ShareBind sharebind = null;
		try
		{
			sharebind = (ShareBind) super.clone();
		} catch (CloneNotSupportedException clonenotsupportedexception)
		{
			clonenotsupportedexception.printStackTrace();
		}
		return sharebind;
	}

	public String getAccess_token()
	{
		return this.access_token;
	}

	public String getExpires_in()
	{
		return this.expires_in;
	}

	public int getId()
	{
		return this.id;
	}

	public String getKeyword1()
	{
		return this.keyword1;
	}

	public String getKeyword2()
	{
		return this.keyword2;
	}

	public String getKeyword3()
	{
		return this.keyword3;
	}

	public String getKeyword4()
	{
		return this.keyword4;
	}

	public String getKeyword5()
	{
		return this.keyword5;
	}

	public String getOpen_id()
	{
		return this.open_id;
	}

	public String getTypename()
	{
		return this.typename;
	}

	public boolean isBind()
	{
		boolean flag;
		if (TextUtils.isEmpty(access_token))
			flag = false;
		else
			flag = true;
		return flag;
	}

	public void setAccess_token(String paramString)
	{
		this.access_token = paramString;
	}

	public void setExpires_in(String paramString)
	{
		this.expires_in = paramString;
	}

	public void setId(int paramInt)
	{
		this.id = paramInt;
	}

	public void setKeyword1(String paramString)
	{
		this.keyword1 = paramString;
	}

	public void setKeyword2(String paramString)
	{
		this.keyword2 = paramString;
	}

	public void setKeyword3(String paramString)
	{
		this.keyword3 = paramString;
	}

	public void setKeyword4(String paramString)
	{
		this.keyword4 = paramString;
	}

	public void setKeyword5(String paramString)
	{
		this.keyword5 = paramString;
	}

	public void setOpen_id(String paramString)
	{
		this.open_id = paramString;
	}

	public void setTypename(String paramString)
	{
		this.typename = paramString;
	}
}