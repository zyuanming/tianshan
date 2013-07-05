package com.tianshan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianshan.R;
import com.tianshan.adapter.ShareAdapter;
import com.tianshan.dbhelper.ShareBindHelper;
import com.tianshan.source.activity.BaseActivity;

/**
 * 绑定分享帐号
 * 
 * @author lkh
 * 
 */
public class ShareActivity extends BaseActivity
{
	private boolean bindPath = false;
	private Button btnBack = null;
	private int[] btnId = { R.id.btn_tencent, R.id.btn_qq, R.id.btn_sina,
			R.id.btn_renren };
	private int[] ivId = { R.id.iv_tencent, R.id.iv_qq, R.id.iv_sina,
			R.id.iv_renren };
	private int[] llId = { R.id.ll_tencent_blog, R.id.ll_qq_space,
			R.id.ll_sina, R.id.ll_renren };
	private View.OnClickListener onLogoutBtnClick = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			ShareBindHelper localShareBindHelper = ShareBindHelper
					.getInstance(ShareActivity.this);
			switch (paramAnonymousView.getId())
			{
			default:
				return;
			case R.id.btn_sina:
				localShareBindHelper.clearShareBindById(3);
				shareElment[2].btnLogout.setVisibility(8);
				shareElment[2].iv_into.setVisibility(0);
				break;
			case R.id.btn_tencent:
				localShareBindHelper.clearShareBindById(4);
				shareElment[0].btnLogout.setVisibility(8);
				shareElment[0].iv_into.setVisibility(0);
				break;
			case R.id.btn_qq:
				localShareBindHelper.clearShareBindById(1);
				shareElment[1].btnLogout.setVisibility(8);
				shareElment[1].iv_into.setVisibility(0);
				break;
			case R.id.btn_renren:
				localShareBindHelper.clearShareBindById(2);
				shareElment[3].btnLogout.setVisibility(8);
				shareElment[3].iv_into.setVisibility(0);
				break;
			}
		}
	};
	private View.OnClickListener onShareBtnClick = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			Intent localIntent = new Intent();
			localIntent.putExtra("type", getIntent().getStringExtra("type"));
			localIntent.putExtra("title", getIntent().getStringExtra("title"));
			localIntent.putExtra("pcurl", getIntent().getStringExtra("pcurl"));
			localIntent.putExtra("BindPath", bindPath);
			sa.dealShareBtnClick(paramAnonymousView.getId(), localIntent);
		}
	};
	private ShareAdapter sa;
	private ShareElment[] shareElment = new ShareElment[4];
	private TextView tvTitle = null;

	private void _initComponent()
	{
		findViewById(R.id.nav_commit).setVisibility(8);
		this.btnBack = ((Button) findViewById(R.id.nav_back));
		this.btnBack.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				finish();
			}
		});
		this.tvTitle = ((TextView) findViewById(R.id.nav_title));
		this.tvTitle.setText(R.string.STR_SHARE_TITLE);
		for (int i = 0;; i++)
		{
			if (i >= this.btnId.length)
			{
				checkBind();
				return;
			}
			this.shareElment[i] = new ShareElment();
			this.shareElment[i].ll_vector = ((LinearLayout) findViewById(this.llId[i]));
			this.shareElment[i].ll_vector
					.setOnClickListener(this.onShareBtnClick);
			this.shareElment[i].btnLogout = ((Button) findViewById(this.btnId[i]));
			this.shareElment[i].btnLogout
					.setOnClickListener(this.onLogoutBtnClick);
			this.shareElment[i].iv_into = ((ImageView) findViewById(this.ivId[i]));
		}
	}

	protected void checkBind()
	{
		ShareBindHelper localShareBindHelper = ShareBindHelper
				.getInstance(this);
		if (localShareBindHelper.getShareBindById(4).isBind())
		{
			this.shareElment[0].btnLogout.setVisibility(0);
			this.shareElment[0].iv_into.setVisibility(8);
		}
		if (localShareBindHelper.getShareBindById(1).isBind())
		{
			this.shareElment[1].btnLogout.setVisibility(0);
			this.shareElment[1].iv_into.setVisibility(8);
		}
		if (localShareBindHelper.getShareBindById(3).isBind())
		{
			this.shareElment[2].btnLogout.setVisibility(0);
			this.shareElment[2].iv_into.setVisibility(8);
		}
		if (localShareBindHelper.getShareBindById(2).isBind())
		{
			this.shareElment[3].btnLogout.setVisibility(0);
			this.shareElment[3].iv_into.setVisibility(8);
		}
	}

	public void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		setContentView(R.layout.share);
		if (getIntent().hasExtra("BindPath"))
			this.bindPath = true;
		this.sa = new ShareAdapter(this);
		_initComponent();
	}

	protected void onResume()
	{
		super.onResume();
		checkBind();
	}

	public class ShareElment
	{
		public Button btnLogout = null;
		public ImageView iv_into = null;
		public LinearLayout ll_vector = null;

		public ShareElment()
		{}
	}

	public static abstract interface ShareType
	{
		public static final int QQ空间 = 1;
		public static final int[] dataIdOrder = { 4, 1, 3, 2 };
		public static final int[] imageIdOrder = { R.drawable.share_qqt,
				R.drawable.share_qqzone, R.drawable.share_sina,
				R.drawable.share_renren };
		public static final String[] textOrder = { "分享至腾讯微博", "分享至QQ空间",
				"分享至新浪微博", "分享至人人网" };
		public static final int 人人网 = 2;
		public static final int 新浪微博 = 3;
		public static final int 腾讯微博 = 4;
	}
}