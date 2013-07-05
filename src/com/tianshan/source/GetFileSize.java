package com.tianshan.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;

public class GetFileSize
{
	/**
	 * 格式化文件大小
	 * 
	 * @param l
	 * @return
	 */
	public String FormetFileSize(long l)
	{
		DecimalFormat decimalformat = new DecimalFormat("#.00");
		String s;
		if (l < 1024L)
			s = (new StringBuilder(String.valueOf(decimalformat.format(l))))
					.append("B").toString();
		else if (l < 0x100000L)
			s = (new StringBuilder(String.valueOf(decimalformat
					.format((double) l / 1024D)))).append("K").toString();
		else if (l < 0x40000000L)
			s = (new StringBuilder(String.valueOf(decimalformat
					.format((double) l / 1048576D)))).append("M").toString();
		else
			s = (new StringBuilder(String.valueOf(decimalformat
					.format((double) l / 1073741824D)))).append("G").toString();
		return s;
	}

	public long getFileSize(File file) throws Exception
	{
		long l = 0L;
		File afile[] = file.listFiles();
		int i = 0;
		do
		{
			if (i >= afile.length)
				return l;
			if (afile[i].isDirectory())
				l += getFileSize(afile[i]);
			else
				l += afile[i].length();
			i++;
		} while (true);
	}

	public long getFileSizes(File file) throws Exception
	{
		long l = 0L;
		if (file.exists())
		{
			l = (new FileInputStream(file)).available();
		} else
		{
			file.createNewFile();
			System.out.println("文件不存在");
		}
		return l;
	}

	public long getlist(File file)
	{
		File afile[] = file.listFiles();
		long l = afile.length;
		int i = 0;
		do
		{
			if (i >= afile.length)
				return l;
			if (afile[i].isDirectory())
				l = (l + getlist(afile[i])) - 1L;
			i++;
		} while (true);
	}
}