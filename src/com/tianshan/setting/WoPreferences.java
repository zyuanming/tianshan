package com.tianshan.setting;

import android.content.Context;
import android.content.SharedPreferences;

import com.tianshan.source.DEBUG;

public class WoPreferences
{
	private static final String preferences_name = "hesinePreferences";
	private static SharedPreferences sp;

	public static boolean IsFirstOpen()
	{
		boolean flag = true;
		if (getIntegerValueDefault("firstopen").intValue() == -1)
			setIntegerValue("firstopen", Integer.valueOf(1));
		else
			flag = false;
		return flag;
	}

	public static void clearAllConfig()
	{
		try
		{
			if (sp != null)
			{
				SharedPreferences.Editor localEditor = sp.edit();
				localEditor.clear();
				localEditor.commit();
			}
		} catch (Exception localException)
		{
			localException.printStackTrace();
			DEBUG.WoPrintStackTrace(localException);
		}
	}

	public static int getDownloadCss()
	{
		return getIntegerValue("getDownloadCss").intValue();
	}

	public static int getDownloadJs()
	{
		return getIntegerValue("getDownloadJs").intValue();
	}

	public static int getDownloadPic()
	{
		return getIntegerValue("getDownloadPic").intValue();
	}

	private static Integer getIntegerValue(String s)
	{
		Integer i = -1;
		try
		{

			if (sp == null)
				i = Integer.valueOf(0);
			else
			{
				i = Integer.valueOf(sp.getInt(s, 0));
			}
		} catch (Exception localException)
		{
			DEBUG.WoPrintStackTrace(localException);
			i = -1;
		}
		return i;
	}

	private static Integer getIntegerValueDefault(String s)
	{
		Integer i = -1;
		try
		{

			if (sp == null)
				i = Integer.valueOf(-1);
			else
			{
				i = Integer.valueOf(sp.getInt(s, -1));
			}
		} catch (Exception localException)
		{
			DEBUG.d("get default Integer key-value from preferences xml and key:"
					+ s);
			i = -1;
		}
		return i;
	}

	public static String getLBSType()
	{
		return getStringValue("lbstype");
	}

	private static long getLongValue(String paramString)
	{
		long l1 = 0L;
		try
		{
			SharedPreferences localSharedPreferences = sp;
			if (localSharedPreferences != null)
			{
				l1 = sp.getLong(paramString, 0L);
			}
		} catch (Exception localException)
		{
			DEBUG.WoPrintStackTrace(localException);
			l1 = -1L;
		}
		return l1;
	}

	public static int getShowPushMsg()
	{
		return getIntegerValue("ShowPushMsg").intValue();
	}

	public static String getShowPushMsgType()
	{
		return getStringValue("ShowPushMsgType");
	}

	private static String getStringValue(String paramString)
	{
		String str = "";
		try
		{
			SharedPreferences localSharedPreferences = sp;
			if (localSharedPreferences != null)
			{
				str = sp.getString(paramString, null);
			}
		} catch (Exception localException)
		{
			DEBUG.d("get string key-value from preferences xml and key:"
					+ paramString);
			str = "";
		}
		return str;
	}

	public static void initPreferences(Context paramContext)
	{
		try
		{
			if (sp == null)
				sp = paramContext.getSharedPreferences(preferences_name, 2);
		} catch (Exception localException)
		{
			DEBUG.d("init preferences xml file exception:"
					+ DEBUG.WoGetStactTrace(localException));
		}
	}

	public static int queryAutoPushMsg()
	{
		return getIntegerValue("AutoPushMsg").intValue();
	}

	public static int queryDownloadImgMode()
	{
		return getIntegerValue("DownloadImgMode").intValue();
	}

	public static void setAutoPushMsg(int paramInt)
	{
		setIntegerValue("AutoPushMsg", Integer.valueOf(paramInt));
	}

	public static void setDownloadCss(int paramInt)
	{
		setIntegerValue("getDownloadCss", Integer.valueOf(paramInt));
	}

	public static void setDownloadImgMode(int paramInt)
	{
		setIntegerValue("DownloadImgMode", Integer.valueOf(paramInt));
	}

	public static void setDownloadJs(int paramInt)
	{
		setIntegerValue("getDownloadJs", Integer.valueOf(paramInt));
	}

	public static void setDownloadPic(int paramInt)
	{
		setIntegerValue("getDownloadPic", Integer.valueOf(paramInt));
	}

	private static void setIntegerValue(String paramString, Integer paramInteger)
	{
		try
		{
			if (sp != null)
			{
				SharedPreferences.Editor localEditor = sp.edit();
				localEditor.putInt(paramString, paramInteger.intValue());
				localEditor.commit();
			}
		} catch (Exception localException)
		{
			localException.printStackTrace();
			DEBUG.d("set String key-(int)value from preferences xml and key:"
					+ paramString + ",value:" + paramInteger);
		}
	}

	public static void setLBSType(String paramString)
	{
		setStringValue("lbstype", paramString);
	}

	private static void setLongValue(String paramString, long paramLong)
	{
		try
		{
			if (sp != null)
			{
				SharedPreferences.Editor localEditor = sp.edit();
				localEditor.putLong(paramString, paramLong);
				localEditor.commit();
			}
		} catch (Exception localException)
		{
			DEBUG.d("set String key-(long)value from preferences xml and key:"
					+ paramString + ",value:" + paramLong);
		}
	}

	public static void setShowPushMsg(int paramInt)
	{
		setIntegerValue("ShowPushMsg", Integer.valueOf(paramInt));
	}

	public static void setShowPushMsgType(String paramString)
	{
		setStringValue("ShowPushMsgType", paramString);
	}

	private static void setStringValue(String paramString1, String paramString2)
	{
		try
		{
			if (sp != null)
			{
				SharedPreferences.Editor localEditor = sp.edit();
				localEditor.putString(paramString1, paramString2);
				localEditor.commit();
			}
		} catch (Exception localException)
		{
			localException.printStackTrace();
			DEBUG.d("set String key-(String)value from preferences xml and key:"
					+ paramString1 + ",value:" + paramString2);
		}
	}
}