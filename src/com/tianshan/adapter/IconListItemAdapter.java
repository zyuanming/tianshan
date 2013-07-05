package com.tianshan.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class IconListItemAdapter extends BaseAdapter
{
	protected ArrayList<IconListItem> iconListItems;
	protected int[] imagesId;
	protected String[] itemText;
	protected int[] itemsId;

	public IconListItemAdapter(Context paramContext, int[] paramArrayOfInt1,
			int[] paramArrayOfInt2, String[] paramArrayOfString)
			throws Exception
	{
		if (paramArrayOfInt2.length != paramArrayOfString.length)
			throw new Exception("图片ID个数与文字个数不相等!");
		this.itemsId = paramArrayOfInt1;
		this.imagesId = paramArrayOfInt2;
		this.itemText = paramArrayOfString;
		this.iconListItems = new ArrayList();
		for (int i = 0; i < this.imagesId.length; i++)
		{
			TextView localTextView = new TextView(paramContext);
			IconListItem localIconListItem = new IconListItem(this.itemsId[i],
					this.itemText[i], this.imagesId[i], localTextView);
			this.iconListItems.add(localIconListItem);
		}
	}

	public int getCount()
	{
		return this.imagesId.length;
	}

	public Object getItem(int paramInt)
	{
		return this.iconListItems.get(paramInt);
	}

	public long getItemId(int paramInt)
	{
		return ((IconListItem) this.iconListItems.get(paramInt)).getId();
	}

	public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
	{
		return ((IconListItem) this.iconListItems.get(paramInt)).gettView();
	}

	private class IconListItem
	{
		private int id;
		private int imgId;
		private TextView tView;
		private String text;

		public IconListItem(int i, String s, int j, TextView textview)
		{
			id = i;
			text = s;
			imgId = j;
			tView = textview;
			textview.setLayoutParams(new android.widget.AbsListView.LayoutParams(
					-1, -2));
			textview.setGravity(16);
			textview.setMinHeight(100);
			textview.setPadding(30, 0, 0, 0);
			textview.setText(text);
			textview.setTextSize(22F);
			textview.setTextColor(0xff000000);
			textview.setCompoundDrawablesWithIntrinsicBounds(imgId, 0, 0, 0);
			textview.setCompoundDrawablePadding(15);
		}

		public int getId()
		{
			return this.id;
		}

		public int getImgId()
		{
			return this.imgId;
		}

		public String getText()
		{
			return this.text;
		}

		public TextView gettView()
		{
			return this.tView;
		}

		public void setId(int paramInt)
		{
			this.id = paramInt;
		}

		public void setImgId(int paramInt)
		{
			this.imgId = paramInt;
		}

		public void setText(String paramString)
		{
			this.text = paramString;
		}

		public void settView(TextView paramTextView)
		{
			this.tView = paramTextView;
		}
	}
}