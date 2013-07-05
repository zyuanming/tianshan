package com.tianshan.source;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import com.ibm.mqtt.IMqttClient;
import com.ibm.mqtt.MqttClient;
import com.ibm.mqtt.MqttException;
import com.ibm.mqtt.MqttPersistence;
import com.ibm.mqtt.MqttPersistenceException;
import com.ibm.mqtt.MqttSimpleCallback;
import com.tianshan.activity.MessageViewActivity;
import com.tianshan.activity.NewsViewActivity;
import com.tianshan.activity.PhotoViewActivity;
import com.tianshan.activity.VideoViewActivity;
import com.tianshan.activity.ViewthreadViewActivity;
import com.tianshan.activity.tab.TabBar;
import com.tianshan.setting.WoPreferences;

public class PushService extends Service
{

	private static final String ACTION_KEEPALIVE;
	private static final String ACTION_RECONNECT;
	private static final String ACTION_START;
	private static final String ACTION_STOP;
	private static final long INITIAL_RETRY_INTERVAL = 10000L;
	private static final long KEEP_ALIVE_INTERVAL = 0x19a280L;
	private static final long MAXIMUM_RETRY_INTERVAL = 0x1b7740L;
	private static int MQTT_BROKER_PORT_NUM = 0;
	private static boolean MQTT_CLEAN_START = false;
	public static String MQTT_CLIENT_ID;
	private static final String MQTT_HOST = "222.82.218.48";
	private static short MQTT_KEEP_ALIVE = 0;
	private static MqttPersistence MQTT_PERSISTENCE = null;
	private static int MQTT_QUALITIES_OF_SERVICE[] = new int[1];
	private static int MQTT_QUALITY_OF_SERVICE = 0;
	private static boolean MQTT_RETAINED_PUBLISH = false;
	private static final int NOTIF_CONNECTED = 0;
	public static String NOTIF_TITLE = "ZhangWo";
	public static final String PREF_DEVICE_ID = "deviceID";
	public static final String PREF_RETRY = "retryInterval";
	public static final String PREF_STARTED = "isStarted";
	public static final String TAG = "PushService";
	private static Context context;
	private String content;
	private int id;
	private ConnectivityManager mConnMan;
	private MQTTConnection mConnection;
	private BroadcastReceiver mConnectivityChanged;
	private NotificationManager mNotifMan;
	private SharedPreferences mPrefs;
	private long mStartTime;
	private boolean mStarted;
	private String type;

	static
	{
		MQTT_BROKER_PORT_NUM = 1883;
		MQTT_CLEAN_START = true;
		MQTT_KEEP_ALIVE = 900;
		MQTT_QUALITY_OF_SERVICE = 0;
		MQTT_RETAINED_PUBLISH = false;
		MQTT_CLIENT_ID = "zwapp";
		ACTION_START = (new StringBuilder(String.valueOf(MQTT_CLIENT_ID)))
				.append(".START").toString();
		ACTION_STOP = (new StringBuilder(String.valueOf(MQTT_CLIENT_ID)))
				.append(".STOP").toString();
		ACTION_KEEPALIVE = (new StringBuilder(String.valueOf(MQTT_CLIENT_ID)))
				.append(".KEEP_ALIVE").toString();
		ACTION_RECONNECT = (new StringBuilder(String.valueOf(MQTT_CLIENT_ID)))
				.append(".RECONNECT").toString();
	}

	private class MQTTConnection implements MqttSimpleCallback
	{

		private void publishToTopic(String s, String s1) throws MqttException
		{
			if (mqttClient == null || !mqttClient.isConnected())
				log("No connection to public to");
			else
				mqttClient.publish(s, s1.getBytes(),
						PushService.MQTT_QUALITY_OF_SERVICE,
						PushService.MQTT_RETAINED_PUBLISH);
		}

		private void subscribeToTopic(String s) throws MqttException
		{
			if (mqttClient == null || !mqttClient.isConnected())
			{
				log("Connection errorNo connection");
			} else
			{
				String as[] = { s };
				mqttClient.subscribe(as, PushService.MQTT_QUALITIES_OF_SERVICE);
			}
		}

		public void connectionLost() throws Exception
		{
			log("Loss of connectionconnection downed");
			stopKeepAlives();
			mConnection = null;
			if (isNetworkAvailable())
				reconnectIfNecessary();
		}

		public void disconnect()
		{
			try
			{
				stopKeepAlives();
				mqttClient.disconnect();
			} catch (MqttPersistenceException mqttpersistenceexception)
			{
				PushService pushservice = PushService.this;
				StringBuilder stringbuilder = new StringBuilder("MqttException");
				String s;
				if (mqttpersistenceexception.getMessage() != null)
					s = mqttpersistenceexception.getMessage();
				else
					s = " NULL";
				pushservice.log(stringbuilder.append(s).toString(),
						mqttpersistenceexception);
			}
		}

		public void publishArrived(String s, byte abyte0[], int i, boolean flag)
		{
			String s1 = new String(abyte0);
			showNote(s1);
			log((new StringBuilder("Got message: ")).append(s1).toString());
		}

		public void sendKeepAlive() throws MqttException
		{
			log("Sending keep alive");
			publishToTopic(
					(new StringBuilder(
							String.valueOf(PushService.MQTT_CLIENT_ID)))
							.append("/keepalive").toString(), mPrefs.getString(
							"deviceID", ""));
		}

		IMqttClient mqttClient;

		public MQTTConnection(String s, String s1) throws MqttException
		{
			mqttClient = null;
			String s2 = (new StringBuilder("tcp://")).append(s).append("@")
					.append(PushService.MQTT_BROKER_PORT_NUM).toString();
			log(s2);
			mqttClient = MqttClient.createMqttClient(s2,
					PushService.MQTT_PERSISTENCE);
			String s3 = (new StringBuilder(
					String.valueOf(PushService.MQTT_CLIENT_ID))).append("/")
					.append(mPrefs.getString("deviceID", "")).toString();
			log(s3);
			mqttClient.connect(s3, PushService.MQTT_CLEAN_START,
					PushService.MQTT_KEEP_ALIVE);
			mqttClient.registerSimpleHandler(this);
			String s4 = (new StringBuilder(
					String.valueOf(PushService.MQTT_CLIENT_ID))).append("/")
					.append(s1).toString();
			subscribeToTopic(s4);
			log((new StringBuilder("Connection established to ")).append(s)
					.append(" on topic ").append(s4).toString());
			mStartTime = System.currentTimeMillis();
			startKeepAlives();
		}
	}

	public PushService()
	{
		type = null;
		content = null;
		id = 0;
		mConnectivityChanged = new BroadcastReceiver()
		{

			public void onReceive(Context context1, Intent intent)
			{
				NetworkInfo networkinfo = (NetworkInfo) intent
						.getParcelableExtra("networkInfo");
				boolean flag;
				if (networkinfo != null && networkinfo.isConnected())
					flag = true;
				else
					flag = false;
				log((new StringBuilder("Connectivity changed: connected="))
						.append(flag).toString());
				if (!flag)
				{
					if (mConnection != null)
					{
						mConnection.disconnect();
						cancelReconnect();
						mConnection = null;
					}
				} else
				{
					reconnectIfNecessary();
				}

			}
		};
	}

	public static void actionPing(Context context1)
	{
		Intent intent = new Intent(context1, PushService.class);
		intent.setAction(ACTION_KEEPALIVE);
		context1.startService(intent);
	}

	/**
	 * 启动指定上下文的PushService服务
	 * 
	 * @param context1
	 *            上下文
	 */
	public static void actionStart(Context context1)
	{
		Intent intent = new Intent(context1, PushService.class);
		intent.setAction(ACTION_START);
		context1.startService(intent);
	}

	/**
	 * 关闭指定上下文的PushService服务
	 * 
	 * @param context1
	 *            上下文
	 */
	public static void actionStop(Context context1)
	{
		Intent intent = new Intent(context1, PushService.class);
		intent.setAction(ACTION_STOP);
		context1.startService(intent);
	}

	private void connect()
	{
		log("Connecting...");
		String s = mPrefs.getString("deviceID", null);
		if (s != null)
		{
			try
			{
				mConnection = new MQTTConnection("222.82.218.48", s);
				setStarted(true);
			} catch (MqttException mqttexception)
			{
				StringBuilder stringbuilder = new StringBuilder(
						"MqttException: ");
				String s1;
				if (mqttexception.getMessage() != null)
					s1 = mqttexception.getMessage();
				else
					s1 = "NULL";
				log(stringbuilder.append(s1).toString());
				if (isNetworkAvailable())
					scheduleReconnect(mStartTime);
			}
		} else
		{
			log("Device ID not found.");
		}
	}

	public static Context getContext()
	{
		return context;
	}

	private void handleCrashedService()
	{
		if (wasStarted())
		{
			log("Handling crashed service...");
			stopKeepAlives();
			start();
		}
	}

	private boolean isNetworkAvailable()
	{
		NetworkInfo networkinfo = mConnMan.getActiveNetworkInfo();
		boolean flag;
		if (networkinfo == null)
			flag = false;
		else
			flag = networkinfo.isConnected();
		return flag;
	}

	private void keepAlive()
	{
		if (mStarted && mConnection != null)
		{
			try
			{
				mConnection.sendKeepAlive();
			} catch (MqttException mqttexception)
			{
				StringBuilder stringbuilder = new StringBuilder(
						"MqttException: ");
				String s;
				if (mqttexception.getMessage() != null)
				{
					s = mqttexception.getMessage();
				} else
				{
					s = "NULL";
				}
				log(stringbuilder.append(s).toString(), mqttexception);
				mConnection.disconnect();
				mConnection = null;
				cancelReconnect();
			}
		}
	}

	private void log(String s)
	{
		log(s, null);
	}

	private void log(String s, Throwable throwable)
	{
		if (throwable != null)
			Log.e("PushService", s, throwable);
		else
			Log.i("PushService", s);
	}

	private void reconnectIfNecessary()
	{
		if (mStarted && mConnection == null)
		{
			log("Reconnecting...");
			try
			{
				connect();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void setContext(Context context1)
	{
		context = context1;
	}

	private void setStarted(boolean flag)
	{
		mPrefs.edit().putBoolean("isStarted", flag).commit();
		mStarted = flag;
	}

	private void showNote(String s)
	{
		NotificationManager notificationmanager;
		Notification notification;
		Intent intent;
		JSONObject jsonobject;
		try
		{
			jsonobject = new JSONObject(s);
			WoPreferences.setShowPushMsg(jsonobject.optInt("pushid"));
			WoPreferences.setShowPushMsgType(jsonobject.optString("type"));
			DEBUG.o(Integer.valueOf(WoPreferences.getShowPushMsg()));
			DEBUG.o(WoPreferences.getShowPushMsgType());
			type = jsonobject.optString("type");
			if ("pm".equals(type))
			{
				String s1 = jsonobject.optString("tousername");
				int i = jsonobject.optInt("msgtoid");
				String s2 = jsonobject.optString("topmid");
				String s3 = jsonobject.optString("message");
				NotificationManager notificationmanager1 = (NotificationManager) context
						.getSystemService("notification");
				Notification notification1 = new Notification(0x7f020001,
						"\u638C\u63E1\u77ED\u6D88\u606F\u63D0\u9192",
						System.currentTimeMillis());
				Intent intent1 = new Intent();
				intent1.setClass(context, MessageViewActivity.class);
				intent1.putExtra("pmid", s2);
				intent1.putExtra("tousername", s1);
				intent1.putExtra("from", "pushpm");
				String as4[] = new String[1];
				as4[0] = (new StringBuilder("ac=mypm&op=view&id=")).append(i)
						.append("&name=").append(s1).toString();
				intent1.putExtra("url", SiteTools.getMobileUrl(as4));
				intent1.setFlags(0x400000);
				PendingIntent pendingintent1 = PendingIntent.getActivity(
						context, 0, intent1, 0x8000000);
				notification1.flags = 16;
				notification1.setLatestEventInfo(context, (new StringBuilder(
						String.valueOf(s1)))
						.append(" \u5BF9\u60A8\u8BF4\uFF1A").toString(), s3,
						pendingintent1);
				notificationmanager1.notify(0, notification1);
			}
			if (id != WoPreferences.getShowPushMsg()
					|| !type.equals(WoPreferences.getShowPushMsgType()))
			{}
			id = jsonobject.optInt("pushid");
			content = jsonobject.optString("content");
			notificationmanager = (NotificationManager) context
					.getSystemService("notification");
			notification = new Notification(0x7f020001,
					"\u60A8\u6709\u65B0\u6D88\u606F",
					System.currentTimeMillis());
			intent = new Intent();
			if (!"news".equals(type))
			{

				if ("topics".equals(type))
				{
					intent.setClass(context, TabBar.class);
					intent.putExtra("fupval", 2);
					intent.putExtra("view_id", 0);
					intent.putExtra("subnav", 1);
					intent.putExtra("from", "pushmsg");
					String as2[] = new String[1];
					as2[0] = (new StringBuilder("ac=news&op=special&cid="))
							.append(id).toString();
					intent.putExtra("url", SiteTools.getSiteUrl(as2));
				} else if ("video".equals(type))
				{
					intent.setClass(context, VideoViewActivity.class);
					intent.putExtra("params", (new StringBuilder("{\"id\":"))
							.append(id).append("}").toString());
					intent.putExtra("from", "pushmsg");
					String as1[] = new String[1];
					as1[0] = (new StringBuilder("ac=video&op=list&cid="))
							.append(id).toString();
					intent.putExtra("url", SiteTools.getSiteUrl(as1));
				} else if ("pic".equals(type))
				{
					intent.setClass(context, PhotoViewActivity.class);
					intent.putExtra("from", "album");
					intent.putExtra("album_id",
							(new StringBuilder(String.valueOf(id))).toString());
				} else if ("thread".equals(type))
				{
					intent.setClass(context, ViewthreadViewActivity.class);
					intent.putExtra("params", (new StringBuilder("{\"tid\":"))
							.append(id).append("}").toString());
					intent.putExtra("from", "pushmsg");
					String as[] = new String[1];
					as[0] = (new StringBuilder("ac=viewthread&tid="))
							.append(id).toString();
					intent.putExtra("url", SiteTools.getMobileUrl(as));
				}
			} else
			{
				intent.setClass(context, NewsViewActivity.class);
				intent.putExtra("params", (new StringBuilder("{\"id\":"))
						.append(id).append("}").toString());
				intent.putExtra("from", "pushmsg");
				String as3[] = new String[1];
				as3[0] = (new StringBuilder("ac=news&op=view&id=")).append(id)
						.toString();
				intent.putExtra("url", SiteTools.getSiteUrl(as3));
			}
			PendingIntent pendingintent = PendingIntent.getActivity(context, 0,
					intent, 0x8000000);
			notification.flags = 16;
			notification.setLatestEventInfo(context,
					"\u638C\u63E1\u65B0\u95FB\u63A8\u9001", content,
					pendingintent);
			notificationmanager.notify(0, notification);
		} catch (JSONException jsonexception)
		{
			jsonexception.printStackTrace();
		}
	}

	private void showNotification(String s)
	{
		Notification notification = new Notification();
		notification.flags = 1 | notification.flags;
		notification.flags = 0x10 | notification.flags;
		notification.defaults = -1;
		notification.icon = 0x7f020001;
		notification.when = System.currentTimeMillis();
		PendingIntent pendingintent = PendingIntent.getActivity(this, 0,
				new Intent(this, MessageViewActivity.class), 0);
		notification.setLatestEventInfo(this, NOTIF_TITLE, s, pendingintent);
		mNotifMan.notify(0, notification);
	}

	private void start()
	{
		log("Starting service...");
		if (mStarted)
		{
			Log.w("PushService",
					"Attempt to start connection that is already active");

			try
			{
				connect();
				registerReceiver(mConnectivityChanged, new IntentFilter(
						"android.net.conn.CONNECTIVITY_CHANGE"));
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void startKeepAlives()
	{
		Intent intent = new Intent();
		intent.setClass(this, PushService.class);
		intent.setAction(ACTION_KEEPALIVE);
		PendingIntent pendingintent = PendingIntent.getService(this, 0, intent,
				0);
		((AlarmManager) getSystemService("alarm")).setRepeating(0,
				0x19a280L + System.currentTimeMillis(), 0x19a280L,
				pendingintent);
	}

	private void stop()
	{
		if (!mStarted)
		{
			Log.w("PushService", "Attempt to stop connection not active.");
			setStarted(false);
			try
			{
				unregisterReceiver(mConnectivityChanged);
				cancelReconnect();
				if (mConnection != null)
				{
					mConnection.disconnect();
					mConnection = null;
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	private void stopKeepAlives()
	{
		Intent intent = new Intent();
		intent.setClass(this, PushService.class);
		intent.setAction(ACTION_KEEPALIVE);
		PendingIntent pendingintent = PendingIntent.getService(this, 0, intent,
				0);
		((AlarmManager) getSystemService("alarm")).cancel(pendingintent);
	}

	private boolean wasStarted()
	{
		return mPrefs.getBoolean("isStarted", false);
	}

	public void cancelReconnect()
	{
		Intent intent = new Intent();
		intent.setClass(this, PushService.class);
		intent.setAction(ACTION_RECONNECT);
		PendingIntent pendingintent = PendingIntent.getService(this, 0, intent,
				0);
		((AlarmManager) getSystemService("alarm")).cancel(pendingintent);
	}

	public IBinder onBind(Intent intent)
	{
		return null;
	}

	public void onCreate()
	{
		super.onCreate();
		log("Creating service");
		mStartTime = System.currentTimeMillis();
		mPrefs = getSharedPreferences("PushService", 0);
		mConnMan = (ConnectivityManager) getSystemService("connectivity");
		mNotifMan = (NotificationManager) getSystemService("notification");
		handleCrashedService();
	}

	public void onDestroy()
	{
		log((new StringBuilder("Service destroyed (started=")).append(mStarted)
				.append(")").toString());
		if (mStarted)
			stop();
	}

	public void onStart(Intent intent, int i)
	{
		super.onStart(intent, i);
		log((new StringBuilder("Service started with intent=")).append(intent)
				.toString());
		if (intent.getAction().equals(ACTION_STOP))
		{
			stop();
			stopSelf();
		}
		if (intent.getAction().equals(ACTION_START))
			start();
		else if (intent.getAction().equals(ACTION_KEEPALIVE))
			keepAlive();
		else if (intent.getAction().equals(ACTION_RECONNECT)
				&& isNetworkAvailable())
			reconnectIfNecessary();
	}

	public void scheduleReconnect(long l)
	{
		long l1 = mPrefs.getLong("retryInterval", 10000L);
		long l2 = System.currentTimeMillis();
		long l3;
		Intent intent;
		PendingIntent pendingintent;
		if (l2 - l < l1)
			l3 = Math.min(4L * l1, 0x1b7740L);
		else
			l3 = 10000L;
		log((new StringBuilder("Rescheduling connection in ")).append(l3)
				.append("ms.").toString());
		mPrefs.edit().putLong("retryInterval", l3).commit();
		intent = new Intent();
		intent.setClass(this, PushService.class);
		intent.setAction(ACTION_RECONNECT);
		pendingintent = PendingIntent.getService(this, 0, intent, 0);
		((AlarmManager) getSystemService("alarm")).set(0, l2 + l3,
				pendingintent);
	}
}
// R.id.head 