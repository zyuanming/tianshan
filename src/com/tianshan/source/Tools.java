package com.tianshan.source;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class Tools
{
	private static final char[] HEX_DIGITS = { 48, 49, 50, 51, 52, 53, 54, 55,
			56, 57, 97, 98, 99, 100, 101, 102 };

	public static boolean _deleteSDFile(String paramString)
	{
		boolean bool1 = _isFileExist(paramString);
		boolean bool2 = false;
		if (bool1)
			bool2 = new File(paramString).delete();
		return bool2;
	}

	/**
	 * 数字转指定日期格式
	 * 
	 * @param l
	 *            毫秒时间
	 * @param s
	 *            日期格式，默认就是"yyyy-MM-dd"
	 * @return
	 */
	public static String _getNumToDateTime(long l, String s)
	{
		Date date = new Date(l);
		SimpleDateFormat simpledateformat1;
		String s1 = null;
		if (s == null)
		{
			SimpleDateFormat simpledateformat = new SimpleDateFormat(
					"yyyy-MM-dd");
			simpledateformat1 = simpledateformat;
		} else
		{
			SimpleDateFormat simpledateformat2 = new SimpleDateFormat(s);
			simpledateformat1 = simpledateformat2;
		}
		try
		{
			s1 = simpledateformat1.format(date);
		} catch (Exception e)
		{
			s1 = "";
			e.printStackTrace();
		}
		return s1;
	}

	public static String _getNumToDateTime(String s)
	{
		if (s == null)
			s = String.valueOf(System.currentTimeMillis());
		if (s.length() == 10)
			s = (new StringBuilder(String.valueOf(s))).append("000").toString();
		return _getNumToDateTime(Long.valueOf(s).longValue(), null);
	}

	public static String _getSDCustomDir(String s)
	{
		File file = new File((new StringBuilder("/sdcard/tianshan/")).append(s)
				.toString());
		if (!file.exists())
			file.mkdirs();
		return file.getAbsolutePath();
	}

	public static long _getTimeStamp()
	{
		return System.currentTimeMillis();
	}

	public static long _getTimeStampUnix()
	{
		return System.currentTimeMillis() / 1000L;
	}

	public static boolean _isFileExist(String paramString)
	{
		File localFile = new File(paramString);
		boolean bool1 = localFile.exists();
		boolean bool2 = false;
		if (bool1)
		{
			boolean bool3 = localFile.isFile();
			bool2 = false;
			if (bool3)
				bool2 = true;
		}
		return bool2;
	}

	public static Boolean _isSdcardMounted()
	{
		Boolean boolean1;
		if (Environment.getExternalStorageState().equals("mounted"))
			boolean1 = Boolean.valueOf(true);
		else
			boolean1 = Boolean.valueOf(false);
		return boolean1;
	}

	public static String _md5(String paramString)
	{
		String str1 = null;
		try
		{
			MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
			localMessageDigest.update(paramString.getBytes());
			String str2 = _toHexString(localMessageDigest.digest());
			str1 = str2;
		} catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
		{
			localNoSuchAlgorithmException.printStackTrace();
			str1 = "";
		}
		return str1;
	}

	public static String _time(int i)
	{
		DEBUG.o(Integer.valueOf(i));
		int j;
		int k;
		int l;
		int i1;
		if (i >= 1000)
		{
			j = i % 1000;
			int j1 = i / 1000;
			if (j1 >= 60)
			{
				i1 = j1 % 60;
				int k1 = j1 / 60;
				if (k1 >= 60)
				{
					k = k1 % 60;
					int l1 = k1 / 60;
					if (l1 >= 60)
					{
						l = l1 % 60;
						int _tmp = l1 / 60;
					} else
					{
						l = l1;
					}
				} else
				{
					k = k1;
					l = 0;
				}
			} else
			{
				i1 = j1;
				k = 0;
				l = 0;
			}
		} else
		{
			j = i;
			k = 0;
			l = 0;
			i1 = 0;
		}
		return (new StringBuilder(String.valueOf(l))).append("'").append(k)
				.append("'").append(i1).append("'").append(j).toString();
	}

	private static String _toHexString(byte[] paramArrayOfByte)
	{
		StringBuilder localStringBuilder = new StringBuilder(
				2 * paramArrayOfByte.length);
		for (int i = 0;; i++)
		{
			if (i >= paramArrayOfByte.length)
				return localStringBuilder.toString();
			localStringBuilder
					.append(HEX_DIGITS[((0xF0 & paramArrayOfByte[i]) >>> 4)]);
			localStringBuilder.append(HEX_DIGITS[(0xF & paramArrayOfByte[i])]);
		}
	}

	public static BitmapFactory.Options getBitmapOptions(String paramString,
			int paramInt)
	{
		BitmapFactory.Options localOptions = new BitmapFactory.Options();
		localOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(paramString, localOptions);
		int i = localOptions.outWidth;
		int j = localOptions.outHeight;
		localOptions.inSampleSize = 1;
		if (i > j)
		{
			if (i > paramInt)
				localOptions.inSampleSize = (1 + i / paramInt);
		} else
		{
			if (j > paramInt)
				localOptions.inSampleSize = (1 + j / paramInt);
		}
		DEBUG.o("path_temp = " + paramString);
		DEBUG.o("inSampleSize = " + localOptions.inSampleSize);
		DEBUG.o("IMAGEWidth = " + i + " IMAGEHeight = " + j);
		localOptions.inJustDecodeBounds = false;
		return localOptions;
	}

	public static String getLocalPathFromUrl(String s)
	{
		String s1 = null;
		if (s != null)
		{
			String s2 = s.substring(1 + s.lastIndexOf('/')).trim();
			if (s2 != null)
			{
				boolean flag = s2.equals("");
				if (!flag)
					s1 = (new StringBuilder("/sdcard/.tianshan/")).append(
							s.substring(1 + s.lastIndexOf('/')).trim())
							.toString();
			}
		}
		return s1;
	}

	public static String readableFileSize(long l)
	{
		String s;
		if (l <= 0L)
		{
			s = "0";
		} else
		{
			String as[] = { "B", "KB", "MB", "GB", "TB" };
			int i = (int) (Math.log10(l) / Math.log10(1024D));
			s = (new StringBuilder(
					String.valueOf((new DecimalFormat("#,##0.#"))
							.format((double) l / Math.pow(1024D, i)))))
					.append(" ").append(as[i]).toString();
		}
		return s;
	}

	public static byte[] resizeImg(String s, int i)
	{
		return null;
	}

	public static void saveImage(Context context, String s, Bitmap bitmap)
	{
		File file1;
		File file = new File("/sdcard/.tianshan/");
		if (!file.exists())
			file.mkdir();
		String s1 = (new StringBuilder("/sdcard/.tianshan/")).append(
				s.substring(1 + s.lastIndexOf('/'))).toString();
		Log.d("aa", (new StringBuilder("save ")).append(s).append("as file: ")
				.append(s1).toString());
		file1 = new File(s1);
		FileOutputStream fileoutputstream;
		if (!file1.exists())
			try
			{
				file1.createNewFile();
			} catch (IOException ioexception)
			{
				Log.e("aa",
						(new StringBuilder("can't create file: "))
								.append(file1).toString(), ioexception);
			}
		try
		{
			fileoutputstream = new FileOutputStream(file1);
			bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 90,
					fileoutputstream);
		} catch (FileNotFoundException fnfe)
		{
			Log.e("aa", (new StringBuilder("Can't save file to "))
					.append(file1).toString());
		}
	}

	public static int strToInt(String paramString)
	{
		return strToInt(paramString, 0);
	}

	public static int strToInt(String paramString, int paramInt)
	{
		try
		{
			int i = Integer.valueOf(paramString).intValue();
			paramInt = i;
		} catch (Exception localException)
		{
			localException.printStackTrace();
		}
		return paramInt;
	}

	public static void stream2File(byte abyte0[], String s) throws Exception
	{

		FileOutputStream fileoutputstream1 = null;
		try
		{

			File file = new File(s);
			boolean flag = file.getParentFile().exists();
			if (!flag)
				file.getParentFile().mkdirs();
			boolean flag1 = file.exists();
			if (flag1)
				file.delete();
			file.createNewFile();
			fileoutputstream1 = new FileOutputStream(file);
			fileoutputstream1.write(abyte0);
			fileoutputstream1.flush();
			fileoutputstream1.close();
		} catch (Exception e)
		{
			if (fileoutputstream1 != null)
			{
				try
				{
					fileoutputstream1.close();
				} catch (IOException ioexception)
				{
					DEBUG.WoPrintStackTrace(ioexception);
					throw new RuntimeException(ioexception.getMessage());
				}
			}
			DEBUG.WoPrintStackTrace(e);
			throw new RuntimeException(e.getMessage());
		}
	}

	public static String trim(String paramString1, String paramString2)
	{
		if ((!"".equals(paramString1)) && (!"".equals(paramString2))
				&& (paramString1.endsWith(paramString2)))
			paramString1 = paramString1.substring(0, paramString1.length()
					- paramString2.length());
		return paramString1;
	}
}