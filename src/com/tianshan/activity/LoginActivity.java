package com.tianshan.activity;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import com.tianshan.R;
import com.tianshan.ZhangWoApp;
import com.tianshan.dbhelper.UserSessionDBHelper;
import com.tianshan.model.UserSession;
import com.tianshan.source.Core;
import com.tianshan.source.DEBUG;
import com.tianshan.source.GPSUtil;
import com.tianshan.source.HttpRequest;
import com.tianshan.source.HttpsRequest;
import com.tianshan.source.ShowMessage;
import com.tianshan.source.SiteTools;
import com.tianshan.source.activity.SecondLevelActivity;
import com.tianshan.source.view.DWebView;
import com.tianshan.source.view.Navbar;
import com.tianshan.source.webinterface.BaseWebInterFace;
import com.tianshan.task.CheckUpdateBaseAsyncTask;
import com.tianshan.task.SendGPSTask;

public class LoginActivity extends SecondLevelActivity implements
		Navbar.OnNavigateListener, BaseWebInterFace.OnWebFinishListener
{
	private boolean error = false;
	private String gps;
	private double[] loc;
	private boolean login = false;
	private String mDeviceID;
	private Navbar navbar;
	private boolean qqlogin = false;
	private CheckUpdateBaseAsyncTask.CheckTaskResultListener sendgpsListener = new CheckUpdateBaseAsyncTask.CheckTaskResultListener()
	{
		public void onTaskResult(boolean flag, HashMap hashmap)
		{
			if (flag)
			{
				String s = (String) hashmap.get("data");
				Log.d("result", (new StringBuilder("*****data**** ="))
						.append(s).toString());
				if (s != null || !s.equals(""))
					try
					{
						JSONObject jsonobject = new JSONObject(s);
						if (jsonobject != null)
						{
							JSONObject jsonobject1 = jsonobject
									.optJSONObject("msg");
							if (jsonobject1 != null)
							{
								DEBUG.o((new StringBuilder(String
										.valueOf(jsonobject1.optString(
												"msgvar", "list_empty"))))
										.append("****************************")
										.toString());
								HttpRequest httprequest1 = new HttpRequest();
								try
								{
									String as1[] = new String[1];
									as1[0] = (new StringBuilder(
											"ac=androidpush&pass=1234&type=add&devicetoken="))
											.append(mDeviceID)
											.append("&uid=")
											.append(ZhangWoApp.getInstance()
													.getUserSession().getUid())
											.toString();
									DEBUG.o(httprequest1._get(SiteTools
											.getApiUrl(as1)));
								} catch (Exception exception1)
								{
									exception1.printStackTrace();
								}
								ShowMessageByHandler(0x7f070072, 1);
								finish();
							}
						}
					} catch (JSONException jsonexception)
					{
						jsonexception.printStackTrace();
					}
			} else
			{
				HttpRequest httprequest = new HttpRequest();
				try
				{
					String as[] = new String[1];
					as[0] = (new StringBuilder(
							"ac=androidpush&pass=1234&type=add&devicetoken="))
							.append(mDeviceID)
							.append("&uid=")
							.append(ZhangWoApp.getInstance().getUserSession()
									.getUid()).toString();
					DEBUG.o(httprequest._get(SiteTools.getApiUrl(as)));
				} catch (Exception exception)
				{
					exception.printStackTrace();
				}
				ShowMessageByHandler(0x7f070072, 1);
				finish();
			}
		}
	};

	private void sendgps()
	{
		GPSUtil localGPSUtil = new GPSUtil(getApplicationContext());
		this.loc = localGPSUtil.setLatitudeAndLongitude();
		if (this.loc == null)
			this.loc = localGPSUtil.setLatitudeAndLongitude();
		if (this.loc != null)
			this.gps = (this.loc[0] + "," + this.loc[1]);
		Log.d("result", "*****gps**** =" + this.gps);
		String str = SiteTools
				.getMobileUrl(new String[] { "ac=lbs", "type=xy" })
				+ "&xy="
				+ this.gps;
		SendGPSTask localSendGPSTask = new SendGPSTask(getApplicationContext(),
				this.sendgpsListener, false);
		String[] arrayOfString = new String[2];
		arrayOfString[0] = str;
		arrayOfString[1] = this.gps;
		localSendGPSTask.execute(arrayOfString);
	}

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
		this.navbar = new Navbar(this, this.navbarbox);
		this.navbar.setCommitBtnVisibility(true);
		this.navbar.setCommitBtnText(R.string.nav_btn_login);
		this.navbar.setOnNavigate(this);
		this.navbar.setTitle(R.string.nav_title_login);
	}

	public void onBack()
	{
		onKeyDown(4, null);
	}

	public void onCommit()
	{
		this.webinterface.GotoUrl("javascript:SubmitForm()");
	}

	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		this.mDeviceID = Settings.Secure.getString(getContentResolver(),
				"android_id");
		_initNavBar(true);
		_initToolBar(false);
		this.webinterface.setListener(this);
		BaseWebInterFace localBaseWebInterFace = this.webinterface;
		String[] arrayOfString = new String[1];
		arrayOfString[0] = ("ac=login&ip=" + Core._getLocalIpAddress());
		localBaseWebInterFace.GotoUrl(SiteTools.getMobileUrl(arrayOfString));
		this.webinterface.setOnPageLoad(new DWebView.onPageLoad()
		{
			public void pageError()
			{}

			public void pageFinished(WebView webview, String s)
			{
				try
				{
					if (!s.contains("connect.php?receive=yes&mod=register"))
					{
						String s1;
						HttpRequest httprequest;
						if (s.contains("loginsubmit=yes")
								|| s.contains("mod=connect"))
						{
							s1 = dwebview
									.getCookieManager()
									.getCookie(
											SiteTools
													.getMobileUrl(new String[] { "ac=login" }));
							Log.d("cookie", s1);
							if (s1 != null && !"".equals(s1)
									&& s1.contains("_auth="))
							{
								httprequest = new HttpRequest();
								String as1[];
								as1 = URLDecoder.decode(s1, "utf-8").split(";");
								for (int i = 0; i < as1.length; i++)
								{
									httprequest._setCookie(as1[i]);
								}

								JSONObject jsonobject2;
								String s6;
								JSONObject jsonobject3;
								String s2 = httprequest
										._get("http://client.xjts.cn/admin/mobile.php?ac=buildlogin");
								Log.d("cookie", s2);
								JSONObject jsonobject = new JSONObject(s2);
								JSONObject jsonobject1 = jsonobject
										.getJSONObject("res");
								Log.d("msg", jsonobject.optJSONObject("msg")
										.optString("msgstr"));
								jsonobject2 = jsonobject1.optJSONObject("list");
								String s3 = URLEncoder.encode(jsonobject2
										.optString("auth"));
								String s4 = jsonobject2.optString("member_uid");
								String s5 = jsonobject2
										.optString("member_username");
								ArrayList arraylist = new ArrayList();
								arraylist.add(new BasicNameValuePair("ac",
										"genmobileauth"));
								arraylist
										.add(new BasicNameValuePair("uid", s4));
								arraylist.add(new BasicNameValuePair(
										"username", s5));
								s6 = HttpsRequest.openUrl(
										"http://client.xjts.cn/admin/api.php",
										"GET", arraylist, null, null, s1);
								String s7 = (new StringBuilder(
										"mobile_apiauth="))
										.append(jsonobject2.optString("auth"))
										.append("; domain=").append(".xjts.cn")
										.append("; path=/; expires=")
										.append(0x1e13380).toString();
								String s8 = (new StringBuilder(
										"mobile_saltkey="))
										.append(jsonobject2
												.optString("saltkey"))
										.append("; domain=").append(".xjts.cn")
										.append("; path=/; expires=")
										.append(0x1e13380).toString();
								String s9 = (new StringBuilder("mobile_auth="))
										.append(s6).append("; domain=")
										.append(".xjts.cn")
										.append("; path=/; expires=")
										.append(0x1e13380).toString();
								String s10 = (new StringBuilder(String
										.valueOf(jsonobject2
												.optString("cookiepre"))))
										.append("auth=").append(s3)
										.append("; domain=").append(".xjts.cn")
										.append("; path=/; expires=")
										.append(0x1e13380).toString();
								String s11 = (new StringBuilder(String
										.valueOf(jsonobject2
												.optString("cookiepre"))))
										.append("saltkey=")
										.append(URLEncoder.encode(jsonobject2
												.optString("saltkey")))
										.append("; domain=").append(".xjts.cn")
										.append("; path=/; expires=")
										.append(0x1e13380).toString();
								Log.d("----nma cookie-----", s7);
								Log.d("----nma cookie-----", s8);
								Log.d("----nma cookie-----", s9);
								Log.d("----nma cookie-----", s10);
								Log.d("----nma cookie-----", s11);
								dwebview.getCookieManager().setAcceptCookie(
										true);
								dwebview.getCookieManager()
										.setCookie(
												SiteTools
														.getMobileUrl(new String[] { "ac=login" }),
												s7);
								CookieSyncManager.getInstance().sync();
								dwebview.getCookieManager()
										.setCookie(
												SiteTools
														.getMobileUrl(new String[] { "ac=login" }),
												s8);
								CookieSyncManager.getInstance().sync();
								dwebview.getCookieManager()
										.setCookie(
												SiteTools
														.getMobileUrl(new String[] { "ac=login" }),
												s9);
								CookieSyncManager.getInstance().sync();
								dwebview.getCookieManager()
										.setCookie(
												SiteTools
														.getMobileUrl(new String[] { "ac=login" }),
												s10);
								CookieSyncManager.getInstance().sync();
								dwebview.getCookieManager()
										.setCookie(
												SiteTools
														.getMobileUrl(new String[] { "ac=login" }),
												s11);
								CookieSyncManager.getInstance().sync();
								jsonobject3 = new JSONObject();
								Exception exception;
								UserSession usersession;
								ZhangWoApp zhangwoapp;
								UserSessionDBHelper usersessiondbhelper;
								try
								{
									jsonobject3.put("cookiepre",
											jsonobject2.optString("cookiepre"));
									jsonobject3.put("auth",
											jsonobject2.optString("auth"));
									jsonobject3.put("saltkey",
											jsonobject2.optString("saltkey"));
									jsonobject3.put("member_uid",
											jsonobject2.optString("member_uid"));
									jsonobject3.put(
											"member_username",
											jsonobject2
													.optString("member_username"));
									jsonobject3.put("groupid",
											jsonobject2.optString("groupid"));
									jsonobject3.put("formhash",
											jsonobject2.optString("formhash"));
									jsonobject3.put("ismoderator", jsonobject2
											.optString("ismoderator"));
									jsonobject3.put("mobile_auth", s6);
								} catch (JSONException jsonexception)
								{
									jsonexception.printStackTrace();
								}
								usersession = new UserSession(jsonobject3);
								zhangwoapp = ZhangWoApp.getInstance();
								zhangwoapp.setUserSession(usersession);
								zhangwoapp.setLoginCookie("auth",
										usersession.getAuth());
								Log.d("auth",
										(new StringBuilder("  auth  =  "))
												.append(usersession.getAuth())
												.toString());
								zhangwoapp.setLoginCookie("saltkey",
										usersession.getSaltkey());
								Log.d("auth",
										(new StringBuilder("  saltkey  =  "))
												.append(usersession
														.getSaltkey())
												.toString());
								zhangwoapp.setLoginCookie("mobile_auth",
										usersession.getmobile_auth());
								Log.d("auth",
										(new StringBuilder("  mobile_auth  =  "))
												.append(usersession
														.getmobile_auth())
												.toString());
								usersession.setWebViewCookies(dwebview
										.getCookieManager()
										.getCookie(
												SiteTools
														.getMobileUrl(new String[] { "ac=login" })));
								zhangwoapp.setLoginCookie("webviewcookies",
										usersession.getWebViewCookies());
								usersessiondbhelper = UserSessionDBHelper
										.getInstance(LoginActivity.this);
								if (usersessiondbhelper
										.getCountByUId(usersession.getUid()) == 0)
								{
									usersessiondbhelper.insert(usersession);
									usersessiondbhelper.update(usersession);
									sendgps();
									if (s.contains("api/mobile/index.php?module=connect&mobilemessage=1"))
										dwebview.loadUrl("javascript:window.DZ.showSource(document.getElementsByTagName('body')[0].innerHTML);");
									else
										Log.d("url",
												(new StringBuilder(String
														.valueOf(s)))
														.append("*****************************")
														.toString());
								}
							}
						}
					} else
					{
						dwebview.loadUrl("javascript:{document.getElementsByName('register')[0].onsubmit=null;document.getElementsByName('login')[0].onsubmit=null;}");
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			public void pageStart(WebView paramAnonymousWebView,
					String paramAnonymousString)
			{
				Log.d("loginwebv", paramAnonymousString);
				JSONObject localJSONObject3;
				HttpRequest localHttpRequest;
				UserSession localUserSession;
				UserSessionDBHelper localUserSessionDBHelper;
				if (!LoginActivity.this.login)
				{
					ZhangWoApp.getInstance().resetUserToGuest();
					LoginActivity.this.login = true;
				}
				if ((paramAnonymousString.contains("qq"))
						&& (!LoginActivity.this.qqlogin))
				{
					Log.d("---------", "111111111111111111");
					LoginActivity.this.navbar.setCommitBtnVisibility(false);
					LoginActivity.this.navbar.setTitle("QQ登录");
					LoginActivity.this.qqlogin = true;
				}
				String[] arrayOfString1;
				JSONObject localJSONObject1;
				String str1;
				if (paramAnonymousString.lastIndexOf("discuz://") > -1)
				{
					Log.d("---------", "2222222222222222");
					arrayOfString1 = paramAnonymousString.split("\\/\\/");
					arrayOfString1[2] = URLDecoder.decode(arrayOfString1[2]);
					if (arrayOfString1[3] != null)
					{
						arrayOfString1[3] = URLDecoder
								.decode(arrayOfString1[3]);
						Log.d("msg", "msg 3" + arrayOfString1[3]);
					}
					localJSONObject1 = null;
					str1 = "";
					try
					{
						JSONObject localJSONObject2 = new JSONObject(
								arrayOfString1[3]);
						localJSONObject1 = localJSONObject2
								.optJSONObject("Variables");
						str1 = URLEncoder.encode(localJSONObject1
								.optString("auth"));
						Log.d("msg", "auth" + str1);
						Log.d("msg", arrayOfString1[1]);
						if ((arrayOfString1[1].contains("login_succeed"))
								|| (arrayOfString1[1]
										.contains("register_succeed"))
								|| (arrayOfString1[1]
										.contains("connect_register_bind_success")))
							if ((str1 != null) && (!str1.equals("null"))
									&& (!str1.equals("")))
							{
								dwebview.setVisibility(0);
								String str2 = LoginActivity.this.dwebview
										.getCookieManager()
										.getCookie(
												SiteTools
														.getMobileUrl(new String[] { "ac=login" }));
								String str3 = localJSONObject1
										.optString("member_uid");
								String str4 = localJSONObject1
										.optString("member_username");
								ArrayList localArrayList = new ArrayList();
								localArrayList.add(new BasicNameValuePair("ac",
										"genmobileauth"));
								localArrayList.add(new BasicNameValuePair(
										"uid", str3));
								localArrayList.add(new BasicNameValuePair(
										"username", str4));
								String str5 = HttpsRequest
										.openUrl(
												"http://client.xjts.cn/admin/api.php",
												"GET", localArrayList, null,
												null, str2);
								String str6 = "mobile_apiauth="
										+ localJSONObject1.optString("auth")
										+ "; domain=" + ".xjts.cn"
										+ "; path=/; expires=" + 31536000;
								String str7 = "mobile_saltkey="
										+ localJSONObject1.optString("saltkey")
										+ "; domain=" + ".xjts.cn"
										+ "; path=/; expires=" + 31536000;
								String str8 = "mobile_auth=" + str5
										+ "; domain=" + ".xjts.cn"
										+ "; path=/; expires=" + 31536000;
								String str9 = localJSONObject1
										.optString("cookiepre")
										+ "auth="
										+ str1
										+ "; domain="
										+ ".xjts.cn"
										+ "; path=/; expires=" + 31536000;
								String str10 = localJSONObject1
										.optString("cookiepre")
										+ "saltkey="
										+ URLEncoder.encode(localJSONObject1
												.optString("saltkey"))
										+ "; domain="
										+ ".xjts.cn"
										+ "; path=/; expires=" + 31536000;
								Log.d("----nma cookie-----", str6);
								Log.d("----nma cookie-----", str7);
								Log.d("----nma cookie-----", str8);
								Log.d("----nma cookie-----", str9);
								Log.d("----nma cookie-----", str10);
								dwebview.getCookieManager().setAcceptCookie(
										true);
								dwebview.getCookieManager()
										.setCookie(
												SiteTools
														.getMobileUrl(new String[] { "ac=login" }),
												str6);
								CookieSyncManager.getInstance().sync();
								dwebview.getCookieManager()
										.setCookie(
												SiteTools
														.getMobileUrl(new String[] { "ac=login" }),
												str7);
								CookieSyncManager.getInstance().sync();
								dwebview.getCookieManager()
										.setCookie(
												SiteTools
														.getMobileUrl(new String[] { "ac=login" }),
												str8);
								CookieSyncManager.getInstance().sync();
								dwebview.getCookieManager()
										.setCookie(
												SiteTools
														.getMobileUrl(new String[] { "ac=login" }),
												str9);
								CookieSyncManager.getInstance().sync();
								dwebview.getCookieManager()
										.setCookie(
												SiteTools
														.getMobileUrl(new String[] { "ac=login" }),
												str10);
								CookieSyncManager.getInstance().sync();
								localJSONObject3 = new JSONObject();
								localJSONObject3
										.put("cookiepre", localJSONObject1
												.optString("cookiepre"));
								localJSONObject3.put("auth",
										localJSONObject1.optString("auth"));
								localJSONObject3.put("saltkey",
										localJSONObject1.optString("saltkey"));
								localJSONObject3.put("member_uid",
										localJSONObject1
												.optString("member_uid"));
								localJSONObject3.put("member_username",
										localJSONObject1
												.optString("member_username"));
								localJSONObject3.put("groupid",
										localJSONObject1.optString("groupid"));
								localJSONObject3.put("formhash",
										localJSONObject1.optString("formhash"));
								localJSONObject3.put("ismoderator",
										localJSONObject1
												.optString("ismoderator"));
								localJSONObject3.put("mobile_auth", str5);
								localUserSession = new UserSession(
										localJSONObject3);
								ZhangWoApp localZhangWoApp = ZhangWoApp
										.getInstance();
								localZhangWoApp
										.setUserSession(localUserSession);
								localZhangWoApp.setLoginCookie("auth",
										localUserSession.getAuth());
								Log.d("auth",
										"  auth  =  "
												+ localUserSession.getAuth());
								localZhangWoApp.setLoginCookie("saltkey",
										localUserSession.getSaltkey());
								Log.d("auth", "  saltkey  =  "
										+ localUserSession.getSaltkey());
								localZhangWoApp.setLoginCookie("mobile_auth",
										localUserSession.getmobile_auth());
								Log.d("auth", "  mobile_auth  =  "
										+ localUserSession.getmobile_auth());
								localUserSession
										.setWebViewCookies(LoginActivity.this.dwebview
												.getCookieManager()
												.getCookie(
														SiteTools
																.getMobileUrl(new String[] { "ac=login" })));
								localZhangWoApp.setLoginCookie(
										"webviewcookies",
										localUserSession.getWebViewCookies());
								localUserSessionDBHelper = UserSessionDBHelper
										.getInstance(LoginActivity.this);
								if (localUserSessionDBHelper
										.getCountByUId(localUserSession
												.getUid()) == 0)
									localUserSessionDBHelper
											.insert(localUserSession);
								else
									localUserSessionDBHelper
											.update(localUserSession);
								sendgps();
								localHttpRequest = new HttpRequest();
								String[] arrayOfString2 = new String[1];
								arrayOfString2[0] = ("ac=androidpush&pass=1234&type=add&devicetoken="
										+ LoginActivity.this.mDeviceID
										+ "&uid=" + ZhangWoApp.getInstance()
										.getUserSession().getUid());
								DEBUG.o(localHttpRequest._get(SiteTools
										.getApiUrl(arrayOfString2)));
							} else
							{
								if (paramAnonymousString
										.contains("Mobile_Android"))
								{
									dwebview.stopLoading();
								} else
								{
									dwebview.loadUrl("http://client.xjts.cn/admin//mobile.php?ac=activation");
									ShowMessage.getInstance(LoginActivity.this)
											._showToast(arrayOfString1[2], 2);
									LoginActivity.this.dwebview
											.loadUrl("http://client.xjts.cn/admin//mobile.php?ac=activation");
								}
							}

					} catch (JSONException localJSONException2)
					{
						localJSONException2.printStackTrace();
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		});
	}

	public void onFinish(String paramString)
	{
		JSONObject localJSONObject2;
		try
		{
			DEBUG.o(paramString);
			JSONObject localJSONObject1 = new JSONObject(paramString);
			DEBUG.o(localJSONObject1);
			localJSONObject2 = localJSONObject1.optJSONObject("msg");
			if ("login_success".equals(localJSONObject2.optString("msgvar")))
			{
				JSONObject localJSONObject3 = localJSONObject1
						.optJSONObject("res");
				UserSession localUserSession;
				UserSessionDBHelper localUserSessionDBHelper;
				if (localJSONObject3 != null)
				{
					JSONObject localJSONObject4 = localJSONObject3
							.optJSONObject("list");
					Log.i("resultJson===", localJSONObject1.toString());
					Log.i("list===", localJSONObject4.toString());
					if (localJSONObject4 != null)
					{
						localUserSession = new UserSession(localJSONObject4);
						ZhangWoApp localZhangWoApp = ZhangWoApp.getInstance();
						localZhangWoApp.setUserSession(localUserSession);
						localZhangWoApp.setLoginCookie("auth",
								localUserSession.getAuth());
						Log.d("auth",
								"  auth  =  " + localUserSession.getAuth());
						localZhangWoApp.setLoginCookie("saltkey",
								localUserSession.getSaltkey());
						Log.d("auth",
								"  saltkey  =  "
										+ localUserSession.getSaltkey());
						localZhangWoApp.setLoginCookie("mobile_auth",
								localUserSession.getmobile_auth());
						Log.d("auth",
								"  mobile_auth  =  "
										+ localUserSession.getmobile_auth());
						String str = this.dwebview
								.getCookieManager()
								.getCookie(
										SiteTools
												.getMobileUrl(new String[] { "ac=login" }));
						Log.d("auth", "  cookie  =  " + str);
						localUserSession.setWebViewCookies(str);
						localZhangWoApp.setLoginCookie("webviewcookies",
								localUserSession.getWebViewCookies());
						localUserSessionDBHelper = UserSessionDBHelper
								.getInstance(this);
						if (localUserSessionDBHelper
								.getCountByUId(localUserSession.getUid()) == 0)
							localUserSessionDBHelper.insert(localUserSession);
						else
							localUserSessionDBHelper.update(localUserSession);
					}
				}
				sendgps();
			}
			if ("invalid_checkcode"
					.equals(localJSONObject2.optString("msgvar")))
				ShowMessage.getInstance(this)._showToast(
						R.string.STR_INVALID_CHECKCODE, 2);
			else
				ShowMessage.getInstance(this)._showToast(
						localJSONObject2.optString("msgstr"), 2);
		} catch (JSONException localJSONException)
		{
			localJSONException.printStackTrace();
		}

	}

	public boolean onKeyDown(int i, KeyEvent keyevent)
	{
		boolean flag;
		if (dwebview.canGoBack() && i == 4)
		{
			dwebview.goBack();
			flag = true;
		} else
		{
			finish();
			flag = false;
		}
		return flag;
	}
}