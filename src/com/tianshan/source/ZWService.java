package com.tianshan.source;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.tianshan.R;
import com.tianshan.ZhangWoApp;
import com.tianshan.activity.MessageViewActivity;
import com.tianshan.activity.NewsViewActivity;
import com.tianshan.activity.PhotoViewActivity;
import com.tianshan.activity.VideoViewActivity;
import com.tianshan.activity.ViewthreadViewActivity;
import com.tianshan.activity.tab.TabBar;
import com.tianshan.setting.WoPreferences;

public class ZWService extends Service
{
	private static final int CLOSE_PROGRESS = 1;
	// private static final int LOAD_PROGRESS;
	private static Context context;
	public static int isShow = 1;
	private String content = null;
	private Handler iHandler;
	private int id = 0;
	private int mTimerId = 0;
	public SharedPreferences preferences;
	private Timer timer = null;
	private TimerTask timerTask = null;
	private String type = null;

	private void addMessageHandler()
	{
		this.iHandler = new Handler()
		{
			public void handleMessage(Message paramAnonymousMessage)
			{
				switch (paramAnonymousMessage.what)
				{
				default:
					super.handleMessage(paramAnonymousMessage);
					return;
				case 0:
					ConnectivityManager localConnectivityManager = (ConnectivityManager) ZWService.this
							.getSystemService("connectivity");
					if ((localConnectivityManager.getActiveNetworkInfo() != null)
							&& (localConnectivityManager.getActiveNetworkInfo()
									.isConnected()))
					{
						ZWService.this.showNote(paramAnonymousMessage.arg1);
						if (ZhangWoApp.getInstance().isLogin())
							ZWService.this.getPm(paramAnonymousMessage.arg1);
					}
					break;
				}
			}
		};
	}

	private void closeTimer()
	{
		if (this.timer != null)
		{
			this.timer.cancel();
			this.timer = null;
		}
		if (this.timerTask != null)
			this.timerTask = null;
		this.mTimerId = 0;
		this.iHandler.sendEmptyMessage(1);
	}

	public static Context getContext()
	{
		return context;
	}

	private void getPm(int paramInt)
	{
		try
		{
			String str1 = HttpsRequest.openUrl(SiteTools
					.getMobileUrl(new String[] { "ac=mypm&op=pushpm" }), "GET",
					null, null, null, ZhangWoApp.getInstance().getUserSession()
							.getWebViewCookies());
			DEBUG.o(str1);
			JSONObject localJSONObject1 = new JSONObject(str1);
			if ((str1 != null) && (localJSONObject1 != null))
			{
				JSONObject localJSONObject2 = localJSONObject1
						.optJSONObject("res");
				DEBUG.o(localJSONObject2);
				JSONArray localJSONArray = localJSONObject2.optJSONObject(
						"list").optJSONArray("list");
				DEBUG.o(localJSONArray);
				int i = localJSONArray.length();
				boolean flag = false;
				String str2 = null;
				int k = 0;
				String str3 = null;
				if (localJSONArray.length() > 0)
					for (int m = 0;; m++)
					{
						if (m >= i)
						{
							if (flag)
							{
								NotificationManager localNotificationManager = (NotificationManager) context
										.getSystemService("notification");
								Notification localNotification = new Notification(
										R.drawable.app_icon, "掌握短消息提示",
										System.currentTimeMillis());
								Intent localIntent = new Intent();
								localIntent.setClass(context,
										MessageViewActivity.class);
								localIntent.putExtra("pmid", str3);
								localIntent.putExtra("tousername", str2);
								localIntent.putExtra("from", "pushpm");
								String[] arrayOfString = new String[1];
								arrayOfString[0] = ("ac=mypm&op=view&id=" + k
										+ "&name=" + str2);
								localIntent.putExtra("url",
										SiteTools.getMobileUrl(arrayOfString));
								localIntent.setFlags(4194304);
								PendingIntent localPendingIntent = PendingIntent
										.getActivity(context, paramInt,
												localIntent, 134217728);
								localNotification.flags = 16;
								localNotification.number = i;
								localNotification.setLatestEventInfo(context,
										str2 + " 对您说：", this.content,
										localPendingIntent);
								localNotificationManager.notify(1,
										localNotification);
							}
						}
						JSONObject localJSONObject3 = (JSONObject) localJSONArray
								.get(m);
						if (localJSONObject3.optInt("isnew") == 1)
						{
							flag = true;
							str2 = localJSONObject3.optString("tousername");
							k = localJSONObject3.optInt("msgtoid");
							str3 = localJSONObject3.optString("topmid");
							this.content = localJSONObject3
									.optString("message");
						}
					}
			}
		} catch (Exception localException)
		{
			localException.printStackTrace();
		}
	}

	public static void setContext(Context paramContext)
	{
		context = paramContext;
	}

	private void showNote(int paramInt)
	{
		Intent localIntent;
		try
		{
			String str = SiteTools.getApiUrl(new String[] { "ac=announce" });
			JSONObject localJSONObject1 = new JSONObject(
					new HttpRequest()._get(str));
			if (localJSONObject1 != null)
			{
				JSONObject localJSONObject2 = localJSONObject1
						.optJSONObject("res");
				JSONObject localJSONObject3 = localJSONObject1
						.optJSONObject("msg");
				if (localJSONObject2 != null)
				{
					JSONObject localJSONObject4 = localJSONObject2
							.optJSONObject("list");
					if (!"list_empty".equals(localJSONObject3
							.optString("msgvar")))
					{
						WoPreferences.setShowPushMsg(localJSONObject4
								.optInt("pushid"));
						WoPreferences.setShowPushMsgType(localJSONObject4
								.optString("type"));
						DEBUG.o(Integer.valueOf(WoPreferences.getShowPushMsg()));
						DEBUG.o(WoPreferences.getShowPushMsgType());
						if ((this.id != WoPreferences.getShowPushMsg())
								|| (!this.type.equals(WoPreferences
										.getShowPushMsgType())))
						{
							this.id = localJSONObject4.optInt("pushid");
							this.content = localJSONObject4
									.optString("content");
							this.type = localJSONObject4.optString("type");
							NotificationManager localNotificationManager = (NotificationManager) context
									.getSystemService("notification");
							Notification localNotification = new Notification(
									R.drawable.app_icon, "您有新消息",
									System.currentTimeMillis());
							localIntent = new Intent();
							if ("news".equals(this.type))
							{
								localIntent.setClass(context,
										NewsViewActivity.class);
								localIntent.putExtra("params", "{\"id\":"
										+ this.id + "}");
								localIntent.putExtra("from", "pushmsg");
								String[] arrayOfString4 = new String[1];
								arrayOfString4[0] = ("ac=news&op=view&id=" + this.id);
								localIntent.putExtra("url",
										SiteTools.getSiteUrl(arrayOfString4));
							} else if ("topics".equals(this.type))
							{
								localIntent.setClass(context, TabBar.class);
								localIntent.putExtra("fupval", 2);
								localIntent.putExtra("view_id", 0);
								localIntent.putExtra("subnav", 1);
								localIntent.putExtra("from", "pushmsg");
								String[] arrayOfString3 = new String[1];
								arrayOfString3[0] = ("ac=news&op=special&cid=" + this.id);
								localIntent.putExtra("url",
										SiteTools.getSiteUrl(arrayOfString3));
							} else if ("video".equals(this.type))
							{
								localIntent.setClass(context,
										VideoViewActivity.class);
								localIntent.putExtra("params", "{\"id\":"
										+ this.id + "}");
								localIntent.putExtra("from", "pushmsg");
								String[] arrayOfString2 = new String[1];
								arrayOfString2[0] = ("ac=video&op=list&cid=" + this.id);
								localIntent.putExtra("url",
										SiteTools.getSiteUrl(arrayOfString2));
							} else if ("pic".equals(this.type))
							{
								localIntent.setClass(context,
										PhotoViewActivity.class);
								localIntent.putExtra("from", "album");
								localIntent.putExtra("album_id", this.id);
							} else if ("thread".equals(this.type))
							{
								localIntent.setClass(context,
										ViewthreadViewActivity.class);
								localIntent.putExtra("params", "{\"tid\":"
										+ this.id + "}");
								localIntent.putExtra("from", "pushmsg");
								String[] arrayOfString1 = new String[1];
								arrayOfString1[0] = ("ac=viewthread&tid=" + this.id);
								localIntent.putExtra("url",
										SiteTools.getMobileUrl(arrayOfString1));
							}
							PendingIntent localPendingIntent = PendingIntent
									.getActivity(context, paramInt,
											localIntent, 134217728);
							localNotification.flags = 16;
							localNotification.setLatestEventInfo(context,
									"掌握新闻推送", this.content, localPendingIntent);
							localNotificationManager.notify(0,
									localNotification);
						}
					}
				}
			}
		} catch (Exception localException)
		{
			localException.printStackTrace();
		}
	}

	private void startTimer()
	{
		if (this.timerTask == null)
		{
			this.timerTask = new TimerTask()
			{
				public void run()
				{
					Message localMessage = new Message();
					localMessage.what = 0;
					ZWService localZWService = ZWService.this;
					int i = 1 + localZWService.mTimerId;
					localZWService.mTimerId = i;
					localMessage.arg1 = i;
					if (WoPreferences.queryAutoPushMsg() == 0)
					{
						ZWService.this.iHandler.sendMessage(localMessage);
						DEBUG.o("getMessage " + ZWService.this.mTimerId);
					} else
					{
						DEBUG.o("getMessage closed ");
					}
				}
			};
			this.timer = new Timer();
			this.timer.schedule(this.timerTask, 0L, 60000L);
		}
	}

	public IBinder onBind(Intent paramIntent)
	{
		return null;
	}

	public void onCreate()
	{
		super.onCreate();
		Log.v("MsgService", "MsgService is run");
		DEBUG.o("获取新推送消息");
		addMessageHandler();
		startTimer();
	}

	public void onDestroy()
	{
		super.onDestroy();
		closeTimer();
		Log.v("MsgService", "on destroy");
	}
}
