package com.tianshan.source.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tianshan.R;

public class PhotoNavbar implements View.OnClickListener
{
	private Button backBtn;
	private TextView counter;
	private OnPhotoNavigateListener listener;

	public PhotoNavbar(Context paramContext, ViewGroup paramViewGroup)
	{
		_init(paramContext, paramViewGroup);
	}

	public void _init(Context paramContext, ViewGroup paramViewGroup)
	{
		View localView = LayoutInflater.from(paramContext).inflate(
				R.layout.photo_nav_title_bar, null);
		this.backBtn = ((Button) localView.findViewById(R.id.photo_nav_back));
		this.counter = ((TextView) localView
				.findViewById(R.id.photo_nav_counter));
		paramViewGroup.addView(localView);
	}

	public void onClick(View paramView)
	{
		if (paramView.getId() == R.id.photo_nav_back)
			this.listener.onBack();
	}

	public void setBackBtnVisibility(boolean flag)
	{
		if (flag)
			backBtn.setVisibility(0);
		else
			backBtn.setVisibility(4);
	}

	public void setCommitBtnVisibility(boolean paramBoolean)
	{
		if (paramBoolean)
			this.counter.setVisibility(4);
	}

	public void setCounter(int paramInt1, int paramInt2)
	{
		this.counter.setText(paramInt1 + "/" + paramInt2);
	}

	public void setCounterVisibility(boolean flag)
	{
		if (flag)
			counter.setVisibility(0);
		else
			counter.setVisibility(4);
	}

	public void setOnPhotoNavigate(
			OnPhotoNavigateListener paramOnPhotoNavigateListener)
	{
		if (paramOnPhotoNavigateListener != null)
		{
			this.backBtn.setOnClickListener(this);
			this.listener = paramOnPhotoNavigateListener;
		}
	}

	public static abstract interface OnPhotoNavigateListener
	{
		public abstract void onBack();
	}
}