package com.tianshan.source;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONObject;

import android.util.Log;

import com.tianshan.ZhangWoApp;
import com.tianshan.model.DownLoadModel;

public class HttpRequest
{
	public static final String BOUNDARY = "7cd4a6d158c";
	public static final String END_MP_BOUNDARY = "--7cd4a6d158c--";
	public static final String MP_BOUNDARY = "--7cd4a6d158c";
	public static final String MULTIPART_FORM_DATA = "multipart/form-data";
	private static final String boundary = "****************fD4fH3gL0hK7aI6";
	private static final String lineEnd = System.getProperty("line.separator");
	private static final String twoHyphens = "--";
	public HttpURLConnection conn;
	// 网络连接的Cookies
	public HashMap<String, String> connCookies = null;
	public String cookiePre = null;
	public BasicHttpParams httpParameters;
	public HttpResponse response = null;

	public HttpRequest()
	{
		ZhangWoApp localZhangWoApp = ZhangWoApp.getInstance();
		if ((localZhangWoApp != null) && (localZhangWoApp.isLogin()))
			_setCookie(localZhangWoApp.getLoginCookie());
	}

	public HttpRequest(ArrayList<String> paramArrayList)
	{
		if (paramArrayList != null)
		{
			for (int i = 0; i < paramArrayList.size(); i++)
			{
				_setCookie((String) paramArrayList.get(i));
			}
		}
	}

	public static HashMap GetHtmlorImg(String s, String s1) throws Exception
	{
		HashMap hashmap = new HashMap();
		HttpURLConnection httpurlconnection = (HttpURLConnection) (new URL(s))
				.openConnection();
		httpurlconnection.setConnectTimeout(6000);
		httpurlconnection.setRequestMethod("GET");
		Log.d("sd",
				(new StringBuilder(String.valueOf(httpurlconnection
						.getResponseCode()))).append(" ** ").append(s)
						.toString());
		if (httpurlconnection.getResponseCode() == 200)
			hashmap.put("content",
					readStream(httpurlconnection.getInputStream()));
		else
			hashmap = null;
		return hashmap;
	}

	/**
	 * 
	 * @param paramString1
	 *            请求URL
	 * @param paramString2
	 *            请求方式 GET\POST
	 * @return
	 * @throws IOException
	 */
	private HttpURLConnection _createConn(String paramString1,
			String paramString2) throws IOException
	{
		URL localURL = new URL(paramString1);
		if (this.conn != null)
			this.conn.disconnect();
		this.conn = ((HttpURLConnection) localURL.openConnection());
		// 如果不设置连接主机超时（timeout），在网络异常的情况下，可能会导致程序僵死而不继续往下执行
		// 单位：毫秒
		this.conn.setConnectTimeout(60000);
		this.conn.setRequestMethod(paramString2);
		this.conn.setUseCaches(false);
		this.conn.setInstanceFollowRedirects(true); // 连接遵循重定向，即请求会跳转
		this.conn.addRequestProperty("User-Agent", ZhangWoApp.getInstance()
				.getUserAgent());
		this.conn.addRequestProperty("Accept-Encoding", "gzip, deflate"); // 设置接收数据的压缩格式
		if (paramString2.equals("POST"))
		{
			// 发送POST请求必须设置允许输出
			this.conn.setDoInput(true); // 设置允许输入
			this.conn.setDoOutput(true); // 设置允许输出
			this.conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
		}
		HashMap localHashMap = this.connCookies;
		String str = null;
		Iterator localIterator;
		if (localHashMap != null)
		{
			str = "";
			if (!this.connCookies.isEmpty())
			{
				localIterator = this.connCookies.entrySet().iterator();
				while (true)
				{
					if (!localIterator.hasNext())
					{
						if (str != null)
							this.conn.addRequestProperty("Cookie", str);
						break;
					}
					Map.Entry localEntry = (Map.Entry) localIterator.next();
					str = str + (String) localEntry.getKey() + "="
							+ (String) localEntry.getValue() + ";";
				}
			}
		}
		return this.conn;
	}

	/**
	 * 初始化请求URL的参数
	 * 
	 * @param s
	 *            请求的基本参数
	 * @param hashmap
	 *            请求URL附加的参数名和参数值对
	 * @param s1
	 *            编码格式 (默认是UTF-8)
	 * @return 完整的URL
	 */
	private String _initUrlParams(String s, HashMap hashmap, String s1)
	{
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append(s);
		if (hashmap != null && !hashmap.isEmpty())
		{
			Iterator iterator;
			if (stringbuilder.indexOf("?") == -1)
				stringbuilder.append('?');
			else
				stringbuilder.append("&");
			iterator = hashmap.entrySet().iterator();
			if (iterator.hasNext())
			{
				Entry entry = (Entry) iterator.next();
				try
				{
					stringbuilder
							.append((String) entry.getKey())
							.append('=')
							.append(URLEncoder.encode(
									(String) entry.getValue(), s1)).append('&');
				} catch (UnsupportedEncodingException unsupportedencodingexception)
				{
					unsupportedencodingexception.printStackTrace();
				}
			} else
			{
				// 删除循环时多添加的最后一个参数连接符
				stringbuilder.deleteCharAt(-1 + stringbuilder.length());
			}
		}
		return stringbuilder.toString();
	}

	public static DownLoadModel downloadRes(String paramString1,
			String paramString2, String paramString3)
	{
		DownLoadModel downloadmodel = null;
		try
		{
			HashMap localHashMap = GetHtmlorImg(paramString1, paramString3);
			downloadmodel = new DownLoadModel();
			if (localHashMap != null)
			{
				downloadmodel.setContent((byte[]) localHashMap.get("content"));
				Log.d("sd", paramString1);
				downloadmodel.setName(paramString2);
				downloadmodel.setType(paramString3);
				downloadmodel.setStatus((String) localHashMap.get("status"));
				downloadmodel.setUrl(paramString1);
			}
		} catch (Exception localException)
		{
			localException.printStackTrace();
		}
		return downloadmodel;
	}

	/**
	 * 对gzip文件进行读取
	 * 
	 * @param httpurlconnection
	 *            网络连接对象
	 * @return gzip文件的输入流
	 * @throws IOException
	 */
	private InputStream getGZIP(HttpURLConnection httpurlconnection)
			throws IOException
	{
		Object obj = httpurlconnection.getInputStream();
		if ("gzip".equals(httpurlconnection.getContentEncoding()))
			obj = new GZIPInputStream(((InputStream) (obj)));
		return ((InputStream) (obj));
	}

	private String postResponse(String paramString) throws IOException
	{
		Integer localInteger = Integer.valueOf(this.conn.getResponseCode());
		String str2;
		String str3;
		String str1 = "";
		if (localInteger.intValue() == 200)
		{
			InputStream localInputStream = getGZIP(this.conn);
			InputStreamReader localInputStreamReader = new InputStreamReader(
					localInputStream, paramString);
			str2 = "";
			BufferedReader localBufferedReader = new BufferedReader(
					localInputStreamReader);
			str3 = localBufferedReader.readLine();
			if (str3 == null)
			{
				localInputStreamReader.close();
				localInputStream.close();
				this.conn.disconnect();
				str1 = str2;
			} else
			{
				str2 = str2 + str3 + "\n";
				if (localInteger.intValue() >= 400)
				{
					str1 = "error";
				} else
				{
					this.conn.disconnect();
					DEBUG.e("HttpUrlConnection Error:" + localInteger);
					str1 = null;
				}
			}
		}
		return str1;
	}

	private void postString(HashMap<String, String> paramHashMap,
			String paramString) throws IOException
	{
		StringBuilder localStringBuilder = new StringBuilder();
		Iterator localIterator;
		if ((paramHashMap != null) && (!paramHashMap.isEmpty()))
		{
			localIterator = paramHashMap.entrySet().iterator();
			while (true)
			{
				if (!localIterator.hasNext())
				{
					localStringBuilder.deleteCharAt(-1
							+ localStringBuilder.length());
					byte[] arrayOfByte = localStringBuilder.toString()
							.getBytes();
					this.conn.setRequestProperty("Content-Length",
							String.valueOf(arrayOfByte.length));
					this.conn.connect();
					OutputStream localOutputStream = this.conn
							.getOutputStream();
					localOutputStream.write(arrayOfByte);
					localOutputStream.flush();
					if (localOutputStream != null)
						localOutputStream.close();
					return;
				}
				Map.Entry localEntry = (Map.Entry) localIterator.next();
				localStringBuilder
						.append((String) localEntry.getKey())
						.append('=')
						.append(URLEncoder.encode(
								((String) localEntry.getValue()).trim(),
								paramString)).append('&');
			}
		}
	}

	public static byte[] readStream(InputStream paramInputStream)
			throws Exception
	{
		byte[] arrayOfByte = new byte[1024];
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		while (true)
		{
			int i = paramInputStream.read(arrayOfByte);
			if (i == -1)
			{
				paramInputStream.close();
				localByteArrayOutputStream.close();
				return localByteArrayOutputStream.toByteArray();
			}
			localByteArrayOutputStream.write(arrayOfByte, 0, i);
		}
	}

	public void _clearCookie()
	{
		this.connCookies = null;
	}

	/**
	 * 
	 * @param paramString请求的基本参数
	 * @return
	 * @throws Exception
	 */
	public String _get(String paramString) throws Exception
	{
		return _get(paramString, null, null);
	}

	/**
	 * 
	 * @param paramString1请求的基本参数
	 * @param paramHashMap请求URL附加的参数名和参数值对
	 * @param paramString2编码格式
	 * @return
	 * @throws Exception
	 */
	public String _get(String paramString1,
			HashMap<String, String> paramHashMap, String paramString2)
			throws Exception
	{
		if (paramString2 == null)
			paramString2 = "utf-8";
		String str1 = _initUrlParams(paramString1, paramHashMap, paramString2);
		_createConn(str1, "GET");
		this.conn.connect();
		Integer localInteger = Integer.valueOf(this.conn.getResponseCode());
		if (localInteger.intValue() == -1)
		{
			_createConn(str1, "GET");
			this.conn.connect();
			localInteger = Integer.valueOf(this.conn.getResponseCode());
		}
		DEBUG.o("GET URL:" + str1);
		DEBUG.o("GET responseCode:" + localInteger);
		StringBuilder localStringBuilder;
		String str3;
		String str2 = null;
		if (localInteger.intValue() == 200)
		{
			InputStream localInputStream = getGZIP(this.conn);
			InputStreamReader localInputStreamReader = new InputStreamReader(
					localInputStream, paramString2);
			localStringBuilder = new StringBuilder();
			BufferedReader localBufferedReader = new BufferedReader(
					localInputStreamReader);
			while (true)
			{
				str3 = localBufferedReader.readLine();
				if (str3 == null)
				{
					localInputStreamReader.close();
					localInputStream.close();
					DEBUG.o("GET URL Input Stream close :" + paramString1);
					this.conn.disconnect();
					str2 = localStringBuilder.toString();
					break;
				} else
				{
					localStringBuilder.append(str3).append("\n");
				}
			}
		} else if (localInteger.intValue() >= 400)
		{
			if (this.conn != null)
				this.conn.disconnect();
			str2 = "error";
		} else if (localInteger.intValue() == -1)
		{
			DEBUG.e("#######HttpUrlConnection Error:" + localInteger);
			HttpURLConnection localHttpURLConnection = this.conn;
			if (localHttpURLConnection != null)
			{
				this.conn.disconnect();
			}
		} else
		{
			if (this.conn != null)
				this.conn.disconnect();
			DEBUG.e("HttpUrlConnection Error:" + localInteger);
		}
		return str2;
	}

	public void _getFile(String paramString1,
			HashMap<String, String> paramHashMap, String paramString2,
			requestCallBack paramrequestCallBack) throws Exception
	{
		String str = _initUrlParams(paramString1, paramHashMap, paramString2);
		_createConn(str, "GET");
		this.conn.connect();
		DEBUG.o("=============vocode url===============" + paramString1);
		Integer localInteger = Integer.valueOf(this.conn.getResponseCode());
		if (localInteger.intValue() == -1)
		{
			_createConn(str, "GET");
			this.conn.connect();
			localInteger = Integer.valueOf(this.conn.getResponseCode());
		}
		DEBUG.o("=============responseCode===============" + localInteger);
		if (localInteger.intValue() == 200)
		{
			InputStream localInputStream = getGZIP(this.conn);
			if (paramrequestCallBack != null)
				paramrequestCallBack.download(localInputStream);
			localInputStream.close();
			if (this.conn != null)
				this.conn.disconnect();
		} else if (localInteger.intValue() >= 400)
		{
			if (this.conn != null)
				this.conn.disconnect();
		} else
		{
			DEBUG.e("HttpUrlConnection Error:" + localInteger);
			if (this.conn != null)
				this.conn.disconnect();
		}
	}

	public String _httpPostFile(String s, HashMap hashmap) throws IOException
	{
		byte abyte1[];
		HttpURLConnection httpurlconnection;
		OutputStream outputstream;
		FileInputStream fileinputstream;
		byte abyte2[];
		DEBUG.o(s);
		DEBUG.o(hashmap.get("uid"));
		DEBUG.o(hashmap.get("avatar"));
		File file = new File(hashmap.get("avatar").toString());
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append((new StringBuilder("--"))
				.append("---------------------------7db1c523809b2")
				.append("\r\n").toString());
		stringbuilder
				.append("Content-Disposition: form-data; name=\"uid\"\r\n");
		stringbuilder.append("\r\n");
		stringbuilder.append((new StringBuilder(String.valueOf(hashmap.get(
				"uid").toString()))).append("\r\n").toString());
		stringbuilder.append((new StringBuilder("--"))
				.append("---------------------------7db1c523809b2")
				.append("\r\n").toString());
		stringbuilder
				.append((new StringBuilder(
						"Content-Disposition: form-data; name=\"avatar\"; filename=\""))
						.append(hashmap.get("avatar").toString()).append("\"")
						.append("\r\n").toString());
		stringbuilder.append("Content-Type: image/pjpeg\r\n");
		stringbuilder.append("\r\n");
		byte abyte0[] = stringbuilder.toString().getBytes("UTF-8");
		abyte1 = (new StringBuilder("\r\n--"))
				.append("---------------------------7db1c523809b2")
				.append("--\r\n").toString().getBytes("UTF-8");
		httpurlconnection = (HttpURLConnection) (new URL(s)).openConnection();
		httpurlconnection.setRequestMethod("POST");
		httpurlconnection.setRequestProperty(
				"Content-Type",
				(new StringBuilder("multipart/form-data; boundary=")).append(
						"---------------------------7db1c523809b2").toString());
		httpurlconnection.setRequestProperty(
				"Content-Length",
				String.valueOf((long) abyte0.length + file.length()
						+ (long) abyte1.length));
		httpurlconnection.setDoOutput(true);
		outputstream = httpurlconnection.getOutputStream();
		fileinputstream = new FileInputStream(file);
		outputstream.write(abyte0);
		abyte2 = new byte[1024];
		while (true)
		{
			int i = fileinputstream.read(abyte2);
			if (i != -1)
			{
				outputstream.write(abyte2, 0, i);
			} else
			{
				BufferedReader bufferedreader = new BufferedReader(
						new InputStreamReader(
								httpurlconnection.getInputStream()));
				String s1 = null;
				outputstream.write(abyte1);
				String s2;
				while (true)
				{
					s2 = bufferedreader.readLine();
					if (s2 == null)
					{
						fileinputstream.close();
						outputstream.close();
						return s1;
					} else
					{
						s1 = (new StringBuilder(String.valueOf(s2))).append(
								"\n").toString();
					}
				}
			}
		}
	}

	public String _post(String paramString1,
			HashMap<String, String> paramHashMap1,
			HashMap<String, String> paramHashMap2, String paramString2)
			throws Exception
	{
		if (paramString2 == null)
			paramString2 = "utf-8";
		String str = _initUrlParams(paramString1, paramHashMap1, paramString2);
		DEBUG.o("=============LOGIN POST URL===========" + str);
		_createConn(str, "POST");
		postString(paramHashMap2, paramString2);
		return postResponse(paramString2);
	}

	public String _postFile(String s, String s1, String s2)
	{
		String s3 = "";
		DataOutputStream dataoutputstream;
		FileInputStream fileinputstream;
		byte abyte0[];
		try
		{
			conn = (HttpURLConnection) (new URL(s)).openConnection();
			conn.setConnectTimeout(60000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "keep-alive");
			conn.setRequestProperty("Cookie", s2);
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=****************fD4fH3gL0hK7aI6");
			conn.connect();
			dataoutputstream = new DataOutputStream(conn.getOutputStream());
			File file = new File(s1);
			Log.i("file.length()",
					(new StringBuilder(String.valueOf(file.length())))
							.toString());
			dataoutputstream.writeBytes((new StringBuilder(
					"--****************fD4fH3gL0hK7aI6")).append(lineEnd)
					.toString());
			dataoutputstream.writeBytes((new StringBuilder(
					"Content-Disposition: form-data; name=\"uploadsubmit\""))
					.append(lineEnd).toString());
			dataoutputstream.writeBytes(lineEnd);
			dataoutputstream.writeBytes((new StringBuilder("ture")).append(
					lineEnd).toString());
			dataoutputstream.writeBytes((new StringBuilder(
					"--****************fD4fH3gL0hK7aI6")).append(lineEnd)
					.toString());
			dataoutputstream
					.writeBytes((new StringBuilder(
							"Content-Disposition: form-data; name=\"attach\"; filename=\""))
							.append(file.getName()).append("\"")
							.append(lineEnd).toString());
			dataoutputstream.writeBytes((new StringBuilder(
					"Content-Type:\"image/png\"")).append(lineEnd).toString());
			dataoutputstream.writeBytes(lineEnd);
			fileinputstream = new FileInputStream(file);
			abyte0 = new byte[1024];
			int i = fileinputstream.read(abyte0);
			if (i != -1)
			{
				dataoutputstream.write(abyte0, 0, i);
			} else
			{
				dataoutputstream.writeBytes(lineEnd);
				dataoutputstream.writeBytes((new StringBuilder(
						"--****************fD4fH3gL0hK7aI6--")).append(lineEnd)
						.toString());
				fileinputstream.close();
				dataoutputstream.flush();
				dataoutputstream.close();
				if (conn.getResponseCode() != 200)
				{
					return s3;
				} else
				{
					InputStream inputstream = conn.getInputStream();
					StringBuilder stringbuilder = new StringBuilder();
					int j = inputstream.read();
					if (j == -1)
					{
						s3 = stringbuilder.toString();
						Log.i("postResult-----------", (new StringBuilder(
								String.valueOf(s3))).toString());
						JSONObject jsonobject = new JSONObject(s3);
						Log.i("resultJson-----------", (new StringBuilder(
								String.valueOf(jsonobject.toString())))
								.toString());
						if (jsonobject != null
								&& "send_faild".equals(jsonobject
										.getString("msg")))
							s3 = "-1";
					}
					stringbuilder.append((char) j);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return s3;
	}

	public void _setCookie(String paramString)
	{
		if (paramString != null)
		{
			if (this.connCookies == null)
				this.connCookies = new HashMap();
			String[] arrayOfString = paramString.split("=");
			if (arrayOfString.length > 1)
				this.connCookies.put(arrayOfString[0],
						URLEncoder.encode(arrayOfString[1]));
		}
	}

	public void _setCookie(HashMap<String, String> paramHashMap)
	{
		if (paramHashMap != null)
		{
			if (this.connCookies == null)
				this.connCookies = new HashMap();
			this.connCookies.putAll(paramHashMap);
		}
	}

	public void _setCookiePre(String paramString)
	{
		this.cookiePre = paramString;
	}

	public static abstract interface requestCallBack
	{
		public abstract void download(Object paramObject);
	}
}