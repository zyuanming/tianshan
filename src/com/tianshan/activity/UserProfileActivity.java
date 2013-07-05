package com.tianshan.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianshan.R;
import com.tianshan.ZhangWoApp;
import com.tianshan.source.Core;
import com.tianshan.source.DEBUG;
import com.tianshan.source.HttpRequest;
import com.tianshan.source.SiteTools;
import com.tianshan.source.activity.BaseActivity;

public class UserProfileActivity extends BaseActivity
{
	private static final int CAMERA_WITH_DATA = 3023;
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	private static final int PHOTO_WITH_DATA = 3025;
	private ImageView avatar;
	private URL avatarurl;
	private Bitmap bmp;
	private Button btnBack = null;
	private Button btnSetAvatar = null;
	private Handler handler = new Handler()
	{
		public void handleMessage(Message paramAnonymousMessage)
		{}
	};
	private ImageView imUserPhoto = null;
	private LinearLayout llMyComment = null;
	private LinearLayout llMyMessage = null;
	private LinearLayout llMySaved = null;
	private LinearLayout llMyTicket = null;
	private LinearLayout llNear = null;
	private Handler loadbmphandler = new Handler(new Handler.Callback()
	{
		public boolean handleMessage(Message paramAnonymousMessage)
		{
			avatar.setImageBitmap(UserProfileActivity.this.bmp);
			return false;
		}
	});
	private Context mContext = null;
	private TextView tvName;
	private TextView tvTitle = null;
	private TextView tvUid;
	private long uid;

	private void _initComponent()
	{
		findViewById(R.id.nav_commit).setVisibility(8);
		this.imUserPhoto = ((ImageView) findViewById(R.id.iv_user));
		this.btnSetAvatar = ((Button) findViewById(R.id.btn_set_avatar));
		this.btnSetAvatar.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				showPhotoSettingDialog();
			}
		});
		this.btnBack = ((Button) findViewById(R.id.nav_back));
		this.btnBack.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				finish();
			}
		});
		this.llMyTicket = ((LinearLayout) findViewById(R.id.ll_my_ticket));
		this.llMyTicket.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				Intent localIntent = new Intent(mContext, MyInfo.class);
				localIntent.putExtra("my_info_url",
						SiteTools.getMobileUrl(new String[] { "ac=mythread" }));
				localIntent.putExtra("my_info_type", "thread");
				mContext.startActivity(localIntent);
			}
		});
		this.llMyComment = ((LinearLayout) findViewById(R.id.ll_my_comment));
		this.llMyComment.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				Intent localIntent = new Intent(mContext, MyInfo.class);
				localIntent.putExtra("my_info_type", "comment");
				mContext.startActivity(localIntent);
			}
		});
		this.llMyMessage = ((LinearLayout) findViewById(R.id.ll_my_message));
		this.llMyMessage.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				Intent localIntent = new Intent(mContext, MyInfo.class);
				localIntent.putExtra(
						"my_info_url",
						SiteTools.getMobileUrl(new String[] { "ac=mypm",
								"op=my" }));
				localIntent.putExtra("my_info_type", "mypm");
				mContext.startActivity(localIntent);
			}
		});
		this.llMySaved = ((LinearLayout) findViewById(R.id.ll_my_saved));
		this.llMySaved.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				Intent localIntent = new Intent();
				localIntent.setClass(mContext, MyFavoriteActivity.class);
				localIntent.putExtra("MyFavorite", 1);
				mContext.startActivity(localIntent);
			}
		});
		this.llNear = ((LinearLayout) findViewById(R.id.ll_nearby));
		this.llNear.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				Intent localIntent = new Intent(mContext, NearbyInfo.class);
				localIntent.putExtra("type", "");
				mContext.startActivity(localIntent);
			}
		});
		this.tvTitle = ((TextView) findViewById(R.id.nav_title));
		this.tvTitle.setText(R.string.STR_USER_PROFILE);
		this.avatar = ((ImageView) findViewById(R.id.iv_userPhoto));
		this.tvName = ((TextView) findViewById(R.id.tv_name));
		this.tvUid = ((TextView) findViewById(R.id.tv_uid));
		this.tvName.setText(ZhangWoApp.getInstance().getUserSession()
				.getUsername());
		this.tvUid.setText(this.tvUid.getText()
				+ String.valueOf(ZhangWoApp.getInstance().getUserSession()
						.getUid()));
		findViewById(R.id.btn_logout).setOnClickListener(
				new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						showLogoutDialog();
					}
				});
	}

	private void _initData()
	{
		this.uid = ZhangWoApp.getInstance().getUserSession().getUid();
		String[] arrayOfString = new String[2];
		arrayOfString[0] = "ac=userinfo";
		arrayOfString[1] = ("uid=" + this.uid);
		String str1 = SiteTools.getApiUrl(arrayOfString);
		HttpRequest localHttpRequest = new HttpRequest();
		try
		{
			String str2 = localHttpRequest._get(str1);
			DEBUG.o(str2);
			JSONObject localJSONObject1 = new JSONObject(str2);
			if (localJSONObject1 != null)
			{
				JSONObject localJSONObject2 = localJSONObject1
						.optJSONObject("res");
				if (localJSONObject2 != null)
				{
					JSONObject localJSONObject3 = localJSONObject2
							.optJSONObject("list");
					int i = localJSONObject3.length();
					if ((localJSONObject3 != null) && (i > 0))
					{
						this.avatarurl = new URL(
								localJSONObject3.getString("avatar"));
						DEBUG.o("avatarurl = " + this.avatarurl);
						new Thread(new Runnable()
						{
							public void run()
							{
								try
								{
									URLConnection localURLConnection = UserProfileActivity.this.avatarurl
											.openConnection();
									localURLConnection.connect();
									InputStream localInputStream = localURLConnection
											.getInputStream();
									UserProfileActivity.this.bmp = BitmapFactory
											.decodeStream(localInputStream);
									localInputStream.close();
									Message localMessage = new Message();
									UserProfileActivity.this.loadbmphandler
											.sendMessage(localMessage);
								} catch (IOException localIOException)
								{
									localIOException.printStackTrace();
								}
							}
						}).start();
					}
				}
			}
		} catch (Exception localException)
		{
			localException.printStackTrace();
		}
	}

	private void cameraPicture()
	{
		startActivityForResult(
				new Intent("android.media.action.IMAGE_CAPTURE"), 3023);
	}

	public static Intent getCropImageIntent(Bitmap paramBitmap)
	{
		Intent localIntent = new Intent("com.android.camera.action.CROP");
		localIntent.setType("image/*");
		localIntent.putExtra("data", paramBitmap);
		localIntent.putExtra("crop", "true");
		localIntent.putExtra("aspectX", 2);
		localIntent.putExtra("aspectY", 2);
		localIntent.putExtra("outputX", 128);
		localIntent.putExtra("outputY", 128);
		localIntent.putExtra("return-data", true);
		return localIntent;
	}

	private void logout()
	{
		ZhangWoApp.getInstance().resetUserToGuest();
		com.tianshan.setting.Setting.QQ_LOGIN = false;
		finish();
	}

	private void saveAvatar(Bitmap paramBitmap)
	{
		File localFile = new File(Core.getSDCardPath() + "avatar.jpg");
		if (localFile.exists())
			localFile.delete();
		try
		{
			FileOutputStream localFileOutputStream = new FileOutputStream(
					localFile);
			if (paramBitmap.compress(Bitmap.CompressFormat.PNG, 100,
					localFileOutputStream))
			{
				localFileOutputStream.flush();
				localFileOutputStream.close();
			}
		} catch (FileNotFoundException localFileNotFoundException)
		{
			localFileNotFoundException.printStackTrace();
		} catch (IOException localIOException)
		{
			localIOException.printStackTrace();
		}
	}

	private void selectPicture()
	{
		Intent localIntent = new Intent("android.intent.action.GET_CONTENT");
		localIntent.setType("image/*");
		localIntent.putExtra("crop", "true");
		localIntent.putExtra("aspectX", 2);
		localIntent.putExtra("aspectY", 2);
		localIntent.putExtra("outputX", 128);
		localIntent.putExtra("outputY", 128);
		localIntent.putExtra("return-data", true);
		startActivityForResult(Intent.createChooser(localIntent, "Select pic"),
				3021);
	}

	private void showLogoutDialog()
	{
		new AlertDialog.Builder(this)
				.setTitle(R.string.STR_LOGOUT_COMMIT)
				.setPositiveButton(17039370,
						new DialogInterface.OnClickListener()
						{
							public void onClick(
									DialogInterface paramAnonymousDialogInterface,
									int paramAnonymousInt)
							{
								logout();
								paramAnonymousDialogInterface.dismiss();
							}
						})
				.setNegativeButton(17039360,
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

	private void showPhotoSettingDialog()
	{
		new AlertDialog.Builder(this)
				.setTitle(R.string.STR_SET_AVATAR)
				.setItems(R.array.menu_avator,
						new DialogInterface.OnClickListener()
						{
							public void onClick(
									DialogInterface dialoginterface, int i)
							{
								if (i == 0)
									selectPicture();
								else
									cameraPicture();
							}
						})
				.setNegativeButton(17039360,
						new DialogInterface.OnClickListener()
						{
							public void onClick(
									DialogInterface paramAnonymousDialogInterface,
									int paramAnonymousInt)
							{
								paramAnonymousDialogInterface.cancel();
							}
						}).create().show();
	}

	private void upAvatarToWebInterface() throws IOException
	{
		String str = Core.getSDCardPath() + "avatar.jpg";
		HttpRequest localHttpRequest = new HttpRequest();
		HashMap localHashMap = new HashMap();
		localHashMap.put("uid", Long.valueOf(this.uid));
		localHashMap.put("avatar", str);
		localHttpRequest._httpPostFile(
				SiteTools.getApiUrl(new String[] { "ac=setavatar" }),
				localHashMap);
		DEBUG.o(str);
	}

	protected void doCropPhoto(Bitmap paramBitmap)
	{
		startActivityForResult(getCropImageIntent(paramBitmap), 3021);
	}

	protected void onActivityResult(int paramInt1, int paramInt2, Intent intent)
	{
		switch (paramInt1)
		{
		case 3022:
		case 3024:
		default:
			return;
		case 3023:
			if (intent != null)
			{
				Bitmap bitmap1 = (Bitmap) intent.getParcelableExtra("data");
				if (bitmap1 != null)
					doCropPhoto(bitmap1);
			}
			break;
		case 3021:
		case 3025:
			if (intent != null)
			{
				Bitmap bitmap = (Bitmap) intent.getParcelableExtra("data");
				if (bitmap != null)
				{
					saveAvatar(bitmap);
					avatar.setImageBitmap(bitmap);
					try
					{
						upAvatarToWebInterface();
					} catch (IOException ioexception)
					{
						ioexception.printStackTrace();
					}
				}
			}
			break;
		}
	}

	public void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		setContentView(R.layout.user_profile);
		this.mContext = this;
		_initComponent();
		_initData();
	}

	public void onDestroy()
	{
		super.onDestroy();
	}
}