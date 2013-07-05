package com.tianshan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianshan.R;
import com.tianshan.ZhangWoApp;
import com.tianshan.activity.tab.SecondNavManager;
import com.tianshan.source.SiteTools;
import com.tianshan.source.activity.BaseActivity;
import com.tianshan.source.webinterface.BaseWebInterFace;

public class MyInfo extends BaseActivity
{
	private Button btnBack = null;
	private LinearLayout columnNav;
	private Intent intent;
	private String loadurl;
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
				BaseWebInterFace localBaseWebInterFace3 = MyInfo.this.webinterface;
				String[] arrayOfString3 = new String[4];
				arrayOfString3[0] = "ac=comment";
				arrayOfString3[1] = "op=my";
				arrayOfString3[2] = "type=news";
				arrayOfString3[3] = ("uid=" + ZhangWoApp.getInstance()
						.getUserSession().getUid());
				localBaseWebInterFace3.GotoUrl(SiteTools
						.getSiteUrl(arrayOfString3));
				break;
			case 1:
				BaseWebInterFace localBaseWebInterFace2 = MyInfo.this.webinterface;
				String[] arrayOfString2 = new String[4];
				arrayOfString2[0] = "ac=comment";
				arrayOfString2[1] = "op=my";
				arrayOfString2[2] = "type=video";
				arrayOfString2[3] = ("uid=" + ZhangWoApp.getInstance()
						.getUserSession().getUid());
				localBaseWebInterFace2.GotoUrl(SiteTools
						.getSiteUrl(arrayOfString2));
				break;
			case 2:
				BaseWebInterFace localBaseWebInterFace1 = MyInfo.this.webinterface;
				String[] arrayOfString1 = new String[4];
				arrayOfString1[0] = "ac=comment";
				arrayOfString1[1] = "op=my";
				arrayOfString1[2] = "type=threadreply";
				arrayOfString1[3] = ("uid=" + ZhangWoApp.getInstance()
						.getUserSession().getUid());
				localBaseWebInterFace1.GotoUrl(SiteTools
						.getSiteUrl(arrayOfString1));
				break;
			}
		}
	};
	private SecondNavManager secondNavManager;
	private TextView tvTitle = null;

	private void _initComponent()
	{
		this.columnNav = ((LinearLayout) findViewById(R.id.subnav));
		Button localButton = (Button) findViewById(R.id.nav_commit);
		localButton.setVisibility(8);
		this.btnBack = ((Button) findViewById(R.id.nav_back));
		this.btnBack.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				finish();
			}
		});
		this.viewbox = ((FrameLayout) findViewById(R.id.view_box));
		addWebView();
		this.tvTitle = ((TextView) findViewById(R.id.nav_title));
		if ("thread".equals(this.intent.getStringExtra("my_info_type")))
			this.tvTitle.setText(R.string.STR_MY_STICK);
		else
		{
			if ("mypm".equals(this.intent.getStringExtra("my_info_type")))
			{
				this.tvTitle.setText(R.string.STR_MY_MESSGAE);
				localButton.setVisibility(0);
				localButton.setMinimumWidth(103);
				localButton.setText("发送消息");
				localButton.setOnClickListener(new View.OnClickListener()
				{
					public void onClick(View paramAnonymousView)
					{
						String str = SiteTools.getMobileUrl(new String[] {
								"ac=mypm", "op=add" });
						MyInfo.this.webinterface
								.GotoUrl("{\"ac\":\"mypm\",\"target\":\"1\",\"direction\":\"0\",\"op\":\"add\",\"url\":\""
										+ str + "\"}");
					}
				});
			} else if ("comment".equals(this.intent
					.getStringExtra("my_info_type")))
			{
				this.secondNavManager = new SecondNavManager(this);
				this.secondNavManager.FillNavDataByType(8);
				this.secondNavManager
						.SetOnSecondNavBtnClick(this.onSecondNavBtnClick);
				this.secondNavManager.ClickBtnByIndex(0);
				this.tvTitle.setText(R.string.STR_MY_COMMENT);
			}
		}
	}

	protected void onActivityResult(int paramInt1, int paramInt2,
			Intent paramIntent)
	{
		super.onActivityResult(paramInt1, paramInt2, paramIntent);
		if ((paramInt2 == -1) && (paramInt1 == 1001))
		{
			Log.d("result", "onActivityResult!!");
			String str = SiteTools.getMobileUrl(new String[] { "ac=mypm",
					"op=my" });
			this.webinterface
					.GotoUrl("{\"ac\":\"mypm\",\"target\":\"0\",\"direction\":\"0\",\"op\":\"my\",\"url\":\""
							+ str + "\"}");
		}
	}

	public void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		setContentView(R.layout.my_info_webview);
		this.intent = getIntent();
		_initComponent();
		this.loadurl = this.intent.getStringExtra("my_info_url");
		if (("thread".equals(this.intent.getStringExtra("my_info_type")))
				|| ("mypm".equals(this.intent.getStringExtra("my_info_type"))))
			this.columnNav.setVisibility(8);
		if (this.loadurl != null)
			this.webinterface.GotoUrl(this.loadurl);
	}
}