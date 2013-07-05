package com.tianshan.source;

import android.content.Context;
import android.os.Environment;
import com.tianshan.model.DownLoadModel;
import com.tianshan.setting.Setting;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class OfflineManager
{
	private static Context _context;

	public static JSONObject getSubDownloadObj(String s) throws Exception
	{
		HttpURLConnection httpurlconnection = (HttpURLConnection) (new URL(s))
				.openConnection();
		httpurlconnection.setConnectTimeout(6000);
		httpurlconnection.setRequestMethod("GET");
		JSONObject jsonobject;
		if (httpurlconnection.getResponseCode() == 200)
		{
			InputStream inputstream = httpurlconnection.getInputStream();
			jsonobject = new JSONObject(new String(readStream(inputstream)));
		} else
		{
			jsonobject = null;
		}
		return jsonobject;
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

	public static void setContext(Context paramContext)
	{
		_context = paramContext;
	}

	public static void writeFile(DownLoadModel downloadmodel)
			throws IOException
	{
		try
		{
			String s;
			File file;
			boolean flag;
			String s1;
			File file1;
			if (Setting.IsCanUseSdCard())
				s = (new StringBuilder(String.valueOf(Environment
						.getExternalStorageDirectory().getPath()))).append("/")
						.append("tianshan").append("/html").toString();
			else
				s = (new StringBuilder(String.valueOf(Core
						._getMobileMemPath(_context)))).append("/")
						.append("tianshan").append("/html").toString();
			file = new File(s);
			if (!file.exists())
				file.mkdirs();
			flag = downloadmodel.getType().equals("html");
			s1 = null;
			file1 = null;
			if (flag)
			{
				s1 = new String(downloadmodel.getContent());
				file1 = new File((new StringBuilder(String.valueOf(s)))
						.append("/").append(downloadmodel.getName()).toString());
				OutputStreamWriter outputstreamwriter;
				try
				{
					file1.createNewFile();
				} catch (IOException ioexception)
				{
					ioexception.printStackTrace();
				}
				outputstreamwriter = new OutputStreamWriter(
						new FileOutputStream(file1), "utf-8");
				outputstreamwriter.write(s1);
				outputstreamwriter.flush();
				outputstreamwriter.close();
			}
		} catch (UnsupportedEncodingException unsupportedencodingexception)
		{
			unsupportedencodingexception.printStackTrace();
		} catch (FileNotFoundException filenotfoundexception)
		{
			filenotfoundexception.printStackTrace();
		}
	}

	public static void writeImg(DownLoadModel downloadmodel)
	{
		String s;
		File file;
		String as[];
		File file1;
		File file2;
		FileOutputStream fileoutputstream;
		try
		{
			if (Setting.IsCanUseSdCard())
				s = (new StringBuilder(String.valueOf(Environment
						.getExternalStorageDirectory().getPath()))).append("/")
						.append("tianshan").append("/html").toString();
			else
				s = (new StringBuilder(String.valueOf(Core
						._getMobileMemPath(_context)))).append("/")
						.append("tianshan").append("/html/attachment")
						.toString();
			file = new File(s);
			if (!file.exists())
				file.mkdirs();
			as = downloadmodel.getUrl().split("/");
			file1 = new File((new StringBuilder(String.valueOf(s))).append("/")
					.append(as[4]).append("/").append(as[5]).toString());
			if (!file1.exists())
				file1.mkdirs();
			file2 = new File((new StringBuilder(String.valueOf(s))).append("/")
					.append(as[4]).append("/").append(as[5]).append("/")
					.append(as[6]).toString());
			try
			{
				file2.createNewFile();
			} catch (IOException ioexception)
			{
				ioexception.printStackTrace();
			}
			fileoutputstream = new FileOutputStream(file2);
			fileoutputstream.write(downloadmodel.getContent(), 0,
					downloadmodel.getContent().length);
			fileoutputstream.flush();
			fileoutputstream.close();
		} catch (IOException ie)
		{
			ie.printStackTrace();
		}
	}
}