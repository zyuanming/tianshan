package com.tianshan.setting;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import java.util.ArrayList;

public class Setting
{
	public static class AppParam
	{

		public static final String IS_NEED_UPDATE = "is_need_update";
		public static final String LAST_CHECK_SUBNAV_TIME = "last_check_subnav_timestamp";
		public static final String LAST_CHECK_UPDATE_TIME = "last_check_update_timestamp";
		public static final String LAST_LAUNCH_TIME = "last_launch_timestamp";
		public static final String NEWUSER_GUIDE_1 = "guide1";
		public static final String NEWUSER_GUIDE_2 = "guide2";
		public static final String NEWUSER_GUIDE_3 = "guide3";
		public static final String NEWUSER_GUIDE_4 = "guide4";
		public static final String NEWUSER_GUIDE_5 = "guide5";
		public static String UPDATE_INFORMATION = "";
		public static String UPDATE_URL = "";

		public AppParam()
		{}
	}

	public static interface QQ_SHARE
	{

		public static final String APP_KEY = "100273331";
		public static final String APP_SECRET = "85766ddde4ca10226dbc7af5a132a2d0";
		public static final String CALLBACK_URL = "http://client.xjts.cn";
		public static final String GET_OPEN_ID = "https://graph.qq.com/oauth2.0/me";
		public static final String SEND_BLOG = "https://graph.qq.com/t/add_t?oauth_consumer_key=100273331&access_token=${access_token}&openid=${openid}";
		public static final String SEND_QQ_SPACE = "https://graph.qq.com/share/add_share";
		public static final String _fld638867439875 = "https://graph.qq.com/oauth2.0/authorize?response_type=token&client_id=100273331&redirect_uri=http://client.xjts.cn&display=mobile&scope=get_user_info,add_t,add_share";
	}

	public static interface RENREN_SHARE
	{

		public static final String ACCESS_TOKEN = "https://graph.renren.com/oauth/token";
		public static final String APP_KEY = "9494cde4ba4b4b9fb901db5919b845e3";
		public static final String APP_SECRET = "72bc49e5343440c08f66533f6f93935e";
		public static final String CALLBACK_URL = "http://client.xjts.cn";
		public static final String SEND_URL = "http://api.renren.com/restserver.do";
		public static final String _fld638867439875 = "https://graph.renren.com/oauth/authorize?client_id=9494cde4ba4b4b9fb901db5919b845e3&redirect_uri=http://client.xjts.cn&response_type=code&scope=publish_feed";
	}

	public static interface SINA_SHARE
	{

		public static final String ACCESS_TOKEN = "https://api.weibo.com/oauth2/access_token";
		public static final String APP_KEY = "2601366196";
		public static final String APP_SECRET = "ffb1d5854b18e3f75d43d43ff5d926e9";
		public static final String CALLBACK_URL = "http://client.xjts.cn";
		public static final String SEND_BLOG = "https://api.weibo.com/2/statuses/update.json";
		public static final String _fld638867439875 = "https://api.weibo.com/oauth2/authorize?client_id=2601366196&redirect_uri=http://client.xjts.cn&response_type=code&display=wap2.0";
	}

	public static class SharedPreferencesTab
	{

		public static final String SHAREDPREFERENCESS_PUBLIC_NAME = "application_tab";

		public SharedPreferencesTab()
		{}
	}

	public Setting()
	{}

	public static boolean IsCanUseSdCard()
	{
		boolean flag = Environment.getExternalStorageState().equals("mounted");
		return flag;
	}

	/**
	 * 检查是否有网络连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkConn(Context context)
	{
		NetworkInfo networkinfo = ((ConnectivityManager) context
				.getSystemService("connectivity")).getActiveNetworkInfo();
		boolean flag;
		if (networkinfo != null && networkinfo.isConnected())
			flag = true;
		else
			flag = false;
		return flag;
	}

	/**
	 * 检查当前是否处于WiFi网络中
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkWifi(Context context)
	{
		boolean flag = true;
		NetworkInfo anetworkinfo[] = ((ConnectivityManager) context
				.getSystemService("connectivity")).getAllNetworkInfo();
		int i = 0;
		do
		{
			if (i >= anetworkinfo.length)
				return flag;
			if (anetworkinfo[i].getState() == android.net.NetworkInfo.State.CONNECTED)
			{
				if (anetworkinfo[i].getType() == ConnectivityManager.TYPE_MOBILE)
					flag = false;
				if (anetworkinfo[i].getType() == ConnectivityManager.TYPE_WIFI)
					flag = true;
			}
			i++;
		} while (true);
	}

	public static boolean ADDINFO = false;
	public static final String APPNAME = "tianshan";
	public static final String CACHE = "cache/";
	public static final String CHARSET_UTF_8 = "utf-8";
	public static boolean CLEARINFO = false;
	public static boolean CLOSE = false;
	public static final String COOKIEDOMAIN = ".xjts.cn";
	public static final String LOCAL_DOWNLOAD_IMAGE_PATH = "/sdcard/.tianshan/";
	public static String POST_FUD = null;
	public static final String PUSHCLIENTID = "zwapp";
	public static final String PUSHSERVERIP = "222.82.218.48";
	public static final int PUSHSERVERPORT = 1883;
	public static boolean QQ_LOGIN = false;
	public static final String SDCARD_PATH = "/sdcard/tianshan/";
	public static final String SITEDOMAIN = "client.xjts.cn/admin";
	public static boolean SUBMITGPS = false;
	public static int TOP_NEWS_COUNT = 0;
	public static boolean UPDATESUBNAR = false; // 更新二级导航
	public static ArrayList activitylist = new ArrayList();
	public static final String callbackUrl = "http://client.xjts.cn";
	public static String downloadUrl = "http://client.xjts.cn/admin/api.php?ac=download&type=";
	public static String downloadimgUrl = "http://client.xjts.cn/admin/";
	public static final long getPushTime = 60000L;
	public static boolean hasTaskRunning = false;
	public static String imglistUrl = "http://client.xjts.cn/admin/page.php?ac=pic&op=list&cid=";
	public static String newsUrl = "http://client.xjts.cn/admin/page.php?ac=news&op=view&id=";
	public static String newslistUrl = "http://client.xjts.cn/admin/page.php?ac=news&op=list&cid=";
	public static final String siteUrl = "http://client.xjts.cn/admin/";
	public static String topicsSublistUrl = "http://client.xjts.cn/admin/page.php?ac=news&op=special&cid=";
	public static String topicslistUrl = "http://client.xjts.cn/admin/page.php?ac=news&op=specialindex";
	public static String videoUrl = "http://client.xjts.cn/admin/page.php?ac=video&op=view&id=";
	public static String videolistUrl = "http://client.xjts.cn/admin/page.php?ac=video&op=list&cid=";

	static
	{
		TOP_NEWS_COUNT = 0;
		SUBMITGPS = false;
		CLEARINFO = false;
		ADDINFO = false;
		UPDATESUBNAR = false;
		hasTaskRunning = false;
		QQ_LOGIN = false;
		CLOSE = false;
	}
}
// R.id.head 