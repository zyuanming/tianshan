package com.tianshan.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.tianshan.source.activity.SecondLevelActivity;
import com.tianshan.source.view.DWebView;
import com.tianshan.source.view.Navbar;

public class NearPeopleViewActivity extends SecondLevelActivity implements
		Navbar.OnNavigateListener
{
	protected View btnComment = null;
	private String name;
	private Navbar navbar;

	protected void _initNavBar(boolean paramBoolean)
	{
		super._initNavBar(paramBoolean);
		this.navbar = new Navbar(this, this.navbarbox);
		this.navbar.setOnNavigate(this);
		this.navbar.setCommitBtnVisibility(false);
	}

	public void onBack()
	{
		finish();
	}

	public void onCommit()
	{}

	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		_initNavBar(true);
		this.webinterface.setOnPageLoad(new DWebView.onPageLoad()
		{
			public void pageError()
			{}

			public void pageFinished(WebView paramAnonymousWebView,
					String paramAnonymousString)
			{}

			public void pageStart(WebView webview, String s)
			{
				String as[] = s.split("&");
				int i = 0;
				do
				{
					if (i >= as.length)
						return;
					String s1 = as[i];
					if (s1.contains("name"))
					{
						String as1[] = s1.split("=");
						name = as1[1];
						try
						{
							String s2 = URLDecoder.decode(name, "UTF-8");
							navbar.setTitle(s2);
						} catch (UnsupportedEncodingException unsupportedencodingexception)
						{
							unsupportedencodingexception.printStackTrace();
						}
					}
					i++;
				} while (true);
			}
		});
	}
}