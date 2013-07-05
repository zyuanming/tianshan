package com.tianshan.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tianshan.R;
import com.tianshan.source.activity.BaseActivity;

public class AboutActivity extends BaseActivity
{
	private Button btnBack = null;
	private TextView tvTitle = null;
	private TextView tvVersion = null;

	private void _initComponent()
	{
		findViewById(R.id.nav_commit).setVisibility(8);
		this.btnBack = ((Button) findViewById(R.id.nav_back));
		this.tvTitle = ((TextView) findViewById(R.id.nav_title));
		this.tvTitle.setText(R.string.STR_ABOUT);
		this.tvVersion = ((TextView) findViewById(R.id.tv_version));
		this.tvVersion.setText(getString(R.string.STR_VERSION)
				+ this.core._getVersionCode());
		this.btnBack.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				finish();
			}
		});
	}

	public void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		setContentView(R.layout.about);
		_initComponent();
	}
}