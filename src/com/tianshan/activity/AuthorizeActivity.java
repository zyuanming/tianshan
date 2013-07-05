package com.tianshan.activity;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.WebView;
import android.widget.Toast;

import com.tianshan.dbhelper.ShareBindHelper;
import com.tianshan.model.ShareBind;
import com.tianshan.source.DEBUG;
import com.tianshan.source.HttpsRequest;
import com.tianshan.source.activity.SecondLevelActivity;
import com.tianshan.source.view.DWebView;
import com.tianshan.source.view.Navbar;

public class AuthorizeActivity extends SecondLevelActivity implements
		Navbar.OnNavigateListener
{
	private boolean bindPath = false;
	private Handler handler = new Handler()
	{
		public void handleMessage(Message paramAnonymousMessage)
		{
			if (paramAnonymousMessage.what == 0)
				AuthorizeActivity.this.webinterface
						.setProgressBarVisible(false);
		}
	};
	private Intent i;
	private boolean isFinished = false;
	protected DWebView.onAuthorizeCallback onAuthorizeListener = new DWebView.onAuthorizeCallback()
	{
		public void onComplete(Integer integer, String s)
		{
			if (!isFinished)
			{
				isFinished = true;
				i = new Intent();
				i.putExtra("sharetype", sharetype);
				i.putExtra("type", getIntent().getStringExtra("type"));
				i.putExtra("title", getIntent().getStringExtra("title"));
				i.putExtra("pcurl", getIntent().getStringExtra("pcurl"));
				i.setClass(AuthorizeActivity.this,
						ShareEditContentActivity.class);
				switch (AuthorizeActivity.this.sharetype)
				{
				default:
					Toast.makeText(AuthorizeActivity.this,
							"哥们,授权信息返回错误,俺也无能为力的说啊~!不行就打110问问警察叔叔吧~!", 0)
							.show();
				case 3:
					dealSinaShare(i, s);
					break;
				case 4:
					dealQQShare(i, s);
					break;
				case 1:
					dealQQShare(i, s);
					break;
				case 2:
					dealRenrenShare(i, s);
					break;
				}
			}
		}
	};
	protected DWebView.onPageLoad onThisWebViewPageLoad = new DWebView.onPageLoad()
	{
		public void pageError()
		{}

		public void pageFinished(WebView paramAnonymousWebView,
				String paramAnonymousString)
		{}

		public void pageStart(WebView paramAnonymousWebView,
				String paramAnonymousString)
		{
			AuthorizeActivity.this.webinterface.setProgressBarVisible(true);
		}
	};
	private ArrayList<BasicNameValuePair> postParam;
	private String resString;
	protected int sharetype;

	protected void _initNavBar(boolean paramBoolean)
	{
		super._initNavBar(paramBoolean);
		Navbar localNavbar = new Navbar(this, this.navbarbox);
		localNavbar.setOnNavigate(this);
		localNavbar.setTitle("授权");
	}

	protected Intent dealQQShare(Intent paramIntent, final String paramString)
	{
		this.postParam = new ArrayList();
		this.postParam.add(new BasicNameValuePair("access_token", paramString));
		paramIntent.putExtra("access_token", paramString);
		new Thread(new Runnable()
		{
			public void run()
			{
				String str = HttpsRequest
						.openUrl("https://graph.qq.com/oauth2.0/me", "GET",
								AuthorizeActivity.this.postParam)
						.replace("callback( ", "").replace(" );", "").trim();
				try
				{
					JSONObject localJSONObject = new JSONObject(str);
					AuthorizeActivity.this.i.putExtra("open_id",
							localJSONObject.getString("openid"));
					ShareBind localShareBind1 = new ShareBind();
					localShareBind1.setId(4);
					localShareBind1.setAccess_token(paramString);
					localShareBind1.setOpen_id(localJSONObject
							.getString("openid"));
					ShareBind localShareBind2 = localShareBind1.clone();
					localShareBind1.setId(1);
					ShareBindHelper.getInstance(AuthorizeActivity.this)
							.updateShareBind(localShareBind1);
					ShareBindHelper.getInstance(AuthorizeActivity.this)
							.updateShareBind(localShareBind2);
					AuthorizeActivity.this.finishiAuthorize();
				} catch (JSONException localJSONException)
				{
					DEBUG.o(localJSONException.getMessage());
				}
			}
		}).start();
		return paramIntent;
	}

	protected Intent dealRenrenShare(Intent intent, String paramString)
	{
		this.postParam = new ArrayList();
		this.postParam = new ArrayList();
		this.postParam.add(new BasicNameValuePair("client_id",
				"9494cde4ba4b4b9fb901db5919b845e3"));
		this.postParam.add(new BasicNameValuePair("client_secret",
				"72bc49e5343440c08f66533f6f93935e"));
		this.postParam.add(new BasicNameValuePair("grant_type",
				"authorization_code"));
		this.postParam.add(new BasicNameValuePair("code", paramString));
		this.postParam.add(new BasicNameValuePair("redirect_uri",
				"http://client.xjts.cn"));
		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					resString = HttpsRequest.openUrl(
							"https://graph.renren.com/oauth/token", "POST",
							postParam);
					JSONObject jsonobject = new JSONObject(resString);
					ShareBind sharebind = new ShareBind();
					sharebind.setId(2);
					sharebind.setAccess_token(jsonobject
							.getString("access_token"));
					sharebind.setExpires_in(jsonobject.getString("expires_in"));
					sharebind.setKeyword1(jsonobject.getString("refresh_token"));
					sharebind.setKeyword2(jsonobject.getString("scope"));
					ShareBindHelper.getInstance(AuthorizeActivity.this)
							.updateShareBind(sharebind);
					i.putExtra("access_token",
							jsonobject.getString("access_token"));
				} catch (JSONException je)
				{
					ShowMessageByHandler(je.getMessage(), 3);
				}
				finishiAuthorize();
			}
		}).start();
		return intent;
	}

	protected Intent dealSinaShare(Intent paramIntent, String paramString)
	{
		this.postParam = new ArrayList();
		this.postParam.add(new BasicNameValuePair("client_id", "2601366196"));
		this.postParam.add(new BasicNameValuePair("client_secret",
				"ffb1d5854b18e3f75d43d43ff5d926e9"));
		this.postParam.add(new BasicNameValuePair("grant_type",
				"authorization_code"));
		this.postParam.add(new BasicNameValuePair("code", paramString));
		this.postParam.add(new BasicNameValuePair("redirect_uri",
				"http://client.xjts.cn"));
		this.resString = "";
		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					resString = HttpsRequest.openUrl(
							"https://api.weibo.com/oauth2/access_token",
							"POST", postParam);
					JSONObject jsonobject = new JSONObject(resString);
					ShareBind sharebind = new ShareBind();
					sharebind.setId(3);
					sharebind.setAccess_token(jsonobject
							.getString("access_token"));
					sharebind.setExpires_in(jsonobject.getString("expires_in"));
					ShareBindHelper.getInstance(AuthorizeActivity.this)
							.updateShareBind(sharebind);
					i.putExtra("access_token",
							jsonobject.getString("access_token"));
				} catch (JSONException je)
				{
					ShowMessageByHandler(je.getMessage(), 3);
				} finally
				{
					finishiAuthorize();
				}
			}
		}).start();
		return paramIntent;
	}

	protected void finishiAuthorize()
	{
		this.handler.sendEmptyMessage(0);
		if (!this.bindPath)
			startActivity(this.i);
		finish();
	}

	protected void initOAuthPage()
	{
		switch (this.sharetype)
		{
		default:
			Toast.makeText(this, "你这是想分享到火星去吗?不包邮哦~!", 0).show();
			return;
		case 3:
			this.webinterface
					.GotoUrl("https://api.weibo.com/oauth2/authorize?client_id=2601366196&redirect_uri=http://client.xjts.cn&response_type=code&display=wap2.0");
			break;
		case 4:
			this.webinterface
					.GotoUrl("https://graph.qq.com/oauth2.0/authorize?response_type=token&client_id=100273331&redirect_uri=http://client.xjts.cn&display=mobile&scope=get_user_info,add_t,add_share");
			break;
		case 1:
			this.webinterface
					.GotoUrl("https://graph.qq.com/oauth2.0/authorize?response_type=token&client_id=100273331&redirect_uri=http://client.xjts.cn&display=mobile&scope=get_user_info,add_t,add_share");
			break;
		case 2:
			this.webinterface
					.GotoUrl("https://graph.renren.com/oauth/authorize?client_id=9494cde4ba4b4b9fb901db5919b845e3&redirect_uri=http://client.xjts.cn&response_type=code&scope=publish_feed");
			break;
		}
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
		this.sharetype = getIntent().getIntExtra("sharetype", 4);
		if (getIntent().hasExtra("BindPath"))
			this.bindPath = true;
		_initNavBar(true);
		this.webinterface.setOnAuthorizeListener(this.onAuthorizeListener);
		this.webinterface.setOnPageLoad(this.onThisWebViewPageLoad);
		initOAuthPage();
	}
}