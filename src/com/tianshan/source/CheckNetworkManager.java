package com.tianshan.source;

import android.webkit.CookieManager;
import com.tianshan.ZhangWoApp;
import com.tianshan.model.UserSession;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.http.message.BasicNameValuePair;

public class CheckNetworkManager
{
	public static HashMap<String, Object> Sendgps(String paramString1,
			String paramString2)
	{
		HashMap localHashMap = new HashMap();
		ArrayList localArrayList = new ArrayList();
		localArrayList.add(new BasicNameValuePair("content", paramString2));
		CookieManager.getInstance();
		String str = HttpsRequest.openUrl(paramString1, "POST", localArrayList,
				null, null, ZhangWoApp.getInstance().getUserSession()
						.getmobile_auth());
		if (str != null)
		{
			try
			{
				if (!str.equals(""))
				{
					localHashMap.put("data", str);
					localHashMap.put("reqsuccess", Boolean.valueOf(true));
				} else
				{
					localHashMap.put("data", str);
					localHashMap.put("reqsuccess", Boolean.valueOf(false));
				}
			} catch (Exception localException)
			{
				localException.printStackTrace();
			}
		}
		return localHashMap;
	}

	/**
	 * 检查是否有更新的应用程序版本
	 * 
	 * @param paramString请求的基本参数
	 * @return
	 */
	public static HashMap<String, Object> checksubnav(String paramString)
	{
		HashMap localHashMap = new HashMap();
		HttpRequest localHttpRequest = new HttpRequest();
		try
		{
			String str = localHttpRequest._get(paramString);
			if ((str != null) && (!str.equals("")))
			{
				localHashMap.put("data", str);
				localHashMap.put("reqsuccess", Boolean.valueOf(true));
			} else
			{
				localHashMap.put("data", str);
				localHashMap.put("reqsuccess", Boolean.valueOf(false));
			}
		} catch (Exception localException)
		{
			localException.printStackTrace();
		}
		return localHashMap;
	}

	public static HashMap<String, Object> getMoreApps(String paramString)
	{
		HashMap localHashMap = new HashMap();
		HttpRequest localHttpRequest = new HttpRequest();
		try
		{
			String str = localHttpRequest._get(paramString);
			if ((str != null) && (!str.equals("")))
			{
				localHashMap.put("data", str);
				localHashMap.put("reqsuccess", Boolean.valueOf(true));
			} else
			{
				localHashMap.put("data", str);
				localHashMap.put("reqsuccess", Boolean.valueOf(false));
			}
		} catch (Exception localException)
		{
			localException.printStackTrace();
		}
		return localHashMap;
	}
}