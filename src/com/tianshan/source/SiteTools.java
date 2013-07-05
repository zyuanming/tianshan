package com.tianshan.source;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.Display;

import com.tianshan.setting.Setting;
import com.tianshan.setting.WoPreferences;

public class SiteTools
{
	private static Context context;

	public SiteTools(Context paramContext)
	{
		context = paramContext;
	}

	private static String _addUrlParams()
	{
		Core core = new Core(context);
		Display display = core._getDisplay();
		int i = WoPreferences.queryDownloadImgMode();
		Log.d("code", (new StringBuilder(String.valueOf(i))).toString());
		if (Setting.checkWifi(context))
			i = 1;
		return (new StringBuilder("&display=")).append(display.getWidth())
				.append("*").append(display.getHeight()).append("&imei=")
				.append(core._getIMEI()).append("&phone=")
				.append(core._getPhoneNumber()).append("&clientversion=")
				.append(core._getVersionCode()).append("&downloadimgmode=")
				.append(i).toString();
	}

	public static String getApiUrl(String[] paramArrayOfString)
	{
		return "http://client.xjts.cn/admin/api.php?"
				+ initGetParam(paramArrayOfString);
	}

	public static String getBBSPcUrl(String[] paramArrayOfString)
	{
		return "http://client.xjts.cn/admin/bbs/forum.php?"
				+ initGetParamForPc(paramArrayOfString);
	}

	public static String getLocalIpAddress()
	{
		String str1 = null;
		try
		{
			InetAddress localInetAddress;
			Enumeration localEnumeration1 = NetworkInterface
					.getNetworkInterfaces();
			Enumeration localEnumeration2;
			do
			{
				if (localEnumeration1.hasMoreElements())
				{
					localEnumeration2 = ((NetworkInterface) localEnumeration1
							.nextElement()).getInetAddresses();
					do
					{
						InetAddress inetaddress = (InetAddress) localEnumeration2
								.nextElement();
						if (!inetaddress.isLoopbackAddress())
						{
							str1 = inetaddress.getHostAddress().toString();
							return str1;
						}
					} while (localEnumeration2.hasMoreElements());
				}
			} while (!localEnumeration1.hasMoreElements());
		} catch (SocketException localSocketException)
		{
			DEBUG.e(localSocketException.toString());
			str1 = null;
		}
		return str1;
	}

	public static String getMobileUrl(String[] paramArrayOfString)
	{
		return "http://client.xjts.cn/admin/mobile.php?"
				+ initGetParam(paramArrayOfString);
	}

	public static String getPcUrl(String[] paramArrayOfString)
	{
		return "http://client.xjts.cn/admin/page.php?"
				+ initGetParamForPc(paramArrayOfString);
	}

	public static String getSiteUrl(String[] as)
	{
		HashMap localHashMap = new HashMap();
		localHashMap.clear();
		String s2 = null;
		if (!Setting.checkConn(context))
		{
			StringBuffer stringbuffer = new StringBuffer();
			for (int i = 0; i < as.length; i++)
			{
				String[] as1 = (String[]) as[i].split("=");
				for (int j = 0; j < as1.length; j++)
				{
					localHashMap.put(as1[0], as1[1]);
				}
				stringbuffer.append((String) localHashMap.get(as1[0])).append(
						"_");
			}
			String s = stringbuffer.toString();
			String s1 = s.substring(0, -1 + s.length());
			Log.d("pp", (new StringBuilder(String.valueOf(s1))).append(".html")
					.toString());
			s2 = (new StringBuilder(String.valueOf(s1))).append(".html")
					.toString();
		} else
		{
			s2 = (new StringBuilder("http://client.xjts.cn/admin/page.php?"))
					.append(initGetParam(as)).toString();
		}
		return s2;
	}

	/**
	 * 创建Android请求地址后面的参数
	 * 
	 * @param paramArrayOfString请求参数的数组
	 * @return
	 */
	public static String initGetParam(String[] paramArrayOfString)
	{
		String str1 = "";
		if (paramArrayOfString != null)
		{
			for (int j = 0; j < paramArrayOfString.length; j++)
			{
				String str2 = paramArrayOfString[j];
				if (!"".equals(str2))
					str1 = str1 + str2 + "&";
			}
			str1 = Tools.trim(str1, "&");
			return str1 + "&system=android" + _addUrlParams();
		} else
		{
			return (new StringBuilder(String.valueOf(str1)))
					.append("&system=android").append(_addUrlParams())
					.toString();
		}
	}

	/**
	 * 创建PC请求地址后面的参数
	 * 
	 * @param paramArrayOfString
	 * @return
	 */
	public static String initGetParamForPc(String[] paramArrayOfString)
	{
		String str1 = "";
		if (paramArrayOfString != null)
		{
			for (int j = 0; j < paramArrayOfString.length; j++)
			{
				String str2 = paramArrayOfString[j];
				if (!"".equals(str2))
					str1 = str1 + str2 + "&";
			}
			str1 = Tools.trim(str1, "&");
		}
		return str1;
	}
}