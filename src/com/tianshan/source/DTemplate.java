package com.tianshan.source;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DTemplate
{
	private Activity activity = null;
	private String[] delimiter = { "\\<\\!\\-\\-\\{", "\\}\\-\\-\\>" };
	private String[] delimiter_prototype = { "<!--{", "}-->" };
	public HashMap<String, ArrayList<String>> loopValueMap = new HashMap();
	public HashMap<String, HashMap<String, String>> loopValues = new HashMap();
	private String tpl_suffix = ".html";

	public DTemplate()
	{}

	public DTemplate(Activity paramActivity)
	{
		this.activity = paramActivity;
	}

	public String _appendTemplate(String paramString)
	{
		Matcher localMatcher = Pattern.compile(
				this.delimiter[0] + "template\\s(.+?)" + this.delimiter[1])
				.matcher(paramString);
		StringBuffer localStringBuffer = new StringBuffer();
		while (true)
		{
			if (!localMatcher.find())
			{
				localMatcher.appendTail(localStringBuffer);
				return localStringBuffer.toString();
			}
			String str1 = _cutStrToVar(localMatcher.group());
			String str2 = _loadTplFile(this.activity, str1.substring(9));
			if (str2.lastIndexOf("{template ") > -1)
				str2 = _appendTemplate(str2);
			localMatcher.appendReplacement(localStringBuffer, str2);
		}
	}

	public String _cutStrToVar(String paramString)
	{
		return paramString.substring(this.delimiter_prototype[0].length(),
				paramString.lastIndexOf(this.delimiter_prototype[1]));
	}

	public void _initValue(HashMap<String, ArrayList<String>> paramHashMap,
			HashMap<String, HashMap<String, String>> paramHashMap1)
	{
		this.loopValueMap = paramHashMap;
		this.loopValues = paramHashMap1;
	}

	public String _loadTplFile(Activity paramActivity, String paramString)
	{
		StringBuffer localStringBuffer = new StringBuffer();
		this.activity = paramActivity;
		try
		{
			InputStreamReader localInputStreamReader = new InputStreamReader(
					this.activity.getResources().getAssets()
							.open("template/" + paramString + this.tpl_suffix),
					"utf-8");
			while (true)
			{
				int i = localInputStreamReader.read();
				if (i <= 0)
					return localStringBuffer.toString();
				char c = (char) i;
				localStringBuffer.append(c);
			}
		} catch (FileNotFoundException localFileNotFoundException)
		{
			Log.v("FileNotFoundException", "Template File Not Found");
		} catch (IOException localIOException)
		{
			localIOException.printStackTrace();
		}
		return localStringBuffer.toString();
	}

	public String _makePregSearch(String paramString)
	{
		return this.delimiter[0] + paramString + this.delimiter[1];
	}

	public String _parseForeach(String paramString,
			HashMap<String, String> paramHashMap)
	{
		Matcher localMatcher1 = Pattern.compile(
				this.delimiter[0] + "for\\s(.+?)" + this.delimiter[1], 32)
				.matcher(paramString);
		while (localMatcher1.find())
		{
			String str1 = _cutStrToVar(localMatcher1.group()).substring(4);
			String str2 = "";
			int i = localMatcher1.group().length();
			if (str1.lastIndexOf(":") > -1)
			{
				String str6 = str1.substring(str1.lastIndexOf(":"))
						.substring(1);
				if ((paramHashMap != null) && (paramHashMap.get(str6) != null))
					str2 = (String) paramHashMap.get(str6);
			}
			Matcher localMatcher2 = Pattern.compile(
					this.delimiter[0] + "for\\s" + str1 + this.delimiter[1]
							+ "(.+?)" + this.delimiter[0] + "\\/for\\s" + str1
							+ this.delimiter[1], 32).matcher(paramString);
			StringBuffer localStringBuffer = new StringBuffer();
			if (str2 != "")
				str1 = str1.substring(0, str1.lastIndexOf(":")) + str2;
			String str3;
			String str4;
			while (localMatcher2.find())
			{
				str3 = localMatcher2.group().substring(i,
						-1 + (localMatcher2.group().length() - i));
				str4 = "";
				if ((this.loopValueMap != null)
						&& (this.loopValueMap.get(str1) != null))
				{
					for (int j = 0;; j++)
					{
						if (j >= ((ArrayList) this.loopValueMap.get(str1))
								.size())
						{
							localMatcher2.appendReplacement(localStringBuffer,
									str4);
							localMatcher2.appendTail(localStringBuffer);
							paramString = localStringBuffer.toString();
							break;
						}
						String str5 = str3;
						if (str3.lastIndexOf("for ") > -1)
							str5 = _parseForeach(str5,
									(HashMap) this.loopValues
											.get(((ArrayList) this.loopValueMap
													.get(str1)).get(j)));
						str4 = str4
								+ _parseSimpleTag(
										str5,
										(HashMap) this.loopValues
												.get(((ArrayList) this.loopValueMap
														.get(str1)).get(j)));
					}
				}
			}
		}
		return paramString;
	}

	public String _parseSimpleTag(String paramString,
			HashMap<String, String> paramHashMap)
	{
		Matcher localMatcher;
		StringBuffer localStringBuffer;
		if (paramHashMap != null)
		{
			localMatcher = Pattern.compile(
					this.delimiter[0] + "(.*?)" + this.delimiter[1]).matcher(
					paramString);
			localStringBuffer = new StringBuffer();
			while (true)
			{
				if (!localMatcher.find())
				{
					localMatcher.appendTail(localStringBuffer);
					paramString = localStringBuffer.toString();
					return paramString;
				}
				String str1 = _cutStrToVar(localMatcher.group());
				if (paramHashMap.get(str1) != null)
				{
					String str2 = (String) paramHashMap.get(str1);
					if (str1.equals("message"))
						str2 = str2.replaceAll("\\$", "\uFF04");
					try
					{
						localMatcher.appendReplacement(localStringBuffer, str2);
					} catch (Exception localException)
					{
						localException.printStackTrace();
					}
				}
			}
		}
		return paramString;
	}

	public String _template(HashMap<String, String> paramHashMap,
			String paramString)
	{
		return _parseSimpleTag(_parseForeach(paramString, null), paramHashMap);
	}

	public void destroy()
	{
		this.loopValueMap = null;
		this.loopValues = null;
		this.activity = null;
		this.tpl_suffix = null;
		this.delimiter_prototype = null;
	}
}