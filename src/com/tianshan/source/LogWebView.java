package com.tianshan.source;

import android.util.Log;
import android.webkit.WebChromeClient;

public class LogWebView extends WebChromeClient
{
  public void addMessageToConsole(String paramString1, int paramInt, String paramString2)
  {
    Log.d("WoWebView", paramString2 + ": Line " + Integer.toString(paramInt) + " : " + paramString1);
  }
}

/* Location:           D:\yuanming\反编译\反编译工具\天上-论坛\tianshan-dex2jar.jar
 * Qualified Name:     com.tianshan.source.LogWebView
 * JD-Core Version:    0.6.2
 */
//R.id.head 