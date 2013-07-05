package com.tianshan;

import android.app.Activity;
import android.app.Application;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import com.tianshan.dbhelper.UserSessionDBHelper;
import com.tianshan.model.UserSession;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 应用程序全局类
 * 
 * @author lkh
 * 
 */
public class ZhangWoApp extends Application
{
	private static ZhangWoApp instance;
	private List<Activity> activityList = new LinkedList();
	private HashMap<String, String> loginCookie = null;
	// 它是一个特殊字符串头，使得服务器能够识别客户使用的操作系统
	// 及版本、CPU 类型、浏览器及版本、浏览器渲染引擎、浏览器语言、浏览器插件等
	// 标准格式为： 浏览器标识 (操作系统标识; 加密等级标识; 浏览器语言) 渲染引擎标识 版本信息
	private String userAgent; // 用户代理
	private UserSession userSession = null;

	public static ZhangWoApp getInstance()
	{
		return instance;
	}

	public static void setInstance(ZhangWoApp paramZhangWoApp)
	{
		instance = paramZhangWoApp;
	}

	public void AddActivity(Activity paramActivity)
	{
		this.activityList.add(paramActivity);
	}

	/**
	 * 退出应用程序
	 */
	public void Exit()
	{
		Iterator localIterator = this.activityList.iterator();
		while (true)
		{
			if (!localIterator.hasNext())
			{
				System.exit(0);
				return;
			}
			((Activity) localIterator.next()).finish();
		}
	}

	public void SetUserAgent(String paramString)
	{
		this.userAgent = paramString;
	}

	/**
	 * 清除登录的Cookie
	 */
	public void clearLoginCookie()
	{
		if (this.loginCookie != null)
			this.loginCookie.clear();
		this.loginCookie = null;
	}

	public HashMap<String, String> getLoginCookie()
	{
		return this.loginCookie;
	}

	public String getUserAgent()
	{
		return this.userAgent;
	}

	public UserSession getUserSession()
	{
		return this.userSession;
	}

	/**
	 * 根据UserSession是否为空来判断用户是否登录
	 * 
	 * @return
	 */
	public boolean isLogin()
	{
		boolean flag;
		if (userSession == null)
			flag = false;
		else
			flag = true;
		return flag;
	}

	public void onCreate()
	{
		super.onCreate();
	}

	/**
	 * 重置用户为游客身份
	 */
	public void resetUserToGuest()
	{
		setUserSession(null);
		clearLoginCookie();
		CookieSyncManager.createInstance(this);
		CookieManager.getInstance().removeAllCookie();
		UserSessionDBHelper.getInstance(this).deleteAll();
	}

	/**
	 * 设置，保存登录的Cookie
	 * 
	 * @param paramString1用户名
	 * @param paramString2密码
	 */
	public void setLoginCookie(String paramString1, String paramString2)
	{
		if (this.loginCookie == null)
			this.loginCookie = new HashMap();
		this.loginCookie.put(paramString1, paramString2);
	}

	/**
	 * 设置，保存用户的会话
	 * 
	 * @param paramUserSession
	 */
	public void setUserSession(UserSession paramUserSession)
	{
		this.userSession = paramUserSession;
	}
}