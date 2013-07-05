package com.tianshan.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;

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
import com.tianshan.source.webinterface.BaseWebInterFace;

public class MessageViewActivity extends SecondLevelActivity implements
		Navbar.OnNavigateListener, BaseWebInterFace.OnWebFinishListener
{
	private final int FAILD = 1;
	private final int NULLCOMMENT = -1;
	private final int SUCCESS = 0;
	private int backToNav = 0;
	private String comment;
	private CommentView commentview;
	private String loadurl;
	private Handler mhandler = new Handler()
	{
		public void handleMessage(Message message)
		{
			switch (message.what)
			{
			default:
				MessageViewActivity.setProgressGone(false);
				super.handleMessage(message);
				return;
			case 0:
				ShowMessage.getInstance(MessageViewActivity.this)._showToast(
						msgstr, 2);
				if (loadurl != null)
					webinterface.GotoUrl(loadurl);
				break;
			case 1:
				ShowMessage.getInstance(MessageViewActivity.this)._showToast(
						msgstr, 2);
				break;
			case -1:
				ShowMessage.getInstance(MessageViewActivity.this)._showToast(
						"请填写消息内容", 2);
			}
		}
	};
	private String msgstr;
	private String name;
	private Navbar navbar;
	private String pmid;

	private boolean _postComment(String s)
	{
		boolean flag = false;
		boolean flag1;
		try
		{
			new HttpRequest();
			DEBUG.o("*****post comment****");
			String as[] = new String[3];
			as[0] = "ac=mypm";
			as[1] = "op=addto";
			as[2] = (new StringBuilder("pmid=")).append(pmid).toString();
			String s1 = SiteTools.getMobileUrl(as);
			DEBUG.o(s1);
			ArrayList arraylist = new ArrayList();
			BasicNameValuePair basicnamevaluepair = new BasicNameValuePair(
					"message", s);
			BasicNameValuePair basicnamevaluepair1 = new BasicNameValuePair(
					"pmid", pmid);
			BasicNameValuePair basicnamevaluepair2 = new BasicNameValuePair(
					"submitform", "123");
			arraylist.add(basicnamevaluepair);
			arraylist.add(basicnamevaluepair1);
			arraylist.add(basicnamevaluepair2);
			String s2 = ZhangWoApp.getInstance().getUserSession()
					.getWebViewCookies();
			DEBUG.o(s2);
			String s3 = HttpsRequest.openUrl(s1, "POST", arraylist, null, null,
					s2);
			Log.d("aa",
					(new StringBuilder(String.valueOf(s3))).append(
							"********************").toString());
			if (s3 != null)
			{
				JSONObject jsonobject = new JSONObject(s3);
				if (jsonobject != null)
				{
					JSONObject jsonobject1 = jsonobject.getJSONObject("msg");
					String s4 = jsonobject1.optString("msgvar");
					msgstr = jsonobject1.optString("msgstr");
					flag1 = "do_success".equals(s4);
					if (flag1)
						flag = true;
					else
						flag = false;
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}

	private void goToNav()
	{
		Intent localIntent = new Intent();
		localIntent.setClass(this, NavigationBar.class);
		localIntent.setFlags(335544320);
		PendingIntent localPendingIntent = PendingIntent.getActivity(this, 100,
				localIntent, 134217728);
		try
		{
			localPendingIntent.send();
			finish();
		} catch (PendingIntent.CanceledException localCanceledException)
		{
			localCanceledException.printStackTrace();
		}
	}

	public static void setProgressGone(boolean flag)
	{
		if (!flag)
		{
			if (!flag)
				progress.setVisibility(8);
		} else
		{
			progress.setVisibility(0);
		}
	}

	protected void _initNavBar(boolean paramBoolean)
	{
		super._initNavBar(paramBoolean);
		this.navbar = new Navbar(this, this.navbarbox);
		this.navbar.setOnNavigate(this);
		this.navbar.setCommitBtnVisibility(true);
		this.navbar.setCommintBtnText(R.string.SEND);
	}

	protected void _initToolBar(boolean flag)
	{
		super._initToolBar(flag);
		Log.d("asd", (new StringBuilder()).append(toolbox).toString());
		commentview = new CommentView(this, toolbox, "message");
	}

	public void onBack()
	{
		if (backToNav == 1)
			goToNav();
		else
			finish();
	}

	public void onCommit()
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

	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		_initNavBar(true);
		_initToolBar(true);
		Bundle localBundle = getIntent().getExtras();
		if ("pushpm".equals(localBundle.getString("from")))
		{
			this.backToNav = 1;
			this.pmid = localBundle.getString("pmid");
			DEBUG.o(localBundle.getString("from"));
			DEBUG.o(localBundle.getString("tousername"));
			DEBUG.o(this.pmid);
			DEBUG.o(localBundle.getString("url"));
			DEBUG.o(localBundle.getString("tousername"));
			this.navbar.setTitle(localBundle.getString("tousername"));
			this.url = localBundle.getString("url");
		} else
		{
			this.webinterface.setListener(this);
			this.webinterface.setOnPageLoad(new DWebView.onPageLoad()
			{
				public void pageError()
				{}

				public void pageFinished(WebView paramAnonymousWebView,
						String paramAnonymousString)
				{
					MessageViewActivity.this.webinterface
							.GotoUrl("javascript:getPMID()");
				}

				public void pageStart(WebView paramAnonymousWebView,
						String paramAnonymousString)
				{
					MessageViewActivity.this.loadurl = paramAnonymousString;
					String[] arrayOfString1 = paramAnonymousString.split("&");
					int i = 0;
					while (true)
					{
						if (i >= arrayOfString1.length)
							return;
						String str1 = arrayOfString1[i];
						if (str1.contains("name"))
						{
							String[] arrayOfString2 = str1.split("=");
							MessageViewActivity.this.name = arrayOfString2[1];
						}
						try
						{
							String str2 = URLDecoder.decode(
									MessageViewActivity.this.name, "UTF-8");
							MessageViewActivity.this.navbar.setTitle(str2);
							i++;
						} catch (UnsupportedEncodingException localUnsupportedEncodingException)
						{
							localUnsupportedEncodingException.printStackTrace();
						}
					}
				}
			});
		}
	}

	public void onFinish(String paramString)
	{
		DEBUG.o(paramString + "------");
		try
		{
			this.pmid = new JSONObject(paramString).optString("pmid");
			DEBUG.o(this.pmid);
		} catch (JSONException localJSONException)
		{
			localJSONException.printStackTrace();
		}
	}

	public boolean onKeyDown(int i, KeyEvent keyevent)
	{
		boolean flag = true;
		if (i != 4 || keyevent.getRepeatCount() != 0)
		{
			if (i == 82)
				flag = false;
		} else
		{
			if (backToNav == 1)
				goToNav();
			else
				finish();
		}
		return flag;
	}
}