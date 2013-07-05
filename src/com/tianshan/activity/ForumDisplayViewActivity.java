package com.tianshan.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tianshan.R;
import com.tianshan.ZhangWoApp;
import com.tianshan.activity.tab.SecondNavManager;
import com.tianshan.dbhelper.FavoriteHelper;
import com.tianshan.source.DEBUG;
import com.tianshan.source.HttpRequest;
import com.tianshan.source.SiteTools;
import com.tianshan.source.activity.SecondLevelActivity;
import com.tianshan.source.view.Navbar;
import com.tianshan.source.webinterface.BaseWebInterFace;

public class ForumDisplayViewActivity extends SecondLevelActivity implements
		Navbar.OnNavigateListener, View.OnClickListener
{
	protected View btnComment = null;
	protected ImageView btnFavImageView = null;
	protected View btnFavourite = null;
	protected View btnShare = null;
	final FavoriteHelper dbHelper = FavoriteHelper.getInstance(this);
	public String fid;
	public String forumname;
	public String fup;
	private boolean isFavourite = false;
	private JSONObject list;
	private SecondNavManager.OnSecondNavBtnClick onSecondNavBtnClick = new SecondNavManager.OnSecondNavBtnClick()
	{
		public void onBtnMoreClick(int paramAnonymousInt)
		{}

		public void onClick(int paramAnonymousInt1, int paramAnonymousInt2)
		{
			switch (paramAnonymousInt2)
			{
			default:
				return;
			case 0:
				BaseWebInterFace basewebinterface2;
				String as2[];
				if (toolshow)
					toolview.setVisibility(0);
				else
					_initToolBar(true);
				Log.d("fid",
						(new StringBuilder(String.valueOf(fid))).append(
								"   ************").toString());
				basewebinterface2 = webinterface;
				as2 = new String[2];
				as2[0] = "ac=forumdisplay";
				as2[1] = (new StringBuilder("fid=")).append(fid).toString();
				basewebinterface2.GotoUrl(SiteTools.getMobileUrl(as2));
				break;
			case 1:
				BaseWebInterFace basewebinterface1 = webinterface;
				String as1[] = new String[2];
				as1[0] = "ac=forum";
				as1[1] = (new StringBuilder("fup=")).append(fup).toString();
				basewebinterface1.GotoUrl(SiteTools.getMobileUrl(as1));
				toolview.setVisibility(8);
				break;
			case 2:
				BaseWebInterFace basewebinterface = webinterface;
				String as[] = new String[3];
				as[0] = "ac=forumdisplay";
				as[1] = (new StringBuilder("fid=")).append(fid).toString();
				as[2] = "toplist=yes";
				basewebinterface.GotoUrl(SiteTools.getMobileUrl(as));
				toolview.setVisibility(8);
				break;
			}
		}
	};
	private SecondNavManager secondNavManager;
	private LinearLayout sub_navbar;
	private boolean toolshow = false;
	private View toolview;
	private String url;

	private void _initSubNav()
	{
		this.sub_navbar = ((LinearLayout) findViewById(R.id.subnav));
		this.sub_navbar.setVisibility(0);
		this.secondNavManager = new SecondNavManager(this);
		List localList = createFORUMSubNavData();
		this.secondNavManager.FillNavDataByMapData(7, localList, false);
		this.secondNavManager.ClickBtnByIndex(0);
		this.secondNavManager.SetOnSecondNavBtnClick(this.onSecondNavBtnClick);
	}

	private List<HashMap<String, Object>> createFORUMSubNavData()
	{
		ArrayList localArrayList = new ArrayList();
		HashMap localHashMap1 = new HashMap();
		localHashMap1.put("id", Integer.valueOf(0));
		localHashMap1.put("name", "帖子列表");
		localArrayList.add(localHashMap1);
		if (Integer.valueOf(this.fup).intValue() > 0)
		{
			HashMap localHashMap2 = new HashMap();
			localHashMap2.put("id", Integer.valueOf(1));
			localHashMap2.put("name", "子版");
			localArrayList.add(localHashMap2);
		}
		HashMap localHashMap3 = new HashMap();
		localHashMap3.put("id", Integer.valueOf(2));
		localHashMap3.put("name", "置顶");
		localArrayList.add(localHashMap3);
		return localArrayList;
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
										ForumDisplayViewActivity.this,
										LoginActivity.class);
								ForumDisplayViewActivity.this
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
		localNavbar.setTitle(this.forumname);
		localNavbar.setCommitBtnVisibility(false);
	}

	protected void _initToolBar(boolean paramBoolean)
	{
		super._initToolBar(paramBoolean);
		this.toolview = LayoutInflater.from(this).inflate(
				R.layout.forum_forum_bt, null);
		this.btnComment = this.toolview.findViewById(R.id.btn_comment);
		this.btnFavourite = this.toolview.findViewById(R.id.btn_favourite);
		this.btnFavImageView = ((ImageView) this.toolview
				.findViewById(R.id.btn_fav_image));
		this.btnComment.setClickable(true);
		this.btnFavourite.setClickable(true);
		this.btnComment.setOnClickListener(this);
		this.btnFavourite.setOnClickListener(this);
		this.toolbox.addView(this.toolview);
		this.toolshow = true;
		if (paramBoolean)
			getisFavourite();
	}

	public boolean getisFavourite()
	{
		if (ZhangWoApp.getInstance().isLogin())
		{
			long l = ZhangWoApp.getInstance().getUserSession().getUid();
			if (this.dbHelper.isInFavorites(5, this.fid + "|" + this.fup, l))
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
			this.webinterface
					.GotoUrl("{\"ac\":\"forumdisplay\",\"fid\":\""
							+ this.fid
							+ "\",\"forumname\":\""
							+ this.forumname
							+ "\",\"fup\":\""
							+ this.fup
							+ "\",\"target\":\"1\",\"direction\":\"0\",\"op\":\"\",\"url\":\""
							+ this.url + "\"}");
			finish();
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
		case R.id.btn_comment:
			if ((ZhangWoApp.getInstance().isLogin())
					|| ("1".equals(this.list.optString("allowpost"))))
			{
				String[] arrayOfString = new String[2];
				arrayOfString[0] = "ac=newthread";
				arrayOfString[1] = ("fid=" + this.fid);
				String str = SiteTools.getMobileUrl(arrayOfString);
				Log.d("asdasd", this.fid);
				this.webinterface
						.GotoUrl("{\"ac\":\"newthread\",\"fid\":\""
								+ this.fid
								+ "\",\"target\":\"1\",\"direction\":\"0\",\"op\":\"\",\"url\":\""
								+ str + "\"}");
			} else
			{
				popupLoginDialog();
			}
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

	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		Bundle localBundle = getIntent().getExtras();
		if (localBundle != null)
		{
			Log.d("fid", "  bundle != null");
			try
			{
				JSONObject localJSONObject = new JSONObject(
						localBundle.getString("params"));
				Log.d("fid", localBundle.getString("params")
						+ "  bundle != null");
				this.fid = localJSONObject.optString("fid");
				this.url = localJSONObject.optString("url");
				Log.d("view", this.url);
				String str1 = localJSONObject.optString("id");
				Log.d("fid", str1 + "  id");
				if ((str1 != null) && (!str1.equals("")))
					this.fid = str1;
				String str2;
				HttpRequest localHttpRequest;
				if ("".equals(localJSONObject.optString("fup")))
				{
					str2 = "0";
				} else
				{
					str2 = localJSONObject.optString("fup");
				}
				try
				{
					this.fup = str2;
					com.tianshan.setting.Setting.POST_FUD = this.fup;
					this.forumname = localJSONObject.optString("forumname");
					localHttpRequest = new HttpRequest();
					String[] arrayOfString = new String[1];
					arrayOfString[0] = ("ac=checkallow&fid=" + this.fid);
					String str3 = localHttpRequest._get(SiteTools
							.getMobileUrl(arrayOfString));
					DEBUG.o(str3);
					this.list = new JSONObject(str3).optJSONObject("res")
							.optJSONObject("list");
					DEBUG.o("************" + this.list);

				} catch (Exception localException)
				{
					localException.printStackTrace();
					ShowMessageByHandler(R.string.message_error_dataintent, 2);
				}
			} catch (JSONException localJSONException)
			{
				ShowMessageByHandler(R.string.message_error_jsondata, 2);
			}
			_initNavBar(true);
			_initSubNav();
			_initToolBar(true);
		}
	}

	public void setisFavourite(boolean paramBoolean)
	{
		long l;
		HttpRequest localHttpRequest;
		if (ZhangWoApp.getInstance().isLogin())
		{
			l = ZhangWoApp.getInstance().getUserSession().getUid();
			this.isFavourite = paramBoolean;
			if (this.isFavourite)
			{
				this.btnFavImageView
						.setImageResource(R.drawable.favourite_select);
				this.dbHelper.insert(5, this.fid + "|" + this.fup,
						this.forumname, l);
				ArrayList localArrayList = new ArrayList();
				localArrayList.add(ZhangWoApp.getInstance().getUserSession()
						.getAuth());
				localArrayList.add(ZhangWoApp.getInstance().getUserSession()
						.getSaltkey());
				localHttpRequest = new HttpRequest(localArrayList);
				try
				{
					String[] arrayOfString = new String[2];
					arrayOfString[0] = "ac=favforum";
					arrayOfString[1] = ("id=" + this.fid);
					localHttpRequest
							._get(SiteTools.getMobileUrl(arrayOfString));
				} catch (Exception localException)
				{
					localException.printStackTrace();
				}
			} else
			{
				this.btnFavImageView.setImageResource(R.drawable.favourite);
				this.dbHelper.delete(5, this.fid + "|" + this.fup, l);
			}
		} else
		{
			popupLoginDialog();
		}
	}
}