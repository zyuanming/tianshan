package com.tianshan.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tianshan.R;
import com.tianshan.ZhangWoApp;

public class iMenuPopupWindow
{
	private boolean isReged = false;
	private boolean isShowing = false;
	private Context mContext = null;
	private popupWindow popWin = null;

	public iMenuPopupWindow(Context paramContext)
	{
		this.mContext = paramContext;
	}

	public void dismiss()
	{
		this.popWin.dismiss();
	}

	public boolean isShowing()
	{
		return this.isShowing;
	}

	public void showPopWindow(View paramView)
	{
		this.isReged = ZhangWoApp.getInstance().isLogin();
		this.popWin = new popupWindow(this.mContext, paramView);
		this.isShowing = true;
		this.popWin.mPopWindow.showAsDropDown(paramView);
		this.popWin.mPopWindow.setFocusable(true);
		this.popWin.mPopWindow.setTouchable(true);
	}

	private class popupWindow
	{
		private View mContentView;
		private LayoutInflater mInflater;
		private PopupWindow mPopWindow;
		private TextView tvDownloadAll = null;
		private TextView tvLogin = null;
		private TextView tvMySaved = null;
		private TextView tvReg = null;
		private TextView tvSettings = null;
		private TextView tvUserProfile = null;

		public popupWindow(Context context, View view)
		{
			super();
			tvUserProfile = null;
			tvSettings = null;
			tvDownloadAll = null;
			tvMySaved = null;
			tvLogin = null;
			tvReg = null;
			mContext = context;
			mInflater = LayoutInflater.from(mContext);
			android.graphics.drawable.Drawable drawable;
			if (isReged)
			{
				mContentView = mInflater.inflate(0x7f030025, null);
				mPopWindow = new PopupWindow(mContentView, 307, 227);
			} else
			{
				mContentView = mInflater.inflate(0x7f030026, null);
				mPopWindow = new PopupWindow(mContentView, 382, 227);
			}
			drawable = mContext.getResources().getDrawable(0x7f02002a);
			mPopWindow.setBackgroundDrawable(drawable);
			mPopWindow.setOutsideTouchable(false);
			mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
			{
				@Override
				public void onDismiss()
				{
					isShowing = false;
				}
			});
			mPopWindow.setTouchInterceptor(new View.OnTouchListener()
			{

				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					if (event.getAction() == 4)
						dismiss();
					return false;
				}
			});
			_init();
		}

		private void _init()
		{
			this.tvSettings = ((TextView) this.mContentView
					.findViewById(R.id.tv_settings));
			this.tvSettings.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View view)
				{
					Intent intent = new Intent(mContext, SettingActivity.class);
					mContext.startActivity(intent);
					dismiss();
				}
			});
			this.tvDownloadAll = ((TextView) this.mContentView
					.findViewById(R.id.tv_download_all));
			this.tvDownloadAll.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View view)
				{
					Intent intent = new Intent(mContext, DownloadAllMsg.class);
					mContext.startActivity(intent);
					dismiss();
				}
			});
			this.tvMySaved = ((TextView) this.mContentView
					.findViewById(R.id.tv_my_saved));
			this.tvMySaved.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View view)
				{
					if (isReged)
					{
						Intent intent = new Intent();
						intent.setClass(mContext, MyFavoriteActivity.class);
						intent.putExtra("MyFavorite", 1);
						mContext.startActivity(intent);
						dismiss();
					} else
					{
						(new android.app.AlertDialog.Builder(mContext))
								.setTitle(0x7f07000d)
								.setPositiveButton(
										0x7f07006f,
										new android.content.DialogInterface.OnClickListener()
										{

											public void onClick(
													DialogInterface dialoginterface,
													int i)
											{
												Intent intent = new Intent(
														mContext,
														LoginActivity.class);
												mContext.startActivity(intent);
												dismiss();
											}
										})
								.setNegativeButton(
										0x1040000,
										new android.content.DialogInterface.OnClickListener()
										{

											public void onClick(
													DialogInterface dialoginterface,
													int i)
											{
												dialoginterface.dismiss();
											}
										}).create().show();
					}
				}
			});
			if (!isReged)
			{
				this.tvLogin = ((TextView) this.mContentView
						.findViewById(R.id.tv_login));
				this.tvLogin.setOnClickListener(new View.OnClickListener()
				{
					public void onClick(View view)
					{
						Intent intent = new Intent();
						intent.setClass(mContext, LoginActivity.class);
						mContext.startActivity(intent);
						dismiss();
					}
				});
				this.tvReg = ((TextView) this.mContentView
						.findViewById(R.id.tv_reg));
				this.tvReg.setOnClickListener(new View.OnClickListener()
				{
					public void onClick(View view)
					{
						Intent intent = new Intent();
						intent.setClass(mContext, RegisterActivity.class);
						mContext.startActivity(intent);
						dismiss();
					}
				});
			} else
			{
				this.tvUserProfile = ((TextView) this.mContentView
						.findViewById(R.id.tv_user_profile));
				this.tvUserProfile
						.setOnClickListener(new View.OnClickListener()
						{
							public void onClick(View view)
							{
								Intent intent = new Intent(mContext,
										UserProfileActivity.class);
								mContext.startActivity(intent);
								dismiss();
							}
						});
			}
		}

		public void dismiss()
		{
			if (this.mPopWindow != null)
				this.mPopWindow.dismiss();
		}
	}
}