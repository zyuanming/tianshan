package com.tianshan.task;

import android.content.Context;
import com.tianshan.source.CheckNetworkManager;
import java.util.HashMap;

public class GetMoreAppTask extends CheckUpdateBaseAsyncTask
{
	private static final String TAG = "GetMoreAppTask";

	public GetMoreAppTask(
			Context paramContext,
			CheckUpdateBaseAsyncTask.CheckTaskResultListener paramCheckTaskResultListener,
			boolean paramBoolean)
	{
		super(paramContext, paramCheckTaskResultListener, false);
	}

	protected HashMap<String, Object> doInBackground(String[] paramArrayOfString)
	{
		HashMap hm = null;
		try
		{
			hm = CheckNetworkManager.getMoreApps(paramArrayOfString[0]);
		} catch (Exception localException)
		{
			hm = null;
			localException.printStackTrace();
		}
		return hm;
	}
}