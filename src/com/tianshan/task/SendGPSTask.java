package com.tianshan.task;

import android.content.Context;
import com.tianshan.source.CheckNetworkManager;
import java.util.HashMap;

public class SendGPSTask extends CheckUpdateBaseAsyncTask
{
	private static final String TAG = "SendGPSTask";

	public SendGPSTask(
			Context paramContext,
			CheckUpdateBaseAsyncTask.CheckTaskResultListener paramCheckTaskResultListener,
			boolean paramBoolean)
	{
		super(paramContext, paramCheckTaskResultListener, false);
	}

	@Override
	protected HashMap<String, Object> doInBackground(String[] paramArrayOfString)
	{
		HashMap hm = null;
		try
		{
			hm = CheckNetworkManager.Sendgps(paramArrayOfString[0],
					paramArrayOfString[1]);
		} catch (Exception localException)
		{
			hm = null;
		}
		return hm;
	}
}