package com.tianshan.task;

import android.content.Context;
import android.util.Log;
import com.tianshan.model.DownLoadModel;
import com.tianshan.source.HttpRequest;

public class DownloadTask extends BaseAsyncTask
{
	private static final String TAG = "DownloadHtmlTask";
	private Context activity;

	public DownloadTask(Context paramContext,
			BaseAsyncTask.TaskResultListener paramTaskResultListener)
	{
		super(paramContext, paramTaskResultListener);
		this.activity = paramContext;
	}

	@Override
	protected Object doInBackground(Object... as)
	{
		DownLoadModel dm = null;
		try
		{
			String[] s = (String[]) as;
			dm = HttpRequest.downloadRes(s[0], s[1], s[2]);
		} catch (Exception localException)
		{
			Log.e("DownloadHtmlTask", "", localException);
			dm = null;
		}
		return dm;
	}
}
