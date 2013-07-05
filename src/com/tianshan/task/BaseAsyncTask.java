package com.tianshan.task;

import android.content.Context;
import android.os.AsyncTask;
import com.tianshan.model.DownLoadModel;

public abstract class BaseAsyncTask extends AsyncTask
{

	private static final String TAG = "BaseAsyncTask";
	protected Context mActivity;
	protected TaskResultListener mListener;
	protected boolean mShowProgress;

	public static interface TaskResultListener
	{

		public abstract void onTaskResult(boolean flag,
				DownLoadModel downloadmodel);
	}

	public BaseAsyncTask(Context context, TaskResultListener taskresultlistener)
	{
		mActivity = context;
		mListener = taskresultlistener;
	}

	protected void onPostExecute(DownLoadModel downloadmodel)
	{
		if (downloadmodel == null || downloadmodel.getContent().length == 0)
		{
			mListener.onTaskResult(false, null);
		} else
		{
			String s = downloadmodel.getStatus();
			if (s != null && s.equals("error"))
				mListener.onTaskResult(false, downloadmodel);
			else
				mListener.onTaskResult(true, downloadmodel);
		}
	}

}