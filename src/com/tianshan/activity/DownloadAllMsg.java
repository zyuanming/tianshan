package com.tianshan.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tianshan.R;
import com.tianshan.setting.Setting;
import com.tianshan.source.OfflineService;
import com.tianshan.source.activity.BaseActivity;

public class DownloadAllMsg extends BaseActivity implements
		OfflineService.OnDownloadDoneListener
{
	public static boolean downloadswitch = false;
	public static final String filterStr = "download_all_progress";
	private Button btnBack = null;
	private Button btnCancel = null;
	private TextView downByte = null;
	private ProgressBar downloadprogress;
	private Handler handler = new Handler(new Handler.Callback()
	{
		public boolean handleMessage(Message paramAnonymousMessage)
		{
			switch (paramAnonymousMessage.what)
			{
			default:
				return false;
			case 1:
				btnCancel.setText("\u5F00\u59CB");
				Setting.hasTaskRunning = false;
				DownloadAllMsg.downloadswitch = false;
				break;
			case 2:
				downloadprogress.setVisibility(8);
				break;
			}
			return false;
		}
	});
	private Context mContext = null;
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mWindowParams;
	private ProgressBar pgBar = null;
	private ProgressBroadCast recver = null;
	private TextView tvByte = null;
	private TextView tvNumber = null;
	private TextView tvType;

	private void _initComponent()
	{
		this.pgBar = ((ProgressBar) findViewById(R.id.pb_download_all));
		this.pgBar.setMax(100);
		this.tvByte = ((TextView) findViewById(R.id.tv_byte));
		this.downByte = ((TextView) findViewById(R.id.down_byte));
		this.tvNumber = ((TextView) findViewById(R.id.tv_number));
		this.tvType = ((TextView) findViewById(R.id.tv_type));
		this.downloadprogress = new ProgressBar(this);
		this.downloadprogress.setBackgroundColor(0);
		this.downloadprogress.setIndeterminateDrawable(getResources()
				.getDrawable(R.drawable.progress_blue_move));
		this.downloadprogress.setIndeterminate(false);
		this.mWindowParams = new WindowManager.LayoutParams();
		this.mWindowParams.gravity = 17;
		this.mWindowParams.height = 72;
		this.mWindowParams.width = 72;
		this.mWindowParams.flags = 408;
		this.mWindowParams.format = -3;
		this.mWindowParams.windowAnimations = 0;
		this.mWindowManager = ((WindowManager) getSystemService("window"));
		this.mWindowManager.addView(this.downloadprogress, this.mWindowParams);
		this.downloadprogress.setVisibility(8);
		this.btnBack = ((Button) findViewById(R.id.nav_back));
		this.btnBack.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				unregisterReceiver(DownloadAllMsg.this.recver);
				finish();
			}
		});
		this.btnCancel = ((Button) findViewById(R.id.btn_nav_cancel));
		if (Setting.hasTaskRunning)
			this.btnCancel.setText("取消");
		else
			btnCancel.setText("\u5F00\u59CB");
		this.btnCancel.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				if (!DownloadAllMsg.downloadswitch)
				{
					downloadswitch = true;
					btnCancel.setText("取消");
					if (Environment.getExternalStorageState().equals("mounted"))
					{
						OfflineService.setContext(mContext);
						OfflineService
								.setOnDownloadDoneListener(DownloadAllMsg.this);
						startService(new Intent(DownloadAllMsg.this,
								OfflineService.class));
						new Thread(new Runnable()
						{
							public void run()
							{
								int i = 0;
								while (true)
								{
									Intent localIntent = new Intent();
									localIntent
											.setAction("download_all_progress");
									int j = i + 1;
									localIntent.putExtra(
											"download_all_progress", i);
									try
									{
										Thread.sleep(150L);
										DownloadAllMsg.this.mContext
												.sendBroadcast(localIntent);
										i = j;
									} catch (InterruptedException localInterruptedException)
									{
										localInterruptedException
												.printStackTrace();
									}
								}
							}
						}).start();
					} else
					{
						DownloadAllMsg.this.ShowMessageByHandler(
								R.string.no_sdcard, 2);
					}
				} else
				{
					if (DownloadAllMsg.downloadswitch)
					{
						btnCancel.setText("\u5F00\u59CB");
						Setting.hasTaskRunning = false;
						downloadswitch = false;
					}
				}
			}
		});
		this.recver = new ProgressBroadCast();
		IntentFilter localIntentFilter = new IntentFilter();
		localIntentFilter.addAction("download_all_progress");
		registerReceiver(this.recver, localIntentFilter);
	}

	public void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		setContentView(R.layout.download_all);
		this.mContext = this;
		_initComponent();
		if (!Setting.hasTaskRunning)
		{
			SharedPreferences.Editor localEditor = this.mContext
					.getSharedPreferences("tianshan", 0).edit();
			localEditor.clear();
			localEditor.commit();
		}
		if (Environment.getExternalStorageState().equals("mounted"))
			if (!Setting.hasTaskRunning)
				new AlertDialog.Builder(this)
						.setMessage(R.string.download_msg)
						.setPositiveButton(R.string.download_ok,
								new DialogInterface.OnClickListener()
								{
									public void onClick(
											DialogInterface paramAnonymousDialogInterface,
											int paramAnonymousInt)
									{
										DownloadAllMsg.downloadswitch = true;
										btnCancel.setText("取消");
										OfflineService.setContext(mContext);
										OfflineService
												.setOnDownloadDoneListener(DownloadAllMsg.this);
										startService(new Intent(
												DownloadAllMsg.this,
												OfflineService.class));
										new Thread(new Runnable()
										{
											public void run()
											{
												int i = 0;
												while (true)
												{
													Intent localIntent = new Intent();
													localIntent
															.setAction("download_all_progress");
													int j = i + 1;
													localIntent
															.putExtra(
																	"download_all_progress",
																	i);
													try
													{
														Thread.sleep(150L);
														DownloadAllMsg.this.mContext
																.sendBroadcast(localIntent);
														i = j;
													} catch (InterruptedException localInterruptedException)
													{
														localInterruptedException
																.printStackTrace();
													}
												}
											}
										}).start();
									}
								})
						.setNegativeButton(R.string.download_no,
								new DialogInterface.OnClickListener()
								{
									public void onClick(
											DialogInterface paramAnonymousDialogInterface,
											int paramAnonymousInt)
									{}
								}).create().show();
			else
			{
				ShowMessageByHandler(R.string.no_sdcard, 2);
			}
	}

	public void ondownloaddone(int paramInt)
	{
		if (paramInt == 0)
		{
			this.downloadprogress.setVisibility(0);
			ShowMessageByHandler("准备数据中请稍候", 2);
		} else
		{
			if (paramInt == 1)
			{
				Message localMessage1 = new Message();
				localMessage1.what = 1;
				this.handler.sendMessage(localMessage1);
				ShowMessageByHandler("离线数据下载完成", 1);
			} else if (paramInt == 2)
			{
				Message localMessage2 = new Message();
				localMessage2.what = 2;
				this.handler.sendMessage(localMessage2);
				ShowMessageByHandler("下载离线数据", 2);
			}
		}
	}

	class ProgressBroadCast extends BroadcastReceiver
	{
		ProgressBroadCast()
		{}

		public void onReceive(Context paramContext, Intent paramIntent)
		{
			if (Setting.hasTaskRunning)
			{
				SharedPreferences localSharedPreferences = DownloadAllMsg.this.mContext
						.getSharedPreferences("tianshan", 0);
				int i = localSharedPreferences.getInt("size", 0);
				int j = localSharedPreferences.getInt("count", 0);
				int k = localSharedPreferences.getInt("progress", 0);
				String str = localSharedPreferences.getString("type", "");
				DownloadAllMsg.this.tvByte.setText(k + "%");
				DownloadAllMsg.this.pgBar.setProgress(k);
				DownloadAllMsg.this.tvNumber.setText(j + "\u7BC7");
				DownloadAllMsg.this.downByte.setText(i + "k");
				DownloadAllMsg.this.tvType.setText(str);
				Log.d("msgd", " savedsize = " + i + "savedcount = " + j
						+ "progress =" + k + "  type = " + str);
			}
		}
	}
}