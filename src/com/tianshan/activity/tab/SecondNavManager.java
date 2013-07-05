package com.tianshan.activity.tab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianshan.R;
import com.tianshan.dbhelper.SubnavHelper;
import com.tianshan.source.DEBUG;
import com.tianshan.source.HttpRequest;
import com.tianshan.source.SiteTools;

public class SecondNavManager
{
	private final String GETFUP = "getFup";
	private final String GETFUPBYISSET = "getFupByisset";
	protected ArrayList<TextView> arrTxt = new ArrayList();
	protected Drawable btnBg;
	protected OnSecondNavBtnClick btnCallBack;
	private View.OnClickListener btnMoreClick = new View.OnClickListener()
	{
		public void onClick(View view)
		{
			clearSelectTxt();
			view.setBackgroundDrawable(btnBg);
			if (btnCallBack != null)
				btnCallBack.onBtnMoreClick(firstType);
		}
	};
	protected Activity c;
	protected int firstType = -999;
	protected TextView more;
	protected View.OnClickListener onBtnClick = new View.OnClickListener()
	{
		public void onClick(View view)
		{
			clearSelectTxt();
			view.setBackgroundDrawable(btnBg);
			more.setBackgroundDrawable(null);
			if (btnCallBack != null)
				btnCallBack.onClick(firstType, view.getId());
		}
	};
	protected LinearLayout subnav;

	public SecondNavManager(Activity paramActivity)
	{
		this.c = paramActivity;
		initUI();
		initEventListener();
	}

	private boolean checkMore(int i)
	{
		boolean flag;
		if (SubnavHelper.getInstance(c).getFupCount(i) > 4)
			flag = true;
		else
			flag = false;
		return flag;
	}

	private void clearUIAndArray()
	{
		this.subnav.removeAllViews();
		this.arrTxt.clear();
	}

	private TextView createTextView(String paramString, int paramInt)
	{
		LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
				800, -2);
		localLayoutParams.leftMargin = 5;
		localLayoutParams.rightMargin = 5;
		localLayoutParams.weight = 1.0F;
		TextView localTextView = new TextView(this.c);
		localTextView.setText(paramString);
		localTextView.setId(paramInt);
		localTextView.setTextSize(16.0F);
		localTextView.setTextColor(-1);
		localTextView.setGravity(17);
		localTextView.setLayoutParams(localLayoutParams);
		localTextView.setBackgroundColor(0);
		localTextView.setOnClickListener(this.onBtnClick);
		return localTextView;
	}

	private void initEventListener()
	{
		this.more.setOnClickListener(this.btnMoreClick);
	}

	private void initUI()
	{
		// 内容界面上一条横栏,包含很多子标签
		this.subnav = ((LinearLayout) this.c.findViewById(R.id.subnav));
		this.btnBg = this.c.getResources().getDrawable(R.drawable.subnavbg);
		this.more = createTextView("更多", -1);
		this.more.setTextColor(-16777216);
	}

	public void ClickBtnByIndex(int paramInt)
	{
		if ((paramInt < this.arrTxt.size()) && (paramInt >= 0))
			((TextView) this.arrTxt.get(paramInt)).performClick();
	}

	public void FillNavDataByMapData(int paramInt,
			List<HashMap<String, Object>> paramList, boolean paramBoolean)
	{
		this.firstType = paramInt;
		clearUIAndArray();
		fillText(paramList, paramBoolean);
	}

	public void FillNavDataByType(int paramInt)
	{
		this.firstType = paramInt;
		clearUIAndArray();
		switch (paramInt)
		{
		case 0:
		case 1:
		case 7:
		default:
			return;
		case 2:
		case 3:
		case 4:
			fillText(getData(paramInt, "getFup"), checkMore(paramInt));
			break;
		case -1:
			fillText(createBBSData(), false);
			break;
		case 5:
			fillText(createBoardData(), false);
			break;
		case 6:
			fillText(createFavouriteData(), false);
			break;
		case 8:
			fillText(createMyCommentData(), false);
			break;
		case 9:
			fillText(createMyNearData(), false);
			break;
		}
	}

	public int GetArrayBtnSize()
	{
		return this.arrTxt.size();
	}

	public int GetBtnIdByIndex(int paramInt)
	{
		return ((TextView) this.arrTxt.get(paramInt)).getId();
	}

	public void SetOnSecondNavBtnClick(
			OnSecondNavBtnClick paramOnSecondNavBtnClick)
	{
		this.btnCallBack = paramOnSecondNavBtnClick;
	}

	protected String checkPushNewThread()
	{
		String str = "";
		try
		{
			String str1 = SiteTools
					.getApiUrl(new String[] { "ac=pushnewthread" });
			String str2 = new HttpRequest()._get(str1);
			if (str2 != null)
			{
				JSONObject localJSONObject = new JSONObject(str2);
				if (localJSONObject != null)
				{
					str = localJSONObject.optString("msg");
				}
			}
		} catch (Exception localException)
		{
			DEBUG.o(localException.getMessage());
		}
		return str;
	}

	protected void clearSelectTxt()
	{
		for (int i = 0; i < this.arrTxt.size(); i++)
		{
			((TextView) this.arrTxt.get(i)).setBackgroundColor(0);
		}
	}

	protected List<HashMap<String, Object>> createBBSData()
	{
		String str = checkPushNewThread();
		ArrayList localArrayList = new ArrayList();
		HashMap localHashMap1 = new HashMap();
		localHashMap1.put("id", Integer.valueOf(0));
		localHashMap1.put("fup", Integer.valueOf(0));
		localHashMap1.put("name", "热帖");
		localArrayList.add(localHashMap1);
		HashMap localHashMap2 = new HashMap();
		localHashMap2.put("id", Integer.valueOf(1));
		localHashMap2.put("fup", Integer.valueOf(1));
		localHashMap2.put("name", "精华");
		localArrayList.add(localHashMap2);
		if ("on".equals(str))
		{
			HashMap localHashMap3 = new HashMap();
			localHashMap3.put("id", Integer.valueOf(2));
			localHashMap3.put("fup", Integer.valueOf(2));
			localHashMap3.put("name", "�?��");
			localArrayList.add(localHashMap3);
		}
		HashMap localHashMap4 = new HashMap();
		localHashMap4.put("id", Integer.valueOf(3));
		localHashMap4.put("fup", Integer.valueOf(3));
		localHashMap4.put("name", "版块");
		localArrayList.add(localHashMap4);
		return localArrayList;
	}

	protected List<HashMap<String, Object>> createBoardData()
	{
		ArrayList localArrayList = new ArrayList();
		HashMap localHashMap1 = new HashMap();
		localHashMap1.put("id", Integer.valueOf(0));
		localHashMap1.put("fup", Integer.valueOf(0));
		localHashMap1.put("name", "热门投稿");
		localArrayList.add(localHashMap1);
		HashMap localHashMap2 = new HashMap();
		localHashMap2.put("id", Integer.valueOf(1));
		localHashMap2.put("fup", Integer.valueOf(1));
		localHashMap2.put("name", "我要投稿");
		localArrayList.add(localHashMap2);
		HashMap localHashMap3 = new HashMap();
		localHashMap3.put("id", Integer.valueOf(2));
		localHashMap3.put("fup", Integer.valueOf(2));
		localHashMap3.put("name", "我的投稿");
		localArrayList.add(localHashMap3);
		return localArrayList;
	}

	protected List<HashMap<String, Object>> createFavouriteData()
	{
		ArrayList localArrayList = new ArrayList();
		int[] arrayOfInt = { R.string.STR_NEWS, R.string.STR_IMG,
				R.string.STR_VEDIO, R.string.STR_TIKET, R.string.STR_OTHER };
		for (int i = 0; i < arrayOfInt.length; i++)
		{
			HashMap localHashMap = new HashMap();
			localHashMap.put("id", Integer.valueOf(i + 1));
			localHashMap.put("name", getRCString(arrayOfInt[i]));
			localArrayList.add(localHashMap);
		}
		return localArrayList;
	}

	protected List<HashMap<String, Object>> createMyCommentData()
	{
		ArrayList localArrayList = new ArrayList();
		int[] arrayOfInt = { R.string.STR_COMMENT_NEWS,
				R.string.STR_COMMENT_VIDEO, R.string.STR_COMMENT_BOARD };
		for (int i = 0; i < arrayOfInt.length; i++)
		{
			HashMap localHashMap = new HashMap();
			localHashMap.put("id", Integer.valueOf(i));
			localHashMap.put("name", getRCString(arrayOfInt[i]));
			localArrayList.add(localHashMap);
		}
		return localArrayList;
	}

	protected List<HashMap<String, Object>> createMyNearData()
	{
		ArrayList localArrayList = new ArrayList();
		int[] arrayOfInt = { R.string.STR_NEAR_NEWS, R.string.STR_NEAR_THREAD,
				R.string.STR_NEAR_BOARD, R.string.STR_NEAR_USER };
		for (int i = 0; i < arrayOfInt.length; i++)
		{
			HashMap localHashMap = new HashMap();
			localHashMap.put("id", Integer.valueOf(i));
			localHashMap.put("name", getRCString(arrayOfInt[i]));
			localArrayList.add(localHashMap);
		}
		return localArrayList;
	}

	protected void fillText(List<HashMap<String, Object>> paramList,
			boolean paramBoolean)
	{
		int j;
		if (paramList != null)
		{
			int i = paramList.size();
			j = i;
			if (paramBoolean)
				i++;
			this.subnav.setWeightSum(i);
			for (int k = 0;; k++)
			{
				if (k >= j)
				{
					if (paramBoolean)
					{
						this.subnav.addView(this.more);
						this.arrTxt.add(this.more);
					}
					return;
				}
				HashMap localHashMap = (HashMap) paramList.get(k);
				TextView localTextView = createTextView(localHashMap
						.get("name").toString(),
						((Integer) localHashMap.get("id")).intValue());
				Log.d("type", localHashMap.get("name").toString() + "    init");
				localTextView.setTextColor(-16777216);
				this.arrTxt.add(localTextView);
				this.subnav.addView(localTextView);
			}
		}
	}

	protected List getData(int i, String s)
	{
		SubnavHelper subnavhelper = SubnavHelper.getInstance(c);
		List list;
		if ("getFup".equals(s))
			list = subnavhelper.getFup(i);
		else if ("getFupByisset".equals(s))
			list = subnavhelper.getFupByisset(i);
		else
			list = subnavhelper.getAll();
		return list;
	}

	protected String getRCString(int paramInt)
	{
		return this.c.getResources().getString(paramInt);
	}

	public static abstract interface OnSecondNavBtnClick
	{
		public abstract void onBtnMoreClick(int paramInt);

		public abstract void onClick(int paramInt1, int paramInt2);
	}

	public static abstract interface SecondNavType
	{
		public static final int BBS = -1;
		public static final int BOARD = 5;
		public static final int FAVOURITE = 6;
		public static final int FORUMDISPLAY = 7;
		public static final int MYCOMMENT = 8;
		public static final int NEARBYINFO = 9;
		public static final int NEWS = 2;
		public static final int PHOTO = 4;
		public static final int VIDEO = 3;
	}
}