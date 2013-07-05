package com.tianshan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.tianshan.R;
import com.tianshan.source.SiteTools;
import com.tianshan.source.activity.SecondLevelActivity;
import com.tianshan.source.view.DWebView;
import com.tianshan.source.view.Navbar;
import com.tianshan.source.webinterface.BaseWebInterFace;

public class ParseAddressActivity extends SecondLevelActivity implements
		Navbar.OnNavigateListener, BaseWebInterFace.OnWebFinishListener
{
	private String type;

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
		localNavbar.setCommintBtnText(R.string.delete_address);
		localNavbar.setTitle(R.string.address_title);
	}

	public void onBack()
	{
		finish();
	}

	public void onBackPressed()
	{
		super.onBackPressed();
		finish();
	}

	public void onCommit()
	{
		Intent localIntent = new Intent();
		localIntent.putExtra("delete", "delete");
		if (this.type.equals("post"))
		{
			localIntent.setClass(this, PostActivity.class);
		} else if (this.type.equals("addboard"))
		{
			localIntent.setClass(this, AddBoardActivity.class);
			setResult(-1, localIntent);
		}
		finish();
	}

	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		_initNavBar(true);
		this.type = getIntent().getStringExtra("type");
		String str1 = getIntent().getStringExtra("xy");
		String str2 = getIntent().getStringExtra("info");
		String[] arrayOfString = new String[4];
		arrayOfString[0] = "ac=map";
		arrayOfString[1] = "op=position";
		arrayOfString[2] = ("xy=" + str1);
		arrayOfString[3] = ("addr=" + str2);
		String str3 = SiteTools.getSiteUrl(arrayOfString);
		Log.d("url", str3);
		this.dwebview.loadUrl(str3);
		this.webinterface.setListener(this);
		this.webinterface.setOnPageLoad(new DWebView.onPageLoad()
		{
			public void pageError()
			{}

			public void pageFinished(WebView paramAnonymousWebView,
					String paramAnonymousString)
			{}

			public void pageStart(WebView paramAnonymousWebView,
					String paramAnonymousString)
			{}
		});
	}

	public void onFinish(String paramString)
	{
		Log.d("finish", paramString);
		String[] arrayOfString = paramString.split("\\|");
		Intent localIntent = new Intent();
		localIntent.putExtra("x", arrayOfString[0]);
		Log.d("finish", arrayOfString[0]);
		localIntent.putExtra("y", arrayOfString[1]);
		Log.d("finish", arrayOfString[1]);
		localIntent.putExtra("info", arrayOfString[2]);
		if (this.type.equals("post"))
			localIntent.setClass(this, PostActivity.class);
		else if (this.type.equals("addboard"))
			localIntent.setClass(this, AddBoardActivity.class);
		setResult(-1, localIntent);
		finish();
	}
}