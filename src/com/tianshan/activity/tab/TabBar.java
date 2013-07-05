package com.tianshan.activity.tab;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.tianshan.R;
import com.tianshan.ZhangWoApp;
import com.tianshan.activity.LoginActivity;
import com.tianshan.activity.NavigationBar;
import com.tianshan.activity.iMenuPopupWindow;
import com.tianshan.dbhelper.SubnavHelper;
import com.tianshan.model.Subnavisement;
import com.tianshan.setting.Setting;
import com.tianshan.source.SiteTools;
import com.tianshan.source.activity.BaseActivity;
import com.tianshan.source.webinterface.BaseWebInterFace;

public class TabBar extends BaseActivity
{
	private int backToNav = 0;
	private Button btn_I_PopMenu;
	private int cur_subnav_index = -1;
	private FirstNavManager firstNavManager;
	private int fupval;
	private TextView header_title;
	private boolean isreload = false;
	private ListView listview;
	private ImageView mLogo;
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mWindowParams;
	// 底部的菜单栏
	private FirstNavManager.OnBtnClickListener onBottomBtnClick = new FirstNavManager.OnBtnClickListener()
	{
		public void onBtnClick(int i, String s)
		{
			secondNavManager.FillNavDataByType(i);
			fupval = i;
			if (cur_subnav_index >= 0)
			{
				secondNavManager.ClickBtnByIndex(cur_subnav_index);
				cur_subnav_index = 0;
			}
			if (secondNavManager.GetArrayBtnSize() <= 0)
				webinterface.ClearView();
			header_title.setText(s);
		}
	};
	private View.OnClickListener onIPopMenuClick = new View.OnClickListener()
	{
		public void onClick(View view)
		{
			if (popWindow == null)
				popWindow = new iMenuPopupWindow(TabBar.this);
			if (!popWindow.isShowing())
			{
				popWindow.showPopWindow(view);
				preferences = getSharedPreferences("application_tab", 0);
				if (preferences.getBoolean("guide2", false))
					setFrame();
			} else
			{
				popWindow.dismiss();
			}
		}
	};
	// 上部的菜单栏
	protected SecondNavManager.OnSecondNavBtnClick onSecondNavBtnClick = new SecondNavManager.OnSecondNavBtnClick()
	{
		// 更多
		public void onBtnMoreClick(int paramAnonymousInt)
		{
			addMoreSubnav(paramAnonymousInt);
		}

		public void onClick(int i, int j)
		{
			checkNetAndReload();
			if (listview != null)
				listview.setVisibility(8);
			dwebview.clearView();
			BaseActivity.listshow = false;
			BaseActivity.setProgressGone(true);
			dwebview.setVisibility(8);
			switch (i)
			{
			case 0:
			case 1:
			default:
				return;
			case 2:
				dealNewsSubnavClick(j);
				break;
			case 3:
				BaseWebInterFace basewebinterface1 = webinterface;
				String as1[] = new String[3];
				as1[0] = "ac=video";
				as1[1] = "op=list";
				as1[2] = (new StringBuilder("cid=")).append(j).toString();
				basewebinterface1.GotoUrl(SiteTools.getSiteUrl(as1));
				break;
			case 4: // 图片
				BaseWebInterFace basewebinterface = webinterface;
				String as[] = new String[3];
				as[0] = "ac=pic";
				as[1] = "op=list";
				as[2] = (new StringBuilder("cid=")).append(j).toString();
				basewebinterface.GotoUrl(SiteTools.getSiteUrl(as));
				break;
			case -1: // 论坛
				dealBBSSubnavClick(j);
				break;
			case 5: // 投稿
				dealBoardSubnavClick(j);
				break;
			}
		}
	};
	protected AdapterView.OnItemClickListener onitemclicklistener = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> paramAnonymousAdapterView,
				View paramAnonymousView, int paramAnonymousInt,
				long paramAnonymousLong)
		{
			if (listview != null)
				listview.setVisibility(8);
			dwebview.clearView();
			BaseActivity.listshow = false;
			BaseActivity.setProgressGone(true);
			dwebview.setVisibility(8);
			paramAnonymousView.setBackgroundColor(getResources().getColor(
					R.color.list_item_selector_color));
			SubnavHelper localSubnavHelper = SubnavHelper
					.getInstance(TabBar.this);
			Map localMap = (Map) listview.getItemAtPosition(paramAnonymousInt);
			new Subnavisement();
			Subnavisement localSubnavisement1 = localSubnavHelper
					.get(secondNavManager.GetBtnIdByIndex(3));
			localSubnavisement1.setIsset(0);
			localSubnavHelper.save(localSubnavisement1);
			new Subnavisement();
			Subnavisement localSubnavisement2 = localSubnavHelper
					.get(((Integer) localMap.get("id")).intValue());
			localSubnavisement2.setIsset(2);
			localSubnavHelper.save(localSubnavisement2);
			secondNavManager.FillNavDataByType(fupval);
			int i = -2 + secondNavManager.GetArrayBtnSize();
			switch (fupval)
			{
			default:
				return;
			case 2:
			case 3:
			case 4:
				secondNavManager.ClickBtnByIndex(i);
				break;
			}
		}
	};
	private iMenuPopupWindow popWindow;
	private int pushfrom = 0;
	private SecondNavManager secondNavManager;

	private void _writeCookie()
	{
		CookieManager localCookieManager;
		String[] arrayOfString;
		if (ZhangWoApp.getInstance().isLogin())
		{
			String str = ZhangWoApp.getInstance().getUserSession()
					.getWebViewCookies();
			if (str != null)
			{
				CookieSyncManager.createInstance(this);
				localCookieManager = this.dwebview.getCookieManager();
				localCookieManager.acceptCookie();
				localCookieManager.removeAllCookie();
				arrayOfString = str.split(";");
				for (int i = 0;; i++)
				{
					if (i >= arrayOfString.length)
					{
						Log.d("auth",
								"cookie  tar= "
										+ localCookieManager
												.getCookie(".xjts.cn"));
						return;
					}
					localCookieManager.setCookie(".xjts.cn", arrayOfString[i]);
					CookieSyncManager.getInstance().sync();
				}
			}
		}
	}

	private void addMoreSubnav(int paramInt)
	{
		BaseActivity.listshow = true;
		BaseActivity.setProgressGone(false);
		this.dwebview.setVisibility(8);
		SimpleAdapter localSimpleAdapter = new SimpleAdapter(this, getData(
				paramInt, "getFupByisset"), R.layout.subnav_list_row,
				new String[] { "id", "fup", "name" }, new int[] { R.id.id,
						R.id.fup, R.id.name });
		this.listview.setAdapter(localSimpleAdapter);
		this.listview.setVisibility(0);
		this.listview.setDividerHeight(0);
	}

	private void checkNetAndReload()
	{
		if (!isHaveNetwork())
		{
			isreload = true;
		} else
		{
			if (isreload)
			{
				finish();
				Intent intent = new Intent();
				intent.setClass(this, TabBar.class);
				intent.putExtra("fupval", fupval);
				intent.putExtra("subnav", cur_subnav_index);
				startActivity(intent);
			}
			isreload = false;
		}
	}

	private int getBundleData(Bundle paramBundle, String paramString)
	{
		int i = 0;
		if (paramBundle != null)
			i = paramBundle.getInt(paramString);
		return i;
	}

	private List getData(int i, String s)
	{
		SubnavHelper subnavhelper = SubnavHelper.getInstance(this);
		List list;
		if (s.equals("getFup"))
			list = subnavhelper.getFup(i);
		else if (s.equals("getFupByisset"))
			list = subnavhelper.getFupByisset(i);
		else
			list = subnavhelper.getAll();
		return list;
	}

	private void goToNav()
	{
		Intent localIntent = new Intent();
		localIntent.setClass(this, NavigationBar.class);
		localIntent.setFlags(335544320); // 335544320 ,14000000

		// PendingIntent.FLAG_UPDATE_CURRENT:表示如果该描述的PendingIntent已存在，
		// 则改变已存在的PendingIntent的Extra数据为新的PendingIntent的Extra数据。
		PendingIntent localPendingIntent = PendingIntent.getActivity(this, 100,
				localIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		try
		{
			localPendingIntent.send();
			finish();
		} catch (PendingIntent.CanceledException localCanceledException)
		{
			localCanceledException.printStackTrace();
		}
	}

	private void initlist()
	{
		this.listview = new ListView(this);
		this.listview.setCacheColorHint(0);
		this.listview.setScrollingCacheEnabled(true);
		ViewGroup.LayoutParams localLayoutParams = new ViewGroup.LayoutParams(
				-1, -2);
		this.viewbox.addView(this.listview, localLayoutParams);
		this.viewbox.setBackgroundColor(-657931);
		this.listview.setVisibility(8);
		this.listview.setOnItemClickListener(this.onitemclicklistener);
	}

	/**
	 * 弹出登录对话框
	 * 
	 * @param paramString
	 */
	private void popupLoginDialog(final String paramString)
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
								BaseWebInterFace localBaseWebInterFace = webinterface;
								String[] arrayOfString = new String[2];
								arrayOfString[0] = "ac=board";
								arrayOfString[1] = ("op=" + paramString);
								localBaseWebInterFace.GotoUrl(SiteTools
										.getSiteUrl(arrayOfString));
								Intent localIntent = new Intent(TabBar.this,
										LoginActivity.class);
								startActivity(localIntent);
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener()
						{
							public void onClick(
									DialogInterface paramAnonymousDialogInterface,
									int paramAnonymousInt)
							{
								paramAnonymousDialogInterface.dismiss();
								secondNavManager.ClickBtnByIndex(0);
							}
						}).create().show();
	}

	/**
	 * 显示用户指引
	 */
	private void setFrame()
	{
		this.mWindowManager = ((WindowManager) getSystemService("window"));
		ImageView localImageView = new ImageView(getApplicationContext());
		localImageView.setScaleType(ImageView.ScaleType.FIT_XY);
		if (!ZhangWoApp.getInstance().isLogin())
		{
			Log.d("login", "false****************");
			if (this.mWindowManager.getDefaultDisplay().getHeight() == 854)
				localImageView.setImageResource(R.drawable.login_navigation854);
			else
			{
				if (mWindowManager.getDefaultDisplay().getHeight() == 800)
					localImageView
							.setImageResource(R.drawable.login_navigation800);
				else if (mWindowManager.getDefaultDisplay().getHeight() == 480)
					localImageView
							.setImageResource(R.drawable.login_navigation480);
			}
		} else
		{
			Log.d("login", "true****************");
			if (this.mWindowManager.getDefaultDisplay().getHeight() == 854)
				localImageView
						.setImageResource(R.drawable.unlogin_navigation854);
			else if (this.mWindowManager.getDefaultDisplay().getHeight() == 800)
				localImageView
						.setImageResource(R.drawable.unlogin_navigation800);
			else if (this.mWindowManager.getDefaultDisplay().getHeight() == 480)
				localImageView
						.setImageResource(R.drawable.unlogin_navigation480);
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
		localImageView.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				mWindowManager.removeView(paramAnonymousView);
				preferences.edit().putBoolean("guide2", false).commit();
			}
		});
	}

	/**
	 * 论坛板块
	 * 
	 * @param paramInt
	 */
	protected void dealBBSSubnavClick(int paramInt)
	{
		switch (paramInt)
		{
		default:
			return;
		case 0:
			webinterface.GotoUrl(SiteTools
					.getMobileUrl(new String[] { "ac=hotthread" }));
			break;
		case 1:
			webinterface.GotoUrl(SiteTools
					.getMobileUrl(new String[] { "ac=digestthread" }));
			break;
		case 2:
			webinterface.GotoUrl(SiteTools
					.getMobileUrl(new String[] { "ac=viewnewthread" }));
			break;
		case 3:
			webinterface.GotoUrl(SiteTools
					.getMobileUrl(new String[] { "ac=forum" }));
			break;
		}
	}

	protected void dealBoardSubnavClick(int paramInt)
	{
		switch (paramInt)
		{
		default:
			return;
		case 0:
			webinterface.GotoUrl(SiteTools.getSiteUrl(new String[] {
					"ac=board", "op=list" }));
		case 1:
			if (!ZhangWoApp.getInstance().isLogin())
				popupLoginDialog("dep");
			else
				webinterface.GotoUrl(SiteTools.getSiteUrl(new String[] {
						"ac=board", "op=dep" }));
			break;
		case 2:
			if (!ZhangWoApp.getInstance().isLogin())
				popupLoginDialog("my");
			else
				webinterface.GotoUrl(SiteTools.getSiteUrl(new String[] {
						"ac=board", "op=my" }));
			break;
		}
	}

	protected void dealNewsSubnavClick(int paramInt)
	{
		switch (paramInt)
		{
		default:
			BaseWebInterFace localBaseWebInterFace = this.webinterface;
			String[] arrayOfString = new String[3];
			arrayOfString[0] = "ac=news";
			arrayOfString[1] = "op=list";
			arrayOfString[2] = ("cid=" + paramInt);
			localBaseWebInterFace.GotoUrl(SiteTools.getSiteUrl(arrayOfString));
		case 1:
			webinterface.GotoUrl(SiteTools.getSiteUrl(new String[] { "ac=news",
					"op=toplist" }));
			break;
		case 6:
			if (pushfrom == 1)
			{
				pushfrom = 0;
				webinterface.GotoUrl(getIntent().getExtras().getString("url"));
			} else
			{
				webinterface.GotoUrl(SiteTools.getSiteUrl(new String[] {
						"ac=news", "op=specialindex" }));
			}
			break;
		}
	}

	protected void initOnClickListener()
	{
		super.initOnClickListener();
		this.btn_I_PopMenu.setOnClickListener(this.onIPopMenuClick);
	}

	protected void initWidget()
	{
		super.initWidget();
		viewbox = ((FrameLayout) findViewById(R.id.view_box));
		header_title = ((TextView) findViewById(R.id.header_title));
		btn_I_PopMenu = ((Button) findViewById(R.id.header_info));
		// 点击“手机天山网”LOGO事件，退回到主界面
		mLogo = ((ImageView) findViewById(R.id.header_logo));
		mLogo.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				for (int i = 0;; i++)
				{
					if (i >= Setting.activitylist.size())
					{
						Intent localIntent = new Intent(TabBar.this
								.getApplicationContext(), NavigationBar.class);
						startActivity(localIntent);
						return;
					}
					if (Setting.activitylist.get(i) != null)
						((Activity) Setting.activitylist.get(i)).finish();
				}
			}
		});
		this.header_title.setText(this.firstNavManager.GetCurrentHeaderText());
	}

	protected void onActivityResult(int paramInt1, int paramInt2,
			Intent paramIntent)
	{
		super.onActivityResult(paramInt1, paramInt2, paramIntent);
		if ((paramInt2 == -1) && (paramInt1 == 1001))
		{
			this.secondNavManager.ClickBtnByIndex(2);
			this.webinterface
					.GotoUrl("{\"ac\":\"board\",\"target\":\"0\",\"direction\":\"0\",\"op\":\"my\",\"url\":\"page.php?ac=board&op=my\"}");
		}
	}

	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		setContentView(R.layout.tabbar);
		new SiteTools(this);
		Bundle localBundle = getIntent().getExtras();
		if ("pushmsg".equals(localBundle.getString("from")))
		{
			pushfrom = 1;
			backToNav = 1;
		}
		cur_subnav_index = getBundleData(localBundle, "subnav");
		firstNavManager = new FirstNavManager(this);
		firstNavManager.SetOnBtnClickListener(onBottomBtnClick);
		initWidget();
		addWebView();
		initlist();
		initOnClickListener();
		fupval = getBundleData(localBundle, "fupval");
		secondNavManager = new SecondNavManager(this);
		secondNavManager.SetOnSecondNavBtnClick(onSecondNavBtnClick);
		firstNavManager.ClickBtnByTypeId(fupval);
	}

	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
	{
		int i = 1;
		boolean flag = true;
		if ((paramInt == KeyEvent.KEYCODE_BACK)
				&& (paramKeyEvent.getRepeatCount() == 0))
		{
			if (this.backToNav == i)
				goToNav();
			else
				finish();
		} else
		{
			if (i == KeyEvent.KEYCODE_MENU)
				flag = false;
		}
		return flag;
	}

	protected void onRestart()
	{
		super.onRestart();
		if ((!ZhangWoApp.getInstance().isLogin()) && (this.fupval == 5))
			this.secondNavManager.ClickBtnByIndex(0);
	}
}