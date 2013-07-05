package com.tianshan.activity;

import java.util.ArrayList;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianshan.R;
import com.tianshan.ZhangWoApp;
import com.tianshan.adapter.ShareAdapter;
import com.tianshan.dbhelper.FavoriteHelper;
import com.tianshan.source.DEBUG;
import com.tianshan.source.HttpRequest;
import com.tianshan.source.SiteTools;
import com.tianshan.source.activity.SecondLevelActivity;
import com.tianshan.source.view.DWebView;
import com.tianshan.source.view.Navbar;
import com.tianshan.source.webinterface.BaseWebInterFace;

public class ViewthreadViewActivity extends SecondLevelActivity implements
		Navbar.OnNavigateListener, View.OnClickListener, DWebView.onPageLoad,
		GestureDetector.OnGestureListener, View.OnTouchListener
{
	public String authorid;
	private int backToNav = 0;
	protected View btnComment = null;
	protected ImageView btnFavImageView = null;
	protected View btnFavourite = null;
	protected View btnSeeAuthor = null;
	protected View btnShare = null;
	private int curPos;
	private int currentType;
	final FavoriteHelper dbHelper = FavoriteHelper.getInstance(this);
	private GestureDetector detector;
	private String fup;
	private String[] idlist;
	private boolean isFavourite = false;
	private JSONObject list;
	private boolean load = false;
	private int minVelocity = 0;
	private String nexttid = "0";
	private String pretid = "0";
	private String shareTitle = null;
	public String tid;
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
										ViewthreadViewActivity.this,
										LoginActivity.class);
								ViewthreadViewActivity.this
										.startActivity(localIntent);
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
		localNavbar.setOnNavigate(this);
		localNavbar.setTitle("帖子详情");
		localNavbar.setCommitBtnVisibility(true);
		this.btnSeeAuthor = ((Button) findViewById(R.id.nav_commit));
		this.btnSeeAuthor.setMinimumWidth(103);
		((TextView) this.btnSeeAuthor).setText(R.string.forum_bt_viewauthor);
		this.btnSeeAuthor.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				BaseWebInterFace localBaseWebInterFace = ViewthreadViewActivity.this.webinterface;
				String[] arrayOfString = new String[3];
				arrayOfString[0] = "ac=viewthread";
				arrayOfString[1] = ("tid=" + ViewthreadViewActivity.this.tid);
				arrayOfString[2] = ("authorid=" + ViewthreadViewActivity.this.authorid);
				localBaseWebInterFace.GotoUrl(SiteTools
						.getMobileUrl(arrayOfString));
			}
		});
	}

	protected void _initToolBar(boolean paramBoolean)
	{
		super._initToolBar(paramBoolean);
		View localView = LayoutInflater.from(this).inflate(
				R.layout.forum_viewthread_bt, null);
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
			if (this.dbHelper.isInFavorites(4, this.tid, l))
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

	protected void onActivityResult(int paramInt1, int paramInt2,
			Intent paramIntent)
	{
		super.onActivityResult(paramInt1, paramInt2, paramIntent);
		if ((paramInt2 == -1) && (paramInt1 == 1001))
		{
			this.dwebview.clearView();
			BaseWebInterFace localBaseWebInterFace = this.webinterface;
			String[] arrayOfString = new String[2];
			arrayOfString[0] = "ac=viewthread";
			arrayOfString[1] = ("tid=" + this.tid);
			localBaseWebInterFace
					.GotoUrl(SiteTools.getMobileUrl(arrayOfString));
		}
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
		Intent localIntent;
		if (this.load)
		{
			localIntent = new Intent();
			switch (i)
			{
			case R.id.btn_fav_image:
			default:
				return;
			case R.id.btn_comment:
				if ((ZhangWoApp.getInstance().isLogin())
						|| ("1".equals(this.list.optString("allowreply"))))
				{
					try
					{
						String str1 = this.webinterface.getNewsTitle().trim()
								.split("\\$\\$")[1];
						String[] arrayOfString2 = new String[3];
						arrayOfString2[0] = "ac=reply";
						arrayOfString2[1] = ("fid=" + str1);
						arrayOfString2[2] = ("tid=" + this.tid);
						String str2 = SiteTools.getMobileUrl(arrayOfString2);
						this.webinterface
								.GotoUrl("{\"ac\":\"reply\",\"fid\":\""
										+ str1
										+ "\",\"target\":\"1\",\"tid\":\""
										+ this.tid
										+ "\",\"direction\":\"0\",\"op\":\"\",\"url\":\""
										+ str2 + "\"}");
					} catch (NullPointerException localNullPointerException)
					{
						ShowMessageByHandler(
								R.string.message_pageload_notcompleted, 2);
					}
				} else
				{
					popupLoginDialog();
				}
				break;
			case R.id.btn_share:
				localIntent.setClass(this, ShareActivity.class);
				localIntent.putExtra("type", "帖子");
				localIntent.putExtra("title", this.shareTitle);
				String[] arrayOfString1 = new String[1];
				arrayOfString1[0] = ("ac=viewthread&tid=" + this.tid);
				localIntent.putExtra("pcurl",
						SiteTools.getPcUrl(arrayOfString1));
				new ShareAdapter(this).createShareDialog(localIntent);
				break;
			case R.id.btn_favourite:
				if (getisFavourite())
					setisFavourite(false);
				else
					setisFavourite(true);
				break;
			}
		}
	}

	public void onCommit()
	{}

	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		Bundle bundle1 = getIntent().getExtras();
		if (bundle1 != null)
		{
			try
			{
				HttpRequest httprequest;
				String as[];
				String s;
				httprequest = new HttpRequest();
				as = new String[1];
				as[0] = (new StringBuilder("ac=checkallow&fid=")).append(fup)
						.toString();
				s = httprequest._get(SiteTools.getMobileUrl(as));
				DEBUG.o(s);
				list = (new JSONObject(s)).optJSONObject("res").optJSONObject(
						"list");
				if ("pushmsg".equals(getIntent().getExtras().getString("from")))
				{
					backToNav = 1;
					DEBUG.o(getIntent().getExtras().getString("from"));
				} else if ("save".equals(getIntent().getExtras().getString(
						"from")))
				{
					currentType = getIntent().getExtras().getInt("currentType");
					curPos = getIntent().getExtras().getInt("position");
					getFavouriteNews(currentType);
				}

				JSONObject jsonobject = new JSONObject(
						bundle1.getString("params"));
				tid = jsonobject.optString("tid");
				fup = jsonobject.optString("fup");
				authorid = jsonobject.optString("authorid");
			} catch (JSONException jsonexception)
			{
				ShowMessageByHandler(0x7f07000b, 2);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		} else
		{
			ShowMessageByHandler(0x7f07000a, 2);
		}
		_initNavBar(true);
		_initToolBar(true);
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

					public void pageFinished(WebView webview, String s1)
					{
						load = true;
					}

					public void pageStart(WebView webview, String s1)
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
		String as[];
		String s = webinterface.getNewsTitle().trim();
		as = s.split("\\$\\$");
		try
		{
			if ("".equals(s) || "undefined".equals(s))
			{
				ShowMessageByHandler(0x7f07000c, 2);
			}
			if (Math.abs(motionevent.getX() - motionevent1.getX()) <= Math
					.abs(motionevent.getY() - motionevent1.getY()))
				flag = false;
			if (motionevent1.getX() - motionevent.getX() <= (float) verticalMinDistance
					|| Math.abs(f) <= (float) minVelocity || !flag)
				if ("0".equals(as[2]) || "0".endsWith(pretid))
				{
					ShowMessageByHandler(0x7f070015, 2);
				}
		} catch (Exception e)
		{
			ShowMessageByHandler(0x7f07000c, 2);
			e.printStackTrace();
		}
		if ("save".equals(getIntent().getExtras().getString("from")))
		{
			curPos = -1 + curPos;
			tid = pretid;
			BaseWebInterFace basewebinterface3 = webinterface;
			String as4[] = new String[2];
			as4[0] = "ac=viewthread";
			as4[1] = (new StringBuilder("tid=")).append(tid).toString();
			basewebinterface3.GotoUrl(SiteTools.getMobileUrl(as4));
			isFavourite = false;
			getisFavourite();
		} else
		{
			BaseWebInterFace basewebinterface2 = webinterface;
			String as3[] = new String[3];
			as3[0] = "ac=viewthread";
			as3[1] = (new StringBuilder("tid=")).append(as[2]).toString();
			as3[2] = (new StringBuilder("authorid=")).append(as[3]).toString();
			basewebinterface2.GotoUrl(SiteTools.getMobileUrl(as3));
			tid = as[2];
			authorid = as[3];
			isFavourite = false;
			getisFavourite();
		}
		if (motionevent.getX() - motionevent1.getX() > (float) verticalMinDistance
				&& Math.abs(f) > (float) minVelocity && flag)
			if ("0".equals(as[4]) || "0".endsWith(nexttid))
				ShowMessageByHandler(0x7f070016, 2);
			else if ("save".equals(getIntent().getExtras().getString("from")))
			{
				curPos = 1 + curPos;
				tid = nexttid;
				BaseWebInterFace basewebinterface1 = webinterface;
				String as2[] = new String[2];
				as2[0] = "ac=viewthread";
				as2[1] = (new StringBuilder("tid=")).append(tid).toString();
				basewebinterface1.GotoUrl(SiteTools.getMobileUrl(as2));
				isFavourite = false;
				getisFavourite();
			} else
			{
				BaseWebInterFace basewebinterface = webinterface;
				String as1[] = new String[3];
				as1[0] = "ac=viewthread";
				as1[1] = (new StringBuilder("tid=")).append(as[4]).toString();
				as1[2] = (new StringBuilder("authorid=")).append(as[5])
						.toString();
				basewebinterface.GotoUrl(SiteTools.getMobileUrl(as1));
				tid = as[4];
				authorid = as[5];
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
				this.pretid = this.idlist[(-1 + this.curPos)];
				this.nexttid = this.idlist[(1 + this.curPos)];
			} else
			{
				if (curPos == 0 && idlist.length > 1)
				{
					pretid = "0";
					nexttid = idlist[1 + curPos];
				} else if (idlist.length == 1)
				{
					pretid = "0";
					nexttid = "0";
				} else if (1 + curPos == idlist.length)
				{
					pretid = idlist[-1 + curPos];
					nexttid = "0";
				}
			}
		} else
		{
			try
			{
				this.shareTitle = this.webinterface.getNewsTitle().trim();
				this.shareTitle = this.shareTitle.split("\\$\\$")[0];
			} catch (Exception e)
			{
				e.printStackTrace();
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
				ArrayList arraylist = new ArrayList();
				arraylist.add(ZhangWoApp.getInstance().getUserSession()
						.getAuth());
				arraylist.add(ZhangWoApp.getInstance().getUserSession()
						.getSaltkey());
				HttpRequest httprequest = new HttpRequest(arraylist);
				try
				{
					String s = webinterface.getNewsTitle().trim();
					String as[] = s.split("\\$\\$");
					String s1;
					String as1[];
					if ("".equals(s) || "undefined".equals(s))
					{
						ShowMessageByHandler(0x7f07000c, 2);
					} else
					{
						dbHelper.insert(4, tid, as[0], l);
						btnFavImageView.setImageResource(0x7f020012);
					}
					s1 = ZhangWoApp.getInstance().getUserSession()
							.getFormhash();
					as1 = new String[4];
					as1[0] = "ac=favthread";
					as1[1] = (new StringBuilder("id=")).append(tid).toString();
					as1[2] = (new StringBuilder("formhash=")).append(s1)
							.toString();
					as1[3] = (new StringBuilder("fid=")).append(as[1])
							.toString();
					httprequest._get(SiteTools.getMobileUrl(as1));
				} catch (NullPointerException nullpointerexception)
				{
					ShowMessageByHandler(0x7f07000c, 2);
				} catch (Exception exception)
				{
					exception.printStackTrace();
				}
			} else
			{
				btnFavImageView.setImageResource(0x7f020011);
				dbHelper.delete(4, tid, l);
			}
		} else
		{
			popupLoginDialog();
		}
	}
}