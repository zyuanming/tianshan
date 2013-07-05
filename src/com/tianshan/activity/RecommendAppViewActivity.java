package com.tianshan.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.tianshan.adapter.DownloadAppListAdapter;
import com.tianshan.model.App;
import com.tianshan.source.DEBUG;
import com.tianshan.source.ShowMessage;
import com.tianshan.source.SiteTools;
import com.tianshan.source.activity.SecondLevelActivity;
import com.tianshan.source.view.Navbar;
import com.tianshan.source.view.Navbar.OnNavigateListener;
import com.tianshan.task.CheckUpdateBaseAsyncTask;
import com.tianshan.task.CheckUpdateBaseAsyncTask.CheckTaskResultListener;
import com.tianshan.task.GetMoreAppTask;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecommendAppViewActivity extends SecondLevelActivity implements
		Navbar.OnNavigateListener
{
	private CheckUpdateBaseAsyncTask.CheckTaskResultListener getmoreListener = new CheckUpdateBaseAsyncTask.CheckTaskResultListener()
	{
		public void onTaskResult(boolean paramAnonymousBoolean,
				HashMap<String, Object> paramAnonymousHashMap)
		{
			RecommendAppViewActivity.this.mData = new ArrayList();
			String str1;
			if (paramAnonymousBoolean)
			{
				str1 = (String) paramAnonymousHashMap.get("data");
				Log.d("result", "*****data**** =" + str1);
				if ((str1 != null) || !(str1.equals("")))
				{
					try
					{
						JSONObject localJSONObject1 = new JSONObject(str1);
						JSONArray localJSONArray;
						int i;
						if (localJSONObject1 != null)
						{
							JSONObject localJSONObject2 = localJSONObject1
									.optJSONObject("msg");
							if (localJSONObject2 != null)
							{
								String str2 = localJSONObject2.optString(
										"msgvar", "list_empty");
								DEBUG.o(localJSONObject2.optString("msgstr"));
								if (!"list_empty".equalsIgnoreCase(str2))
								{
									JSONObject localJSONObject3 = localJSONObject1
											.optJSONObject("res");
									if (localJSONObject3 != null)
									{
										localJSONArray = localJSONObject3
												.optJSONArray("list");
										i = localJSONArray.length();
										if ((localJSONArray != null) && (i > 0))
										{
											for (int j = 0;; j++)
											{
												if (j >= i)
												{
													RecommendAppViewActivity.this
															._initView();
													return;
												}
												App localApp = new App();
												localApp.setName(Uri
														.decode(localJSONArray
																.getJSONObject(
																		j)
																.optString(
																		"appname")));
												DEBUG.o("appname ="
														+ Uri.decode(localJSONArray
																.getJSONObject(
																		j)
																.optString(
																		"appname")));
												localApp.setDesc(Uri
														.decode(localJSONArray
																.getJSONObject(
																		j)
																.optString(
																		"appinfo")));
												DEBUG.o("appinfo = "
														+ Uri.decode(localJSONArray
																.getJSONObject(
																		j)
																.optString(
																		"appinfo")));
												localApp.setIconUrl(Uri
														.decode(localJSONArray
																.getJSONObject(
																		j)
																.optString(
																		"applogo")));
												DEBUG.o("applogo = "
														+ Uri.decode(localJSONArray
																.getJSONObject(
																		j)
																.optString(
																		"applogo")));
												localApp.setDownloadUrl(Uri
														.decode(localJSONArray
																.getJSONObject(
																		j)
																.optString(
																		"appurl")));
												DEBUG.o("appurl = "
														+ Uri.decode(localJSONArray
																.getJSONObject(
																		j)
																.optString(
																		"appurl")));
												mData.add(localApp);
											}
										}
									}
								}
							}
						}
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	};
	private ListView listview;
	private DownloadAppListAdapter mAdapter;
	private ArrayList<App> mData;
	protected AdapterView.OnItemClickListener onitemclicklistener = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> paramAnonymousAdapterView,
				View paramAnonymousView, int paramAnonymousInt,
				long paramAnonymousLong)
		{
			String str = ((App) RecommendAppViewActivity.this.mData
					.get(paramAnonymousInt)).getDownloadUrl();
			Log.d("down", str);
			if ((str != null) && (!str.equals("")))
				RecommendAppViewActivity.this.startActivity(new Intent(
						"android.intent.action.VIEW", Uri.parse(str)));
		}
	};

	private void _initView()
	{
		progress.setVisibility(8);
		listview = new ListView(this);
		listview.setCacheColorHint(0);
		listview.setScrollingCacheEnabled(true);
		android.view.ViewGroup.LayoutParams layoutparams = new android.view.ViewGroup.LayoutParams(
				-1, -2);
		viewbox.addView(listview, layoutparams);
		viewbox.setBackgroundColor(0xfff5f5f5);
		listview.setOnItemClickListener(onitemclicklistener);
		mAdapter = new DownloadAppListAdapter(getApplicationContext());
		if (mData.size() == 0)
		{
			ShowMessage.getInstance(this)._showToast("暂无推荐应用", 2);
		} else
		{
			mAdapter.setData(mData);
			listview.setAdapter(mAdapter);
		}
	}

	private void getData()
	{
		progress.setVisibility(0);
		String str = SiteTools.getApiUrl(new String[] { "ac=hotapp" });
		new GetMoreAppTask(getApplicationContext(), this.getmoreListener, false)
				.execute(new String[] { str });
	}

	protected void _initNavBar(boolean paramBoolean)
	{
		super._initNavBar(paramBoolean);
		Navbar localNavbar = new Navbar(this, this.navbarbox);
		localNavbar.setOnNavigate(this);
		localNavbar.setTitle("推荐应用");
		localNavbar.setCommitBtnVisibility(false);
	}

	public void onBack()
	{
		finish();
	}

	public void onCommit()
	{}

	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		getData();
		_initNavBar(true);
	}
}