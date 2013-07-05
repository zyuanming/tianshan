package com.tianshan.source.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tianshan.source.DEBUG;
import com.tianshan.source.activity.BaseActivity;

public class DWebView extends WebView
{
	private final String JavaScriptInterFaceName = "ZW";
	private BaseActivity context = null;
	public DWebView dwebview = this;
	private Boolean onHtmlError = Boolean.valueOf(false);
	private HashSet<onPageLoad> onPageLoadListeners = new HashSet();
	private onAuthorizeCallback onauthorcb = null;
	private onLoadUrl onloadurl = null;
	private onPageError onpageerror = null;
	public WebChromeClient webChromeClient = null;
	public WebViewClient webViewClient = null;
	private WebSettings websettings = null;

	public DWebView(Context paramContext)
	{
		super(paramContext);
	}

	public DWebView(Context paramContext, AttributeSet paramAttributeSet)
	{
		super(paramContext, paramAttributeSet);
	}

	/**
	 * 使用addJavascriptInterface()允许JavaScript来控制您的应用程序。 这是一个非常有用的功能或一个危险的安全问题
	 * 。当HTML在WebView是不可靠的(例如,部分或所有的HTML是由一些人或过程产生的),那么攻击者可以注入HTML
	 * ,将执行你的代码和可能的任何代码的攻击者的选择。
	 * 不要使用addJavascriptInterface(),除非所有的HTML在这个WebView文字由你指定。
	 * Java对象绑定运行在另一个线程的线程,而不是在当前线程中。
	 * 
	 * @param paramObject
	 *            绑定到JavaScript的对象
	 */
	public void _addJavaScriptInterFace(Object paramObject)
	{
		// 绑定指定对象到JavaScript，使得这个方法可以从JavaScript中访问
		// 第二个参数是暴露给JavaScript的类的名称
		addJavascriptInterface(paramObject, JavaScriptInterFaceName);
	}

	public WebSettings _getSettings()
	{
		return this.websettings;
	}

	public void _init(BaseActivity paramBaseActivity)
	{
		this.context = paramBaseActivity;
		this.websettings = getSettings();
		this.websettings.setJavaScriptEnabled(true); // 设置支持Javascript的参数,会导致XSS漏洞
		this.websettings.setBuiltInZoomControls(false); // 设置不出现缩放工具
		this.websettings.setSupportZoom(false); // 设置不可以支持缩放
		this.websettings.setJavaScriptCanOpenWindowsAutomatically(true);// 设置JavaScript自动打开窗口
		this.websettings.setPluginsEnabled(true); // 设置支持插件
		setBackgroundColor(0);
		// 为WebView设置初始化的伸缩比例，如果WebSettings.getUseWideViewPort()是真的,WebView向所有方向放大
		// 否则显示原始的大小，如果初始化值大于0，WebView会以此为初始化值
		setInitialScale(this.context.core._getWebViewScale());

		// 指定滚动条的样式。滚动条可以覆盖overlaid或插图inset。当是插图时,它们增加到填充的视图。滚动条可以画在
		// 内部填充区域或边缘的视图。例如,如果一个视图有一个背景绘图，你想在指定的绘图内填充滚动条,
		// 可以使用滚动条内叠加（SCROLLBARS_INSIDE_OVERLAY）或滚动条内插图（SCROLLBARS_INSIDE_INSET）。
		// 如果你想让他们出现在视图的边缘,忽略了填充,然后您可以使用滚动条外覆盖或滚动条外插图。

		/**
		 * View.SCROLLBARS_INSIDE_OVERLAY 在内容区显示滚动条，不增加填充物。 滚动条将半透明地覆盖在视图的内容
		 */
		setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
	}

	public void _initWebChromeClient()
	{
		_initWebChromeClient(null);
	}

	/**
	 * 
	 * @param webchromeclient
	 *            WebChromeClient是辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等
	 */
	public void _initWebChromeClient(WebChromeClient webchromeclient)
	{
		if (webchromeclient != null)
		{
			webChromeClient = webchromeclient;
		} else
		{
			webChromeClient = new WebChromeClient()
			{
				// 网页加载进度变化回调方法
				public void onProgressChanged(WebView webview, int i)
				{}
			};
			setWebChromeClient(webChromeClient);
		}
	}

	public void _initWebViewClient()
	{
		_initWebViewClient(null);
	}

	public void _initWebViewClient(WebViewClient paramWebViewClient)
	{
		if (paramWebViewClient != null)
			this.webViewClient = paramWebViewClient;
		else
		{
			this.webViewClient = new WebViewClient()
			{
				// 通知主机应用程序页面接在成功，这个方法只在主框架中调用，
				// 但是渲染的图片可能不会更新，为了通知新图片，可以调用
				// WebView.PictureListener.onNewPicture.
				@Override
				public void onPageFinished(WebView paramAnonymousWebView,
						String paramAnonymousString)
				{
					super.onPageFinished(paramAnonymousWebView,
							paramAnonymousString);
					Iterator localIterator2;
					if (onHtmlError.booleanValue())
					{
						dwebview.loadUrl("javascript:showerr('" + "加载失败" + "')");
						localIterator2 = onPageLoadListeners.iterator();
						while (localIterator2.hasNext())
						{
							((DWebView.onPageLoad) localIterator2.next())
									.pageError();
						}
					} else
					{
						Iterator localIterator1 = onPageLoadListeners
								.iterator();
						while (localIterator1.hasNext())
							((DWebView.onPageLoad) localIterator1.next())
									.pageFinished(paramAnonymousWebView,
											paramAnonymousString);
					}
				}

				@Override
				public void onPageStarted(WebView paramAnonymousWebView,
						String paramAnonymousString, Bitmap paramAnonymousBitmap)
				{
					ArrayList localArrayList = new ArrayList();
					localArrayList.add(new Integer(-1));
					String str = checkOAuthBack(paramAnonymousString,
							localArrayList);
					if (str != null)
					{
						if (onauthorcb != null)
							onauthorcb.onComplete(
									(Integer) localArrayList.get(0), str);
					} else
					{
						if ((paramAnonymousString.endsWith(".mp4"))
								|| (paramAnonymousString
										.endsWith("some other supported type")))
						{
							Intent localIntent = new Intent(
									"android.intent.action.VIEW");
							localIntent.setDataAndType(
									Uri.parse(paramAnonymousString),
									"video/mp4");
							context.startActivity(localIntent);
						} else
						{
							super.onPageStarted(paramAnonymousWebView,
									paramAnonymousString, paramAnonymousBitmap);
							Iterator localIterator = onPageLoadListeners
									.iterator();
							while (localIterator.hasNext())
								((DWebView.onPageLoad) localIterator.next())
										.pageStart(paramAnonymousWebView,
												paramAnonymousString);
						}
					}
				}

				@Override
				public void onReceivedError(WebView paramAnonymousWebView,
						int paramAnonymousInt, String paramAnonymousString1,
						String paramAnonymousString2)
				{
					DWebView.this.onHtmlError = Boolean.valueOf(true);
					DEBUG.o("failingUrl:" + paramAnonymousString2);
					DEBUG.o("description:" + paramAnonymousString1);
					DWebView.this.dwebview
							.loadUrl("file:///android_asset/html_error.html");
				}

				@Override
				public boolean shouldOverrideUrlLoading(WebView webview,
						String s)
				{
					Log.d("shouldOverrideUrlLoading ", s);
					boolean flag;
					if (s.lastIndexOf("discuz://") > -1 && onloadurl != null)
					{
						onloadurl.load(s);
						flag = true;
					} else
					{
						flag = false;
					}
					return flag;
				}
			};
			setWebViewClient(this.webViewClient);
		}
	}

	protected String checkOAuthBack(String paramString,
			ArrayList<Integer> paramArrayList)
	{
		String str = null;
		if (paramString.startsWith("http://client.xjts.cn/?code"))
		{
			String[] arrayOfString4 = paramString.split("=");
			int k = arrayOfString4.length;
			if (k >= 2)
			{
				paramArrayList.clear();
				paramArrayList.add(Integer.valueOf(3));
				str = arrayOfString4[1];
			}
		} else
		{
			if (paramString.startsWith("http://client.xjts.cn/?#access_token="))
			{
				String[] arrayOfString2 = paramString.split("&");
				int j = arrayOfString2.length;
				if (j >= 2)
				{
					String[] arrayOfString3 = arrayOfString2[0].split("=");
					paramArrayList.clear();
					paramArrayList.add(Integer.valueOf(4));
					str = arrayOfString3[1];
				}
			} else
			{
				boolean bool = paramString
						.startsWith("http://client.xjts.cn/?code");
				if (bool)
				{
					String[] arrayOfString1 = paramString.split("=");
					int i = arrayOfString1.length;
					if (i >= 2)
					{
						paramArrayList.clear();
						paramArrayList.add(Integer.valueOf(2));
						str = arrayOfString1[1];
					}
				}
			}
		}
		return str;
	}

	public CookieManager getCookieManager()
	{
		return CookieManager.getInstance();
	}

	public boolean isError()
	{
		return this.onHtmlError.booleanValue();
	}

	public void loadDataWithBaseURL(String paramString1, String paramString2,
			String paramString3, String paramString4, String paramString5)
	{
		super.loadDataWithBaseURL(paramString1, paramString2, paramString3,
				paramString4, paramString5);
	}

	public void setOnAuthorizeListener(
			onAuthorizeCallback paramonAuthorizeCallback)
	{
		this.onauthorcb = paramonAuthorizeCallback;
	}

	public void setOnLoadUrl(onLoadUrl paramonLoadUrl)
	{
		this.onloadurl = paramonLoadUrl;
	}

	public void setOnPageError(onPageError paramonPageError)
	{
		this.onpageerror = paramonPageError;
	}

	public void setOnPageLoad(onPageLoad paramonPageLoad)
	{
		this.onPageLoadListeners.add(paramonPageLoad);
	}

	public static abstract interface onAuthorizeCallback
	{
		public abstract void onComplete(Integer paramInteger, String paramString);
	}

	public static abstract interface onLoadUrl
	{
		public abstract void load(String paramString);
	}

	public static abstract interface onPageError
	{
		public abstract void pageError(WebView paramWebView);
	}

	public static abstract interface onPageLoad
	{
		public abstract void pageError();

		public abstract void pageFinished(WebView paramWebView,
				String paramString);

		public abstract void pageStart(WebView paramWebView, String paramString);
	}
}