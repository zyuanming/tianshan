package com.tianshan.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.ImageView;

import com.tianshan.R;
import com.tianshan.ZhangWoApp;
import com.tianshan.source.DEBUG;
import com.tianshan.source.HttpsRequest;
import com.tianshan.source.ShowMessage;
import com.tianshan.source.SiteTools;
import com.tianshan.source.activity.SecondLevelActivity;
import com.tianshan.source.view.DWebView;
import com.tianshan.source.view.Navbar;
import com.tianshan.source.webinterface.BaseWebInterFace;

public class ReplyActivity extends SecondLevelActivity implements
		Navbar.OnNavigateListener, View.OnClickListener,
		BaseWebInterFace.OnWebFinishListener
{
	private static final int CAMERA_WITH_DATA = 102;
	public static final int REQUEST_IMAGE = 101;
	ArrayList<String> aidlist = new ArrayList();
	private String fid = null;
	private ImageView getPhotoBtn;
	private Handler handler = new Handler(new Handler.Callback()
	{
		public boolean handleMessage(Message paramAnonymousMessage)
		{
			ReplyActivity.progress.setVisibility(8);
			String str = paramAnonymousMessage.getData().getString("code");
			ReplyActivity.this.webinterface.GotoUrl("javascript:getImgId("
					+ str + ")");
			return false;
		}
	});
	private String imagePath;
	private Uri photoUri;
	private boolean submitForm = false;
	private boolean submitId = false;
	private ImageView takePhotoBtn;

	private void getPhoto()
	{
		Intent localIntent = new Intent();
		localIntent.setType("image/*");
		localIntent.setAction("android.intent.action.GET_CONTENT");
		startActivityForResult(
				Intent.createChooser(localIntent, "Select Picture"), 101);
	}

	private String postBitmap(String s)
	{
		String as[] = new String[1];
		as[0] = (new StringBuilder("ac=uploadattach&fid=")).append(fid)
				.toString();
		String s1 = SiteTools.getMobileUrl(as);
		int i = (int) ZhangWoApp.getInstance().getUserSession().getUid();
		ArrayList arraylist = new ArrayList();
		BasicNameValuePair basicnamevaluepair = new BasicNameValuePair(
				"uploadsubmit", "1234");
		BasicNameValuePair basicnamevaluepair1 = new BasicNameValuePair("uid",
				(new StringBuilder(String.valueOf(i))).toString());
		BasicNameValuePair basicnamevaluepair2 = new BasicNameValuePair("fid",
				fid);
		arraylist.add(basicnamevaluepair);
		arraylist.add(basicnamevaluepair1);
		arraylist.add(basicnamevaluepair2);
		CookieSyncManager.createInstance(this);
		CookieManager.getInstance();
		return HttpsRequest.openUrl(s1, "POST", arraylist, "Filedata", s,
				ZhangWoApp.getInstance().getUserSession().getWebViewCookies());
	}

	private void takePhoto()
	{
		try
		{
			Intent localIntent = new Intent(
					"android.media.action.IMAGE_CAPTURE");
			String str = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new Date());
			ContentValues localContentValues = new ContentValues();
			localContentValues.put("title", str);
			this.photoUri = getContentResolver().insert(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					localContentValues);
			this.imagePath = getRealPathFromURI(this.photoUri,
					getContentResolver());
			localIntent.putExtra("output", this.photoUri);
			startActivityForResult(localIntent, 102);
		} catch (ActivityNotFoundException localActivityNotFoundException)
		{
			localActivityNotFoundException.printStackTrace();
		}
	}

	public void ShowMessageByHandler(int paramInt1, int paramInt2)
	{
		ShowMessageByHandler(getString(paramInt1), paramInt2);
	}

	protected void _initNavBar(boolean paramBoolean)
	{
		super._initNavBar(paramBoolean);
		Navbar localNavbar = new Navbar(this, this.navbarbox);
		localNavbar.setOnNavigate(this);
		localNavbar.setCommitBtnVisibility(true);
		localNavbar.setCommintBtnText(R.string.POST_TITLE_COMMINT);
		localNavbar.setTitle(R.string.REPLIES_TITLE);
	}

	protected void _initToolBar(boolean paramBoolean)
	{
		super._initToolBar(paramBoolean);
		View localView1 = LayoutInflater.from(this).inflate(
				R.layout.post_bottom, null);
		View localView2 = localView1.findViewById(R.id.gps_view);
		ImageView localImageView = (ImageView) localView1
				.findViewById(R.id.getgps);
		localView2.setVisibility(8);
		localImageView.setVisibility(8);
		this.getPhotoBtn = ((ImageView) localView1.findViewById(R.id.getphoto));
		this.takePhotoBtn = ((ImageView) localView1
				.findViewById(R.id.takephoto));
		this.getPhotoBtn.setClickable(true);
		this.takePhotoBtn.setClickable(true);
		this.takePhotoBtn.setOnClickListener(this);
		this.getPhotoBtn.setOnClickListener(this);
		this.toolbox.addView(localView1);
	}

	public String getRealPathFromURI(Uri paramUri,
			ContentResolver paramContentResolver)
	{
		Cursor localCursor = paramContentResolver.query(paramUri,
				new String[] { "_data" }, null, null, null);
		int i = localCursor.getColumnIndexOrThrow("_data");
		localCursor.moveToFirst();
		String str = localCursor.getString(i);
		localCursor.close();
		return str;
	}

	protected void onActivityResult(int paramInt1, int paramInt2,
			Intent paramIntent)
	{
		super.onActivityResult(paramInt1, paramInt2, paramIntent);
		if (paramInt2 == -1)
			switch (paramInt1)
			{
			default:
				return;
			case 101:
				if (paramIntent != null)
				{
					Uri localUri = paramIntent.getData();
					Cursor localCursor = getContentResolver().query(localUri,
							null, null, null, null);
					int i = localCursor.getColumnIndexOrThrow("_data");
					localCursor.moveToFirst();
					this.imagePath = localCursor.getString(i);
					progress.setVisibility(0);
					new Thread(new Runnable()
					{
						public void run()
						{
							String str = postBitmap(imagePath);
							Log.d("postCode", str);
							if (!str.endsWith("-1"))
							{
								submitId = true;
								Message localMessage = new Message();
								Bundle localBundle = new Bundle();
								localBundle.putString("code", str);
								localMessage.setData(localBundle);
								handler.sendMessage(localMessage);
								aidlist.add(str);
							} else
							{
								ShowMessageByHandler(
										getResources().getString(
												R.string.POST_PIC_EORROR), 3);
							}
						}
					}).start();
				}
				break;
			case 102:
				File localFile = new File(this.imagePath);
				if (localFile.length() == 0L)
				{
					localFile.delete();
				} else
				{
					progress.setVisibility(0);
					new Thread(new Runnable()
					{
						public void run()
						{
							String str = postBitmap(ReplyActivity.this.imagePath);
							Log.d("postCode", str);
							if (!str.endsWith("-1"))
							{
								submitId = true;
								Message localMessage = new Message();
								Bundle localBundle = new Bundle();
								localBundle.putString("code", str);
								localMessage.setData(localBundle);
								handler.sendMessage(localMessage);
								aidlist.add(str);
							} else
							{
								ShowMessageByHandler(
										getResources().getString(
												R.string.POST_PIC_EORROR), 3);
							}
						}
					}).start();
				}
				break;
			}
	}

	public void onBack()
	{
		finish();
	}

	public void onClick(View paramView)
	{
		switch (paramView.getId())
		{
		default:
			return;
		case R.id.getphoto:
			if (ZhangWoApp.getInstance().isLogin())
			{
				getPhoto();
			} else
			{
				ShowMessageByHandler(
						getResources().getString(
								R.string.POST_PIC_EORROR_NOLOGIN), 3);
			}
		case R.id.takephoto:
			if (ZhangWoApp.getInstance().isLogin())
				getPhoto();
			else
				ShowMessageByHandler(
						getResources().getString(
								R.string.POST_PIC_EORROR_NOLOGIN), 3);
		}
	}

	public void onCommit()
	{
		this.submitForm = true;
		StringBuilder localStringBuilder;
		if (this.aidlist.size() != 0)
		{
			localStringBuilder = new StringBuilder();
			localStringBuilder.append("[");
			for (int i = 0; i < aidlist.size(); i++)
			{
				localStringBuilder.append("'")
						.append((String) this.aidlist.get(i)).append("'")
						.append(",");
			}
			String str1 = localStringBuilder.toString();
			String str2 = str1.substring(0, -1 + str1.length()) + "]";
			this.webinterface.GotoUrl("javascript:SubmitForm(" + str2 + ")");
		}
		this.webinterface.GotoUrl("javascript:SubmitForm()");
	}

	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		_initNavBar(true);
		_initToolBar(true);
		Bundle bundle1 = getIntent().getExtras();
		if (bundle1 != null)
			try
			{
				fid = (new JSONObject(bundle1.getString("params")))
						.optString("fid");
			} catch (JSONException jsonexception)
			{
				jsonexception.printStackTrace();
			}
		DEBUG.o(fid);
		webinterface.setListener(this);
		webinterface
				.setOnPageLoad(new com.tianshan.source.view.DWebView.onPageLoad()
				{

					public void pageError()
					{}

					public void pageFinished(WebView webview, String s)
					{
						String as[] = s.split("&")[1].split("=");
						fid = as[1];
					}

					public void pageStart(WebView webview, String s)
					{}
				});
	}

	public void onFinish(String paramString)
	{
		if (this.submitId)
		{
			this.submitId = false;
			new Timer().schedule(new TimerTask()
			{
				public void run()
				{
					ShowMessageByHandler(
							getResources().getString(R.string.POST_PIC_SUSSECC),
							1);
					ReplyActivity.progress.setVisibility(8);
				}
			}, 300L);
		} else
		{
			if (this.submitForm)
			{
				this.submitForm = false;
				try
				{
					DEBUG.i(paramString + "   = op");
					JSONObject localJSONObject = new JSONObject(paramString)
							.getJSONObject("Message");
					String str1 = localJSONObject.getString("messageval");
					String str2 = localJSONObject.getString("messagestr");
					DEBUG.i(str2);
					if (!"post_reply_succeed".equals(str1))
						ShowMessage.getInstance(this)._showToast(str2, 2);
				} catch (JSONException localJSONException)
				{
					localJSONException.printStackTrace();
				}
				ShowMessageByHandler(R.string.REPLY_SUSSECC, 1);
				setResult(-1, new Intent(this, ViewthreadViewActivity.class));
				finish();
			}
		}
	}
}