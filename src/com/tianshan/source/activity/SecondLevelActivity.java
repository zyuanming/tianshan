package com.tianshan.source.activity;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tianshan.R;

public class SecondLevelActivity extends BaseActivity
{
	protected LinearLayout navbarbox;
	protected LinearLayout toolbox;

	protected void _initNavBar(boolean flag)
	{
		navbarbox = (LinearLayout) findViewById(0x7f090041);
		navbarbox
				.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(
						-1, -2));
		if (flag)
			navbarbox.setVisibility(0);
		else
			navbarbox.setVisibility(4);
	}

	protected void _initToolBar(boolean flag)
	{
		toolbox = (LinearLayout) findViewById(0x7f090042);
		if (flag)
			toolbox.setVisibility(0);
		else
			toolbox.setVisibility(4);
	}

	protected void initOnClickListener()
	{
		super.initOnClickListener();
	}

	protected void initWidget()
	{
		super.initWidget();
		this.viewbox = ((FrameLayout) findViewById(R.id.view_box));
	}

	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		setContentView(R.layout.second_level);
		Bundle localBundle = getIntent().getExtras();
		if (localBundle != null)
			this.url = localBundle.getString("url");
		initWidget();
		addWebView();
		initOnClickListener();
	}
}