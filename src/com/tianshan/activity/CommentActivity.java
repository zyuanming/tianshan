package com.tianshan.activity;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianshan.R;
import com.tianshan.ZhangWoApp;
import com.tianshan.source.DEBUG;
import com.tianshan.source.HttpRequest;
import com.tianshan.source.HttpsRequest;
import com.tianshan.source.ShowMessage;
import com.tianshan.source.SiteTools;
import com.tianshan.source.activity.BaseActivity;
import com.tianshan.source.view.CommentView;
import com.tianshan.source.view.DWebView;

public class CommentActivity extends BaseActivity implements
		DWebView.onPageLoad
{
	private final int FAILD = 1;
	private final int NULLCOMMENT = -1;
	private final int SUCCESS = 0;
	private Button btnBack;
	private Button btnComment;
	String comment;
	private CommentView commentview;
	String commonid;
	private String countComment = null;
	private Handler mhandler = new Handler()
	{
		public void handleMessage(Message paramAnonymousMessage)
		{
			CommentActivity.setProgressGone(false);
			switch (paramAnonymousMessage.what)
			{
			default:
				super.handleMessage(paramAnonymousMessage);
				return;
			case 0:
				ShowMessage.getInstance(CommentActivity.this)._showToast(
						"评论发布成功", 2);
				if (url != null)
					dwebview.reload();
			case 1:
				ShowMessage.getInstance(CommentActivity.this)._showToast(
						"评论发布失败", 2);
				break;
			case -1:
				ShowMessage.getInstance(CommentActivity.this)._showToast(
						"请填写评论内容", 2);
				break;
			}
		}
	};
	private TextView tvTitle;
	String type;

	private void _initComponent()
	{
		this.btnBack = ((Button) findViewById(R.id.nav_back));
		this.btnBack.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				finish();
			}
		});
		this.btnComment = ((Button) findViewById(R.id.nav_commit));
		this.btnComment.setText(R.string.nav_btn_commit);
		this.btnComment.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				if (!ZhangWoApp.getInstance().isLogin())
				{
					popupLoginDialog();
				} else
				{
					CommentActivity.setProgressGone(true);
					comment = commentview.getCommentString();
					commentview.finishComment();
					(new Thread(new Runnable()
					{

						public void run()
						{
							if ("".equals(comment))
								mhandler.obtainMessage(-1).sendToTarget();
							else if (_postComment(comment))
								mhandler.obtainMessage(0).sendToTarget();
							else
								mhandler.obtainMessage(1).sendToTarget();
						}
					})).start();
				}
			}
		});
		this.tvTitle = ((TextView) findViewById(R.id.nav_title));
		this.tvTitle.setText(R.string.STR_COMMENT);
		this.commentview = new CommentView(this,
				(LinearLayout) findViewById(R.id.post_common), "comment");
		this.viewbox = ((FrameLayout) findViewById(R.id.view_box));
		addWebView();
	}

	private boolean _postComment(String paramString)
	{
		boolean bool1 = false;
		new HttpRequest();
		try
		{
			DEBUG.o("*****post comment****");
			String[] arrayOfString = new String[4];
			arrayOfString[0] = "ac=comment";
			arrayOfString[1] = "op=list";
			arrayOfString[2] = ("type=" + this.type);
			arrayOfString[3] = ("tid=" + this.commonid);
			String str1 = SiteTools.getSiteUrl(arrayOfString);
			ArrayList localArrayList = new ArrayList();
			localArrayList.add(new BasicNameValuePair("content", paramString));
			CookieSyncManager.createInstance(this);
			CookieManager.getInstance();
			String str2 = ZhangWoApp.getInstance().getUserSession()
					.getmobile_auth();
			Log.d("mobile_auth", str2);
			String str3 = HttpsRequest.openUrl(str1, "POST", localArrayList,
					null, null, str2);
			DEBUG.o(str3);
			bool1 = false;
			if (str3 != null)
			{
				JSONObject localJSONObject1 = new JSONObject(str3);
				bool1 = false;
				if (localJSONObject1 != null)
				{
					JSONObject localJSONObject2 = localJSONObject1
							.getJSONObject("msg");
					String str4 = localJSONObject2.optString("msgvar");
					String str5 = localJSONObject2.optString("msgstr");
					boolean bool2 = "send_success".equals(str4);
					bool1 = false;
					if (bool2)
					{
						bool1 = true;
					} else
					{
						boolean bool3 = "send_faild".equals(str4);
						bool1 = false;
						if (bool3)
						{
							bool1 = false;
							Log.d("msgstr", str5);
							bool1 = false;
						}
					}
				}
			}
		} catch (Exception localException)
		{
			localException.printStackTrace();
		}
		return bool1;
	}

	private String getBundleData(Bundle paramBundle, String paramString)
	{
		String str = null;
		if (paramBundle != null)
			str = paramBundle.getString(paramString);
		return str;
	}

	private void popupLoginDialog()
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
								DEBUG.o("ok");
								Intent localIntent = new Intent(
										CommentActivity.this,
										LoginActivity.class);
								CommentActivity.this.startActivity(localIntent);
							}
						})
				.setNegativeButton(17039360,
						new DialogInterface.OnClickListener()
						{
							public void onClick(
									DialogInterface paramAnonymousDialogInterface,
									int paramAnonymousInt)
							{
								paramAnonymousDialogInterface.dismiss();
								DEBUG.o("no");
							}
						}).create().show();
	}

	public void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		setContentView(R.layout.comment);
		Bundle localBundle = getIntent().getExtras();
		this.commonid = getBundleData(localBundle, "commid");
		this.type = getBundleData(localBundle, "type");
		_initComponent();
		String[] arrayOfString = new String[4];
		arrayOfString[0] = "ac=comment";
		arrayOfString[1] = "op=list";
		arrayOfString[2] = ("type=" + this.type);
		arrayOfString[3] = ("id=" + this.commonid);
		this.url = SiteTools.getSiteUrl(arrayOfString);
		this.webinterface.GotoUrl(this.url);
		this.dwebview.setOnPageLoad(this);
	}

	public void pageError()
	{}

	public void pageFinished(WebView paramWebView, String paramString)
	{
		this.countComment = this.webinterface.getCommentCount();
		if (!"".equals(this.countComment))
			this.countComment = ("(" + this.countComment + ")");
		String str = "评论" + this.countComment;
		this.tvTitle.setText(str);
		DEBUG.o(this.countComment);
	}

	public void pageStart(WebView paramWebView, String paramString)
	{}
}