package com.tianshan.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.tianshan.R;
import com.tianshan.source.DEBUG;
import com.tianshan.source.ShowMessage;
import com.tianshan.source.activity.SecondLevelActivity;
import com.tianshan.source.view.DWebView;
import com.tianshan.source.view.Navbar;
import com.tianshan.source.webinterface.BaseWebInterFace;

public class SendMessageActivity extends SecondLevelActivity implements
		Navbar.OnNavigateListener, BaseWebInterFace.OnWebFinishListener
{
	private String name;
	private Navbar navbar;

	protected void _initNavBar(boolean paramBoolean)
	{
		super._initNavBar(paramBoolean);
		this.navbar = new Navbar(this, this.navbarbox);
		this.navbar.setOnNavigate(this);
		this.navbar.setCommitBtnVisibility(true);
		this.navbar.setCommintBtnText(R.string.SEND);
	}

	public void onBack()
	{
		finish();
	}

	public void onCommit()
	{
		this.webinterface.GotoUrl("javascript:SubmitForm()");
	}

	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		_initNavBar(true);
		this.webinterface.setListener(this);
		this.webinterface.setOnPageLoad(new DWebView.onPageLoad()
		{
			public void pageError()
			{}

			public void pageFinished(WebView paramAnonymousWebView,
					String paramAnonymousString)
			{}

			public void pageStart(WebView paramAnonymousWebView,
					String paramAnonymousString)
			{
				Log.d("start", "pageStart *** = " + paramAnonymousString);
				String[] arrayOfString1 = paramAnonymousString.split("&");
				int i = 0;
				while (true)
				{
					if (i >= arrayOfString1.length)
					{
						if (navbar.getTitle().equals(""))
							navbar.setTitle(R.string.SEND_MESSGAE);
						return;
					}
					String str1 = arrayOfString1[i];
					if (str1.contains("name"))
					{
						String[] arrayOfString2 = str1.split("=");
						name = arrayOfString2[1];
					}
					try
					{
						String str2 = URLDecoder.decode(name, "UTF-8");
						navbar.setTitle(str2);
						i++;
					} catch (UnsupportedEncodingException localUnsupportedEncodingException)
					{
						localUnsupportedEncodingException.printStackTrace();
					}
				}
			}
		});
	}

	public void onFinish(String paramString)
	{
		Log.d("msg", "op = " + paramString);
		try
		{
			JSONObject localJSONObject = new JSONObject(paramString)
					.getJSONObject("msg");
			String str1 = localJSONObject.getString("msgvar");
			String str2 = localJSONObject.getString("msgstr");
			DEBUG.i(str2);
			if (!"do_success".equals(str1))
			{
				ShowMessage.getInstance(this)._showToast(str2, 2);
			} else
			{
				ShowMessageByHandler(str2, 1);
				setResult(-1, new Intent(this, MyInfo.class));
				finish();
			}
		} catch (Exception localException)
		{
			localException.printStackTrace();
		}
	}
}