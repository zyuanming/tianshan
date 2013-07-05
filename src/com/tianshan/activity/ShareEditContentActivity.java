package com.tianshan.activity;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tianshan.R;
import com.tianshan.adapter.ShareAdapter;
import com.tianshan.exception.ShareException;
import com.tianshan.source.HttpsRequest;
import com.tianshan.source.Tools;

public class ShareEditContentActivity extends Activity
{
	private String access_token;
	private Button btnBack;
	private Button btnComment;
	private Button btnShare;
	private EditText editText;
	private Handler handler = new Handler()
	{
		public void handleMessage(Message paramAnonymousMessage)
		{
			switch (paramAnonymousMessage.what)
			{
			default:
				return;
			case 1:
				ShareEditContentActivity.this.pBar.setVisibility(0);
				break;
			case 0:
				ShareEditContentActivity.this.pBar.setVisibility(8);
				try
				{
					ShareEditContentActivity.this.sa.dealResult(
							String.valueOf(paramAnonymousMessage.obj),
							ShareEditContentActivity.this.sharetype);
					ShareEditContentActivity.this.finish();
					ShareEditContentActivity.this.sa.showToastMessage("发�?成功!");
				} catch (ShareException localShareException)
				{
					ShareEditContentActivity.this.sa
							.showToastMessage(localShareException.getMessage());
					ShareEditContentActivity.this.sa.dealResultError(
							localShareException.getShareType(),
							localShareException.getErrorCode(),
							ShareEditContentActivity.this.getIntent());
					ShareEditContentActivity.this.finish();
				} finally
				{
					ShareEditContentActivity.this.finish();
				}
				break;
			}
		}
	};
	protected final int maxLength = 140;
	private TextView navTitle;
	private View.OnClickListener onBtnBackClick = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			ShareEditContentActivity.this.finish();
		}
	};
	private View.OnClickListener onBtnShareClick = new View.OnClickListener()
	{
		public void onClick(View paramAnonymousView)
		{
			if (!checkContent())
			{
				((InputMethodManager) ShareEditContentActivity.this
						.getSystemService("input_method"))
						.hideSoftInputFromWindow(editText.getWindowToken(), 0);
				switch (sharetype)
				{
				default:
					Toast.makeText(
							ShareEditContentActivity.this,
							"亲~!再次声明,本信息只能发�?���?并且不包�?但愿火星银民能理解你的意思 May the force be with u~!",
							0).show();
					break;
				case 3:
					sendContentToSina();
					break;
				case 4:
					sendContentToQQBlog();
					break;
				case 1:
					sendContentToQQSpace();
					break;
				case 2:
					sendContentToRenren();
				}
			}
		}
	};
	private String open_id;
	private ProgressBar pBar;
	private FrameLayout.LayoutParams pBarParams = new FrameLayout.LayoutParams(
			-2, -2, 17);
	private String pcurl;
	private ArrayList<BasicNameValuePair> postParam;
	private ShareAdapter sa = null;
	private Runnable sendContent = new Runnable()
	{
		public void run()
		{
			handler.sendEmptyMessage(1);
			String str = HttpsRequest.openUrl(send_url, "POST", postParam);
			Message localMessage = new Message();
			localMessage.obj = str;
			localMessage.what = 0;
			handler.sendMessage(localMessage);
		}
	};
	private String send_url;
	private int sharetype;
	private String title;
	private TextView txtCount;
	private TextView txtShareTypeName;
	private TextWatcher watcher = new TextWatcher()
	{
		public void afterTextChanged(Editable paramAnonymousEditable)
		{}

		public void beforeTextChanged(CharSequence paramAnonymousCharSequence,
				int paramAnonymousInt1, int paramAnonymousInt2,
				int paramAnonymousInt3)
		{}

		public void onTextChanged(CharSequence paramAnonymousCharSequence,
				int paramAnonymousInt1, int paramAnonymousInt2,
				int paramAnonymousInt3)
		{
			String str = editText.getText().toString();
			txtCount.setText(String.valueOf(140 - str.length()));
		}
	};

	private void _initEditContent()
	{
		String str1 = getIntent().getStringExtra("type");
		this.title = getIntent().getStringExtra("title");
		this.pcurl = getIntent().getStringExtra("pcurl");
		String str2 = "";
		if ((str1 != null) && (this.title != null) && (this.pcurl != null))
			str2 = "分享" + str1 + "-" + this.title + " " + this.pcurl;
		this.editText.setText(str2);
		this.txtCount.setText(String.valueOf(140 - str2.length()));
	}

	protected String calculateRenrenFuckSig(
			ArrayList<BasicNameValuePair> paramArrayList)
	{
		ArrayList localArrayList = new ArrayList();
		int i = 0;
		StringBuilder localStringBuilder;
		if (i >= paramArrayList.size())
		{
			Collections.sort(localArrayList);
			localStringBuilder = new StringBuilder();
			for (int j = 0; j < localArrayList.size(); j++)
			{
				localStringBuilder.append((String) localArrayList.get(j));
				BasicNameValuePair localBasicNameValuePair = (BasicNameValuePair) paramArrayList
						.get(i);
				localArrayList.add(localBasicNameValuePair.getName() + "="
						+ localBasicNameValuePair.getValue());
			}
			localStringBuilder.append("72bc49e5343440c08f66533f6f93935e");
			return Tools._md5(localStringBuilder.toString());
		}
		return null;
	}

	protected boolean checkContent()
	{
		int i = this.editText.getText().toString().length();
		boolean bool = false;
		if (i > 140)
		{
			Toast.makeText(this, "总字数超了裁点吧~!", 1).show();
			bool = true;
		}
		return bool;
	}

	protected void initQQData()
	{
		Intent localIntent = getIntent();
		this.access_token = localIntent.getStringExtra("access_token");
		this.open_id = localIntent.getStringExtra("open_id");
	}

	protected void initRenrenData()
	{
		this.access_token = getIntent().getStringExtra("access_token");
	}

	protected void initShareType()
	{
		this.sharetype = getIntent().getIntExtra("sharetype", 4);
		switch (this.sharetype)
		{
		default:
			this.navTitle.setText("分享至火星");
			this.txtShareTypeName.setText("火星");
			Toast.makeText(this, "你这是想分享到火星去吗?不包邮哦~!", 0).show();
		case 3:
			this.navTitle.setText("分享至新浪微博");
			this.txtShareTypeName.setText("新浪微博");
			initSinaData();
			break;
		case 4:
			this.navTitle.setText("分享至腾讯微博");
			this.txtShareTypeName.setText("腾讯微博");
			initQQData();
			break;
		case 1:
			this.navTitle.setText("分享至QQ空间");
			this.txtShareTypeName.setText("QQ空间");
			initQQData();
			break;
		case 2:
			this.navTitle.setText("分享至人人网");
			this.txtShareTypeName.setText("人人网");
			initRenrenData();
			break;
		}
	}

	protected void initSinaData()
	{
		this.access_token = getIntent().getStringExtra("access_token");
	}

	protected void initUI()
	{
		this.btnComment = ((Button) findViewById(R.id.nav_commit));
		this.btnComment.setVisibility(8);
		this.editText = ((EditText) findViewById(R.id.share_edit_content));
		this.editText.addTextChangedListener(this.watcher);
		this.btnShare = ((Button) findViewById(R.id.btn_edit_share));
		this.btnShare.setOnClickListener(this.onBtnShareClick);
		this.btnBack = ((Button) findViewById(R.id.nav_back));
		this.btnBack.setOnClickListener(this.onBtnBackClick);
		this.navTitle = ((TextView) findViewById(R.id.nav_title));
		this.txtShareTypeName = ((TextView) findViewById(R.id.share_edit_type_name));
		this.txtCount = ((TextView) findViewById(R.id.share_edit_count));
		this.pBar = new ProgressBar(this);
		this.pBar.setBackgroundColor(0);
		this.pBar.setIndeterminateDrawable(getResources().getDrawable(
				R.drawable.progress_blue_move));
		this.pBar.setIndeterminate(false);
		addContentView(this.pBar, this.pBarParams);
		this.pBar.setVisibility(8);
	}

	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		setContentView(R.layout.share_edit_content);
		this.sa = new ShareAdapter(this);
		initUI();
		initShareType();
		_initEditContent();
	}

	protected void sendContentToQQBlog()
	{
		this.postParam = new ArrayList();
		this.postParam.add(new BasicNameValuePair("access_token",
				this.access_token));
		this.postParam.add(new BasicNameValuePair("openid", this.open_id));
		this.postParam.add(new BasicNameValuePair("oauth_consumer_key",
				"100273331"));
		this.postParam.add(new BasicNameValuePair("content", this.editText
				.getText().toString()));
		this.send_url = "https://graph.qq.com/t/add_t?oauth_consumer_key=100273331&access_token=${access_token}&openid=${openid}"
				.replace("${access_token}", this.access_token).replace(
						"${openid}", this.open_id);
		new Thread(this.sendContent).start();
	}

	protected void sendContentToQQSpace()
	{
		this.postParam = new ArrayList();
		this.postParam.add(new BasicNameValuePair("access_token",
				this.access_token));
		this.postParam.add(new BasicNameValuePair("openid", this.open_id));
		this.postParam.add(new BasicNameValuePair("oauth_consumer_key",
				"100273331"));
		this.postParam.add(new BasicNameValuePair("title", "由掌WO发布的分"));
		this.postParam.add(new BasicNameValuePair("url",
				"http://client.xjts.cn/admin/"));
		this.postParam.add(new BasicNameValuePair("comment", this.title));
		this.postParam.add(new BasicNameValuePair("summary", this.editText
				.getText().toString()));
		this.postParam.add(new BasicNameValuePair("format", "json"));
		this.postParam.add(new BasicNameValuePair("source", "2"));
		this.postParam.add(new BasicNameValuePair("type", "4"));
		this.postParam.add(new BasicNameValuePair("site",
				"http://client.xjts.cn/admin/"));
		this.postParam.add(new BasicNameValuePair("nswb", "1"));
		this.send_url = "https://graph.qq.com/share/add_share";
		new Thread(this.sendContent).start();
	}

	protected void sendContentToRenren()
	{
		this.postParam = new ArrayList();
		this.postParam
				.add(new BasicNameValuePair("method", "feed.publishFeed"));
		this.postParam.add(new BasicNameValuePair("v", "1.0"));
		this.postParam.add(new BasicNameValuePair("name", this.title));
		this.postParam.add(new BasicNameValuePair("description", this.editText
				.getText().toString()));
		this.postParam.add(new BasicNameValuePair("url", this.pcurl));
		this.postParam.add(new BasicNameValuePair("access_token",
				this.access_token));
		this.postParam.add(new BasicNameValuePair("format", "JSON"));
		this.postParam.add(new BasicNameValuePair("sig",
				calculateRenrenFuckSig(this.postParam)));
		this.send_url = "http://api.renren.com/restserver.do";
		new Thread(this.sendContent).start();
	}

	protected void sendContentToSina()
	{
		this.postParam = new ArrayList();
		this.postParam.add(new BasicNameValuePair("access_token",
				this.access_token));
		this.postParam.add(new BasicNameValuePair("status", this.editText
				.getText().toString()));
		this.send_url = "https://api.weibo.com/2/statuses/update.json";
		new Thread(this.sendContent).start();
	}
}