package com.tianshan.source.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tianshan.R;

public class Navbar implements View.OnClickListener
{
	private Button backBtn;
	private Button commitBtn;
	private OnNavigateListener listener;
	private TextView title;

	public Navbar(Context paramContext, ViewGroup paramViewGroup)
	{
		_init(paramContext, paramViewGroup);
	}

	public void _init(Context paramContext, ViewGroup paramViewGroup)
	{
		View localView = LayoutInflater.from(paramContext).inflate(
				R.layout.nav_title_bar, null);
		this.backBtn = ((Button) localView.findViewById(R.id.nav_back));
		this.commitBtn = ((Button) localView.findViewById(R.id.nav_commit));
		this.commitBtn.setVisibility(4);
		this.title = ((TextView) localView.findViewById(R.id.nav_title));
		this.title.setText("");
		paramViewGroup.addView(localView);
	}

	public String getTitle()
	{
		return (String) this.title.getText();
	}

	public void onClick(View paramView)
	{
		int i = paramView.getId();
		if (i == R.id.nav_back)
			this.listener.onBack();
		else
		{
			if (i == R.id.nav_commit)
				this.listener.onCommit();
		}
	}

	public void setBackBtnVisibility(boolean flag)
	{
		if (flag)
			backBtn.setVisibility(0);
		else
			backBtn.setVisibility(4);
	}

	public void setCommintBtnText(int paramInt)
	{
		this.commitBtn.setText(paramInt);
	}

	public void setCommitBtnText(int paramInt)
	{
		this.commitBtn.setText(paramInt);
	}

	public void setCommitBtnVisibility(boolean flag)
	{
		if (flag)
			commitBtn.setVisibility(0);
		else
			commitBtn.setVisibility(4);
	}

	public void setOnNavigate(OnNavigateListener paramOnNavigateListener)
	{
		if (paramOnNavigateListener != null)
		{
			this.backBtn.setOnClickListener(this);
			this.commitBtn.setOnClickListener(this);
			this.listener = paramOnNavigateListener;
		}
	}

	public void setTitle(int paramInt)
	{
		this.title.setText(paramInt);
	}

	public void setTitle(CharSequence paramCharSequence)
	{
		this.title.setText(paramCharSequence);
	}

	public static abstract interface OnNavigateListener
	{
		public abstract void onBack();

		public abstract void onCommit();
	}
}

/*
 * Location: D:\yuanming\反编译\反编译工具\天上-论坛\tianshan-dex2jar.jar Qualified Name:
 * com.tianshan.source.view.Navbar JD-Core Version: 0.6.2
 */
// R.id.head 