package com.tianshan.source;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.hardware.Camera;

public class CameraHelper
{
	private static final String KEY_PICTURE_SIZE = "picture-size";
	private static final String KEY_PREVIEW_SIZE = "preview-size";
	private static final String SUPPORTED_VALUES_SUFFIX = "-values";
	private Camera.Parameters mParms;

	public CameraHelper(Camera.Parameters paramParameters)
	{
		this.mParms = paramParameters;
	}

	private ArrayList splitSize(String s)
	{
		ArrayList arraylist = null;
		if (s != null)
		{
			StringTokenizer stringtokenizer = new StringTokenizer(s, ",");
			arraylist = new ArrayList();
			while (stringtokenizer.hasMoreElements())
			{
				if (arraylist.size() == 0)
					arraylist = null;
				Size size = strToSize(stringtokenizer.nextToken());
				if (size != null)
					arraylist.add(size);
			}
		}
		return arraylist;
	}

	private Size strToSize(String s)
	{
		Size size = null;
		if (s != null)
		{
			int i = s.indexOf('x');
			if (i != -1)
			{
				String s1 = s.substring(0, i);
				String s2 = s.substring(i + 1);
				size = new Size(Integer.parseInt(s1), Integer.parseInt(s2));
			}

		}
		return size;
	}

	public List<Size> getSupportedPictureSizes()
	{
		return splitSize(this.mParms.get("picture-size-values"));
	}

	public List<Size> getSupportedPreviewSizes()
	{
		return splitSize(this.mParms.get("preview-size-values"));
	}

	public class Size
	{
		public int height;
		public int width;

		public Size(int i, int j)
		{
			this.width = i;
			this.height = j;
		}

		public boolean equals(Object obj)
		{
			boolean flag;
			boolean flag1;
			flag = obj instanceof Size;
			flag1 = false;
			if (flag)
			{
				Size size = (Size) obj;
				int i = width;
				int j = size.width;
				flag1 = false;
				if (i == j)
				{
					int k = height;
					int l = size.height;
					flag1 = false;
					if (k == l)
						flag1 = true;
				}
			}
			return flag1;
		}

		public int hashCode()
		{
			return 32713 * this.width + this.height;
		}
	}
}

/*
 * Location: D:\yuanming\反编译\反编译工具\天上-论坛\tianshan-dex2jar.jar Qualified Name:
 * com.tianshan.source.CameraHelper JD-Core Version: 0.6.2
 */
// R.id.head 