package com.tianshan.adapter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianshan.R;
import com.tianshan.model.App;
import com.tianshan.source.AsyncImageLoader;
import com.tianshan.source.Tools;

public class DownloadAppListAdapter extends BaseAdapter
{
	private static final String TAG = "DownloadAppListAdapter";
	private App app;
	private Context mContext;
	private ArrayList<App> mData;
	private AsyncImageLoader mImageLoader;
	LayoutInflater mInflater;

	public DownloadAppListAdapter(Context paramContext)
	{
		this.mContext = paramContext;
		this.mInflater = LayoutInflater.from(paramContext);
		this.mImageLoader = new AsyncImageLoader(this.mContext);
		this.mData = new ArrayList();
	}

	public int getCount()
	{
		return this.mData.size();
	}

	public Object getItem(int paramInt)
	{
		return this.mData.get(paramInt);
	}

	public long getItemId(int paramInt)
	{
		return paramInt;
	}

	public View getView(int i, View view, ViewGroup viewgroup)
	{
		final View itemView;
		ImageView imageview;
		String s;
		String s1;
		TextView textview;
		TextView textview1;
		if (view == null)
			itemView = mInflater.inflate(0x7f030004, null);
		else
			itemView = view;
		app = (App) mData.get(i);
		imageview = (ImageView) itemView.findViewById(0x7f090011);
		s = (new StringBuilder("http://client.xjts.cn/admin/")).append(
				app.getIconUrl()).toString();
		s1 = Tools.getLocalPathFromUrl(s);
		if (s1 == null || "".equals(s1) || !(new File(s1)).exists())
		{
			try
			{
				imageview.setImageResource(0x7f020001);
				URL url = new URL(s);
				if (url != null)
				{
					String s2 = url.toExternalForm();
					imageview.setTag(s2);
					mImageLoader
							.loadDrawable(
									s2,
									new com.tianshan.source.AsyncImageLoader.AsyncImageLoaderCallback()
									{

										public void imageLoaded(boolean flag,
												Drawable drawable, String s3)
										{
											ImageView imageview1 = (ImageView) itemView
													.findViewWithTag(s3);
											if (imageview1 != null)
											{
												imageview1
														.setImageDrawable(drawable);
												android.graphics.Bitmap bitmap = ((BitmapDrawable) drawable)
														.getBitmap();
												Tools.saveImage(mContext, s3,
														bitmap);
											}
										}
									});
				}
			} catch (MalformedURLException malformedurlexception)
			{
				malformedurlexception.printStackTrace();
			}
		} else
		{
			imageview.setImageBitmap(BitmapFactory.decodeFile(s1));
		}
		textview = (TextView) itemView.findViewById(0x7f090013);
		if (app.getName() != null)
			textview.setText(app.getName());
		textview1 = (TextView) itemView.findViewById(0x7f090014);
		if (app.getDesc() != null)
			textview1.setText(app.getDesc());
		return itemView;
	}

	public void setData(ArrayList<App> paramArrayList)
	{
		this.mData = paramArrayList;
		notifyDataSetChanged();
	}
}