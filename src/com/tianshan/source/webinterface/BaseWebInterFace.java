package com.tianshan.source.webinterface;

import java.io.File;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.tianshan.R;
import com.tianshan.model.UserSession;
import com.tianshan.setting.Setting;
import com.tianshan.source.DEBUG;
import com.tianshan.source.SiteTools;
import com.tianshan.source.activity.BaseActivity;
import com.tianshan.source.view.DWebView;

public class BaseWebInterFace
{
	public static final int ACTIVITY_RESULT_URL_FOR_FINISH = 1002;
	public static final int ACTIVITY_RESULT_URL_NAME = 1001;
	private HashMap<String, String> activitylists;
	private boolean allowWrite = false;
	private String commentCount = null;
	private BaseActivity context;
	private OnWebFinishListener listener;
	private String newsTitle;
	private String pagehash;
	private ProgressBar progress;
	private boolean qqlogin = false;
	private DWebView webview;

	public BaseWebInterFace(BaseActivity paramBaseActivity,
			DWebView paramDWebView, ProgressBar paramProgressBar)
	{
		this.context = paramBaseActivity;
		this.webview = paramDWebView;
		this.progress = paramProgressBar;
	}

	private String getFinalUrl(String s, String s1, String s2, int i)
	{
		String s3;
		if (s1.equals("news"))
		{
			if (((ConnectivityManager) context.getSystemService("connectivity"))
					.getActiveNetworkInfo().getType() != 1)
			{
				String s4 = (new StringBuilder(String.valueOf(s1))).append("_")
						.append(s2).append("_").append(i).append(".html")
						.toString();
				if ((new File((new StringBuilder("/mnt/sdcard/tianshan/html/"))
						.append(s4).toString())).exists())
					s3 = (new StringBuilder("file://sdcard/tianshan/html/"))
							.append(s4).toString();
				else
					s3 = (new StringBuilder("http://client.xjts.cn/admin/"))
							.append(SiteTools.initGetParam(new String[] { s }))
							.toString();
			} else
			{
				s3 = (new StringBuilder("http://client.xjts.cn/admin/"))
						.append(SiteTools.initGetParam(new String[] { s }))
						.toString();
			}
		} else if (!s.startsWith("http"))
			s3 = (new StringBuilder("http://client.xjts.cn/admin/")).append(
					SiteTools.initGetParam(new String[] { s })).toString();
		else
			s3 = s;
		return s3;
	}

	private String getSecondLevelActivityPackage(String paramString1,
			String paramString2)
	{
		Log.d("ac", paramString1 + paramString2);
		String str = paramString1 + "_" + paramString2;
		if (this.activitylists == null)
		{
			this.activitylists = new HashMap();
			this.activitylists.put("news_view",
					"com.tianshan.activity.NewsViewActivity");
			this.activitylists.put("board_view",
					"com.tianshan.activity.BoardActivity");
			this.activitylists.put("video_view",
					"com.tianshan.activity.VideoViewActivity");
			this.activitylists.put("board_add",
					"com.tianshan.activity.AddBoardActivity");
			this.activitylists.put("newthread_",
					"com.tianshan.activity.PostActivity");
			this.activitylists.put("reply_",
					"com.tianshan.activity.ReplyActivity");
			this.activitylists.put("forumdisplay_",
					"com.tianshan.activity.ForumDisplayViewActivity");
			this.activitylists.put("viewthread_",
					"com.tianshan.activity.ViewthreadViewActivity");
			this.activitylists.put("register_",
					"com.tianshan.activity.RegisterActivity");
			this.activitylists.put("news_special",
					"com.tianshan.activity.SecondLevelTopicsActivity");
			this.activitylists.put("ad_ad",
					"com.tianshan.activity.ShowADActivity");
			this.activitylists.put("mypm_add",
					"com.tianshan.activity.SendMessageActivity");
			this.activitylists.put("mypm_view",
					"com.tianshan.activity.MessageViewActivity");
			this.activitylists.put("mythread_",
					"com.tianshan.activity.NearPeopleViewActivity");
			this.activitylists.put("login_",
					"com.tianshan.activity.LoginActivity");
		}
		return (String) this.activitylists.get(str);
	}

	private void gotoUrlWithNet(String s)
	{
		if (s.startsWith("http"))
		{
			context.handler.post(new ProgressShowRunnable());
			Log.d("yaksa%%", s);
			webview.loadUrl(s);
			webview.setOnPageLoad(new com.tianshan.source.view.DWebView.onPageLoad()
			{

				public void pageError()
				{
					try
					{
						progress.setVisibility(8);
						Thread.sleep(50L);
					} catch (InterruptedException interruptedexception)
					{
						interruptedexception.printStackTrace();
					}
					webview.setVisibility(0);
				}

				public void pageFinished(WebView webview1, String s7)
				{
					Log.d("login",
							(new StringBuilder("pageFinished")).append(s7)
									.toString());
					if (!BaseActivity.listshow)
						webview.setVisibility(0);
					context.handler.post(new ProgressHideRunnable());
				}

				public void pageStart(WebView webview1, String s7)
				{
					progress.setVisibility(0);
				}
			});
		} else
		{
			if (s.startsWith("javascript:"))
			{
				webview.loadUrl(s);
				if (!Setting.SUBMITGPS && !Setting.CLEARINFO
						&& !Setting.ADDINFO)
					context.handler.post(new ProgressShowRunnable());
			} else
			{
				if (s.startsWith("file://"))
					webview.loadUrl(s);
			}
		}

		JSONObject jsonobject;
		try
		{
			jsonobject = new JSONObject(s);
			Log.d("chenglong", s);
			Intent intent = new Intent();
			int i = jsonobject.optInt("target");
			String s1 = jsonobject.optString("url");
			String s2 = jsonobject.optString("ac");
			String s3 = jsonobject.optString("op");
			String s4 = getFinalUrl(s1, s2, s3, jsonobject.optInt("id"));
			jsonobject.optInt("direction");
			Log.d("chenglong", (new StringBuilder("getFinalUrl  = "))
					.append(s4).toString());
			intent.putExtra("url", s4);
			if (i != 1)
			{
				if (i == 2)
				{
					intent.putExtra("params", s);
					context.setResult(1002, intent);
					context.finish();
				} else if (i == 3)
				{
					String s5 = getSecondLevelActivityPackage(s2, s3);
					if (s5 != null)
					{
						intent.setClassName(context, s5);
						intent.putExtra("params", s);
						context.startActivityForResult(intent, 1001);
						context.finish();
					}
				} else if (i == 0)
				{
					DEBUG.o((new StringBuilder("  target = 0      "))
							.append(s4).toString());
					webview.loadUrl(s4);
					context.handler.post(new ProgressShowRunnable());
				}
			} else
			{
				String s6 = getSecondLevelActivityPackage(s2, s3);
				if (s6 == null)
				{
					webview.loadUrl(s4);
					context.handler.post(new ProgressShowRunnable());
					webview.setOnPageLoad(new com.tianshan.source.view.DWebView.onPageLoad()
					{

						public void pageError()
						{
							try
							{
								progress.setVisibility(8);
								Thread.sleep(50L);
							} catch (InterruptedException interruptedexception)
							{
								interruptedexception.printStackTrace();
							}
							webview.setVisibility(0);
						}

						public void pageFinished(WebView webview1, String s7)
						{
							Log.d("login", (new StringBuilder("pageFinished"))
									.append(s7).toString());
							if (!BaseActivity.listshow)
								webview.setVisibility(0);
							context.handler.post(new ProgressHideRunnable());
						}

						public void pageStart(WebView webview1, String s7)
						{
							progress.setVisibility(0);
						}
					});
				} else
				{
					intent.setClassName(context, s6);
					intent.putExtra("params", s);
					context.startActivityForResult(intent, 1001);
				}
			}
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	private void gotoUrlWithoutNet(String s)
	{
		webview.setVisibility(0);
		progress.setVisibility(8);
		JSONObject jsonobject1;
		try
		{
			jsonobject1 = new JSONObject(s);
		} catch (JSONException e)
		{
			jsonobject1 = null;
			e.printStackTrace();
		}
		if (!s.startsWith("javascript:"))
		{
			if (jsonobject1 != null)
			{
				Intent intent = new Intent();
				String s1 = jsonobject1.optString("url");
				int i = jsonobject1.optInt("target");
				jsonobject1.optInt("direction");
				String s2 = jsonobject1.optString("ac");
				String s3 = jsonobject1.optString("op");
				int j = jsonobject1.optInt("id");
				if (!s1.startsWith("http"))
					s1 = (new StringBuilder(String.valueOf(s2))).append("_")
							.append(s3).append("_").append(j).append(".html")
							.toString();
				intent.putExtra("url", s1);
				if (i == 1)
				{
					String s5 = getSecondLevelActivityPackage(s2, s3);
					if (s5 != null)
					{
						intent.setClassName(context, s5);
						Log.d("aaa", s);
						intent.putExtra("params", s);
						context.startActivityForResult(intent, 1001);
					} else
					{
						webview.loadUrl((new StringBuilder(
								"file://sdcard/tianshan/html/")).append(s1)
								.toString());
					}
				} else if (i == 2)
				{
					intent.putExtra("params", s);
					context.setResult(1002, intent);
					context.finish();
				} else if (i == 3)
				{
					String s4 = getSecondLevelActivityPackage(s2, s3);
					if (s4 != null)
					{
						intent.setClassName(context, s4);
						intent.putExtra("params", s);
						context.startActivityForResult(intent, 1001);
						context.finish();
					}
				} else if (i == 0)
					webview.loadUrl(s1);
			} else
			{
				webview.loadUrl((new StringBuilder(
						"file://sdcard/tianshan/html/")).append(s).toString());
			}
			webview.setOnPageLoad(new com.tianshan.source.view.DWebView.onPageLoad()
			{

				public void pageError()
				{
					try
					{
						Thread.sleep(50L);
					} catch (InterruptedException interruptedexception)
					{
						interruptedexception.printStackTrace();
					}
					webview.setVisibility(0);
				}

				public void pageFinished(WebView webview1, String s6)
				{}

				public void pageStart(WebView webview1, String s6)
				{}
			});
		} else
		{
			webview.loadUrl(s);
		}
	}

	private void webviewLoadUrl_AnimationIn(int i)
	{
		if (context != null && webview != null)
		{
			final Animation animation = AnimationUtils
					.loadAnimation(context, i);
			context.handler.post(new Runnable()
			{
				public void run()
				{
					webview.startAnimation(animation);
				}
			});
		}
	}

	private void webviewLoadUrl_AnimationOut(final String paramString,
			final int i)
	{
		if (context != null && webview != null)
		{
			final Animation localAnimation = AnimationUtils.loadAnimation(
					this.context, R.anim.fade_left_out);
			final int AnimationIn_drawable = 0x7f040002;
			switch (i)
			{
			default:
				break;
			case R.anim.fade_right_out:
				this.webview.setOnPageLoad(new DWebView.onPageLoad()
				{
					public void pageError()
					{}

					public void pageFinished(WebView paramAnonymousWebView,
							String paramAnonymousString)
					{
						BaseWebInterFace.this.progress.setVisibility(8);
						BaseWebInterFace.this.webviewLoadUrl_AnimationIn(i);
					}

					public void pageStart(WebView paramAnonymousWebView,
							String paramAnonymousString)
					{}
				});
				localAnimation.setFillAfter(true);
				localAnimation
						.setAnimationListener(new Animation.AnimationListener()
						{
							public void onAnimationEnd(
									Animation paramAnonymousAnimation)
							{
								BaseWebInterFace.this.webview.clearView();
								BaseWebInterFace.this.webview
										.loadUrl(paramString);
								BaseWebInterFace.this.progress.setVisibility(0);
							}

							public void onAnimationRepeat(
									Animation paramAnonymousAnimation)
							{}

							public void onAnimationStart(
									Animation paramAnonymousAnimation)
							{}
						});
				this.context.handler.post(new Runnable()
				{
					public void run()
					{
						BaseWebInterFace.this.webview
								.startAnimation(localAnimation);
					}
				});
				break;
			}
		}
	}

	public void ClearView()
	{
		this.webview.clearView();
	}

	public void GotoNewsPic(String paramString1, String paramString2)
	{
		if ((paramString1 != null) && (Setting.checkConn(this.context)))
		{
			Intent localIntent = new Intent();
			localIntent.setClassName(this.context,
					"com.tianshan.activity.PhotoViewActivity");
			localIntent.putExtra("album_id", paramString1);
			localIntent.putExtra("from", paramString2);
			this.context.startActivityForResult(localIntent, 1001);
		}
	}

	public void GotoPic(String paramString)
	{
		if ((paramString != null) && (Setting.checkConn(this.context)))
		{
			Intent localIntent = new Intent();
			localIntent.setClassName(this.context,
					"com.tianshan.activity.PhotoViewActivity");
			localIntent.putExtra("album_id", paramString);
			localIntent.putExtra("from", "album");
			this.context.startActivityForResult(localIntent, 1001);
		}
	}

	public void GotoUrl(String s)
	{
		Log.d("params", s);
		if (Setting.checkConn(context))
			gotoUrlWithNet(s);
		else
			gotoUrlWithoutNet(s);
	}

	public void Referer()
	{
		this.webview.reload();
	}

	public void SetMember(String paramString1, String paramString2)
	{
		UserSession localUserSession = new UserSession();
		localUserSession.setUid(Long.valueOf(paramString1).longValue());
		localUserSession.setUsername(paramString2);
	}

	public void SetMemberPerm(String paramString)
	{
		if ((paramString != null) && (!"undefined".equals(paramString)))
		{
			try
			{
				JSONObject localJSONObject = new JSONObject(paramString);
				if (localJSONObject.has("allowWrite"))
					this.allowWrite = localJSONObject.optBoolean("allowWrite");
			} catch (JSONException localJSONException)
			{
				localJSONException.printStackTrace();
			}
		}
	}

	public void ShowMessage(final String message, final String icon)
	{
		if (context != null)
			(new Timer()).schedule(new TimerTask()
			{
				public void run()
				{
					context.handler.post(new ProgressHideRunnable());
					int i = Integer.valueOf(icon).intValue();
					context.ShowMessageByHandler(message, i);
				}
			}, 500L);
	}

	public void finish(final String paramString)
	{
		this.context.handler.post(new Runnable()
		{
			public void run()
			{
				BaseWebInterFace.this.progress.setVisibility(8);
				if (BaseWebInterFace.this.listener != null)
					BaseWebInterFace.this.listener.onFinish(paramString);
			}
		});
	}

	public String getCommentCount()
	{
		return this.commentCount;
	}

	public void getFavState(String paramString1, String paramString2)
	{}

	public String getNewsTitle()
	{
		return this.newsTitle;
	}

	public String getPagehash()
	{
		return this.pagehash;
	}

	public boolean getWritable()
	{
		return this.allowWrite;
	}

	public void setCommentCount(String paramString)
	{
		this.commentCount = paramString;
	}

	public void setFullScreen(String s)
	{
		if (this.context != null)
		{
			int i = Integer.valueOf(s).intValue();
			final LinearLayout navbar_box = (LinearLayout) context
					.findViewById(0x7f090041);
			final LinearLayout tool_box = (LinearLayout) context
					.findViewById(0x7f090042);
			if (navbar_box != null && tool_box != null)
			{
				final byte VisibilityStatus;
				if (i == 1)
					VisibilityStatus = 8;
				else
					VisibilityStatus = 0;
				context.handler.post(new Runnable()
				{
					public void run()
					{
						navbar_box.setVisibility(VisibilityStatus);
						tool_box.setVisibility(VisibilityStatus);
					}
				});
			}
		}

	}

	public void setListener(OnWebFinishListener paramOnWebFinishListener)
	{
		this.listener = paramOnWebFinishListener;
	}

	public void setNewsTitle(String paramString)
	{
		this.newsTitle = paramString;
	}

	public void setOnAuthorizeListener(
			DWebView.onAuthorizeCallback paramonAuthorizeCallback)
	{
		this.webview.setOnAuthorizeListener(paramonAuthorizeCallback);
	}

	public void setOnPageLoad(DWebView.onPageLoad paramonPageLoad)
	{
		this.webview.setOnPageLoad(paramonPageLoad);
	}

	public ProgressBar setProgressBarVisible(boolean flag)
	{
		if (flag)
			progress.setVisibility(0);
		else
			progress.setVisibility(8);
		return progress;
	}

	public void sethash(String paramString)
	{
		this.pagehash = paramString;
	}

	public static abstract interface OnWebFinishListener
	{
		public abstract void onFinish(String paramString);
	}

	private class ProgressHideRunnable implements Runnable
	{
		private ProgressHideRunnable()
		{}

		public void run()
		{
			BaseWebInterFace.this.progress.setVisibility(8);
		}
	}

	private class ProgressShowRunnable implements Runnable
	{
		private ProgressShowRunnable()
		{}

		public void run()
		{
			BaseWebInterFace.this.progress.setVisibility(0);
		}
	}
}