package com.tianshan.source;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import com.tianshan.setting.Setting;
import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Core
{
	private static final char[] HEX_DIGITS = { 48, 49, 50, 51, 52, 53, 54, 55,
			56, 57, 97, 98, 99, 100, 101, 102 };
	private Context context;
	private Display display;
	public String siteKey = null;

	public Core(Context paramContext)
	{
		this.context = paramContext;
	}

	/**
	 * Context.getFilesDir()--->返回保存当前应用程序文件在系统目录中的绝对路径。
	 * 
	 * @param paramContext
	 * @return
	 */
	public static String _getADPath(Context paramContext)
	{
		File localFile = new File(paramContext.getFilesDir() + File.separator
				+ "ad");
		if (!localFile.exists())
			localFile.mkdirs();
		return localFile.getAbsolutePath() + File.separator;
	}

	/**
	 * 获得手机当前网络的IP地址，可以是IPV4，或者是IPV6
	 * 
	 * @return
	 */
	public static String _getLocalIpAddress()
	{
		String str1 = null;
		try
		{
			InetAddress localInetAddress;
			// 获得本地系统中所有有效的网络接口列表
			Enumeration localEnumeration1 = NetworkInterface
					.getNetworkInterfaces();
			Enumeration localEnumeration2;
			do
			{
				if (localEnumeration1.hasMoreElements())
				{
					localEnumeration2 = ((NetworkInterface) localEnumeration1
							.nextElement()).getInetAddresses();
					// 获得绑定到指定网络接口的地址列表
					do
					{
						InetAddress inetaddress = (InetAddress) localEnumeration2
								.nextElement();
						// 判断当前地址是不是有效的回传地址，IPV4的是以192.d.d开头，IPV6是以：：1
						if (!inetaddress.isLoopbackAddress())
						{
							str1 = inetaddress.getHostAddress().toString();
							return str1;
						}
					} while (localEnumeration2.hasMoreElements());
				}
			} while (!localEnumeration1.hasMoreElements());
		} catch (SocketException localSocketException)
		{
			DEBUG.e(localSocketException.toString());
			str1 = null;
		}
		return str1;
	}

	public static String _getMobileMemPath(Context paramContext)
	{
		return paramContext.getFilesDir().getAbsolutePath() + File.separator;
	}

	private PackageInfo _getPackAgeInfo()
	{
		PackageManager localPackageManager = this.context.getPackageManager();
		PackageInfo localPackageInfo1 = null;
		try
		{
			localPackageInfo1 = localPackageManager.getPackageInfo(
					this.context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException localNameNotFoundException)
		{
			localNameNotFoundException.printStackTrace();
		}
		return localPackageInfo1;
	}

	public static String _getPhotoCachePath(Context context1)
	{
		File file;
		if (Setting.IsCanUseSdCard())
			file = new File(
					(new StringBuilder(String.valueOf(getSDCardPath())))
							.append(File.separator).append("tianshan")
							.append(File.separator).append("photo_cache")
							.toString());
		else
			file = new File((new StringBuilder())
					.append(context1.getFilesDir()).append(File.separator)
					.append("tianshan").append(File.separator)
					.append("photo_cache").toString());
		if (!file.exists())
			file.mkdirs();
		return (new StringBuilder(String.valueOf(file.getAbsolutePath())))
				.append(File.separator).toString();
	}

	public static String _getPhotosPath(Context context1)
	{
		File file;
		if (Setting.IsCanUseSdCard())
			file = new File(
					(new StringBuilder(String.valueOf(getSDCardPath())))
							.append(File.separator).append("tianshan")
							.append(File.separator).append("photos").toString());
		else
			file = new File((new StringBuilder())
					.append(context1.getFilesDir()).append(File.separator)
					.append("tianshan").append(File.separator).append("photos")
					.toString());
		if (!file.exists())
			file.mkdirs();
		return (new StringBuilder(String.valueOf(file.getAbsolutePath())))
				.append(File.separator).toString();
	}

	/**
	 * 使窗口没有标题
	 */
	private void _windowNoTitle()
	{
		((Activity) this.context).requestWindowFeature(1);
	}

	public static String getSDCardPath()
	{
		String s = Environment.getExternalStorageState();
		String s1;
		if (TextUtils.isEmpty(s))
		{
			s1 = "";
		} else
		{
			boolean flag;
			if (!s.equals("mounted") && !s.equals("shared"))
				flag = false;
			else
				flag = true;
			if (flag)
				s1 = (new StringBuilder(String.valueOf(Environment
						.getExternalStorageDirectory().toString()))).append(
						File.separator).toString();
			else
				s1 = "";
		}
		return s1;
	}

	public int _getActiveNetWorkType()
	{
		return ((ConnectivityManager) this.context
				.getSystemService("connectivity")).getActiveNetworkInfo()
				.getType();
	}

	public String _getBuild()
	{
		return "and_all-" + String.valueOf(_getVersionCode());
	}

	public String _getDefaultUserAgent()
	{
		return new WebView(this.context).getSettings().getUserAgentString();
	}

	public Display _getDisplay()
	{
		this.display = ((Activity) this.context).getWindowManager()
				.getDefaultDisplay();
		return this.display;
	}

	/**
	 * 获得手机的IMEI号
	 * 
	 * @return
	 */
	public String _getIMEI()
	{
		return ((TelephonyManager) this.context.getSystemService("phone"))
				.getDeviceId();
	}

	/**
	 * 获得当前上下文中内存信息
	 */
	public void _getMemoryInfo()
	{
		ActivityManager localActivityManager = (ActivityManager) this.context
				.getSystemService("activity");
		ActivityManager.MemoryInfo localMemoryInfo = new ActivityManager.MemoryInfo();
		localActivityManager.getMemoryInfo(localMemoryInfo);
		DEBUG.o(" minfo.availMem " + localMemoryInfo.availMem);
		DEBUG.o(" minfo.lowMemory " + localMemoryInfo.lowMemory);
		DEBUG.o(" minfo.threshold " + localMemoryInfo.threshold);
	}

	public String _getMessageByName(String[] paramArrayOfString)
	{
		String str = _getStringByName("message_" + paramArrayOfString[0]);
		if ("".equals(str))
			str = paramArrayOfString[1];
		return str;
	}

	public String _getMobileDataParams(String as[], String s)
	{
		String s1 = "";
		String s2 = "";
		int i = 0;
		do
		{
			if (i >= as.length)
				return (new StringBuilder(
						String.valueOf(Tools._md5((new StringBuilder(String
								.valueOf(Tools._md5(s1)))).append(s).toString()))))
						.append("|").append(s1).toString();
			StringBuilder stringbuilder = new StringBuilder(String.valueOf(s1));
			String s3;
			StringBuilder stringbuilder1;
			String s4;
			if (s1 == "")
				s3 = as[i];
			else
				s3 = (new StringBuilder("|")).append(as[i]).toString();
			s1 = stringbuilder.append(s3).toString();
			stringbuilder1 = new StringBuilder(String.valueOf(s2));
			if (s2 == "")
				s4 = as[i];
			else
				s4 = (new StringBuilder("|")).append(as[i]).toString();
			s2 = stringbuilder1.append(s4).toString();
			i++;
		} while (true);
	}

	public String _getPackAgeName()
	{
		return _getPackAgeInfo().packageName;
	}

	public String _getPhoneNumber()
	{
		return ((TelephonyManager) this.context.getSystemService("phone"))
				.getLine1Number();
	}

	public String _getSiteKey()
	{
		return this.siteKey;
	}

	public String _getStringByName(String s)
	{
		int i = context.getResources().getIdentifier(s, "string",
				_getPackAgeName());
		String s1;
		if (i == 0)
			s1 = "";
		else
			s1 = context.getString(i);
		return s1;
	}

	public int _getTitleHeight(View paramView)
	{
		Rect localRect = new Rect();
		paramView.getWindowVisibleDisplayFrame(localRect);
		return localRect.top;
	}

	public int _getVersionCode()
	{
		return _getPackAgeInfo().versionCode;
	}

	public String _getVersionName()
	{
		return _getPackAgeInfo().versionName;
	}

	public int _getWebViewScale()
	{
		int i = _getDisplay().getWidth();
		char c;
		if (i < 480)
			c = 'D';
		else if (i == 720)
			c = '\341';
		else if (i > 720)
			c = '\310';
		else
			c = 'd';
		return c;
	}

	/**
	 * 是否有网络连接 1、网络信息为空或网络没有连接---> false 2、当前漫游在这个网络，会产生额外数据---> false
	 * 
	 * @return
	 */
	public boolean _hasInternet()
	{
		boolean flag = true;
		NetworkInfo networkinfo = ((ConnectivityManager) context
				.getSystemService("connectivity")).getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isConnected())
			flag = false;
		else if (networkinfo.isRoaming())
		{
			flag = false;
		}
		return flag;
	}

	/**
	 * 显示地请求关闭当前上下文中软输入区
	 */
	public void _hideSoftInput()
	{
		((InputMethodManager) this.context.getSystemService("input_method"))
				.hideSoftInputFromWindow(((Activity) this.context)
						.getCurrentFocus().getWindowToken(), 2);
	}

	/**
	 * 显示地请求关闭特定EditText上的软输入区
	 * 
	 * @param paramEditText
	 */
	public void _hideSoftInput(EditText paramEditText)
	{
		((InputMethodManager) this.context.getSystemService("input_method"))
				.hideSoftInputFromWindow(paramEditText.getWindowToken(), 0);
	}

	/**
	 * 初始化窗口，没有标题栏
	 */
	public void _initWindow()
	{
		_windowNoTitle();
	}

	/**
	 * 显示地请求当前输入法的软输入区显示给用户，如果需要的话。
	 * 
	 * @param paramEditText
	 */
	public void _showSoftInput(EditText paramEditText)
	{
		((InputMethodManager) this.context.getSystemService("input_method"))
				.showSoftInput(paramEditText, 2);
	}
}