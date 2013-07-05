package com.tianshan.source;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import java.io.ByteArrayOutputStream;
import java.io.File;

public class BitmapUtil
{
	public static byte[] BitmapToBytes(Bitmap paramBitmap)
	{
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		paramBitmap.compress(Bitmap.CompressFormat.PNG, 100,
				localByteArrayOutputStream);
		return localByteArrayOutputStream.toByteArray();
	}

	public static BitmapDrawable BitmapToDrawable(Bitmap paramBitmap)
	{
		return new BitmapDrawable(paramBitmap);
	}

	public static Bitmap BytesToBitmap(byte abyte0[])
	{
		Bitmap bitmap;
		if (abyte0.length != 0)
			bitmap = BitmapFactory.decodeByteArray(abyte0, 0, abyte0.length);
		else
			bitmap = null;
		return bitmap;
	}

	public static Bitmap DrawableToBitmap(Drawable drawable)
	{
		int i = drawable.getIntrinsicWidth();
		int j = drawable.getIntrinsicHeight();
		android.graphics.Bitmap.Config config;
		Bitmap bitmap;
		Canvas canvas;
		if (drawable.getOpacity() != -1)
			config = android.graphics.Bitmap.Config.ARGB_8888;
		else
			config = android.graphics.Bitmap.Config.RGB_565;
		bitmap = Bitmap.createBitmap(i, j, config);
		canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	public static Bitmap Scale9Grid(Bitmap paramBitmap, Rect paramRect,
			int paramInt1, int paramInt2)
	{
		Bitmap localBitmap = Bitmap.createBitmap(paramInt1, paramInt2,
				Bitmap.Config.ARGB_8888);
		Canvas localCanvas = new Canvas(localBitmap);
		int i = paramBitmap.getWidth();
		int j = paramBitmap.getHeight();
		int k = paramRect.left;
		int m = i - paramRect.right;
		int n = paramRect.top;
		int i1 = j - paramRect.bottom;
		int i2 = paramInt1 - m;
		int i3 = paramInt2 - i1;
		localCanvas.drawBitmap(paramBitmap, new Rect(0, 0, k, n), new Rect(0,
				0, k, n), null);
		localCanvas.drawBitmap(paramBitmap, new Rect(paramRect.right, 0, i, n),
				new Rect(i2, 0, paramInt1, n), null);
		localCanvas.drawBitmap(paramBitmap,
				new Rect(0, paramRect.bottom, k, j), new Rect(0, i3, k,
						paramInt2), null);
		localCanvas.drawBitmap(paramBitmap, new Rect(paramRect.right,
				paramRect.bottom, i, j),
				new Rect(i2, i3, paramInt1, paramInt2), null);
		localCanvas.drawBitmap(paramBitmap, new Rect(k, 0, paramRect.right, n),
				new Rect(k, 0, i2, n), null);
		localCanvas.drawBitmap(paramBitmap, new Rect(k, paramRect.bottom,
				paramRect.right, j), new Rect(k, i3, i2, paramInt2), null);
		localCanvas.drawBitmap(paramBitmap,
				new Rect(0, n, k, paramRect.bottom), new Rect(0, n, k, i3),
				null);
		localCanvas.drawBitmap(paramBitmap, new Rect(paramRect.right, n, i,
				paramRect.bottom), new Rect(i2, n, paramInt1, i3), null);
		localCanvas.drawBitmap(paramBitmap, paramRect, new Rect(paramRect.left,
				paramRect.top, i2, i3), null);
		return localBitmap;
	}

	public static Drawable Scale9Grid(Drawable paramDrawable, Rect paramRect,
			int paramInt1, int paramInt2)
	{
		return BitmapToDrawable(Scale9Grid(DrawableToBitmap(paramDrawable),
				paramRect, paramInt1, paramInt2));
	}

	public static int dip2px(Context paramContext, float paramFloat)
	{
		return (int) (0.5F + paramFloat
				* paramContext.getResources().getDisplayMetrics().density);
	}

	public static Bitmap freeBit(String paramString)
	{
		BitmapFactory.Options localOptions = new BitmapFactory.Options();
		localOptions.inJustDecodeBounds = false;
		localOptions.inPreferredConfig = Bitmap.Config.RGB_565;
		localOptions.inSampleSize = 1;
		return BitmapFactory.decodeFile(paramString, localOptions);
	}

	public static Bitmap getBitmapAutoFitByPath(String paramString,
			int paramInt1, int paramInt2)
	{
		File localFile = new File(paramString);
		BitmapFactory.Options localOptions = new BitmapFactory.Options();
		localOptions.inJustDecodeBounds = true;
		localOptions.inPreferredConfig = Bitmap.Config.RGB_565;
		BitmapFactory.decodeFile(localFile.getPath(), localOptions);
		float f1 = localOptions.outWidth;
		float f2 = localOptions.outHeight;
		float f3 = f1 / paramInt1;
		float f4 = f2 / paramInt2;
		float f5 = 1.0F;
		if ((f3 > 1.0F) || (f4 > 1.0F))
			f5 = Math.max(f3, f4);
		localOptions.inSampleSize = ((int) Math.ceil(f5));
		localOptions.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(localFile.getPath(), localOptions);
	}

	public static int px2dip(Context paramContext, float paramFloat)
	{
		return (int) (0.5F + paramFloat
				/ paramContext.getResources().getDisplayMetrics().density);
	}
}