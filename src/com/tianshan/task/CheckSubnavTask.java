package com.tianshan.task;

import android.content.Context;
import com.tianshan.source.CheckNetworkManager;
import java.util.HashMap;

/**
 * 检查应用程序新版本的Task
 * 
 * @author lkh
 * 
 */
public class CheckSubnavTask extends CheckUpdateBaseAsyncTask
{
	private static final String TAG = "CheckSubnavTask";

	public CheckSubnavTask(
			Context paramContext,
			CheckUpdateBaseAsyncTask.CheckTaskResultListener paramCheckTaskResultListener,
			boolean paramBoolean)
	{
		super(paramContext, paramCheckTaskResultListener, false);
	}

	protected HashMap<String, Object> doInBackground(String[] paramArrayOfString)
	{
		HashMap hashmap = null;
		try
		{
			hashmap = CheckNetworkManager.checksubnav(paramArrayOfString[0]);
		} catch (Exception localException)
		{
			hashmap = null;
		}
		return hashmap;
	}
}
