package com.tianshan;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

import com.tianshan.activity.NavigationBar;
import com.tianshan.dbhelper.SubnavHelper;
import com.tianshan.model.Subnavisement;
import com.tianshan.setting.Setting;
import com.tianshan.setting.WoPreferences;
import com.tianshan.source.AD;
import com.tianshan.source.DEBUG;
import com.tianshan.source.SiteTools;
import com.tianshan.source.Subnav;
import com.tianshan.source.activity.BaseActivity;
import com.tianshan.task.CheckSubnavTask;
import com.tianshan.task.CheckUpdateBaseAsyncTask;

public class StartActivity extends BaseActivity
{
	private static SubnavHelper dbHelper = null;
	private String channeltime;
	private String url;
	private WindowManager mWindowManager;

	/**
	 * 更新二级导航的任务回调方法
	 */
	private CheckUpdateBaseAsyncTask.CheckTaskResultListener checksubnavListener = new CheckUpdateBaseAsyncTask.CheckTaskResultListener()
	{
		public void onTaskResult(boolean paramAnonymousBoolean,
				HashMap<String, Object> paramAnonymousHashMap)
		{
			Log.d("result", "*****success**** =" + paramAnonymousBoolean);
			if (paramAnonymousBoolean)
			{
				String str = (String) paramAnonymousHashMap.get("data");
				Log.d("result", "*****data**** =" + str);
				if ((str != null) || (!str.equals("")))
				{
					try
					{
						JSONObject localJSONObject1 = new JSONObject(str);
						if (localJSONObject1 != null)
						{

							JSONObject localJSONObject2 = localJSONObject1
									.optJSONObject("msg");
							if ((localJSONObject2 != null)
									&& !("list_empty"
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
									int i = localJSONArray.length();
									if ((localJSONArray != null) && (i > 0))
									{
										int j = localJSONArray.length();
										StartActivity.dbHelper = SubnavHelper
												.getInstance(StartActivity.this
														.getApplicationContext());
										ArrayList localArrayList1 = new ArrayList();
										ArrayList localArrayList2 = new ArrayList();
										for (int k = 0;; k++)
										{
											if (k >= j)
											{
												Subnav.clearSubnav(
														localArrayList2,
														StartActivity.this
																.getApplicationContext());
												Subnav.upSetSubnav(
														localArrayList1,
														StartActivity.this
																.getApplicationContext());
												SharedPreferences.Editor localEditor = StartActivity.this.preferences
														.edit();
												localEditor.putBoolean(
														"updatasubnav", true);
												localEditor.commit();
												Message localMessage2 = new Message();
												localMessage2.what = 1;
												StartActivity.this.splashHandler
														.sendMessage(localMessage2);
												break;
											}
											Subnavisement localSubnavisement1 = new Subnavisement(
													localJSONArray
															.optJSONObject(k));
											localArrayList2
													.add(Integer
															.valueOf(localSubnavisement1
																	.getId()));
											Subnavisement localSubnavisement2 = StartActivity.dbHelper
													.get(localSubnavisement1
															.getId());
											StartActivity.dbHelper
													.save(localSubnavisement1);
											if ((localSubnavisement2 != null)
													&& (localSubnavisement2
															.getIsset() == 2))
											{
												localSubnavisement1.setIsset(2);
												StartActivity.dbHelper
														.save(localSubnavisement1);
											}
											localArrayList1
													.add(Integer
															.valueOf(localSubnavisement1
																	.getFup()));
										}
									}
								}
							}
						}
					} catch (Exception localException)
					{
						localException.printStackTrace();
					}
				} else
				{
					Message localMessage3 = new Message();
					localMessage3.what = 1;
					splashHandler.sendMessage(localMessage3);
				}
			} else
			{
				Message localMessage1 = new Message();
				localMessage1.what = 1;
				splashHandler.sendMessage(localMessage1);
			}
		}
	};

	private Handler splashHandler = new Handler()
	{
		public void handleMessage(Message paramAnonymousMessage)
		{
			switch (paramAnonymousMessage.what)
			{
			default:
				super.handleMessage(paramAnonymousMessage);
				return;
			case 1: // 进入用户主界面
				DEBUG.o("******3******Start NavigationBar");
				startActivity(new Intent(StartActivity.this,
						NavigationBar.class));
				finish();
				break;
			case 2: // 开始检查应用程序新版本，启动一个异步Task
				String str = SiteTools.getApiUrl(new String[] { "ac=subnav" });
				new CheckSubnavTask(getApplicationContext(),
						checksubnavListener, false)
						.execute(new String[] { str });
				break;
			}
		}
	};

	/**
	 * android:configChanges="orientation|keyboard|keyboardHidden" 　　
	 * 通过查阅Android API可以得知androi
	 * :onConfigurationChanged实际对应的是Activity里的onConfigurationChanged()方法.
	 * 在AndroidManifest.xml中添加上诉代码的含义是表示在改变屏幕方向、弹出软件盘和隐藏软键盘时，不再去执行onCreate()方法，
	 * 而是直接执行onConfigurationChanged
	 * ()。如果不申明此段代码，系统就会停止并重启当前Activity,造成重复初始化，资源浪费，
	 * 降低程序效率，而且更有可能因为重复的初始化而导致数据的丢失。这是需要千万避免的。
	 */
	public void onConfigurationChanged(Configuration paramConfiguration)
	{
		super.onConfigurationChanged(paramConfiguration);
	}

	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		setContentView(R.layout.start);
		DEBUG.o("StartActivity Init");
		DEBUG.o("*****0*******new SiteTools");

		// 第一次打开应用程序，显示用户指引
		if (WoPreferences.IsFirstOpen())
		{
			WoPreferences.setDownloadImgMode(1);
			mWindowManager = (WindowManager) getSystemService("window");
			if (mWindowManager.getDefaultDisplay().getHeight() == 854
					|| mWindowManager.getDefaultDisplay().getHeight() == 800
					|| mWindowManager.getDefaultDisplay().getHeight() != 480)
			{
				preferences = getSharedPreferences("application_tab", 0);
				preferences.edit().putBoolean("guide1", true).commit();
				preferences.edit().putBoolean("guide2", true).commit();
				preferences.edit().putBoolean("guide3", true).commit();
				preferences.edit().putBoolean("guide4", true).commit();
				preferences.edit().putBoolean("guide5", true).commit();
			}
		}
		new SiteTools(this);
		DEBUG.o("*****1*******display AD");
		String s = AD.getLastADImg(this);
		if (s != null)
		{
			DEBUG.o("first   if");
			Drawable drawable = Drawable.createFromPath(s);
			findViewById(R.id.splash_screen).setBackgroundDrawable(drawable);
		} else
		{
			DEBUG.o("first   else");
		}
		/**
		 * 更新二级导航
		 */
		(new Thread(new Runnable()
		{
			public void run()
			{
				DEBUG.o("******2******first checkNewSubnav");
				String s1 = preferences.getString(
						"last_check_subnav_timestamp", null);
				boolean flag = preferences.getBoolean("updatasubnav", false);
				Log.d("sub",
						(new StringBuilder(String.valueOf(s1))).append("***")
								.toString());
				if (s1 == null || "".equals(s1) || !flag)
				{
					if (!Setting.checkConn(getApplicationContext())) // 没有网络
					{
						Editor editor = preferences.edit();
						editor.putBoolean("updatasubnav", false);
						editor.commit();
						Message message1 = new Message();
						message1.what = 1;
						splashHandler.sendMessageDelayed(message1, 2000L);
					} else
					// 有网络
					{
						Log.d("sub", "updata subnav");
						Message message = new Message();
						message.what = 2;
						splashHandler.sendMessage(message);
					}
				} else
				{
					Message message2 = new Message();
					message2.what = 1;
					splashHandler.sendMessageDelayed(message2, 2000L);
				}
			}
		})).start();
	}
}