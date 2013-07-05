package com.tianshan.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tianshan.R;
import com.tianshan.ZhangWoApp;
import com.tianshan.setting.Setting;
import com.tianshan.setting.WoPreferences;
import com.tianshan.source.DEBUG;
import com.tianshan.source.GPSUtil;
import com.tianshan.source.HttpsRequest;
import com.tianshan.source.ShowMessage;
import com.tianshan.source.SiteTools;
import com.tianshan.source.activity.SecondLevelActivity;
import com.tianshan.source.view.DWebView;
import com.tianshan.source.view.Navbar;
import com.tianshan.source.webinterface.BaseWebInterFace;

public class PostActivity extends SecondLevelActivity implements
		Navbar.OnNavigateListener, View.OnClickListener,
		BaseWebInterFace.OnWebFinishListener
{
	private static final int CAMERA_WITH_DATA = 102;
	private static final int PARSE_ADDRESS = 103;
	public static final int REQUEST_IMAGE = 101;
	private String address;
	ArrayList<String> aidlist = new ArrayList();
	private String fid;
	private ImageView getGps;
	private ImageView getPhotoBtn;
	private Handler handler = new Handler(new Handler.Callback()
	{
		public boolean handleMessage(Message paramAnonymousMessage)
		{
			PostActivity.progress.setVisibility(8);
			String str = paramAnonymousMessage.getData().getString("code");
			webinterface.GotoUrl("javascript:getImgId(" + str + ")");
			return false;
		}
	});
	private String imagePath;
	private boolean isSwitch = true;
	private double[] loc;
	private TextView mGpsInfo;
	private ImageView mGpsTag;
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mWindowParams;
	private ProgressBar mprogress;
	private Uri photoUri;
	private ImageView selectAddress;
	private boolean submitForm = false;
	private boolean submitId = false;
	private ImageView takePhotoBtn;

	private void GpsSetting(boolean paramBoolean)
	{
		if (paramBoolean)
		{
			WoPreferences.setLBSType("clear");
			this.isSwitch = false;
			this.getGps.setImageResource(R.drawable.gps_off);
			this.mprogress.setVisibility(4);
			this.mGpsTag.setVisibility(0);
			this.mGpsInfo.setText(R.string.NO_GPS);
			this.selectAddress.setClickable(false);
			this.mGpsInfo.setClickable(false);
			Setting.CLEARINFO = true;
			this.webinterface.GotoUrl("javascript:clearInfo()");
		} else
		{
			WoPreferences.setLBSType("add");
			this.isSwitch = true;
			this.mprogress.setVisibility(0);
			this.getGps.setImageResource(R.drawable.gps_on);
			this.mGpsTag.setVisibility(8);
			this.mGpsInfo.setText(R.string.geting_gps);
			this.loc = new GPSUtil(getApplicationContext())
					.setLatitudeAndLongitude();
			if (this.loc != null)
			{
				String str = this.loc[0] + "," + this.loc[1];
				Setting.SUBMITGPS = true;
				this.webinterface.GotoUrl("javascript:xy2address('" + str
						+ "')");
			} else
			{
				Toast.makeText(getApplicationContext(), R.string.get_gps_error,
						1).show();
				this.mprogress.setVisibility(4);
				this.mGpsTag.setVisibility(0);
				this.mGpsInfo.setText(R.string.get_gps_error);
			}
		}
	}

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

	private void setBtnclickable(boolean paramBoolean)
	{
		if (paramBoolean)
		{
			this.getPhotoBtn.setClickable(true);
			this.takePhotoBtn.setClickable(true);
			this.getGps.setClickable(true);
			this.selectAddress.setClickable(true);
		} else
		{
			this.getPhotoBtn.setClickable(false);
			this.takePhotoBtn.setClickable(false);
			this.getGps.setClickable(false);
			this.selectAddress.setClickable(false);
		}
	}

	private void setFrame()
	{
		this.mWindowManager = ((WindowManager) getSystemService("window"));
		ImageView localImageView = new ImageView(getApplicationContext());
		localImageView.setScaleType(ImageView.ScaleType.FIT_XY);
		if (this.mWindowManager.getDefaultDisplay().getHeight() == 854)
			localImageView.setImageResource(R.drawable.send_navigation854);
		else if (this.mWindowManager.getDefaultDisplay().getHeight() == 800)
			localImageView.setImageResource(R.drawable.send_navigation800);
		else if (this.mWindowManager.getDefaultDisplay().getHeight() == 480)
			localImageView.setImageResource(R.drawable.send_navigation480);
		localImageView.bringToFront();
		this.mWindowParams = new WindowManager.LayoutParams();
		this.mWindowParams.gravity = 17;
		this.mWindowParams.height = this.mWindowManager.getDefaultDisplay()
				.getHeight();
		this.mWindowParams.width = this.mWindowManager.getDefaultDisplay()
				.getWidth();
		this.mWindowParams.flags = 392;
		this.mWindowParams.format = -3;
		this.mWindowParams.windowAnimations = 0;
		this.mWindowManager.addView(localImageView, this.mWindowParams);
		setBtnclickable(false);
		localImageView.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				setBtnclickable(true);
				mWindowManager.removeView(paramAnonymousView);
				preferences.edit().putBoolean("guide4", false).commit();
			}
		});

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

	protected void _initNavBar(boolean paramBoolean)
	{
		super._initNavBar(paramBoolean);
		Navbar localNavbar = new Navbar(this, this.navbarbox);
		localNavbar.setOnNavigate(this);
		localNavbar.setCommitBtnVisibility(true);
		localNavbar.setCommintBtnText(R.string.POST_TITLE_COMMINT);
		localNavbar.setTitle(R.string.POST_TITLE);
	}

	protected void _initToolBar(boolean paramBoolean)
	{
		super._initToolBar(paramBoolean);
		View localView = LayoutInflater.from(this).inflate(
				R.layout.post_bottom, null);
		this.mprogress = ((ProgressBar) localView
				.findViewById(R.id.gpsprogress));
		this.mGpsInfo = ((TextView) localView.findViewById(R.id.gpsinfo));
		this.mGpsTag = ((ImageView) localView.findViewById(R.id.gpstag));
		this.selectAddress = ((ImageView) localView
				.findViewById(R.id.selectaddress));
		this.getPhotoBtn = ((ImageView) localView.findViewById(R.id.getphoto));
		this.takePhotoBtn = ((ImageView) localView.findViewById(R.id.takephoto));
		this.getGps = ((ImageView) localView.findViewById(R.id.getgps));
		this.getPhotoBtn.setClickable(true);
		this.takePhotoBtn.setClickable(true);
		this.getGps.setOnClickListener(this);
		this.selectAddress.setOnClickListener(this);
		this.takePhotoBtn.setOnClickListener(this);
		this.getPhotoBtn.setOnClickListener(this);
		this.mGpsInfo.setOnClickListener(this);
		this.toolbox.addView(localView);
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

	protected void onActivityResult(int paramInt1, int paramInt2, Intent intent)
	{
		super.onActivityResult(paramInt1, paramInt2, intent);
		if (paramInt2 == -1)
			switch (paramInt1)
			{
			default:
				return;
			case 101:
				if (intent != null)
				{
					Uri uri = intent.getData();
					Cursor cursor = getContentResolver().query(uri, null, null,
							null, null);
					int k = cursor.getColumnIndexOrThrow("_data");
					cursor.moveToFirst();
					imagePath = cursor.getString(k);
					progress.setVisibility(0);
					(new Thread(new Runnable()
					{

						public void run()
						{
							String s2 = postBitmap(imagePath);
							if (!s2.endsWith("-1"))
							{
								submitId = true;
								Message message = new Message();
								Bundle bundle = new Bundle();
								bundle.putString("code", s2);
								message.setData(bundle);
								handler.sendMessage(message);
								aidlist.add(s2);
							} else
							{
								ShowMessageByHandler(
										getResources().getString(0x7f070065), 3);
							}
						}
					})).start();
				}
				break;
			case 102:
				File file = new File(imagePath);
				if (file.length() == 0L)
				{
					file.delete();
				} else
				{
					progress.setVisibility(0);
					(new Thread(new Runnable()
					{

						public void run()
						{
							String s2 = postBitmap(imagePath);
							if (!s2.endsWith("-1"))
							{
								submitId = true;
								Message message = new Message();
								Bundle bundle = new Bundle();
								bundle.putString("code", s2);
								message.setData(bundle);
								handler.sendMessage(message);
								aidlist.add(s2);
							} else
							{
								ShowMessageByHandler(
										getResources().getString(0x7f070065), 3);
							}
						}
					})).start();
				}
				break;
			case 103:
				if (intent.getStringExtra("delete") != null
						&& intent.getStringExtra("delete").equals("delete"))
				{
					GpsSetting(true);
				} else
				{
					address = intent.getStringExtra("info");
					String s = intent.getStringExtra("x");
					String s1 = intent.getStringExtra("y");
					double ad[] = new double[2];
					ad[0] = Double.parseDouble(s);
					ad[1] = Double.parseDouble(s1);
					loc = ad;
					mprogress.setVisibility(4);
					mGpsTag.setVisibility(0);
					mGpsInfo.setText(address);
					getGps.setImageResource(0x7f020018);
					mGpsInfo.setText(address);
					selectAddress.setClickable(true);
					mGpsInfo.setClickable(true);
					Setting.ADDINFO = true;
					webinterface.GotoUrl((new StringBuilder(
							"javascript:addInfo('")).append(loc[0])
							.append("','").append(loc[1]).append("','")
							.append(address).append("')").toString());
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
			break;
		case R.id.takephoto:
			if (ZhangWoApp.getInstance().isLogin())
			{
				takePhoto();
			} else
			{
				ShowMessageByHandler(
						getResources().getString(
								R.string.POST_PIC_EORROR_NOLOGIN), 3);
			}
			break;
		case R.id.selectaddress:
			if (this.loc != null)
			{
				Intent localIntent = new Intent();
				localIntent.setClass(this, ParseAddressActivity.class);
				localIntent.putExtra("type", "post");
				localIntent.putExtra("xy", this.loc[0] + "," + this.loc[1]);
				localIntent.putExtra("info", this.address);
				startActivityForResult(localIntent, 103);
			}
			break;
		case R.id.getgps:
			if (this.isSwitch)
			{
				GpsSetting(true);
			} else if (!this.isSwitch)
			{
				GpsSetting(false);
			}
			break;
		case R.id.gpsinfo:
			this.mprogress.setVisibility(0);
			this.mGpsTag.setVisibility(8);
			this.mGpsInfo.setText(R.string.geting_gps);
			this.loc = new GPSUtil(getApplicationContext())
					.setLatitudeAndLongitude();
			if (this.loc != null)
			{
				String str = this.loc[0] + "," + this.loc[1];
				Setting.SUBMITGPS = true;
				this.webinterface.GotoUrl("javascript:xy2address('" + str
						+ "')");
			} else
			{
				Toast.makeText(getApplicationContext(), R.string.get_gps_error,
						1).show();
				this.mprogress.setVisibility(4);
				this.mGpsTag.setVisibility(0);
				this.mGpsInfo.setText(R.string.get_gps_error);
			}
			break;
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
		if (preferences.getBoolean("guide4", false))
			setFrame();
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
					{
						Log.d("aa", "pageError");
					}

					public void pageFinished(WebView webview, String s)
					{
						if (WoPreferences.getLBSType() == null
								|| WoPreferences.getLBSType().equals("clear"))
						{
							mGpsTag.setVisibility(0);
							mprogress.setVisibility(4);
							GpsSetting(true);
						} else
						{
							GpsSetting(false);
						}
					}

					public void pageStart(WebView webview, String s)
					{
						CookieSyncManager.createInstance(PostActivity.this);
						String s1 = dwebview.getCookieManager().getCookie(
								"http://client.xjts.cn/admin/");
						Log.d("auth", (new StringBuilder("cookie post = "))
								.append(s1).toString());
						mGpsTag.setVisibility(0);
						mprogress.setVisibility(4);
						mGpsInfo.setText("\u65E0\u5B9A\u4F4D\u4FE1\u606F!");
					}
				});
	}

	public void onFinish(String paramString)
	{
		DEBUG.o(paramString);
		if (this.submitId)
		{
			this.submitId = false;
			if (paramString.equals("success"))
				ShowMessageByHandler(
						getResources().getString(R.string.POST_PIC_SUSSECC), 1);
			else
				ShowMessageByHandler(R.string.POST_PIC_EORROR, 3);
		} else
		{
			if (this.submitForm)
			{
				this.submitForm = false;
				try
				{
					JSONObject localJSONObject2 = new JSONObject(paramString)
							.getJSONObject("Message");
					String str1 = localJSONObject2.getString("messageval");
					String str2 = localJSONObject2.getString("messagestr");
					DEBUG.i(str2);
					if (!"post_newthread_succeed".equals(str1))
						ShowMessage.getInstance(this)._showToast(str2, 2);
				} catch (JSONException localJSONException2)
				{
					localJSONException2.printStackTrace();
				}
				ShowMessageByHandler(R.string.POST_SUSSECC, 1);
				setResult(-1, new Intent(this, ForumDisplayViewActivity.class));
				finish();
			} else if (Setting.SUBMITGPS)
			{
				Setting.SUBMITGPS = false;
				try
				{
					JSONObject localJSONObject1 = new JSONObject(paramString);
					this.mprogress.setVisibility(4);
					this.mGpsTag.setVisibility(0);
					this.mGpsInfo
							.setText(localJSONObject1.getString("address"));
					this.address = localJSONObject1.getString("address");
					this.getGps.setImageResource(R.drawable.gps_on);
					this.mGpsInfo.setText(this.address);
					this.selectAddress.setClickable(true);
					this.mGpsInfo.setClickable(true);
					Setting.ADDINFO = true;
					this.webinterface.GotoUrl("javascript:addInfo('"
							+ this.loc[0] + "','" + this.loc[1] + "','"
							+ this.address + "')");
				} catch (JSONException localJSONException1)
				{
					localJSONException1.printStackTrace();
				}
			} else if (Setting.CLEARINFO)
			{
				Setting.CLEARINFO = false;
			} else if (Setting.ADDINFO)
			{
				Setting.ADDINFO = false;
			}
		}
	}
}