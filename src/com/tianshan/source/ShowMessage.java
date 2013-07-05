package com.tianshan.source;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tianshan.R;

public class ShowMessage
{
	private static ShowMessage instance = null;
	public int TOAST_ICON_ERROR = 3;
	public int TOAST_ICON_INFO = 2;
	public int TOAST_ICON_SUCCESS = 1;
	public int TOAST_TIMER = 2;
	private Activity activity;
	private int icon;
	private String messageStr;

	public ShowMessage(Activity paramActivity)
	{
		this.activity = paramActivity;
	}

	private void _show()
	{
		Toast toast = new Toast(activity);
		View view = activity.getLayoutInflater().inflate(R.layout.toast, null);
		TextView textview = (TextView) view.findViewById(R.id.toast_text);
		ImageView imageview = (ImageView) view.findViewById(R.id.toast_icon);
		if (icon == TOAST_ICON_ERROR)
			imageview.setImageResource(R.drawable.icon_toast_error);
		else if (icon == TOAST_ICON_INFO)
			imageview.setImageResource(R.drawable.icon_toast_info);
		else
			imageview.setImageResource(R.drawable.icon_toast_success);
		textview.setText(messageStr);
		toast.setView(view);
		if (TOAST_TIMER == 1)
			toast.setDuration(1);
		else
			toast.setDuration(0);
		toast.show();
	}

	public static ShowMessage getInstance(Activity paramActivity)
	{
		if (instance == null)
			instance = new ShowMessage(paramActivity);
		return instance;
	}

	public void _showToast(int paramInt1, int paramInt2)
	{
		this.messageStr = this.activity.getBaseContext().getResources()
				.getString(paramInt1);
		this.icon = paramInt2;
		_show();
	}

	public void _showToast(int i, int j, int k)
	{
		messageStr = activity.getBaseContext().getResources().getString(i);
		icon = j;
		int l;
		if (k == 0)
			l = TOAST_TIMER;
		else
			l = 2;
		TOAST_TIMER = l;
		_show();
	}

	public void _showToast(String paramString, int paramInt)
	{
		this.messageStr = paramString;
		this.icon = paramInt;
		_show();
	}

	public void _showToast(String s, int i, int j)
	{
		messageStr = s;
		icon = i;
		int k;
		if (j == 0)
			k = TOAST_TIMER;
		else
			k = 2;
		TOAST_TIMER = k;
		_show();
	}

	public void jsToast(String paramString, int paramInt)
	{
		this.messageStr = paramString;
		this.icon = paramInt;
		_show();
	}

	public void jsToast(String s, int i, int j)
	{
		messageStr = s;
		icon = i;
		int k;
		if (j == 0)
			k = TOAST_TIMER;
		else
			k = 2;
		TOAST_TIMER = k;
		_show();
	}
}