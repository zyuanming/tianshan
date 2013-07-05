package com.tianshan.source.activity;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.tianshan.R;
import com.tianshan.ZhangWoApp;
import com.tianshan.activity.DownloadAllMsg;
import com.tianshan.activity.LoginActivity;
import com.tianshan.activity.MyFavoriteActivity;
import com.tianshan.activity.NearbyInfo;
import com.tianshan.activity.RegisterActivity;
import com.tianshan.activity.SettingActivity;
import com.tianshan.activity.UserProfileActivity;
import com.tianshan.setting.Setting;
import com.tianshan.setting.WoPreferences;
import com.tianshan.source.Core;
import com.tianshan.source.DEBUG;
import com.tianshan.source.HttpRequest;
import com.tianshan.source.ShowMessage;
import com.tianshan.source.SiteTools;
import com.tianshan.source.Subnav;
import com.tianshan.source.Tools;
import com.tianshan.source.view.DWebView;
import com.tianshan.source.webinterface.BaseWebInterFace;

public class BaseActivity extends Activity
{
	public static boolean listshow = false;
	public static ProgressBar progress;
	public static boolean progressshow = false;
	public Core core;
	public DWebView dwebview;
	public Handler handler = new Handler();
	public SharedPreferences preferences;
	public String url = null;
	public FrameLayout viewbox;
	public BaseWebInterFace webinterface;

	public static void setProgressGone(boolean paramBoolean)
	{
		if (paramBoolean)
			progress.setVisibility(0);
		else
		{
			progress.setVisibility(8);
		}
	}

	public void ShowMessageByHandler(int paramInt1, int paramInt2)
	{
		ShowMessageByHandler(getString(paramInt1), paramInt2);
	}

	public void ShowMessageByHandler(final String paramString,
			final int paramInt)
	{
		if (this.handler != null)
			this.handler.post(new Runnable()
			{
				public void run()
				{
					ShowMessage.getInstance(BaseActivity.this)._showToast(
							paramString, paramInt);
				}
			});
	}

	public void ShowMessageByHandler(String[] paramArrayOfString, int paramInt)
	{
		ShowMessageByHandler(this.core._getMessageByName(paramArrayOfString),
				paramInt);
	}

	/**
	 * 通过WebView来显示网络内容
	 */
	protected void addWebView()
	{
		if (this.viewbox == null)
			DEBUG.o("viewbox is null!");
		else
		{
			this.viewbox.removeAllViews();
			this.dwebview = new DWebView(this);
			progress = new ProgressBar(this);
			progress.setBackgroundColor(0);
			progress.setIndeterminateDrawable(getResources().getDrawable(
					R.drawable.progress_blue_move));
			// 设置进度条的不确定状态，这时，进度会忽略，转而是显示不间断的动画，如果当前进度条的是style只支持不确定状态，那么这个方法会忽略
			progress.setIndeterminate(false);
			this.webinterface = new BaseWebInterFace(this, this.dwebview,
					progress);
			this.viewbox.addView(this.dwebview);
			FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.WRAP_CONTENT,
					FrameLayout.LayoutParams.WRAP_CONTENT, 17);
			this.viewbox.addView(progress, localLayoutParams);
			progress.setVisibility(8);
			this.dwebview._init(this);
			this.dwebview._initWebChromeClient();
			this.dwebview._initWebViewClient();
			this.dwebview._addJavaScriptInterFace(this.webinterface);
			if (this.url != null)
				this.webinterface.GotoUrl(this.url);
		}
	}

	protected void initInterface()
	{}

	protected void initOnClickListener()
	{}

	protected void initWidget()
	{}

	public boolean isHaveNetwork()
	{
		ConnectivityManager connectivitymanager = (ConnectivityManager) getSystemService("connectivity");
		boolean flag;
		if (connectivitymanager.getActiveNetworkInfo() != null
				&& connectivitymanager.getActiveNetworkInfo().isConnected())
			flag = true;
		else
			flag = false;
		return flag;
	}

	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		ZhangWoApp.setInstance((ZhangWoApp) getApplicationContext());
		preferences = getSharedPreferences("application_tab", 0);
		core = new Core(this);
		core._initWindow();
		WoPreferences.initPreferences(this);
		Setting.activitylist.add(this);
		ZhangWoApp.getInstance().SetUserAgent(core._getDefaultUserAgent());
		isHaveNetwork();
	}

	public boolean onCreateOptionsMenu(Menu paramMenu)
	{
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem paramMenuItem)
	{
		switch (paramMenuItem.getItemId())
		{
		default:
			return false;
		case 2: // 设置
			startActivity(new Intent(this, SettingActivity.class));
			break;
		case 3: // 离线
			startActivity(new Intent(this, DownloadAllMsg.class));
			break;
		case 4: // 收藏
			if (ZhangWoApp.getInstance().isLogin())
			{
				Intent localIntent2 = new Intent();
				localIntent2.setClass(this, MyFavoriteActivity.class);
				localIntent2.putExtra("MyFavorite", 1);
				startActivity(localIntent2);
			} else
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
												BaseActivity.this
														.getApplicationContext(),
												LoginActivity.class);
										BaseActivity.this
												.startActivity(localIntent);
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
			break;
		case 5: // 关闭
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
						Setting.UPDATESUBNAR = true;
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
										Subnav.checkNewSubnav(BaseActivity.this
												.getApplicationContext());
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
			for (int i = 0; i < Setting.activitylist.size(); i++)
				if (Setting.activitylist.get(i) != null)
					((Activity) Setting.activitylist.get(i)).finish();

			break;
		case 6: // 个人中心
			startActivity(new Intent(this, UserProfileActivity.class));
			break;
		case 7: // 身边
			Intent localIntent1 = new Intent(this, NearbyInfo.class);
			localIntent1.putExtra("type", "");
			startActivity(localIntent1);
			break;
		case 8: // 注册
			startActivity(new Intent(this, RegisterActivity.class));
			break;
		case 9: // 登录
			startActivity(new Intent(this, LoginActivity.class));
			break;
		}
		return false;
	}

	/**
	 * 弹出菜单
	 */
	public boolean onPrepareOptionsMenu(Menu paramMenu)
	{
		paramMenu.clear();
		paramMenu.add(0, 2, 3, "设置").setIcon(R.drawable.ico_set);
		paramMenu.add(0, 3, 4, "离线").setIcon(R.drawable.ico_download);
		paramMenu.add(0, 4, 5, "收藏").setIcon(R.drawable.ico_collect);
		paramMenu.add(0, 5, 6, "关闭").setIcon(R.drawable.ico_quite);
		if (ZhangWoApp.getInstance().isLogin())
		{
			paramMenu.add(0, 6, 1, "个人中心").setIcon(R.drawable.ico_person);
			paramMenu.add(0, 7, 2, "身边").setIcon(R.drawable.ico_side);
		} else
		{
			if (!ZhangWoApp.getInstance().isLogin())
			{
				paramMenu.add(0, 8, 1, "注册").setIcon(R.drawable.ico_register);
				paramMenu.add(0, 9, 2, "登录").setIcon(R.drawable.ico_login);
			}
		}
		return true;
	}

	protected void onSaveInstanceState(Bundle paramBundle)
	{
		super.onSaveInstanceState(paramBundle);
	}
}