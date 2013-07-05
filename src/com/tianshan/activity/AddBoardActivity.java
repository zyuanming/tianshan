package com.tianshan.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tianshan.R;
import com.tianshan.ZhangWoApp;
import com.tianshan.activity.tab.TabBar;
import com.tianshan.setting.Setting;
import com.tianshan.setting.WoPreferences;
import com.tianshan.source.DEBUG;
import com.tianshan.source.GPSUtil;
import com.tianshan.source.HttpRequest;
import com.tianshan.source.SiteTools;
import com.tianshan.source.activity.SecondLevelActivity;
import com.tianshan.source.view.DWebView;
import com.tianshan.source.view.Navbar;
import com.tianshan.source.webinterface.BaseWebInterFace;

public class AddBoardActivity extends SecondLevelActivity implements
		Navbar.OnNavigateListener, BaseWebInterFace.OnWebFinishListener,
		View.OnClickListener
{
	private static final int CAMERA_WITH_DATA = 102;
	private static final int PARSE_ADDRESS = 103;
	public static final int REQUEST_IMAGE = 101;
	private String address;
	ArrayList<String> aidlist = new ArrayList();
	private ImageView getGps;
	private ImageView getPhotoBtn;
	private Handler handler = new Handler(new Handler.Callback()
	{
		public boolean handleMessage(Message paramAnonymousMessage)
		{
			progress.setVisibility(8);
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
			this.mGpsInfo.setText("你还没定位");
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
			this.mGpsInfo.setText("正在定位中...");
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
				Toast.makeText(getApplicationContext(), "无法获取位置信息", 1).show();
				this.mprogress.setVisibility(4);
				this.mGpsTag.setVisibility(0);
				this.mGpsInfo.setText("无法获取位置信息");
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

	private String postfile(String paramString)
	{
		String str1 = SiteTools.getSiteUrl(new String[] { "ac=uploadattach" });
		String str2 = ZhangWoApp.getInstance().getUserSession()
				.getmobile_auth();
		return new HttpRequest()._postFile(str1, paramString, str2);
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
		if (this.mWindowManager.getDefaultDisplay().getHeight() == 800)
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
				AddBoardActivity.this.preferences.edit()
						.putBoolean("guide5", false).commit();
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
		localNavbar.setTitle("我要报料");
		localNavbar.setCommitBtnText(R.string.nav_btn_commit);
		localNavbar.setCommitBtnVisibility(true);
	}

	protected void _initToolBar(boolean paramBoolean)
	{
		super._initToolBar(paramBoolean);
		View localView = LayoutInflater.from(this).inflate(
				R.layout.post_bottom, null);
		this.getPhotoBtn = ((ImageView) localView.findViewById(R.id.getphoto));
		this.takePhotoBtn = ((ImageView) localView.findViewById(R.id.takephoto));
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
		this.mGpsInfo.setOnClickListener(this);
		this.getGps.setOnClickListener(this);
		this.selectAddress.setOnClickListener(this);
		this.takePhotoBtn.setOnClickListener(this);
		this.getPhotoBtn.setOnClickListener(this);
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
							String s2 = postfile(imagePath);
							if (s2.endsWith("-2"))
								ShowMessageByHandler(
										"\u56FE\u7247\u592A\u5927\u4E86", 1);
							else if (!s2.endsWith("-1") && !"".equals(s2))
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
								ShowMessageByHandler(0x7f070065, 3);
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
							String s2 = postfile(imagePath);
							if (s2.endsWith("-2"))
								ShowMessageByHandler(
										"\u56FE\u7247\u592A\u5927\u4E86", 1);
							else if (!s2.endsWith("-1") && !"".equals(s2))
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
								ShowMessageByHandler(0x7f070065, 3);
							}
						}
					})).start();
				}
				break;
			case 103:
				if (intent.getStringExtra("delete") != null
						&& !intent.getStringExtra("delete").equals(""))
				{
					GpsSetting(true);
				} else
				{
					Log.d("broad", " broad  onActivityResult ");
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
			getPhoto();
			break;
		case R.id.takephoto:
			takePhoto();
			break;
		case R.id.selectaddress:
			if (loc != null)
			{
				Intent intent = new Intent();
				intent.setClass(this, ParseAddressActivity.class);
				intent.putExtra("type", "addboard");
				intent.putExtra("xy",
						(new StringBuilder(String.valueOf(loc[0]))).append(",")
								.append(loc[1]).toString());
				intent.putExtra("info", address);
				startActivityForResult(intent, 103);
			}
			break;
		case R.id.getgps:
			if (isSwitch)
				GpsSetting(true);
			else if (!isSwitch)
				GpsSetting(false);
			break;
		case R.id.gpsinfo:
			mprogress.setVisibility(0);
			mGpsTag.setVisibility(8);
			mGpsInfo.setText("\u6B63\u5728\u5B9A\u4F4D\u4E2D...");
			loc = (new GPSUtil(getApplicationContext()))
					.setLatitudeAndLongitude();
			if (loc != null)
			{
				String s = (new StringBuilder(String.valueOf(loc[0])))
						.append(",").append(loc[1]).toString();
				Setting.SUBMITGPS = true;
				webinterface.GotoUrl((new StringBuilder(
						"javascript:xy2address('")).append(s).append("')")
						.toString());
			} else
			{
				Toast.makeText(getApplicationContext(),
						"\u65E0\u6CD5\u83B7\u53D6\u4F4D\u7F6E\u4FE1\u606F", 1)
						.show();
				mprogress.setVisibility(4);
				mGpsTag.setVisibility(0);
				mGpsInfo.setText("\u65E0\u6CD5\u83B7\u53D6\u4F4D\u7F6E\u4FE1\u606F");
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
			for (int i = 0; i < this.aidlist.size(); i++)
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

	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		_initNavBar(true);
		_initToolBar(true);
		this.preferences = getSharedPreferences("application_tab", 0);
		if (this.preferences.getBoolean("guide5", false))
			setFrame();
		this.webinterface.setListener(this);
		this.webinterface.setOnPageLoad(new DWebView.onPageLoad()
		{
			public void pageError()
			{}

			public void pageFinished(WebView paramAnonymousWebView,
					String paramAnonymousString)
			{
				if ((WoPreferences.getLBSType() == null)
						|| (WoPreferences.getLBSType().equals("clear")))
				{
					AddBoardActivity.this.mGpsTag.setVisibility(0);
					AddBoardActivity.this.mprogress.setVisibility(4);
					AddBoardActivity.this.GpsSetting(true);
				} else
				{
					AddBoardActivity.this.GpsSetting(false);
				}
			}

			public void pageStart(WebView paramAnonymousWebView,
					String paramAnonymousString)
			{
				mGpsTag.setVisibility(0);
				mprogress.setVisibility(4);
				mGpsInfo.setText("无定位信号");
			}
		});
	}

	public void onFinish(String s)
	{
		DEBUG.i(s);
		if (!submitId)
		{
			try
			{
				if (submitForm)
					submitForm = false;
				Log.d("msg", s);
				JSONObject jsonobject1 = new JSONObject(s);
				if (jsonobject1 != null)
				{
					String s1 = jsonobject1.getJSONObject("msg").getString(
							"msgvar");
					DEBUG.o(s1);
					if ("send_success".equals(s1))
					{
						ShowMessageByHandler("\u53D1\u5E03\u6210\u529F", 1);
						setResult(-1, new Intent(this, TabBar.class));
						finish();
					}
					if ("to_login".equals(s1))
						ShowMessageByHandler(0x7f070010, 3);
					else if ("message_too_frequent".equals(s1))
						ShowMessageByHandler(0x7f07000e, 3);
					else if ("send_faild".equals(s1))
						ShowMessageByHandler(0x7f07000f, 3);
					if (Setting.SUBMITGPS)
					{
						Setting.SUBMITGPS = false;
						try
						{
							JSONObject jsonobject = new JSONObject(s);
							mprogress.setVisibility(4);
							mGpsTag.setVisibility(0);
							mGpsInfo.setText(jsonobject.getString("address"));
							address = jsonobject.getString("address");
							getGps.setImageResource(0x7f020018);
							mGpsInfo.setText(address);
							selectAddress.setClickable(true);
							mGpsInfo.setClickable(true);
							Setting.ADDINFO = true;
							webinterface.GotoUrl((new StringBuilder(
									"javascript:addInfo('")).append(loc[0])
									.append("','").append(loc[1]).append("','")
									.append(address).append("')").toString());
						} catch (JSONException jsonexception)
						{
							jsonexception.printStackTrace();
						}
					} else if (Setting.CLEARINFO)
						Setting.CLEARINFO = false;
					else if (Setting.ADDINFO)
						Setting.ADDINFO = false;
				}
			} catch (JSONException jsonexception1)
			{
				jsonexception1.printStackTrace();
			}
		} else
		{
			submitId = false;
			Log.d("msg", s);
			if (s.equals("success"))
				ShowMessageByHandler(getResources().getString(0x7f070064), 1);
			else
				ShowMessageByHandler(0x7f070065, 3);
		}
	}
}