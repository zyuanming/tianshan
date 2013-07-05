package com.tianshan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianshan.R;
import com.tianshan.activity.tab.SecondNavManager;
import com.tianshan.source.GPSUtil;
import com.tianshan.source.SiteTools;
import com.tianshan.source.activity.BaseActivity;
import com.tianshan.source.view.DWebView.onPageLoad;
import com.tianshan.source.webinterface.BaseWebInterFace;

public class NearbyInfo extends BaseActivity implements onPageLoad
{
	private Button btnBack = null;
	private LinearLayout columnNav;
	private String gps;
	private double[] loc;
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
				BaseWebInterFace localBaseWebInterFace4 = NearbyInfo.this.webinterface;
				String[] arrayOfString4 = new String[3];
				arrayOfString4[0] = "ac=lbs";
				arrayOfString4[1] = "type=news";
				arrayOfString4[2] = ("xy=" + NearbyInfo.this.gps);
				localBaseWebInterFace4.GotoUrl(SiteTools
						.getMobileUrl(arrayOfString4));
			case 1:
				BaseWebInterFace localBaseWebInterFace3 = NearbyInfo.this.webinterface;
				String[] arrayOfString3 = new String[3];
				arrayOfString3[0] = "ac=lbs";
				arrayOfString3[1] = "type=post";
				arrayOfString3[2] = ("xy=" + NearbyInfo.this.gps);
				localBaseWebInterFace3.GotoUrl(SiteTools
						.getMobileUrl(arrayOfString3));
			case 2:
				BaseWebInterFace localBaseWebInterFace2 = NearbyInfo.this.webinterface;
				String[] arrayOfString2 = new String[3];
				arrayOfString2[0] = "ac=lbs";
				arrayOfString2[1] = "type=thread";
				arrayOfString2[2] = ("xy=" + NearbyInfo.this.gps);
				localBaseWebInterFace2.GotoUrl(SiteTools
						.getMobileUrl(arrayOfString2));
			case 3:
				BaseWebInterFace localBaseWebInterFace1 = NearbyInfo.this.webinterface;
				String[] arrayOfString1 = new String[3];
				arrayOfString1[0] = "ac=lbs";
				arrayOfString1[1] = "type=user";
				arrayOfString1[2] = ("xy=" + NearbyInfo.this.gps);
				localBaseWebInterFace1.GotoUrl(SiteTools
						.getMobileUrl(arrayOfString1));
			}
		}
	};
	private String sType = "";
	private SecondNavManager secondNavManager;
	private TextView tvTitle = null;

	private void _initComponent()
	{
		this.columnNav = ((LinearLayout) findViewById(R.id.subnav));
		findViewById(R.id.nav_commit).setVisibility(8);
		this.btnBack = ((Button) findViewById(R.id.nav_back));
		this.tvTitle = ((TextView) findViewById(R.id.nav_title));
		this.tvTitle.setText(R.string.STR_NEARBY);
		this.btnBack.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				finish();
			}
		});
		this.viewbox = ((FrameLayout) findViewById(R.id.view_box));
		addWebView();
		this.secondNavManager = new SecondNavManager(this);
		this.secondNavManager.FillNavDataByType(9);
		this.secondNavManager.SetOnSecondNavBtnClick(this.onSecondNavBtnClick);
		if (this.sType.equals("news"))
			this.secondNavManager.ClickBtnByIndex(0);
		else
		{
			if (this.sType.equals("thread"))
				this.secondNavManager.ClickBtnByIndex(1);
			else if (this.sType.equals("board"))
				this.secondNavManager.ClickBtnByIndex(2);
			else if (this.sType.equals("user"))
				this.secondNavManager.ClickBtnByIndex(3);
			else if (this.sType.equals(""))
				this.secondNavManager.ClickBtnByIndex(3);
		}
	}

	public void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		setContentView(R.layout.nearby_info);
		Intent localIntent = getIntent();
		this.url = localIntent.getStringExtra("nearby_url");
		this.sType = localIntent.getStringExtra("type");
		GPSUtil localGPSUtil = new GPSUtil(getApplicationContext());
		this.loc = localGPSUtil.setLatitudeAndLongitude();
		if (this.loc == null)
			this.loc = localGPSUtil.setLatitudeAndLongitude();
		if (this.loc != null)
			this.gps = (this.loc[0] + "," + this.loc[1]);
		_initComponent();
	}

	public void pageError()
	{}

	public void pageFinished(WebView paramWebView, String paramString)
	{}

	public void pageStart(WebView paramWebView, String paramString)
	{}
}