package com.tianshan.activity;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.EditText;

import com.tianshan.R;
import com.tianshan.ZhangWoApp;
import com.tianshan.source.DEBUG;
import com.tianshan.source.HttpRequest;
import com.tianshan.source.HttpsRequest;
import com.tianshan.source.ShowMessage;
import com.tianshan.source.SiteTools;
import com.tianshan.source.activity.SecondLevelActivity;
import com.tianshan.source.view.CommentView;
import com.tianshan.source.view.DWebView;
import com.tianshan.source.view.Navbar;

public class BoardActivity extends SecondLevelActivity implements
		Navbar.OnNavigateListener, TextWatcher, DWebView.onPageLoad
{
	private final int FAILD = 1;
	private final int NULLCOMMENT = -1;
	private final int SUCCESS = 0;
	private String boardId;
	protected String comment;
	private EditText commentEntry;
	private CommentView commentview;
	private Handler mhandler = new Handler()
	{
		public void handleMessage(Message message)
		{

			switch (message.what)
			{
			default:
				BoardActivity.setProgressGone(false);
				super.handleMessage(message);
				return;
			case 0:
				ShowMessage.getInstance(BoardActivity.this)._showToast(
						"评论发布成功", 2);
				if (url != null)
					webinterface.GotoUrl(url);
				break;
			case 1:
				ShowMessage.getInstance(BoardActivity.this)._showToast(
						"评论发布失败", 2);
				break;
			case -1:
				ShowMessage.getInstance(BoardActivity.this)._showToast(
						"请填写评论内容", 2);
				break;
			}
		}

	};
	private Navbar navbar;

	private boolean _postComment(String s)
	{
		new HttpRequest();
		boolean flag = false;
		boolean flag1;
		DEBUG.o("*****post comment****");
		String as[] = new String[4];
		as[0] = "ac=comment";
		as[1] = "op=list";
		as[2] = "type=threadreply";
		as[3] = (new StringBuilder("tid=")).append(boardId).toString();
		String s1 = SiteTools.getSiteUrl(as);
		ArrayList arraylist = new ArrayList();
		arraylist.add(new BasicNameValuePair("content", s));
		CookieSyncManager.createInstance(this);
		CookieManager.getInstance();
		String s2 = ZhangWoApp.getInstance().getUserSession().getmobile_auth();
		DEBUG.o(s2);
		String s3 = HttpsRequest.openUrl(s1, "POST", arraylist, null, null, s2);
		if (s3 != null)
		{
			try
			{
				JSONObject jsonobject = new JSONObject(s3);
				flag = false;
				if (jsonobject != null)
				{
					flag1 = "send_success".equals(jsonobject.getJSONObject(
							"msg").optString("msgvar"));
					if (flag1)
						flag = true;
					else
						flag = false;
				}
			} catch (JSONException je)
			{
				je.printStackTrace();
			}
		}
		return flag;
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
										BoardActivity.this, LoginActivity.class);
								startActivity(localIntent);
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

	protected void _initNavBar(boolean paramBoolean)
	{
		super._initNavBar(paramBoolean);
		this.navbar = new Navbar(this, this.navbarbox);
		this.navbar.setOnNavigate(this);
		this.navbar.setTitle("查看报料");
		this.navbar.setCommitBtnVisibility(true);
		this.navbar.setCommitBtnText(R.string.nav_btn_commit);
	}

	protected void _initToolBar(boolean paramBoolean)
	{
		super._initToolBar(paramBoolean);
		this.commentview = new CommentView(this, this.toolbox, "comment");
	}

	public void afterTextChanged(Editable editable)
	{
		if (commentEntry.getText().length() > 0)
			commentEntry.setEnabled(true);
		else
			commentEntry.setEnabled(false);
	}

	public void beforeTextChanged(CharSequence paramCharSequence,
			int paramInt1, int paramInt2, int paramInt3)
	{}

	public void onBack()
	{
		finish();
	}

	public void onCommit()
	{
		if (!ZhangWoApp.getInstance().isLogin())
		{
			popupLoginDialog();
		} else
		{
			setProgressGone(true);
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

	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		_initNavBar(true);
		this.dwebview.setOnPageLoad(this);
		_initToolBar(true);
		Bundle localBundle = getIntent().getExtras();
		if (localBundle != null)
		{
			try
			{
				this.boardId = new JSONObject(localBundle.getString("params"))
						.optString("id");
			} catch (JSONException localJSONException)
			{
				localJSONException.printStackTrace();
			}
		}
	}

	public void onTextChanged(CharSequence paramCharSequence, int paramInt1,
			int paramInt2, int paramInt3)
	{}

	public void pageError()
	{}

	public void pageFinished(WebView paramWebView, String paramString)
	{}

	public void pageStart(WebView paramWebView, String paramString)
	{}
}