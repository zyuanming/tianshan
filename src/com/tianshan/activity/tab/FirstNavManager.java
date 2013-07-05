package com.tianshan.activity.tab;

import java.util.ArrayList;

import android.view.View;

import com.tianshan.R;

/**
 * 一级导航管理类,就是下面显示的菜单栏
 * 
 * @author lkh
 * 
 */
public class FirstNavManager
{
	protected ArrayList<View> arrBtns;
	protected ArrayList<Integer> arrType;
	protected OnBtnClickListener btnCallBack = null;
	protected ArrayList<Integer> btnIds;
	protected TabBar c;
	private int current_btnId = R.id.btn_tabbar_news;
	private int current_type;
	protected View.OnClickListener onBtnClickListener = new View.OnClickListener()
	{
		public void onClick(View view)
		{
			ClearSelectBtn();
			view.setBackgroundDrawable(c.getResources().getDrawable(
					R.drawable.nav_bottom_current_bg));
			if (btnCallBack != null)
			{
				current_btnId = view.getId();
				current_type = ((Integer) arrType.get(btnIds.indexOf(Integer
						.valueOf(current_btnId)))).intValue();
				btnCallBack.onBtnClick(current_type, GetCurrentHeaderText());
			}
		}
	};
	protected String[] titles;

	public FirstNavManager(TabBar paramTabBar)
	{
		this.c = paramTabBar;
		initHeaderText();
		initBtnIds();
		initBtns();
		initTypeIds();
	}

	private void initBtnIds()
	{
		this.btnIds = new ArrayList();
		this.btnIds.add(Integer.valueOf(R.id.btn_tabbar_news)); // 新闻
		this.btnIds.add(Integer.valueOf(R.id.btn_tabbar_video)); // 视频
		this.btnIds.add(Integer.valueOf(R.id.btn_tabbar_pic)); // 图片
		this.btnIds.add(Integer.valueOf(R.id.btn_tabbar_bbs)); // 投稿
		this.btnIds.add(Integer.valueOf(R.id.btn_tabbar_board)); // 论坛
	}

	private void initBtns()
	{
		this.arrBtns = new ArrayList();
		for (int i = 0; i < this.btnIds.size(); i++)
		{
			View localView = this.c.findViewById(((Integer) this.btnIds.get(i))
					.intValue());
			localView.setClickable(true);
			localView.setBackgroundDrawable(null);
			localView.setOnClickListener(this.onBtnClickListener);
			this.arrBtns.add(localView);
		}
	}

	private void initHeaderText()
	{
		String[] arrayOfString = new String[5];
		arrayOfString[0] = this.c.getResources().getString(
				R.string.STR_CATEGORY1); // 新闻
		arrayOfString[1] = this.c.getResources().getString(
				R.string.STR_CATEGORY2); // 视频
		arrayOfString[2] = this.c.getResources().getString(
				R.string.STR_CATEGORY3); // 图片
		arrayOfString[3] = this.c.getResources().getString(
				R.string.STR_CATEGORY4); // 论坛
		arrayOfString[4] = this.c.getResources().getString(
				R.string.STR_CATEGORY5); // 投稿
		this.titles = arrayOfString;
	}

	private void initTypeIds()
	{
		this.arrType = new ArrayList();
		this.arrType.add(Integer.valueOf(2));
		this.arrType.add(Integer.valueOf(3));
		this.arrType.add(Integer.valueOf(4));
		this.arrType.add(Integer.valueOf(-1));
		this.arrType.add(Integer.valueOf(5));
	}

	public void ClearSelectBtn()
	{
		for (int i = 0; i < this.arrBtns.size(); i++)
		{
			((View) this.arrBtns.get(i)).setBackgroundDrawable(null);
		}
	}

	public void ClickBtnByIndex(int paramInt)
	{
		((View) this.arrBtns.get(paramInt)).performClick();
	}

	public void ClickBtnByTypeId(int paramInt)
	{
		((View) this.arrBtns
				.get(this.arrType.indexOf(Integer.valueOf(paramInt))))
				.performClick();
	}

	public int GetCurrentBtnId()
	{
		return this.current_btnId;
	}

	public int GetCurrentBtnIndex()
	{
		return this.btnIds.indexOf(Integer.valueOf(this.current_btnId));
	}

	public String GetCurrentHeaderText()
	{
		return this.titles[this.btnIds.indexOf(Integer
				.valueOf(this.current_btnId))];
	}

	public int GetCurrentType()
	{
		return this.current_type;
	}

	public void SetOnBtnClickListener(OnBtnClickListener paramOnBtnClickListener)
	{
		this.btnCallBack = paramOnBtnClickListener;
	}

	public static abstract interface OnBtnClickListener
	{
		public abstract void onBtnClick(int paramInt, String paramString);
	}
}