package com.tianshan.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import com.tianshan.model.PhotoInfo;
import com.tianshan.source.BitmapUtil;
import com.tianshan.source.Core;
import java.util.ArrayList;

public class PhotoListAdapter extends BaseAdapter
{
	private Bitmap initTempBitmap;
	private Context mContext;
	private ArrayList<PhotoInfo> mData;
	private LayoutInflater mInflater;
	private int mWidth;

	public PhotoListAdapter(Context paramContext,
			ArrayList<PhotoInfo> paramArrayList, int paramInt)
	{
		this.mContext = paramContext;
		this.mData = paramArrayList;
		this.mWidth = paramInt;
		this.mInflater = LayoutInflater.from(paramContext);
	}

	public int getCount()
	{
		return this.mData.size();
	}

	public PhotoInfo getItem(int paramInt)
	{
		return (PhotoInfo) this.mData.get(paramInt);
	}

	public long getItemId(int paramInt)
	{
		return 0L;
	}

	public View getView(int i, View view, ViewGroup viewgroup)
	{
		View view1;
		ImageView imageview;
		PhotoInfo photoinfo;
		String s;
		if (view == null)
			view1 = mInflater.inflate(0x7f03000c, null);
		else
			view1 = view;
		imageview = (ImageView) view1.findViewById(0x7f090022);
		imageview
				.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(
						-4 + mWidth / 4, -4 + mWidth / 4));
		imageview.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
		photoinfo = (PhotoInfo) mData.get(i);
		s = (new StringBuilder(
				String.valueOf(Core._getPhotoCachePath(mContext))))
				.append(photoinfo.getId()).append(".jpg").toString();
		if (photoinfo.getBit() == null)
		{
			Bitmap bitmap = BitmapUtil.getBitmapAutoFitByPath(s, -2 + mWidth
					/ 4, -2 + mWidth / 4);
			photoinfo.setBit(bitmap);
			imageview.setImageBitmap(bitmap);
		} else
		{
			imageview.setImageBitmap(photoinfo.getBit());
		}
		return view1;
	}
}