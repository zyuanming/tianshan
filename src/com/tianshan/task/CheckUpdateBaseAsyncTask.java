package com.tianshan.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import java.util.HashMap;

/**
 * 检查更新的基本Task
 * 
 * @author lkh
 * 
 */
public abstract class CheckUpdateBaseAsyncTask extends
		AsyncTask<String, Void, HashMap<String, Object>>
{
	private static final String TAG = "CheckUpdateBaseAsyncTask";
	protected Context mActivity;
	protected CheckTaskResultListener mListener;
	protected ProgressDialog mProgress;
	protected boolean mShowProgress;

	public CheckUpdateBaseAsyncTask(Context paramContext,
			CheckTaskResultListener paramCheckTaskResultListener)
	{
		this(paramContext, paramCheckTaskResultListener, true);
	}

	public CheckUpdateBaseAsyncTask(Context paramContext,
			CheckTaskResultListener paramCheckTaskResultListener,
			boolean paramBoolean)
	{
		this.mActivity = paramContext;
		this.mListener = paramCheckTaskResultListener;
		this.mShowProgress = paramBoolean;
	}

	/**
	 * Runs on the UI thread after doInBackground(Params...). The specified
	 * result is the value returned by doInBackground(Params...). This method
	 * won't be invoked if the task was cancelled.
	 * 
	 * 在doInBackground()方法执行后执行，如果任务task被取消了，这个方法不会被调用
	 */
	@Override
	protected void onPostExecute(HashMap hashmap)
	{
		try
		{
			if (mProgress != null)
				mProgress.cancel();
		} catch (IllegalArgumentException illegalargumentexception)
		{
			illegalargumentexception.printStackTrace();
		}
		if (hashmap == null || hashmap.isEmpty())
			mListener.onTaskResult(false, null);
		else
			mListener.onTaskResult(
					((Boolean) hashmap.get("reqsuccess")).booleanValue(),
					hashmap);
	}

	/**
	 * Runs on the UI thread before doInBackground(Params...).
	 * 在doInBackground()方法执行前执行
	 */
	@Override
	protected void onPreExecute()
	{}

	/**
	 * 任务执行完的回调接口
	 * 
	 * @author lkh
	 * 
	 */
	public static interface CheckTaskResultListener
	{
		public abstract void onTaskResult(boolean paramBoolean,
				HashMap<String, Object> paramHashMap);
	}
}