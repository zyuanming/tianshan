package com.tianshan.source;

import android.util.Log;
import java.io.PrintStream;

public class DEBUG
{

	public DEBUG()
	{}

	public static String WoGetStactTrace(Exception exception)
	{
		String s;
		if (exception == null)
		{
			s = null;
		} else
		{
			s = exception.toString();
			StackTraceElement astacktraceelement[] = exception.getStackTrace();
			int j = 0;
			while (astacktraceelement != null && j < astacktraceelement.length)
			{
				s = (new StringBuilder(String.valueOf(s))).append("\n")
						.append(astacktraceelement[j].toString()).toString();
				j++;
			}
		}
		return s;
	}

	public static void WoPrintStackTrace(Exception exception)
	{
		if (exception != null)
		{
			Log.e("Wo",
					(new StringBuilder("Eeception: ")).append(
							exception.toString()).toString());
			StackTraceElement astacktraceelement[] = exception.getStackTrace();
			int j = 0;
			while (astacktraceelement != null && j < astacktraceelement.length)
			{
				Log.e("Wo", astacktraceelement[j].toString());
				j++;
			}
		}
	}

	public static void d(Object obj)
	{
		if (showDebug.booleanValue())
			Log.d("Discuz DEBUG(D)", (new StringBuilder()).append(obj)
					.toString());
	}

	public static void e(Object obj)
	{
		if (showDebug.booleanValue())
			Log.v("Discuz DEBUG(V)", (new StringBuilder()).append(obj)
					.toString());
	}

	public static void i(Object obj)
	{
		if (showDebug.booleanValue())
			Log.i("Discuz DEBUG(I):", (new StringBuilder()).append(obj)
					.toString());
	}

	public static void o(Object obj)
	{
		if (showDebug.booleanValue())
			System.out.println(obj);
	}

	private static Boolean showDebug = Boolean.valueOf(true);

}
