package com.tianshan.source.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class AlphaPhotoView extends View
{
	private static final String ALPHA_COLOR = "#777777";
	public static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	public static final String PATH = Environment.getExternalStorageDirectory()
			.toString() + "/AndroidMedia/";
	int bh;
	private Bitmap bitmap;
	int bw;
	Context context;
	private float height;
	boolean isdraw = false;
	private int mCurrentX;
	private int mCurrentY;
	private Path mPath = new Path();
	private Matrix matrix = new Matrix();
	Paint paint = new Paint();
	private Uri uri;
	int w;
	private float width;

	public AlphaPhotoView(Context paramContext)
	{
		super(paramContext);
	}

	public AlphaPhotoView(Context paramContext, AttributeSet paramAttributeSet)
	{
		super(paramContext, paramAttributeSet);
		this.context = paramContext;
		this.mPath.addRect(new RectF(0.0F, 0.0F, 200.0F, 200.0F),
				Path.Direction.CW);
		this.matrix.setScale(1.0F, 1.0F);
	}

	public AlphaPhotoView(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt)
	{
		super(paramContext, paramAttributeSet, paramInt);
	}

	private void fitScreen(int i, int j)
	{
		try
		{
			android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeStream(context.getContentResolver()
					.openInputStream(uri), null, options);
			int k = (int) Math.ceil((float) options.outHeight / (float) j);
			int l = (int) Math.ceil((float) options.outWidth / (float) i);
			System.out.println((new StringBuilder("hRatio:")).append(k)
					.append("  wRatio:").append(l).toString());
			if (k > 1 || l > 1)
				if (k > l)
					options.inSampleSize = k;
				else
					options.inSampleSize = l;
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeStream(context.getContentResolver()
					.openInputStream(uri), null, options);
			bw = bitmap.getWidth();
			bh = bitmap.getHeight();
			height = (j * 2 - bh) / 2;
			width = (i - bw) / 2;
		} catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

	private Uri insertImage(ContentResolver contentresolver, String s, long l,
			String s1, String s2, Bitmap bitmap1, byte abyte0[])
	{
		FileOutputStream fileoutputstream = null;
		String s3 = (new StringBuilder(String.valueOf(s1))).append(s2)
				.toString();
		File file1 = new File(s1, s2);
		Uri uri1 = null;
		try
		{
			boolean flag1 = file1.createNewFile();
			File file = new File(s1);
			boolean flag = file.exists();
			if (!flag)
				file.mkdirs();
			if (!flag1)
			{
				ContentValues contentvalues;
				contentvalues = new ContentValues(7);
				contentvalues.put("title", s);
				contentvalues.put("_display_name", s2);
				contentvalues.put("datetaken", Long.valueOf(l));
				contentvalues.put("mime_type", "image/jpeg");
				contentvalues.put("_data", s3);
				uri1 = contentresolver.insert(IMAGE_URI, contentvalues);
			} else
			{
				FileOutputStream fileoutputstream1 = new FileOutputStream(file1);
				if (bitmap1 == null)
				{
					ContentValues contentvalues;
					contentvalues = new ContentValues(7);
					contentvalues.put("title", s);
					contentvalues.put("_display_name", s2);
					contentvalues.put("datetaken", Long.valueOf(l));
					contentvalues.put("mime_type", "image/jpeg");
					contentvalues.put("_data", s3);
					uri1 = contentresolver.insert(IMAGE_URI, contentvalues);
				} else
				{
					bitmap1.compress(
							android.graphics.Bitmap.CompressFormat.JPEG, 75,
							fileoutputstream1);
					fileoutputstream = fileoutputstream1;
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			if (fileoutputstream != null)
				try
				{
					fileoutputstream.close();
				} catch (Exception e1)
				{
					e.printStackTrace();
				}
		}
		return uri1;
	}

	public Uri getBitmap()
	{
		long l = System.currentTimeMillis();
		String str = DateFormat.format("yyyy-MM-dd kk.mm.ss", l).toString()
				+ ".jpg";
		Bitmap localBitmap = Bitmap.createBitmap(this.bitmap, this.mCurrentX,
				this.mCurrentY, 180, 180);
		return insertImage(this.context.getContentResolver(), str, l, PATH,
				str, localBitmap, null);
	}

	protected void onDraw(Canvas paramCanvas)
	{
		super.onDraw(paramCanvas);
		this.w = getWidth();
		int i = getHeight();
		if (!this.isdraw)
		{
			fitScreen(this.w, i / 2);
			this.isdraw = true;
		}
		paramCanvas.drawBitmap(this.bitmap, this.width, this.height, null);
		paramCanvas.drawARGB(100, 0, 0, 0);
		paramCanvas.translate(this.mCurrentX, this.mCurrentY);
		paramCanvas.clipPath(this.mPath);
		paramCanvas.translate(this.width - this.mCurrentX, this.height
				- this.mCurrentY);
		paramCanvas.drawBitmap(this.bitmap, this.matrix, null);
	}

	public boolean onTouchEvent(MotionEvent paramMotionEvent)
	{
		switch (paramMotionEvent.getAction())
		{
		case 1:
		default:
			break;
		case 0:
			this.mCurrentX = ((int) paramMotionEvent.getX());
			this.mCurrentY = ((int) paramMotionEvent.getY());
			break;
		case 2:
			this.mCurrentX = ((int) paramMotionEvent.getX());
			this.mCurrentY = ((int) paramMotionEvent.getY());
			if ((this.mCurrentX >= this.width)
					&& (200 + this.mCurrentX < this.w)
					&& (this.mCurrentY >= this.height)
					&& (200 + this.mCurrentY < this.bh + this.height))
				invalidate();
			break;
		}
		return true;
	}

	public void setBitmap(Uri paramUri)
	{
		this.uri = paramUri;
	}
}