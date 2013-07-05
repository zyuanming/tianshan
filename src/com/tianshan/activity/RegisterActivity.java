package com.tianshan.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.tianshan.R;
import com.tianshan.ZhangWoApp;
import com.tianshan.dbhelper.UserSessionDBHelper;
import com.tianshan.model.UserSession;
import com.tianshan.source.DEBUG;
import com.tianshan.source.ShowMessage;
import com.tianshan.source.SiteTools;
import com.tianshan.source.activity.SecondLevelActivity;
import com.tianshan.source.view.Navbar;
import com.tianshan.source.webinterface.BaseWebInterFace;

public class RegisterActivity extends SecondLevelActivity implements
		Navbar.OnNavigateListener, BaseWebInterFace.OnWebFinishListener
{
	private void showAlert(int paramInt)
	{
		new AlertDialog.Builder(this)
				.setTitle(paramInt)
				.setPositiveButton(17039370,
						new DialogInterface.OnClickListener()
						{
							public void onClick(
									DialogInterface paramAnonymousDialogInterface,
									int paramAnonymousInt)
							{
								paramAnonymousDialogInterface.dismiss();
							}
						}).create().show();
	}

	protected void _initNavBar(boolean paramBoolean)
	{
		super._initNavBar(paramBoolean);
		Navbar localNavbar = new Navbar(this, this.navbarbox);
		localNavbar.setCommitBtnVisibility(true);
		localNavbar.setCommitBtnText(R.string.nav_btn_commit);
		localNavbar.setOnNavigate(this);
		localNavbar.setTitle(R.string.nav_title_register);
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
		_initToolBar(false);
		this.webinterface.setListener(this);
		this.webinterface.GotoUrl(SiteTools
				.getMobileUrl(new String[] { "ac=register" }));
	}

	public void onFinish(String s)
	{
		JSONObject jsonobject1 = null;
		try
		{
			JSONObject jsonobject = new JSONObject(s);
			DEBUG.o(jsonobject);
			jsonobject1 = jsonobject.optJSONObject("msg");
			if ("register_success".equals(jsonobject1.optString("msgvar")))
			{
				JSONObject jsonobject2 = jsonobject.optJSONObject("res");
				if (jsonobject2 != null)
				{
					JSONObject jsonobject3 = jsonobject2.optJSONObject("list");
					if (jsonobject3 != null)
					{
						UserSession usersession = new UserSession(jsonobject3);
						ZhangWoApp zhangwoapp = ZhangWoApp.getInstance();
						zhangwoapp.setUserSession(usersession);
						zhangwoapp
								.setLoginCookie("auth", usersession.getAuth());
						zhangwoapp.setLoginCookie("saltkey",
								usersession.getSaltkey());
						zhangwoapp.setLoginCookie("mobile_auth",
								usersession.getmobile_auth());
						usersession
								.setWebViewCookies(dwebview
										.getCookieManager()
										.getCookie(
												SiteTools
														.getMobileUrl(new String[] { "ac=login" })));
						zhangwoapp.setLoginCookie("webviewcookies",
								usersession.getWebViewCookies());
						UserSessionDBHelper usersessiondbhelper = UserSessionDBHelper
								.getInstance(this);
						if (usersessiondbhelper.getCountByUId(usersession
								.getUid()) == 0)
							usersessiondbhelper.insert(usersession);
						else
							usersessiondbhelper.update(usersession);
					}
				}
				ShowMessageByHandler(0x7f07006d, 1);
				finish();
			}
		} catch (JSONException jsonexception)
		{
			jsonexception.printStackTrace();
			if ("invalid_checkcode".equals(jsonobject1.optString("msgvar")))
				ShowMessage.getInstance(this)._showToast(
						R.string.STR_INVALID_CHECKCODE, 2);
			else
				ShowMessage.getInstance(this)._showToast(
						jsonobject1.optString("msgstr"), 2);
		}
	}
}