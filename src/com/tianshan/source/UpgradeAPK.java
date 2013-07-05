package com.tianshan.source;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;

import com.tianshan.setting.Setting.AppParam;

public class UpgradeAPK
{
	public boolean checkVersion = false; // 是否有新版本
	private Core core;
	public int newVersion;
	private SharedPreferences preferences;
	private String url;

	public UpgradeAPK()
	{}

	public UpgradeAPK(SharedPreferences paramSharedPreferences, Core paramCore)
	{
		this.preferences = paramSharedPreferences;
		this.core = paramCore;
	}

	public void checkUpdate()
	{
		int i = this.core._getVersionCode();
		boolean bool = isNeedUpdate();
		HttpRequest localHttpRequest;
		if (!bool)
		{
			localHttpRequest = new HttpRequest();
			try
			{
				DEBUG.o("*****check new version****");
				if (this.preferences.getBoolean("firstopen", true))
				{
					String[] arrayOfString2 = new String[5];
					arrayOfString2[0] = "ac=update";
					arrayOfString2[1] = "platform=android";
					arrayOfString2[2] = ("build=" + i);
					arrayOfString2[3] = ("osversion=" + Build.VERSION.RELEASE);
					arrayOfString2[4] = "install=1";
					this.url = SiteTools.getApiUrl(arrayOfString2);
					SharedPreferences.Editor localEditor = this.preferences
							.edit();
					localEditor.putBoolean("firstopen", false);
					localEditor.commit();
				} else
				{
					String str = localHttpRequest._get(this.url);
					if (str != null)
					{
						JSONObject localJSONObject1 = new JSONObject(str);
						if (localJSONObject1 != null)
						{
							JSONObject localJSONObject2 = localJSONObject1
									.optJSONObject("msg");
							if ((localJSONObject2 != null)
									&& (!"list_empty"
											.equalsIgnoreCase(localJSONObject2
													.optString("msgvar",
															"list_empty"))))
							{
								JSONObject localJSONObject3 = localJSONObject1
										.optJSONObject("res");
								if (localJSONObject3 != null)
								{
									JSONArray localJSONArray = localJSONObject3
											.optJSONArray("list");
									if ((localJSONArray != null)
											&& (localJSONArray.length() > 0))
									{
										JSONObject localJSONObject4 = localJSONArray
												.optJSONObject(0);
										this.newVersion = localJSONObject4
												.optInt("build");
										if (i < this.newVersion)
										{
											AppParam.UPDATE_INFORMATION = localJSONObject4
													.optString("description");
											AppParam.UPDATE_URL = localJSONObject4
													.optString("url");
											bool = true;
											checkVersion = true;
										}
									}
								}
							}
						}
					}
					String[] arrayOfString1 = new String[3];
					arrayOfString1[0] = "ac=update";
					arrayOfString1[1] = "platform=android";
					arrayOfString1[2] = ("build=" + i);
					this.url = SiteTools.getApiUrl(arrayOfString1);
				}
			} catch (Exception localException)
			{
				localException.printStackTrace();
			}
		}
		preferences.edit().putBoolean("is_need_update", bool).commit();
	}

	/**
	 * 下载新版本
	 * 
	 * @param paramContext
	 * @param paramString
	 */
	public void downLoad(Context paramContext, String paramString)
	{
		paramContext.startActivity(new Intent("android.intent.action.VIEW", Uri
				.parse(paramString)));
	}

	/**
	 * 检查是否到期？？？
	 * 
	 * @return
	 */
	public boolean isExpire()
	{
		String str = Tools._getNumToDateTime(new Date().getTime(), null);
		boolean bool1 = str.equals(this.preferences.getString(
				"last_check_update_timestamp", null));
		boolean bool2 = false;
		if (!bool1)
		{
			this.preferences.edit()
					.putString("last_check_update_timestamp", str).commit();
			bool2 = true;
		}
		return bool2;
	}

	/**
	 * 是否需要更新应用程序
	 * 
	 * @return
	 */
	public boolean isNeedUpdate()
	{
		return this.preferences.getBoolean("is_need_update", false);
	}

	/**
	 * 重置更新标签
	 */
	public void resetUpdateTag()
	{
		this.preferences.edit().putBoolean("is_need_update", false).commit();
	}
}