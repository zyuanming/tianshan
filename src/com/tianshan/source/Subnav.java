package com.tianshan.source;

import android.content.Context;
import com.tianshan.dbhelper.SubnavHelper;
import com.tianshan.model.Subnavisement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 二级导航
 * 
 * @author lkh
 * 
 */
public class Subnav
{
	private static SubnavHelper dbHelper = null;

	public static void checkNewSubnav(Context paramContext)
	{
		try
		{
			String str1 = SiteTools.getApiUrl(new String[] { "ac=subnav" });
			String str2 = new HttpRequest()._get(str1);
			DEBUG.o(str2);
			JSONObject localJSONObject1 = new JSONObject(str2);
			if (localJSONObject1 != null)
			{
				JSONObject localJSONObject2 = localJSONObject1
						.optJSONObject("msg");
				if ((localJSONObject2 != null)
						&& (!"list_empty".equalsIgnoreCase(localJSONObject2
								.optString("msgvar", "list_empty"))))
				{
					JSONObject localJSONObject3 = localJSONObject1
							.optJSONObject("res");
					if (localJSONObject3 != null)
					{
						JSONArray localJSONArray = localJSONObject3
								.optJSONArray("list");
						int i = localJSONArray.length();
						if ((localJSONArray != null) && (i > 0))
						{
							int j = localJSONArray.length();
							dbHelper = SubnavHelper.getInstance(paramContext);
							ArrayList localArrayList1 = new ArrayList();
							ArrayList localArrayList2 = new ArrayList();
							for (int k = 0;; k++)
							{
								if (k >= j)
								{
									clearSubnav(localArrayList2, paramContext);
									upSetSubnav(localArrayList1, paramContext);
									break;
								}
								Subnavisement localSubnavisement1 = new Subnavisement(
										localJSONArray.optJSONObject(k));
								localArrayList2.add(Integer
										.valueOf(localSubnavisement1.getId()));
								Subnavisement localSubnavisement2 = dbHelper
										.get(localSubnavisement1.getId());
								dbHelper.save(localSubnavisement1);
								if ((localSubnavisement2 != null)
										&& (localSubnavisement2.getIsset() == 2))
								{
									localSubnavisement1.setIsset(2);
									dbHelper.save(localSubnavisement1);
								}
								localArrayList1.add(Integer
										.valueOf(localSubnavisement1.getFup()));
							}
						}
					}
				}
			}
		} catch (Exception localException)
		{
			localException.printStackTrace();
		}
	}

	/**
	 * 清除二级导航数据
	 * 
	 * @param paramList
	 * @param paramContext
	 */
	public static void clearSubnav(List paramList, Context paramContext)
	{
		dbHelper = SubnavHelper.getInstance(paramContext);
		Iterator localIterator = dbHelper.getAll().iterator();
		while (true)
		{
			if (!localIterator.hasNext())
				return;
			int i = ((Integer) ((HashMap) localIterator.next()).get("id"))
					.intValue();
			if (!paramList.contains(Integer.valueOf(i)))
			{
				DEBUG.o("DELETE ID:" + i);
				Subnavisement localSubnavisement = new Subnavisement();
				localSubnavisement.setId(i);
				dbHelper.delete(localSubnavisement);
			}
		}
	}

	/**
	 * 保存二级导航数据
	 * 
	 * @param paramList
	 * @param paramContext
	 */
	public static void upSetSubnav(List paramList, Context paramContext)
	{
		dbHelper = SubnavHelper.getInstance(paramContext);
		Iterator localIterator1;
		if (paramList.size() > 0)
		{
			localIterator1 = paramList.iterator();
			while (true)
			{
				if (!localIterator1.hasNext())
					return;
				int i = ((Integer) localIterator1.next()).intValue();
				Subnavisement localSubnavisement1 = dbHelper.getDiyFup(i);
				Iterator localIterator2 = dbHelper.getFupDisplay(i).iterator();
				int j = 1;
				int k = 4;
				if (localSubnavisement1 != null)
					k = 3;
				while (localIterator2.hasNext())
				{
					Subnavisement localSubnavisement2 = (Subnavisement) localIterator2
							.next();
					if (j <= k)
					{
						localSubnavisement2.setIsset(1);
						dbHelper.save(localSubnavisement2);
					}
					j++;
				}
			}
		}
	}
}