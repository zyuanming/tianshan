package com.tianshan.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.tianshan.R;
import com.tianshan.source.activity.SecondLevelActivity;
import com.tianshan.source.view.DWebView;
import com.tianshan.source.view.Navbar;

public class ShowADActivity extends SecondLevelActivity implements
		Navbar.OnNavigateListener, View.OnClickListener
{
	protected View btnComment = null;
	protected ImageView btnFavImageView = null;
	protected View btnFavourite = null;
	protected View btnShare = null;
	private String mCurUrl;

	protected void _initNavBar(boolean paramBoolean)
	{
		super._initNavBar(paramBoolean);
		Navbar localNavbar = new Navbar(this, this.navbarbox);
		localNavbar.setOnNavigate(this);
		localNavbar.setTitle("");
		localNavbar.setCommitBtnVisibility(false);
	}

	protected void _initToolBar(boolean paramBoolean)
	{
		super._initToolBar(paramBoolean);
		View localView1 = LayoutInflater.from(this).inflate(
				R.layout.ad_detail_bottom, null);
		View localView2 = localView1.findViewById(R.id.btn_refresh);
		View localView3 = localView1.findViewById(R.id.btn_gotochrome);
		localView2.setOnClickListener(this);
		localView3.setOnClickListener(this);
		this.toolbox.addView(localView1);
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
		case R.id.btn_refresh:
			this.webinterface.GotoUrl(this.mCurUrl);
			break;
		case R.id.btn_gotochrome:
			Intent localIntent = new Intent();
			localIntent.setAction("android.intent.action.VIEW");
			localIntent.setData(Uri.parse(this.mCurUrl));
			localIntent.setClassName("com.android.browser",
					"com.android.browser.BrowserActivity");
			startActivity(localIntent);
			break;
		}
	}

	public void onCommit()
	{}

	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		_initNavBar(true);
		_initToolBar(true);
		this.webinterface.setOnPageLoad(new DWebView.onPageLoad()
		{
			public void pageError()
			{}

			public void pageFinished(WebView paramAnonymousWebView,
					String paramAnonymousString)
			{}

			public void pageStart(WebView paramAnonymousWebView,
					String paramAnonymousString)
			{
				mCurUrl = paramAnonymousString;
			}
		});
	}
}