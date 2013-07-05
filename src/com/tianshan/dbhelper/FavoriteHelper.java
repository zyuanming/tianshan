package com.tianshan.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FavoriteHelper extends DB
{
	public static final int FAV_TYPE_IMG = 2;
	public static final int FAV_TYPE_MAX = 5;
	public static final int FAV_TYPE_NEWS = 1;
	public static final int FAV_TYPE_OTHER = 5;
	public static final int FAV_TYPE_TICKET = 4;
	public static final int FAV_TYPE_VEDIO = 3;
	public static final String KEY_FAV_ID = "id";
	public static final String KEY_FAV_NEWS_ID = "news_id";
	public static final String KEY_FAV_NEWS_SUMMARY = "news_summary";
	public static final String KEY_FAV_NEWS_TYPE = "news_type";
	public static final String KEY_FAV_NEWS_UID = "news_uid";
	public static final String TABLE_NAME = "favorites";
	private static String[] columns = { "id", "news_id", "news_uid",
			"news_type", "news_summary" };
	private static FavoriteHelper dbObj = null;

	private FavoriteHelper(Context paramContext)
	{
		super(paramContext);
	}

	public static FavoriteHelper getInstance(Context paramContext)
	{
		if (dbObj == null)
			dbObj = new FavoriteHelper(paramContext);
		return dbObj;
	}

	public boolean delete(int i, String s, long l)
	{
		String s1 = (new StringBuilder("news_id='")).append(s).append("' AND ")
				.append("news_type").append(" = ").append(i).append(" AND ")
				.append("news_uid").append(" = ").append(l).toString();
		boolean flag;
		if (getWritableDatabase().delete("favorites", s1, null) > 0)
			flag = true;
		else
			flag = false;
		close();
		return flag;
	}

	public int insert(int paramInt, String paramString1, String paramString2,
			long paramLong)
	{
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("news_uid", Long.valueOf(paramLong));
		localContentValues.put("news_id", paramString1);
		localContentValues.put("news_type", Integer.valueOf(paramInt));
		localContentValues.put("news_summary", paramString2);
		int i = (int) getWritableDatabase().insert("favorites", null,
				localContentValues);
		close();
		return i;
	}

	public boolean isInFavorites(int i, String s, long l)
	{
		SQLiteDatabase sqlitedatabase = getWritableDatabase();
		String as[] = { "news_id" };
		String as1[] = new String[3];
		as1[0] = (new StringBuilder()).append(i).toString();
		as1[1] = (new StringBuilder()).append(s).toString();
		as1[2] = (new StringBuilder()).append(l).toString();
		Cursor cursor = sqlitedatabase.query("favorites", as,
				"news_type = ? AND news_id = ? AND news_uid = ?", as1, null,
				null, null);
		boolean flag = false;
		if (cursor != null)
		{
			int j = cursor.getCount();
			flag = false;
			if (j > 0)
				flag = true;
		}
		if (cursor != null)
			cursor.close();
		return flag;
	}
}