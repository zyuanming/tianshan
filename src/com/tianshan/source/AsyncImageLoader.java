package com.tianshan.source;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

public class AsyncImageLoader
{
	private HashMap<String, SoftReference<Drawable>> imageCache = new HashMap();
	private Context mContext;

	public AsyncImageLoader(Context paramContext)
	{
		this.mContext = paramContext;
	}

	public Drawable loadDrawable(String paramString,
			AsyncImageLoaderCallback paramAsyncImageLoaderCallback)
	{
		Drawable localDrawable = null;
		if (this.imageCache.containsKey(paramString))
		{
			localDrawable = (Drawable) ((SoftReference) this.imageCache
					.get(paramString)).get();
			if (localDrawable == null)
			{
				new AsyncImageLoaderTask(paramAsyncImageLoaderCallback)
						.execute(new String[] { paramString });
			}
		}
		return localDrawable;
	}

	public static abstract interface AsyncImageLoaderCallback
	{
		public abstract void imageLoaded(boolean paramBoolean,
				Drawable paramDrawable, String paramString);
	}

	private class AsyncImageLoaderTask extends
			AsyncTask<String, Void, Drawable>
	{
		protected AsyncImageLoader.AsyncImageLoaderCallback imageCallback;
		private String imageUrl;

		public AsyncImageLoaderTask(
				AsyncImageLoader.AsyncImageLoaderCallback arg2)
		{
			this.imageCallback = arg2;
		}

		private Drawable loadImageFromUrl(String s)
		{
			InputStream inputstream = null;
			Drawable drawable;
			try
			{
				inputstream = (new URL(s)).openStream();
			} catch (MalformedURLException e)
			{
				if (inputstream != null)
					try
					{
						inputstream.close();
					} catch (IOException ioexception2)
					{
						ioexception2.printStackTrace();
					}
				e.printStackTrace();

			} catch (IOException e)
			{
				if (inputstream != null)
				{
					try
					{
						inputstream.close();
					} catch (IOException ioexception2)
					{
						ioexception2.printStackTrace();
					}
				}
				e.printStackTrace();
			}
			drawable = Drawable.createFromStream(inputstream, "src");
			if (inputstream != null)
				try
				{
					inputstream.close();
				} catch (IOException ioexception2)
				{
					ioexception2.printStackTrace();
				}
			return drawable;
		}

		protected Drawable doInBackground(String[] paramArrayOfString)
		{
			this.imageUrl = paramArrayOfString[0];
			Drawable localDrawable = loadImageFromUrl(this.imageUrl);
			AsyncImageLoader.this.imageCache.put(this.imageUrl,
					new SoftReference(localDrawable));
			return localDrawable;
		}

		protected void onPostExecute(Drawable drawable)
		{
			if (drawable != null && imageUrl != null && !imageUrl.equals(""))
				imageCallback.imageLoaded(true, drawable, imageUrl);
			else
				imageCallback.imageLoaded(false, null, imageUrl);
		}
	}
}