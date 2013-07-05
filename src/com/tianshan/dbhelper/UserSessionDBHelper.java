package com.tianshan.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tianshan.model.UserSession;
import com.tianshan.source.Tools;

public class UserSessionDBHelper extends DB
{
	private static UserSessionDBHelper dbObj = null;
	private final String KEY_AUTH = "auth";
	private final String KEY_DATELINE = "dateline";
	private final String KEY_FORMHASH = "formhash";
	private final String KEY_GROUPID = "groupid";
	private final String KEY_ID = "_id";
	private final String KEY_MOBILE_AUTH = "mobile_auth";
	private final String KEY_SALTKEY = "saltkey";
	private final String KEY_UID = "uid";
	private final String KEY_USERNAME = "username";
	private final String KEY_WEBVIEWCOOKIES = "webviewcookies";
	private final String TABLE_NAME = "common_usersession";
	private final String[] columns = { "_id", "uid", "username", "groupid",
			"saltkey", "auth", "dateline", "formhash", "mobile_auth",
			"webviewcookies" };

	private UserSessionDBHelper(Context paramContext)
	{
		super(paramContext);
	}

	/**
	 * 构造用户会话
	 * 
	 * @param paramCursor
	 *            包含用户信息的Cursor
	 * @return
	 */
	private UserSession constructUserSession(Cursor paramCursor)
	{
		UserSession localUserSession = new UserSession();
		localUserSession.setUid(paramCursor.getLong(1));
		localUserSession.setUsername(paramCursor.getString(2));
		localUserSession.setGroupid(paramCursor.getInt(3));
		localUserSession.setSaltkey(paramCursor.getString(4));
		localUserSession.setAuth(paramCursor.getString(5));
		localUserSession.setFormhash(paramCursor.getString(7));
		localUserSession.setmobile_auth(paramCursor.getString(8));
		localUserSession.setWebViewCookies(paramCursor.getString(9));
		return localUserSession;
	}

	public static UserSessionDBHelper getInstance(Context paramContext)
	{
		if (dbObj == null)
			dbObj = new UserSessionDBHelper(paramContext);
		return dbObj;
	}

	public boolean deleteAll()
	{
		boolean flag;
		if (getWritableDatabase().delete("common_usersession", null, null) > 0)
			flag = true;
		else
			flag = false;
		close();
		return flag;
	}

	public UserSession getByUId(long paramLong)
	{
		SQLiteDatabase localSQLiteDatabase = getReadableDatabase();
		Cursor localCursor = getWritableDatabase().query(true,
				"common_usersession", this.columns, "uid='" + paramLong + "'",
				null, null, null, null, "1");
		boolean bool = localCursor.moveToFirst();
		UserSession localUserSession = null;
		if (bool)
			localUserSession = constructUserSession(localCursor);
		localCursor.close();
		localSQLiteDatabase.close();
		return localUserSession;
	}

	public int getCountByUId(long paramLong)
	{
		String str = "SELECT COUNT(*) AS COUNT FROM common_usersession WHERE uid='"
				+ paramLong + "'";
		SQLiteDatabase localSQLiteDatabase = getReadableDatabase();
		Cursor localCursor = getWritableDatabase().rawQuery(str, null);
		boolean bool = localCursor.moveToNext();
		int i = 0;
		if (bool)
			i = localCursor.getInt(0);
		localCursor.close();
		localSQLiteDatabase.close();
		return i;
	}

	public UserSession getLast()
	{
		SQLiteDatabase localSQLiteDatabase = getReadableDatabase();
		Cursor localCursor = getWritableDatabase().query(true,
				"common_usersession", this.columns,
				"dateline<'" + Tools._getTimeStamp() + "'", null, null, null,
				null, "1");
		boolean bool = localCursor.moveToFirst();
		UserSession localUserSession = null;
		if (bool)
			localUserSession = constructUserSession(localCursor);
		localCursor.close();
		localSQLiteDatabase.close();
		return localUserSession;
	}

	public boolean insert(UserSession usersession)
	{
		ContentValues contentvalues = usersession._getSqlVal();
		contentvalues.put("dateline", Long.valueOf(Tools._getTimeStamp()));
		boolean flag;
		if (getWritableDatabase().insert("common_usersession", null,
				contentvalues) > 0L)
			flag = true;
		else
			flag = false;
		close();
		return flag;
	}

	public boolean update(UserSession usersession)
	{
		ContentValues contentvalues = usersession._getSqlVal();
		contentvalues.put("dateline", Long.valueOf(Tools._getTimeStamp()));
		boolean flag;
		if (getWritableDatabase().update(
				"common_usersession",
				contentvalues,
				(new StringBuilder("uid='")).append(usersession.getUid())
						.append("'").toString(), null) > 0)
			flag = true;
		else
			flag = false;
		close();
		return flag;
	}
}