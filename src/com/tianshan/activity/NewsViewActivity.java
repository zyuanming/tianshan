package com.tianshan.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;

import com.tianshan.R;
import com.tianshan.ZhangWoApp;
import com.tianshan.adapter.ShareAdapter;
import com.tianshan.dbhelper.FavoriteHelper;
import com.tianshan.source.DEBUG;
import com.tianshan.source.SiteTools;
import com.tianshan.source.activity.SecondLevelActivity;
import com.tianshan.source.view.DWebView;
import com.tianshan.source.view.Navbar;
import com.tianshan.source.webinterface.BaseWebInterFace;

public class NewsViewActivity extends SecondLevelActivity implements
		Navbar.OnNavigateListener, View.OnClickListener, DWebView.onPageLoad,
		GestureDetector.OnGestureListener, View.OnTouchListener
{
	private int backToNav = 0;
	protected View btnComment = null;
	protected ImageView btnFavImageView = null;
	protected View btnFavourite = null;
	protected View btnShare = null;
	private int curPos;
	private int currentType;
	final FavoriteHelper dbHelper = FavoriteHelper.getInstance(this);
	private GestureDetector detector;
	private boolean frame = false;
	private String[] idlist;
	private boolean isFavourite = false;
	private boolean load = false;
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mWindowParams;
	private int minVelocity = 0;
	public String newsid;
	private String newspcurl = null;
	private String nextid = "0";
	private String preid = "0";
	private String stitle = null;
	private int verticalMinDistance = 120;

	private void getFavouriteNews(int i)
	{
		long l = ZhangWoApp.getInstance().getUserSession().getUid();
		SQLiteDatabase sqlitedatabase = dbHelper.getWritableDatabase();
		String as[] = new String[2];
		as[0] = (new StringBuilder()).append(i).toString();
		as[1] = (new StringBuilder()).append(l).toString();
		Cursor cursor = sqlitedatabase.query("favorites", null,
				"news_type = ? AND news_uid = ?", as, null, null, null);
		Log.d("a", (new StringBuilder(String.valueOf(cursor.getCount())))
				.toString());
		StringBuffer stringbuffer = new StringBuffer();
		do
		{
			if (!cursor.moveToNext())
			{
				String s = stringbuffer.toString();
				idlist = s.substring(0, -1 + s.length()).split(",");
				return;
			}
			stringbuffer.append(
					cursor.getString(cursor.getColumnIndex("news_id"))).append(
					",");
		} while (true);
	}

	private void goToNav()
	{
		Intent localIntent = new Intent();
		localIntent.setClass(this, NavigationBar.class);
		localIntent.setFlags(335544320);
		PendingIntent localPendingIntent = PendingIntent.getActivity(this, 100,
				localIntent, 134217728);
		try
		{
			localPendingIntent.send();
			finish();
		} catch (PendingIntent.CanceledException localCanceledException)
		{
			localCanceledException.printStackTrace();
		}
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
								DEBUG.o("ok");
								Intent localIntent = new Intent(
										NewsViewActivity.this,
										LoginActivity.class);
								startActivity(localIntent);
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
								DEBUG.o("no");
							}
						}).create().show();
	}

	private void setBtnclickable(boolean paramBoolean)
	{
		if (paramBoolean)
		{
			this.btnComment.setClickable(true);
			this.btnShare.setClickable(true);
			this.btnFavourite.setClickable(true);
		} else
		{
			this.btnComment.setClickable(false);
			this.btnShare.setClickable(false);
			this.btnFavourite.setClickable(false);
		}
	}

	private void setFrame()
	{
		this.frame = true;
		this.mWindowManager = ((WindowManager) getSystemService("window"));
		ImageView localImageView = new ImageView(getApplicationContext());
		localImageView.setScaleType(ImageView.ScaleType.FIT_XY);
		if (this.mWindowManager.getDefaultDisplay().getHeight() == 854)
			localImageView.setImageResource(R.drawable.news_navigation854);
		else
		{
			if (this.mWindowManager.getDefaultDisplay().getHeight() == 800)
				localImageView.setImageResource(R.drawable.news_navigation800);
			else if (this.mWindowManager.getDefaultDisplay().getHeight() == 480)
				localImageView.setImageResource(R.drawable.news_navigation480);
		}
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
		localImageView.setOnTouchListener(this);
	}

	protected void _initNavBar(boolean paramBoolean)
	{
		super._initNavBar(paramBoolean);
		Navbar localNavbar = new Navbar(this, this.navbarbox);
		localNavbar.setOnNavigate(this);
		localNavbar.setTitle(R.string.viewnews_title);
		localNavbar.setCommitBtnVisibility(false);
	}

	protected void _initToolBar(boolean paramBoolean)
	{
		super._initToolBar(paramBoolean);
		View localView = LayoutInflater.from(this).inflate(
				R.layout.video_detail_bottom, null);
		this.btnComment = localView.findViewById(R.id.btn_comment);
		this.btnShare = localView.findViewById(R.id.btn_share);
		this.btnFavourite = localView.findViewById(R.id.btn_favourite);
		this.btnFavImageView = ((ImageView) localView
				.findViewById(R.id.btn_fav_image));
		this.btnComment.setClickable(true);
		this.btnShare.setClickable(true);
		this.btnFavourite.setClickable(true);
		this.btnComment.setOnClickListener(this);
		this.btnShare.setOnClickListener(this);
		this.btnFavourite.setOnClickListener(this);
		this.toolbox.addView(localView);
	}

	public boolean getisFavourite()
	{
		if (ZhangWoApp.getInstance().isLogin())
		{
			long l = ZhangWoApp.getInstance().getUserSession().getUid();
			if (this.dbHelper.isInFavorites(1, this.newsid, l))
			{
				this.isFavourite = true;
				this.btnFavImageView
						.setImageResource(R.drawable.favourite_select);
			}
		}
		if ((!ZhangWoApp.getInstance().isLogin()) || (!this.isFavourite))
		{
			this.isFavourite = false;
			this.btnFavImageView.setImageResource(R.drawable.favourite);
		}
		return this.isFavourite;
	}

	public void onBack()
	{
		if (backToNav == 1)
			goToNav();
		else
			finish();
	}

	public void onClick(View paramView)
	{
		int i = paramView.getId();
		Intent localIntent1 = new Intent();
		if (this.load)
			switch (i)
			{
			case R.id.btn_fav_image:
			default:
				return;
			case R.id.btn_comment:
				Intent localIntent2 = new Intent();
				localIntent2.setClass(this, CommentActivity.class);
				localIntent2.putExtra("commid", this.newsid);
				localIntent2.putExtra("type", "news");
				startActivity(localIntent2);
				break;
			case R.id.btn_share:
				localIntent1.setClass(this, ShareActivity.class);
				localIntent1.putExtra("type", "新闻");
				localIntent1.putExtra("title", this.stitle);
				String[] arrayOfString = new String[3];
				arrayOfString[0] = "ac=news";
				arrayOfString[1] = "op=pcview";
				arrayOfString[2] = ("id=" + this.newsid);
				localIntent1.putExtra("pcurl",
						SiteTools.getPcUrl(arrayOfString));
				new ShareAdapter(this).createShareDialog(localIntent1);
				break;
			case R.id.btn_favourite:
				if (getisFavourite())
					setisFavourite(false);
				else
					setisFavourite(true);
				break;
			}
	}

	public void onCommit()
	{}

	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		_initNavBar(true);
		_initToolBar(true);
		preferences = getSharedPreferences("application_tab", 0);
		if (preferences.getBoolean("guide3", false))
			setFrame();
		Bundle bundle1 = getIntent().getExtras();
		if (bundle1 != null)
		{
			if ("pushmsg".equals(getIntent().getExtras().getString("from")))
			{
				backToNav = 1;
				DEBUG.o(getIntent().getExtras().getString("from"));
			} else if ("save".equals(getIntent().getExtras().getString("from")))
			{
				currentType = getIntent().getExtras().getInt("currentType");
				curPos = getIntent().getExtras().getInt("position");
				getFavouriteNews(currentType);
			}
			try
			{
				newsid = (new JSONObject(bundle1.getString("params")))
						.optString("id");
			} catch (JSONException jsonexception)
			{
				ShowMessageByHandler(0x7f07000b, 2);
			}
		} else
		{
			ShowMessageByHandler(0x7f07000a, 2);
		}
		getisFavourite();
		dwebview.setOnPageLoad(this);
		detector = new GestureDetector(this);
		dwebview.setOnTouchListener(this);
		dwebview.setLongClickable(true);
		webinterface
				.setOnPageLoad(new com.tianshan.source.view.DWebView.onPageLoad()
				{

					public void pageError()
					{}

					public void pageFinished(WebView webview, String s)
					{
						load = true;
					}

					public void pageStart(WebView webview, String s)
					{}
				});
	}

	public boolean onDown(MotionEvent paramMotionEvent)
	{
		return false;
	}

	public boolean onFling(MotionEvent motionevent, MotionEvent motionevent1,
			float f, float f1)
	{
		boolean flag = true;
		try
		{
			if ("".equals(stitle) || "undefined".equals(stitle))
			{
				ShowMessageByHandler(R.string.message_pageload_notcompleted, 2);
			}
			NullPointerException nullpointerexception;
			if (Math.abs(motionevent.getX() - motionevent1.getX()) <= Math
					.abs(motionevent.getY() - motionevent1.getY()))
				flag = false;
			if (motionevent.getX() - motionevent1.getX() > (float) verticalMinDistance
					&& Math.abs(f) > (float) minVelocity && flag)
				if ("0".equals(nextid))
				{
					ShowMessageByHandler(R.string.message_notice_nopreid_news,
							2);
				}
		} catch (Exception e)
		{
			ShowMessageByHandler(R.string.message_pageload_notcompleted, 2);
		}
		if ("save".equals(getIntent().getExtras().getString("from")))
		{
			curPos = 1 + curPos;
			Log.d("pos", (new StringBuilder("  <----- ")).append(curPos)
					.toString());
		}
		BaseWebInterFace basewebinterface1 = webinterface;
		String as1[] = new String[3];
		as1[0] = "ac=news";
		as1[1] = "op=view";
		as1[2] = (new StringBuilder("id=")).append(nextid).toString();
		basewebinterface1.GotoUrl(SiteTools.getSiteUrl(as1));
		newsid = nextid;
		isFavourite = false;
		getisFavourite();
		if (motionevent1.getX() - motionevent.getX() > (float) verticalMinDistance
				&& Math.abs(f) > (float) minVelocity && flag)
			if ("0".equals(preid))
			{
				ShowMessageByHandler(R.string.message_notice_nonextid_news, 2);
			} else
			{
				if ("save".equals(getIntent().getExtras().getString("from")))
				{
					curPos = -1 + curPos;
					Log.d("pos", (new StringBuilder("  -----> "))
							.append(curPos).toString());
				}
				BaseWebInterFace basewebinterface = webinterface;
				String as[] = new String[3];
				as[0] = "ac=news";
				as[1] = "op=view";
				as[2] = (new StringBuilder("id=")).append(preid).toString();
				basewebinterface.GotoUrl(SiteTools.getSiteUrl(as));
				newsid = preid;
				isFavourite = false;
				getisFavourite();
			}
		return false;
	}

	public boolean onKeyDown(int i, KeyEvent keyevent)
	{
		boolean flag = true;
		if (i != 4 || keyevent.getRepeatCount() != 0)
		{
			if (i == 82)
				flag = false;
		} else
		{
			if (backToNav == 1)
				goToNav();
			else
				finish();
		}
		return flag;
	}

	public void onLongPress(MotionEvent paramMotionEvent)
	{}

	public boolean onScroll(MotionEvent paramMotionEvent1,
			MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
	{
		return false;
	}

	public void onShowPress(MotionEvent paramMotionEvent)
	{}

	public boolean onSingleTapUp(MotionEvent paramMotionEvent)
	{
		return false;
	}

	public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
	{
		switch (paramMotionEvent.getAction())
		{
		case 0:
		default:
			break;
		case 1:
			if (this.frame)
			{
				this.frame = false;
				setBtnclickable(true);
				this.mWindowManager.removeView(paramView);
				this.preferences.edit().putBoolean("guide3", false).commit();
			}
		}
		return this.detector.onTouchEvent(paramMotionEvent);
	}

	public void pageError()
	{}

	public void pageFinished(WebView webview, String s)
	{
		Log.d("finish", "get ID  NEXT  PRE");
		if (!"save".equals(getIntent().getExtras().getString("from")))
		{
			String s1 = webinterface.getNewsTitle();
			if (s1 != null)
			{
				String as[] = s1.trim().split("\\$\\$");
				String s2;
				if (as[0] == null)
					s2 = "";
				else
					s2 = as[0];
				stitle = s2;
				if (as.length == 3)
				{
					String s3;
					String s4;
					if (as[1] == null)
						s3 = "0";
					else
						s3 = as[1];
					preid = s3;
					if (as[2] == null)
						s4 = "0";
					else
						s4 = as[2];
					nextid = s4;
				}
			}
		} else
		{
			if (curPos <= 0 || 1 + curPos >= idlist.length)
			{
				if (curPos == 0 && idlist.length > 1)
				{
					preid = "0";
					nextid = idlist[1 + curPos];
				} else if (idlist.length == 1)
				{
					preid = "0";
					nextid = "0";
				} else if (1 + curPos == idlist.length)
				{
					preid = idlist[-1 + curPos];
					nextid = "0";
				}
			} else
			{
				preid = idlist[-1 + curPos];
				nextid = idlist[1 + curPos];
			}
		}
	}

	public void pageStart(WebView paramWebView, String paramString)
	{}

	public void setisFavourite(boolean flag)
	{
		if (ZhangWoApp.getInstance().isLogin())
		{
			long l = ZhangWoApp.getInstance().getUserSession().getUid();
			isFavourite = flag;
			if (isFavourite)
			{
				btnFavImageView.setImageResource(R.drawable.favourite_select);
				if ("".equals(stitle) || "undefined".equals(stitle))
					ShowMessageByHandler(
							R.string.message_pageload_notcompleted, 2);
				else
					dbHelper.insert(1, newsid, stitle, l);
			} else
			{
				btnFavImageView.setImageResource(R.drawable.favourite);
				dbHelper.delete(1, newsid, l);
			}
		} else
		{
			popupLoginDialog();
		}
	}
}