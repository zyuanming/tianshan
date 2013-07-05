package com.tianshan.source;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.tianshan.dbhelper.AdvertisementHelper;
import com.tianshan.model.Advertisement;
import com.tianshan.setting.Setting;

public class AD
{
	/**
	 * 
	 * @param paramContext
	 * @param paramLong
	 *            时间
	 */
	public static void checkNewAD(Context paramContext, long paramLong)
	{
		DEBUG.o("*****check new ad****");
		int i;
		try
		{
			String[] arrayOfString = new String[3];
			arrayOfString[0] = "ac=ad";
			arrayOfString[1] = "type=home";
			arrayOfString[2] = ("updatetime=" + paramLong);
			String str1 = SiteTools.getApiUrl(arrayOfString);
			HttpRequest localHttpRequest = new HttpRequest();
			String str2 = localHttpRequest._get(str1);
			JSONObject localJSONObject1 = new JSONObject(str2);
			if (localJSONObject1 != null)
			{
				JSONObject localJSONObject2 = localJSONObject1
						.optJSONObject("msg");
				if (localJSONObject2 != null)
				{
					String str3 = localJSONObject2.optString("msgvar",
							"list_empty");
					DEBUG.o(localJSONObject2.optString("msgstr"));
					if ("list_sucess".equalsIgnoreCase(str3))
					{
						com.tianshan.setting.Setting.CLOSE = false;
						JSONObject localJSONObject3 = localJSONObject1
								.optJSONObject("res");
						if (localJSONObject3 != null)
						{
							JSONArray localJSONArray = localJSONObject3
									.optJSONArray("list");
							if ((localJSONArray != null)
									&& (localJSONArray.length() > 0))
							{
								i = localJSONArray.length();
								final AdvertisementHelper localAdvertisementHelper = AdvertisementHelper
										.getInstance(paramContext);
								for (int j = 0; j < i; j++)
								{
									final Advertisement localAdvertisement = new Advertisement(
											localJSONArray.optJSONObject(j));
									final String imgPath = Core
											._getADPath(paramContext)
											+ localAdvertisement.getAdId();
									if ((localAdvertisementHelper
											.get(localAdvertisement.getAdId()) == null)
											&& !"".equals(localAdvertisement
													.getUrl()))
									{
										String str5 = localAdvertisement
												.getUrl();
										HttpRequest.requestCallBack local1 = new HttpRequest.requestCallBack()
										{
											public void download(
													Object paramAnonymousObject)
											{
												try
												{
													InputStream localInputStream = (InputStream) paramAnonymousObject;
													FileOutputStream localFileOutputStream = new FileOutputStream(
															imgPath);
													byte[] arrayOfByte = new byte[1024];
													while (true)
													{
														int i = localInputStream
																.read(arrayOfByte);
														if (i == -1)
														{
															localFileOutputStream
																	.close();
															localAdvertisementHelper
																	.save(localAdvertisement);
															break;
														}
														localFileOutputStream
																.write(arrayOfByte,
																		0, i);
													}
												} catch (FileNotFoundException localFileNotFoundException)
												{
													localFileNotFoundException
															.printStackTrace();
												} catch (IOException localIOException)
												{
													localIOException
															.printStackTrace();
												}
											}
										};
										localHttpRequest._getFile(str5, null,
												null, local1);
									}
								}
							}
						}
					} else
					{
						if ("site_temporarily_closed".equalsIgnoreCase(str3))
							Setting.CLOSE = true;
					}
				}
			}
		} catch (Exception localException)
		{
			localException.printStackTrace();
		}
	}

	public static String getLastADImg(Context paramContext)
	{
		Advertisement localAdvertisement = AdvertisementHelper.getInstance(
				paramContext).getLast(Tools._getTimeStamp() / 1000L);
		String str = null;
		if (localAdvertisement != null)
			str = Core._getADPath(paramContext) + localAdvertisement.getAdId();
		return str;
	}
}