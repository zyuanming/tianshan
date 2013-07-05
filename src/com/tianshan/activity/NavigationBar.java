package com.tianshan.activity;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianshan.R;
import com.tianshan.ZhangWoApp;
import com.tianshan.activity.tab.TabBar;
import com.tianshan.dbhelper.UserSessionDBHelper;
import com.tianshan.model.UserSession;
import com.tianshan.setting.Setting;
import com.tianshan.source.AD;
import com.tianshan.source.Core;
import com.tianshan.source.DEBUG;
import com.tianshan.source.GPSUtil;
import com.tianshan.source.HttpRequest;
import com.tianshan.source.PushService;
import com.tianshan.source.ShowMessage;
import com.tianshan.source.SiteTools;
import com.tianshan.source.Subnav;
import com.tianshan.source.Tools;
import com.tianshan.source.UpgradeAPK;
import com.tianshan.source.activity.BaseActivity;
import com.tianshan.task.CheckUpdateBaseAsyncTask;
import com.tianshan.task.SendGPSTask;

public class NavigationBar extends BaseActivity implements
		View.OnClickListener, View.OnTouchListener
{
	private View download_btn;
	private View express_btn;
	private View fav_btn;
	private View forum_btn;
	private boolean frame = false;
	private String gps;
	private Handler handler = new Handler(new Handler.Callback()
	{
		public boolean handleMessage(Message paramAnonymousMessage)
		{
			if (paramAnonymousMessage.what == 1)
				sendgps();
			else
			{
				new AlertDialog.Builder(NavigationBar.this)
						.setTitle("站点暂时关闭")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener()
								{
									public void onClick(
											DialogInterface paramAnonymous2DialogInterface,
											int paramAnonymous2Int)
									{
										finish();
									}
								}).create().show();
			}
			return false;
		}
	});
	private double[] loc;
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mWindowParams;
	private View near_btn;
	private View news_btn;
	private View photo_btn;
	private View profile_btn;
	private CheckUpdateBaseAsyncTask.CheckTaskResultListener sendgpsListener = new CheckUpdateBaseAsyncTask.CheckTaskResultListener()
	{
		public void onTaskResult(boolean paramAnonymousBoolean,
				HashMap<String, Object> paramAnonymousHashMap)
		{
			String str;
			if (paramAnonymousBoolean)
			{
				str = (String) paramAnonymousHashMap.get("data");
				Log.d("result", "*****data**** =" + str);
				if ((str != null) || !(str.equals("")))
				{
					try
					{
						JSONObject localJSONObject1 = new JSONObject(str);
						if (localJSONObject1 != null)
						{
							JSONObject localJSONObject2 = localJSONObject1
									.optJSONObject("msg");
							if (localJSONObject2 != null)
								localJSONObject2.optString("msgvar",
										"list_empty").equals("send_success");
						}
					} catch (JSONException localJSONException)
					{
						localJSONException.printStackTrace();
					}
				}
			}

		}
	};
	private View setting_btn;
	private View topic_btn;
	private UpgradeAPK upgrader;
	private View vedio_btn;

	private int _getTopNewsCount(long paramLong)
	{
		HttpRequest localHttpRequest = new HttpRequest();
		int i = 0;
		try
		{
			DEBUG.o("*****check top news count****");
			String[] arrayOfString = new String[2];
			arrayOfString[0] = "ac=home";
			arrayOfString[1] = ("time=" + paramLong);
			String str = localHttpRequest._get(SiteTools
					.getApiUrl(arrayOfString));
			if (str != null)
			{
				JSONObject localJSONObject1 = new JSONObject(str);
				if (localJSONObject1 != null)
				{
					JSONObject localJSONObject2 = localJSONObject1
							.optJSONObject("res").optJSONObject("list");
					if (localJSONObject2 != null)
					{
						int j = localJSONObject2.optInt("count");
						i = j;
					}
				}
			}
		} catch (Exception localException)
		{
			localException.printStackTrace();
		}
		return i;
	}

	private void popupLoginDialog()
	{
		new AlertDialog.Builder(this)
				.setTitle(R.string.message_notice_nologin)
				.setPositiveButton(R.string.nav_btn_login,
						new DialogInterface.OnClickListener()
						{
							public void onClick(
									DialogInterface paramAnonymousDialogInterface,
									int paramAnonymousInt)
							{
								Intent localIntent = new Intent(
										NavigationBar.this, LoginActivity.class);
								startActivity(localIntent);
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener()
						{
							public void onClick(
									DialogInterface paramAnonymousDialogInterface,
									int paramAnonymousInt)
							{
								paramAnonymousDialogInterface.dismiss();
							}
						}).create().show();
	}

	/**
	 * 弹出更新对话框
	 */
	private void popupUpdateDialog()
	{
		new AlertDialog.Builder(this)
				.setTitle(R.string.message_system_update)
				.setMessage(Setting.AppParam.UPDATE_INFORMATION)
				.setPositiveButton(R.string.message_update_app_ok,
						new DialogInterface.OnClickListener()
						{
							public void onClick(
									DialogInterface paramAnonymousDialogInterface,
									int paramAnonymousInt)
							{
								upgrader.downLoad(NavigationBar.this,
										Setting.AppParam.UPDATE_URL);
								paramAnonymousDialogInterface.dismiss();
							}
						})
				.setNegativeButton(R.string.message_update_app_no,
						new DialogInterface.OnClickListener()
						{
							public void onClick(
									DialogInterface paramAnonymousDialogInterface,
									int paramAnonymousInt)
							{
								paramAnonymousDialogInterface.dismiss();
							}
						}).create().show();
		upgrader.resetUpdateTag();
	}

	/**
	 * 发送手机GPS位置信息
	 */
	private void sendgps()
	{
		GPSUtil localGPSUtil = new GPSUtil(getApplicationContext());
		this.loc = localGPSUtil.setLatitudeAndLongitude();
		if (this.loc == null)
			this.loc = localGPSUtil.setLatitudeAndLongitude();
		if (this.loc != null)
			this.gps = (this.loc[0] + "," + this.loc[1]);
		String str = SiteTools
				.getMobileUrl(new String[] { "ac=lbs", "type=xy" })
				+ "&xy="
				+ this.gps;
		SendGPSTask localSendGPSTask = new SendGPSTask(getApplicationContext(),
				this.sendgpsListener, false);
		String[] arrayOfString = new String[2];
		arrayOfString[0] = str;
		arrayOfString[1] = this.gps;
		localSendGPSTask.execute(arrayOfString);
	}

	private void setBtnclickable(boolean paramBoolean)
	{
		if (paramBoolean)
		{
			this.topic_btn.setClickable(true);
			this.topic_btn.setOnTouchListener(this);
			this.vedio_btn.setClickable(true);
			this.vedio_btn.setOnTouchListener(this);
			this.photo_btn.setClickable(true);
			this.photo_btn.setOnTouchListener(this);
			this.forum_btn.setClickable(true);
			this.forum_btn.setOnTouchListener(this);
			this.news_btn.setClickable(true);
			this.news_btn.setOnTouchListener(this);
			this.express_btn.setClickable(true);
			this.express_btn.setOnTouchListener(this);
			this.near_btn.setClickable(true);
			this.near_btn.setOnTouchListener(this);
			this.setting_btn.setClickable(true);
			this.profile_btn.setClickable(true);
			this.download_btn.setClickable(true);
			this.fav_btn.setClickable(true);
		} else
		{
			if (!paramBoolean)
			{
				this.topic_btn.setClickable(false);
				this.topic_btn.setOnTouchListener(null);
				this.vedio_btn.setClickable(false);
				this.vedio_btn.setOnTouchListener(null);
				this.photo_btn.setClickable(false);
				this.photo_btn.setOnTouchListener(null);
				this.forum_btn.setClickable(false);
				this.forum_btn.setOnTouchListener(null);
				this.news_btn.setClickable(false);
				this.news_btn.setOnTouchListener(null);
				this.express_btn.setClickable(false);
				this.express_btn.setOnTouchListener(null);
				this.near_btn.setClickable(false);
				this.near_btn.setOnTouchListener(null);
				this.setting_btn.setClickable(false);
				this.profile_btn.setClickable(false);
				this.download_btn.setClickable(false);
				this.fav_btn.setClickable(false);
			}
		}
	}

	private void setFrame()
	{
		frame = true;
		mWindowManager = ((WindowManager) getSystemService("window"));
		ImageView localImageView = new ImageView(getApplicationContext());
		localImageView.setScaleType(ImageView.ScaleType.FIT_XY);
		if (mWindowManager.getDefaultDisplay().getHeight() == 854) // 获取屏幕高的密度
			localImageView.setImageResource(R.drawable.nav_navigation854);
		// 根据手机屏幕的长度不同，设置大小不同的介绍页面
		if (mWindowManager.getDefaultDisplay().getHeight() == 800)
			localImageView.setImageResource(R.drawable.nav_navigation800);
		else if (mWindowManager.getDefaultDisplay().getHeight() == 480)
			localImageView.setImageResource(R.drawable.nav_navigation480);
		localImageView.bringToFront();
		mWindowParams = new WindowManager.LayoutParams();
		mWindowParams.gravity = 17;
		new Core(getApplicationContext());
		mWindowParams.height = mWindowManager.getDefaultDisplay().getHeight();
		mWindowParams.width = mWindowManager.getDefaultDisplay().getWidth();
		mWindowParams.flags = 392;
		mWindowParams.format = PixelFormat.TRANSLUCENT; // 支持半透明
		mWindowParams.windowAnimations = 0;
		mWindowManager.addView(localImageView, mWindowParams);
		setBtnclickable(false);
		localImageView.setOnTouchListener(this);
	}

	protected void initBtnsEvent(View[] av)
	{
		for (int i = 0; i < av.length; i++)
		{
			av[i].setClickable(true);
			av[i].setOnClickListener(this);
			av[i].setOnTouchListener(this);
		}
	}

	/**
	 * 得到主界面的各个控件
	 */
	protected void initWidget()
	{
		super.initWidget();
		this.topic_btn = findViewById(R.id.topic_btn);
		this.vedio_btn = findViewById(R.id.vedio_btn);
		this.photo_btn = findViewById(R.id.photo_btn);
		this.forum_btn = findViewById(R.id.forum_btn);
		this.news_btn = findViewById(R.id.news_btn);
		this.express_btn = findViewById(R.id.express_btn);
		this.near_btn = findViewById(R.id.near_btn);
		View[] arrayOfView = new View[7];
		arrayOfView[0] = this.topic_btn;
		arrayOfView[1] = this.vedio_btn;
		arrayOfView[2] = this.photo_btn;
		arrayOfView[3] = this.forum_btn;
		arrayOfView[4] = this.news_btn;
		arrayOfView[5] = this.express_btn;
		arrayOfView[6] = this.near_btn;
		initBtnsEvent(arrayOfView);
		this.setting_btn = findViewById(R.id.setting_btn);
		this.setting_btn.setOnClickListener(this);
		this.profile_btn = findViewById(R.id.profile_btn);
		this.profile_btn.setOnClickListener(this);
		this.download_btn = findViewById(R.id.download_btn);
		this.download_btn.setOnClickListener(this);
		this.fav_btn = findViewById(R.id.fav_btn);
		this.fav_btn.setOnClickListener(this);
		((TextView) findViewById(R.id.btn_notice)).setText((new StringBuilder(
				String.valueOf(Setting.TOP_NEWS_COUNT))).toString());
	}

	/**
	 * 主界面各个按钮的点击事件
	 */
	public void onClick(View paramView)
	{
		if (!this.preferences.getBoolean("updatasubnav", false))
			ShowMessage.getInstance(this)._showToast("更新二级导航失败，请刷新网络", 2);
		else
		{
			if (Setting.UPDATESUBNAR)
			{
				ShowMessage.getInstance(this)._showToast("正在更新二级导航，请稍等", 2);
			} else
			{
				int i = paramView.getId();
				if (i == R.id.news_btn) // 新闻
				{
					Intent localIntent1 = new Intent();
					localIntent1.setClass(this, TabBar.class);
					localIntent1.putExtra("fupval", 2);
					localIntent1.putExtra("subnav", 0);
					startActivity(localIntent1);
				} else
				{
					if (i == R.id.vedio_btn) // 视频
					{
						Intent localIntent2 = new Intent();
						localIntent2.setClass(this, TabBar.class);
						localIntent2.putExtra("fupval", 3);
						localIntent2.putExtra("subnav", 0);
						startActivity(localIntent2);
					}
					if (i == R.id.topic_btn) // 专题
					{
						Intent localIntent10 = new Intent();
						localIntent10.setClass(this, TabBar.class);
						localIntent10.putExtra("fupval", 2);
						localIntent10.putExtra("subnav", 1);
						startActivity(localIntent10);
					}
				}
				if (i == R.id.photo_btn) // 图片
				{
					Intent localIntent3 = new Intent();
					localIntent3.setClass(this, TabBar.class);
					localIntent3.putExtra("fupval", 4);
					localIntent3.putExtra("subnav", 0);
					startActivity(localIntent3);
				} else if (i == R.id.forum_btn) // 论坛
				{
					Intent localIntent4 = new Intent();
					localIntent4.setClass(this, TabBar.class);
					localIntent4.putExtra("fupval", -1);
					localIntent4.putExtra("subnav", 0);
					startActivity(localIntent4);
				} else if (i == R.id.express_btn) // 投稿
				{
					Intent localIntent5 = new Intent();
					localIntent5.setClass(this, TabBar.class);
					localIntent5.putExtra("fupval", 5);
					startActivity(localIntent5);
				} else if (i == R.id.setting_btn) // 设置
				{
					Intent localIntent6 = new Intent();
					localIntent6.setClass(this, SettingActivity.class);
					localIntent6.putExtra("fupval", 1);
					startActivity(localIntent6);
				} else if (i == R.id.profile_btn) // 个人帐号
				{
					Intent localIntent7 = new Intent();
					if (ZhangWoApp.getInstance().isLogin())
					{
						localIntent7.setClass(this, UserProfileActivity.class);
						startActivity(localIntent7);
					} else
					{
						localIntent7.setClass(this, LoginActivity.class);
						startActivity(localIntent7);
					}
				} else if (i == R.id.download_btn) // 离线下载
				{
					startActivity(new Intent(this, DownloadAllMsg.class));
				} else if (i == R.id.near_btn) // 身边
				{
					if (ZhangWoApp.getInstance().isLogin())
					{
						Intent localIntent9 = new Intent(this, NearbyInfo.class);
						localIntent9.putExtra("type", "");
						startActivity(localIntent9);
					} else
					{
						popupLoginDialog();
					}
				} else if (i == R.id.fav_btn) // 收藏
				{
					if (ZhangWoApp.getInstance().isLogin())
					{
						Intent localIntent8 = new Intent();
						localIntent8.setClass(this, MyFavoriteActivity.class);
						localIntent8.putExtra("MyFavorite", 1);
						startActivity(localIntent8);
					} else
					{
						popupLoginDialog();
					}
				}
			}
		}
	}

	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		setContentView(R.layout.navigation);
		initWidget();
		this.preferences = getSharedPreferences("application_tab", 0);
		if (this.preferences.getBoolean("guide1", false))
			setFrame();
		DEBUG.o("NavigationBar Init");
		DEBUG.o("******4******popupUpdateDialog");
		this.upgrader = new UpgradeAPK(this.preferences, this.core);
		if (this.upgrader.isNeedUpdate())
			popupUpdateDialog();
		DEBUG.o(Core._getLocalIpAddress());
		DEBUG.o("******5******Run getPushMessageService");
		// 获得设备的唯一标识码
		String str = Settings.Secure.getString(getContentResolver(),
				"android_id");
		DEBUG.o(str);
		Editor localEditor = getSharedPreferences("PushService", 0).edit();
		localEditor.putString("deviceID", str);
		localEditor.commit();
		HttpRequest localHttpRequest = new HttpRequest();
		try
		{
			String[] arrayOfString = new String[1];
			arrayOfString[0] = ("ac=androidpush&pass=1234&type=add&devicetoken=" + str);
			DEBUG.o(localHttpRequest._get(SiteTools.getApiUrl(arrayOfString)));
			PushService.setContext(this);
			PushService.actionStart(ZhangWoApp.getInstance());
			Setting.UPDATESUBNAR = false;
			new Thread(new Runnable()
			{
				public void run()
				{
					DEBUG.o("******6******get last UserSession");
					UserSession localUserSession = UserSessionDBHelper
							.getInstance(NavigationBar.this).getLast();
					if (localUserSession != null)
						ZhangWoApp.getInstance().setUserSession(
								localUserSession);
					DEBUG.o("******7******checkUpdate");
					UpgradeAPK localUpgradeAPK = new UpgradeAPK(preferences,
							core);
					if (localUpgradeAPK.isExpire())
						localUpgradeAPK.checkUpdate(); // 检查应用程序更新
					DEBUG.o("******8******checkNewAD");
					long l = Tools._getTimeStamp() / 1000L;
					AD.checkNewAD(NavigationBar.this, l);
					if (Setting.CLOSE)
					{
						Message localMessage1 = new Message();
						handler.sendMessage(localMessage1);
					}
					DEBUG.o("******9******_getTopNewsCount");
					String str = preferences.getString("last_launch_timestamp",
							null);
					if (str != null && !"".equals(str))
						Setting.TOP_NEWS_COUNT = _getTopNewsCount(Long
								.parseLong(str));
					else
						Setting.TOP_NEWS_COUNT = _getTopNewsCount(l);
					preferences
							.edit()
							.putString(
									"last_launch_timestamp",
									(new StringBuilder(String.valueOf(l)))
											.toString()).commit();
					if (ZhangWoApp.getInstance().isLogin())
					{
						Message message1 = new Message();
						message1.what = 1;
						handler.sendMessage(message1);
					}
				}
			}).start();
		} catch (Exception localException)
		{
			localException.printStackTrace();
		}
	}

	public boolean onKeyDown(int i, KeyEvent paramKeyEvent)
	{
		if ((i == KeyEvent.KEYCODE_BACK)
				&& (paramKeyEvent.getRepeatCount() == 0))
		{
			new Thread(new Runnable()
			{
				public void run()
				{
					DEBUG.o("exit up subnav");
					String str1 = preferences.getString(
							"last_check_subnav_timestamp", null);
					HttpRequest localHttpRequest = new HttpRequest();
					try
					{
						String[] arrayOfString = new String[2];
						arrayOfString[0] = "ac=home";
						arrayOfString[1] = ("time=" + Tools._getTimeStamp() / 1000L);
						String str2 = localHttpRequest._get(SiteTools
								.getApiUrl(arrayOfString));
						Setting.UPDATESUBNAR = true; // 更新二级导航
						if (str2 != null)
						{
							JSONObject localJSONObject1 = new JSONObject(str2);
							if (localJSONObject1 != null)
							{
								JSONObject localJSONObject2 = localJSONObject1
										.optJSONObject("res").optJSONObject(
												"list");
								if (localJSONObject2 != null)
								{
									String str3 = localJSONObject2
											.optString("channeltime");
									DEBUG.o(str3);
									DEBUG.o(str1);
									if ((str1 != null) && (!"".equals(str1))
											&& (!str3.equals(str1)))
									{
										DEBUG.o("up subnav...");
										Subnav.checkNewSubnav(NavigationBar.this);
									}
									preferences
											.edit()
											.putString(
													"last_check_subnav_timestamp",
													str3).commit();
									Setting.UPDATESUBNAR = false;
								}
							}
						}
					} catch (Exception localException)
					{
						localException.printStackTrace();
					}
				}
			}).start();
			finish();
		}
		boolean flag = true;
		if (i == KeyEvent.KEYCODE_MENU)
		{
			flag = false;
		}
		return flag;
	}

	public boolean onTouch(View view, MotionEvent paramMotionEvent)
	{
		switch (paramMotionEvent.getAction())
		{
		default:
			break;
		case MotionEvent.ACTION_DOWN: // 0
			if (!frame)
				view.setBackgroundColor(0xff5392cb);
			break;
		case MotionEvent.ACTION_UP: // 1
			if (!frame)
			{
				view.setBackgroundColor(0x80000000);
			} else
			{
				frame = false;
				setBtnclickable(true);
				mWindowManager.removeView(view);
				preferences.edit().putBoolean("guide1", false).commit();
			}
			break;
		}
		return false;
	}
}