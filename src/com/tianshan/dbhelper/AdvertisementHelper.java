package com.tianshan.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tianshan.model.Advertisement;
import java.util.ArrayList;

public class AdvertisementHelper extends DB
{
	public static final String KEY_AD_ID = "adid";
	public static final String KEY_END_TIME = "endtime";
	public static final String KEY_START_TIME = "starttime";
	public static final String KEY_UPDATE_TIME = "updatetime";
	public static final String TABLE_NAME = "advertisement";
	private static String[] columns = { "adid", "starttime", "endtime",
			"updatetime" };
	private static AdvertisementHelper dbObj = null;

	private AdvertisementHelper(Context paramContext)
	{
		super(paramContext);
	}

	private Advertisement constructAdvertisement(Cursor paramCursor)
	{
		Advertisement localAdvertisement = new Advertisement();
		localAdvertisement.setAdId(paramCursor.getInt(0));
		localAdvertisement
				.setStartTime(Long.parseLong(paramCursor.getString(1)));
		localAdvertisement.setEndTime(Long.parseLong(paramCursor.getString(2)));
		localAdvertisement.setUpdateTime(Long.parseLong(paramCursor
				.getString(3)));
		return localAdvertisement;
	}

	public static AdvertisementHelper getInstance(Context paramContext)
	{
		if (dbObj == null)
			dbObj = new AdvertisementHelper(paramContext);
		return dbObj;
	}

	private boolean insert(Advertisement advertisement)
	{
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("adid", Integer.valueOf(advertisement.getAdId()));
		contentvalues
				.put("starttime",
						(new StringBuilder(String.valueOf(advertisement
								.getStartTime()))).toString());
		contentvalues.put("endtime",
				(new StringBuilder(String.valueOf(advertisement.getEndTime())))
						.toString());
		contentvalues.put(
				"updatetime",
				(new StringBuilder(
						String.valueOf(advertisement.getUpdateTime())))
						.toString());
		boolean flag;
		if (getWritableDatabase().insert("advertisement", null, contentvalues) > 0L)
			flag = true;
		else
			flag = false;
		close();
		return flag;
	}

	private boolean update(Advertisement advertisement)
	{
		ContentValues contentvalues = new ContentValues();
		contentvalues
				.put("starttime",
						(new StringBuilder(String.valueOf(advertisement
								.getStartTime()))).toString());
		contentvalues.put("endtime",
				(new StringBuilder(String.valueOf(advertisement.getEndTime())))
						.toString());
		contentvalues.put(
				"updatetime",
				(new StringBuilder(
						String.valueOf(advertisement.getUpdateTime())))
						.toString());
		String s = (new StringBuilder("adid='"))
				.append(advertisement.getAdId()).append("'").toString();
		boolean flag;
		if (getWritableDatabase().update("advertisement", contentvalues, s,
				null) > 0)
			flag = true;
		else
			flag = false;
		close();
		return flag;
	}

	public boolean delete(Advertisement advertisement)
	{
		String s = (new StringBuilder("adid='"))
				.append(advertisement.getAdId()).append("'").toString();
		boolean flag;
		if (getWritableDatabase().delete("advertisement", s, null) > 0)
			flag = true;
		else
			flag = false;
		close();
		return flag;
	}

	public Advertisement get(int paramInt)
	{
		String str = "adid='" + paramInt + "'";
		Cursor localCursor = getReadableDatabase().query(true, "advertisement",
				columns, str, null, null, null, null, null);
		boolean bool = localCursor.moveToFirst();
		Advertisement localAdvertisement = null;
		if (bool)
			localAdvertisement = constructAdvertisement(localCursor);
		localCursor.close();
		close();
		return localAdvertisement;
	}

	public ArrayList<Advertisement> getAll()
	{
		Cursor localCursor = getReadableDatabase().query("advertisement",
				columns, null, null, null, null, null, null);
		int i = localCursor.getCount();
		ArrayList localArrayList = null;
		if (i > 0)
		{
			localArrayList = new ArrayList();
			if (localCursor.moveToFirst())
				do
					localArrayList.add(constructAdvertisement(localCursor));
				while (localCursor.moveToNext());
			localCursor.close();
			close();
		}
		return localArrayList;
	}

	public Advertisement getLast(long paramLong)
	{
		String str = "[starttime] < " + paramLong + " AND [endtime] > "
				+ paramLong;
		Cursor localCursor = getReadableDatabase().query(true, "advertisement",
				columns, str, null, null, null, null, "1");
		boolean bool = localCursor.moveToFirst();
		Advertisement localAdvertisement = null;
		if (bool)
			localAdvertisement = constructAdvertisement(localCursor);
		localCursor.close();
		close();
		return localAdvertisement;
	}

	public boolean save(Advertisement advertisement)
	{
		boolean flag;
		if (get(advertisement.getAdId()) == null)
			flag = insert(advertisement);
		else
			flag = update(advertisement);
		return flag;
	}
}