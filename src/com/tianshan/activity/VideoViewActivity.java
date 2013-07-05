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

public class VideoViewActivity extends SecondLevelActivity implements
		Navbar.OnNavigateListener, View.OnClickListener, DWebView.onPageLoad,
		GestureDetector.OnGestureListener, View.OnTouchListener
{
	public String Videoid;
	private int backToNav = 0;
	protected View btnComment = null;
	protected ImageView btnFavImageView = null;
	protected View btnFavourite = null;
	protected View btnShare = null;
	private int curPos;
	private int currentType;
	final FavoriteHelper dbHelper = FavoriteHelper.getInstance(this);
	private GestureDetector detector;
	protected boolean favSelect = false;
	private String[] idlist;
	private boolean load = false;
	private int minVelocity = 0;
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
										VideoViewActivity.this,
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

	protected void _initNavBar(boolean paramBoolean)
	{
		super._initNavBar(paramBoolean);
		Navbar localNavbar = new Navbar(this, this.navbarbox);
		localNavbar.setCommitBtnVisibility(false);
		localNavbar.setOnNavigate(this);
		localNavbar.setTitle("查看视频");
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

	public boolean isFavSelect()
	{
		if (ZhangWoApp.getInstance().isLogin())
		{
			long l = ZhangWoApp.getInstance().getUserSession().getUid();
			if (this.dbHelper.isInFavorites(3, this.Videoid, l))
			{
				this.favSelect = true;
				this.btnFavImageView
						.setImageResource(R.drawable.favourite_select);
			}
		}
		if ((!ZhangWoApp.getInstance().isLogin()) || (!this.favSelect))
		{
			this.favSelect = false;
			this.btnFavImageView.setImageResource(R.drawable.favourite);
		}
		return this.favSelect;
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
		if (this.load)
		{
			if (i == this.btnComment.getId())
			{
				Intent localIntent1 = new Intent();
				localIntent1.setClass(this, CommentActivity.class);
				localIntent1.putExtra("commid", this.Videoid);
				localIntent1.putExtra("type", "video");
				startActivity(localIntent1);
			} else
			{
				if (i == this.btnShare.getId())
				{
					Intent localIntent2 = new Intent();
					localIntent2.putExtra("type", "视频");
					localIntent2.putExtra("title", this.stitle);
					String[] arrayOfString = new String[3];
					arrayOfString[0] = "ac=video";
					arrayOfString[1] = "op=pcview";
					arrayOfString[2] = ("id=" + this.Videoid);
					localIntent2.putExtra("pcurl",
							SiteTools.getPcUrl(arrayOfString));
					new ShareAdapter(this).createShareDialog(localIntent2);
				} else if (i == this.btnFavourite.getId())
				{
					if (isFavSelect())
						setFavSelect(false);
					else
						setFavSelect(true);
				}
			}
		}
	}

	public void onCommit()
	{}

	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		_initNavBar(true);
		_initToolBar(true);
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
				Videoid = (new JSONObject(bundle1.getString("params")))
						.optString("id");
			} catch (JSONException jsonexception)
			{
				ShowMessageByHandler(0x7f07000b, 2);
			}
		} else
		{
			ShowMessageByHandler(0x7f07000a, 2);
		}
		isFavSelect();
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

	public boolean onFling(MotionEvent paramMotionEvent1,
			MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
	{
		boolean flag = true;
		try
		{
			if (("".equals(this.stitle)) || ("undefined".equals(this.stitle)))
			{
				ShowMessageByHandler(R.string.message_pageload_notcompleted, 2);
			}
			if (Math.abs(paramMotionEvent1.getX() - paramMotionEvent2.getX()) <= Math
					.abs(paramMotionEvent1.getY() - paramMotionEvent2.getY()))
			{
				flag = false;
			}
			if ((paramMotionEvent1.getX() - paramMotionEvent2.getX() > this.verticalMinDistance)
					&& (Math.abs(paramFloat1) > this.minVelocity) && !flag)
				if ("0".equals(this.nextid))
					ShowMessageByHandler(R.string.message_notice_nopreid_video,
							2);
		} catch (NullPointerException localNullPointerException)
		{
			ShowMessageByHandler(R.string.message_pageload_notcompleted, 2);
		}
		if ("save".equals(getIntent().getExtras().getString("from")))
		{
			this.curPos = (1 + this.curPos);
			Log.d("pos", "  <----- " + this.curPos);
		}
		BaseWebInterFace localBaseWebInterFace2 = this.webinterface;
		String[] arrayOfString2 = new String[3];
		arrayOfString2[0] = "ac=video";
		arrayOfString2[1] = "op=view";
		arrayOfString2[2] = ("id=" + this.nextid);
		localBaseWebInterFace2.GotoUrl(SiteTools.getSiteUrl(arrayOfString2));
		this.Videoid = this.nextid;
		this.favSelect = false;
		isFavSelect();
		if ((paramMotionEvent2.getX() - paramMotionEvent1.getX() > this.verticalMinDistance)
				&& (Math.abs(paramFloat1) > this.minVelocity) && flag)
			if ("0".equals(this.preid))
			{
				ShowMessageByHandler(R.string.message_notice_nonextid_video, 2);
			} else
			{
				if ("save".equals(getIntent().getExtras().getString("from")))
				{
					this.curPos = (-1 + this.curPos);
					Log.d("pos", "  -----> " + this.curPos);
				}
				BaseWebInterFace localBaseWebInterFace1 = this.webinterface;
				String[] arrayOfString1 = new String[3];
				arrayOfString1[0] = "ac=video";
				arrayOfString1[1] = "op=view";
				arrayOfString1[2] = ("id=" + this.preid);
				localBaseWebInterFace1.GotoUrl(SiteTools
						.getSiteUrl(arrayOfString1));
				this.Videoid = this.preid;
				this.favSelect = false;
				isFavSelect();
			}
		return false;
	}

	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
	{
		boolean flag = true;
		if ((paramInt == 4) && (paramKeyEvent.getRepeatCount() == 0))
		{
			if (this.backToNav == 1)
			{
				goToNav();
			} else
			{
				finish();
			}
		} else
		{
			if (paramInt == 82)
				flag = false;
		}
		return flag;
	}

	public void onLongPress(MotionEvent paramMotionEvent)
	{}

	protected void onResume()
	{
		super.onResume();
		this.webinterface.Referer();
	}

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
		return this.detector.onTouchEvent(paramMotionEvent);
	}

	public void pageError()
	{}

	public void pageFinished(WebView paramWebView, String paramString)
	{
		if ("save".equals(getIntent().getExtras().getString("from")))
		{
			if ((this.curPos > 0) && (1 + this.curPos < this.idlist.length))
			{
				this.preid = this.idlist[(-1 + this.curPos)];
				this.nextid = this.idlist[(1 + this.curPos)];
			} else
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
			}
		} else
		{
			try
			{
				String as[] = webinterface.getNewsTitle().trim()
						.split("\\$\\$");
				String s1;
				if (as[0] == null)
					s1 = "";
				else
					s1 = as[0];
				stitle = s1;
				if (as.length == 3)
				{
					String s2;
					String s3;
					if (as[1] == null)
						s2 = "0";
					else
						s2 = as[1];
					preid = s2;
					if (as[2] == null)
						s3 = "0";
					else
						s3 = as[2];
					nextid = s3;
				}
			} catch (NullPointerException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void pageStart(WebView paramWebView, String paramString)
	{}

	public void setFavSelect(boolean paramBoolean)
	{
		if (ZhangWoApp.getInstance().isLogin())
		{
			long l = ZhangWoApp.getInstance().getUserSession().getUid();
			this.favSelect = paramBoolean;
			if (paramBoolean)
			{
				this.btnFavImageView
						.setImageResource(R.drawable.favourite_select);
				try
				{
					if (("".equals(this.stitle))
							|| ("undefined".equals(this.stitle)))
						ShowMessageByHandler(
								R.string.message_pageload_notcompleted, 2);
					else
						this.dbHelper.insert(3, this.Videoid, this.stitle, l);
				} catch (NullPointerException localNullPointerException)
				{
					ShowMessageByHandler(
							R.string.message_pageload_notcompleted, 2);
				}
			} else
			{
				this.btnFavImageView.setImageResource(R.drawable.favourite);
				this.dbHelper.delete(3, this.Videoid, l);
			}
		} else
		{
			popupLoginDialog();
		}
	}
}