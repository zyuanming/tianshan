package com.tianshan.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CacheManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianshan.R;
import com.tianshan.setting.Setting;
import com.tianshan.setting.WoPreferences;
import com.tianshan.source.DEBUG;
import com.tianshan.source.GetFileSize;
import com.tianshan.source.ShowMessage;
import com.tianshan.source.UpgradeAPK;
import com.tianshan.source.activity.BaseActivity;

public class SettingActivity extends BaseActivity
{
	private long allfilesize;
	private Button bntTitleBack = null;
	private CheckBox cbAutoPushMsg = null;
	private CheckBox cbNotDownloadImg = null;
	private TextView clearCacheText;
	private TextView current_version;
	private int files;
	private LinearLayout llAbout = null;
	private LinearLayout llClearCache = null;
	private LinearLayout llConfigShareAcct = null;
	private LinearLayout llNewUserGuide = null;
	private LinearLayout llRecommend_app = null;
	private LinearLayout ll_update_version = null;
	private Context mContext = null;
	private WindowManager mWindowManager;
	private TextView new_version = null;
	private TextView tvTitle;
	private UpgradeAPK upgrader;

	private void _initComponent()
	{
		boolean bool1 = true;
		findViewById(R.id.nav_commit).setVisibility(8);
		// 推荐应用
		this.llRecommend_app = ((LinearLayout) findViewById(R.id.ll_recommend_app));
		this.llRecommend_app.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				new Intent();
				Intent intent = new Intent(mContext,
						RecommendAppViewActivity.class);
				startActivity(intent);
			}
		});
		// 绑定分享帐号
		this.llConfigShareAcct = ((LinearLayout) findViewById(R.id.ll_config_share_acct));
		this.llConfigShareAcct.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				Intent intent = new Intent(mContext, ShareActivity.class);
				intent.putExtra("BindPath", true);
				startActivity(intent);
			}
		});
		// 清除缓存
		this.llClearCache = ((LinearLayout) findViewById(R.id.ll_clear_cache));
		this.llClearCache.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				popupClearCacheDialog();
			}
		});
		this.clearCacheText = ((TextView) this.llClearCache.getChildAt(0));
		new Thread(new Runnable()
		{
			public void run()
			{
				GetFileSize localGetFileSize = new GetFileSize();
				HashMap localHashMap;
				try
				{
					localHashMap = new HashMap();
					localHashMap.put(Integer.valueOf(0),
							"/sdcard/tianshan/html/");
					localHashMap.put(Integer.valueOf(1),
							"/sdcard/tianshan/cache/");
					localHashMap.put(Integer.valueOf(2),
							"/sdcard/tianshan/photo_cache/");
					if (Setting.IsCanUseSdCard())
						localHashMap.put(Integer.valueOf(3),
								"/sdcard/.tianshan/");
					else
					{
						localHashMap.put(
								Integer.valueOf(3),
								(new StringBuilder())
										.append(getApplication().getFilesDir())
										.append(File.separator)
										.append("tianshan")
										.append(File.separator)
										.append("photo_cache").toString());
					}
					for (int i = 0; i < 4; i++)
					{
						DEBUG.o(localHashMap.get(Integer.valueOf(i)));
						File localFile = new File((String) localHashMap
								.get(Integer.valueOf(i)));
						if (localFile.isDirectory())
						{
							System.out.println("文件个数           "
									+ localGetFileSize.getlist(localFile));
							System.out.println("目录");
							long l = localGetFileSize.getFileSize(localFile);
							SettingActivity localSettingActivity1 = SettingActivity.this;
							localSettingActivity1.allfilesize = (l + localSettingActivity1.allfilesize);
							String str = localGetFileSize.FormetFileSize(l);
							System.out.println((String) localHashMap
									.get(Integer.valueOf(i)) + "目录的大小为" + str);
							SettingActivity localSettingActivity2 = SettingActivity.this;
							localSettingActivity2.files = ((int) (localSettingActivity2.files + localGetFileSize
									.getlist(localFile)));
						}
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				if (files > 0 || allfilesize > 0L)
					clearCacheText.setText(clearCacheText.getText()
							+ "(文件:"
							+ files
							+ "�?"
							+ localGetFileSize
									.FormetFileSize(SettingActivity.this.allfilesize)
							+ ")");

			}
		}).start();
		// 关于
		this.llAbout = ((LinearLayout) findViewById(R.id.ll_about));
		this.llAbout.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				Intent intent = new Intent(mContext, AboutActivity.class);
				startActivity(intent);
			}
		});
		// 版本更新
		this.ll_update_version = ((LinearLayout) findViewById(R.id.ll_update_version));
		this.ll_update_version.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				if (!upgrader.checkVersion)
					ShowMessage.getInstance(SettingActivity.this)._showToast(
							"亲，木有新版哦", 2);
				else
					popupUpdateDialog();
			}
		});
		this.current_version = ((TextView) this.ll_update_version
				.findViewById(R.id.current_version));
		Integer localInteger = Integer.valueOf(this.core._getVersionCode());
		this.current_version.setText(localInteger.toString() + ".0 Beta");
		if (this.upgrader.newVersion > 0)
		{
			this.new_version = ((TextView) this.ll_update_version
					.findViewById(R.id.new_version));
			this.new_version.setBackgroundColor(-16777216);
			this.new_version.setText("New V" + this.upgrader.newVersion + ".0");
		}
		this.llNewUserGuide = ((LinearLayout) findViewById(R.id.ll_new_user_guide));
		this.llNewUserGuide.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				for (int i = 0;; i++)
				{
					if (i >= Setting.activitylist.size())
					{
						SettingActivity.this.preferences = SettingActivity.this
								.getSharedPreferences("application_tab", 0);
						SettingActivity.this.preferences.edit()
								.putBoolean("guide1", true).commit();
						SettingActivity.this.preferences.edit()
								.putBoolean("guide2", true).commit();
						SettingActivity.this.preferences.edit()
								.putBoolean("guide3", true).commit();
						SettingActivity.this.preferences.edit()
								.putBoolean("guide4", true).commit();
						SettingActivity.this.preferences.edit()
								.putBoolean("guide5", true).commit();
						Intent localIntent = new Intent(
								SettingActivity.this.mContext,
								NavigationBar.class);
						SettingActivity.this.startActivity(localIntent);
						return;
					}
					if (Setting.activitylist.get(i) != null)
						((Activity) Setting.activitylist.get(i)).finish();
				}
			}
		});
		mWindowManager = (WindowManager) getSystemService("window");
		if (mWindowManager.getDefaultDisplay().getHeight() != 854
				&& mWindowManager.getDefaultDisplay().getHeight() != 800
				&& mWindowManager.getDefaultDisplay().getHeight() != 480)
			llNewUserGuide.setVisibility(8);
		cbNotDownloadImg = (CheckBox) findViewById(0x7f09005b);
		CheckBox checkbox = cbNotDownloadImg;
		boolean flag1;
		CheckBox checkbox1;
		if (WoPreferences.queryDownloadImgMode() == 0)
			flag1 = bool1;
		else
			flag1 = false;
		checkbox.setChecked(flag1);
		cbNotDownloadImg
				.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener()
				{

					public void onCheckedChanged(CompoundButton compoundbutton,
							boolean flag2)
					{
						Log.d("wifi",
								(new StringBuilder(String.valueOf(flag2)))
										.append("isChecked").toString());
						if (flag2)
							WoPreferences.setDownloadImgMode(0);
						else
							WoPreferences.setDownloadImgMode(1);
					}
				});
		cbAutoPushMsg = (CheckBox) findViewById(R.id.cb_auto_push_msg);
		checkbox1 = cbAutoPushMsg;
		if (WoPreferences.queryAutoPushMsg() != 0)
			bool1 = false;
		checkbox1.setChecked(bool1);
		this.cbAutoPushMsg
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
				{
					public void onCheckedChanged(CompoundButton compoundbutton,
							boolean flag2)
					{
						if (flag2)
							WoPreferences.setAutoPushMsg(0);
						else
							WoPreferences.setAutoPushMsg(1);
					}
				});
		this.tvTitle = ((TextView) findViewById(R.id.nav_title));
		this.tvTitle.setText(getString(R.string.STR_SETTING));
		this.bntTitleBack = ((Button) findViewById(R.id.nav_back));
		this.bntTitleBack.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				finish();
			}
		});
	}

	private boolean deleteFile(File file)
	{
		boolean flag;
		if (file.isDirectory())
		{

			String as[];
			as = file.list();
			for (int i = 0; i < as.length; i++)
			{
				if (!deleteFile(new File(file, as[i])))
				{
					flag = false;
					return flag;
				}
			}
		}
		flag = file.delete();
		return flag;
	}

	/**
	 * 清除缓存对话框
	 */
	private void popupClearCacheDialog()
	{
		new AlertDialog.Builder(this)
				.setTitle(R.string.message_clear_cache)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener()
						{
							public void onClick(
									DialogInterface paramAnonymousDialogInterface,
									int paramAnonymousInt)
							{
								DEBUG.o("ok");
								ArrayList localArrayList = new ArrayList();
								localArrayList.add("/sdcard/tianshan/cache/"
										.toString());
								localArrayList
										.add("/sdcard/tianshan/photo_cache/");
								localArrayList.add("/sdcard/tianshan/html/");
								localArrayList.add("/sdcard/.tianshan/");
								Iterator localIterator = localArrayList
										.iterator();
								File localFile2;
								File[] arrayOfFile2;
								while (localIterator.hasNext())
								{
									String s = ((String) localIterator.next())
											.toString();
									DEBUG.o(s);
									File file = new File(s);
									if (file.exists())
									{
										File afile[] = file.listFiles();
										int j = 0;
										while (j < afile.length)
										{
											deleteFile(afile[j]);
											j++;
										}
									}
								}
								// android提供的基本缓存目录
								localFile2 = CacheManager.getCacheFileBaseDir();
								if ((localFile2 != null)
										&& (localFile2.exists())
										&& (localFile2.isDirectory()))
								{
									arrayOfFile2 = localFile2.listFiles();
									for (int k = 0;; k++)
									{
										if (k >= arrayOfFile2.length)
										{
											localFile2.delete();
											clearCacheText
													.setText(R.string.STR_CLEAR_CACHE);
											paramAnonymousDialogInterface
													.dismiss();
											ShowMessage.getInstance(
													SettingActivity.this)
													._showToast("缓存清除成功", 2);
											return;
										}
										arrayOfFile2[k].delete();
									}
								}
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener()
						{
							public void onClick(
									DialogInterface paramAnonymousDialogInterface,
									int paramAnonymousInt)
							{
								DEBUG.o("no");
								paramAnonymousDialogInterface.dismiss();
							}
						}).create().show();
	}

	/**
	 * 更新应用程序对话框
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
									DialogInterface dialoginterface, int i)
							{
								upgrader.downLoad(SettingActivity.this,
										Setting.AppParam.UPDATE_URL);
								dialoginterface.dismiss();
							}
						})
				.setNegativeButton(R.string.message_update_app_no,
						new DialogInterface.OnClickListener()
						{
							public void onClick(
									DialogInterface dialoginterface, int i)
							{
								dialoginterface.dismiss();
							}
						}).create().show();
		this.upgrader.resetUpdateTag();
	}

	public void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		setContentView(R.layout.setting);
		this.mContext = this;
		this.upgrader = new UpgradeAPK(this.preferences, this.core);
		this.upgrader.checkUpdate();
		_initComponent();
	}
}