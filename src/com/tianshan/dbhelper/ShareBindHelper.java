package com.tianshan.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tianshan.model.ShareBind;

/**
 * 绑定分享帐号帮助类，继承数据库
 * 
 * @author lkh
 * 
 */
public class ShareBindHelper extends DB
{
	public static final String TABLE_NAME = "share_bind";
	private static ShareBindHelper dbObj = null;
	private final String KEY_ACCESS_TOKEN = "access_token";
	private final String KEY_EXPIRES_IN = "expires_in";
	private final String KEY_ID = "id";
	private final String KEY_KEYWORD1 = "keyword1";
	private final String KEY_KEYWORD2 = "keyword2";
	private final String KEY_KEYWORD3 = "keyword3";
	private final String KEY_KEYWORD4 = "keyword4";
	private final String KEY_KEYWORD5 = "keyword5";
	private final String KEY_OPEN_ID = "open_id";
	private final String KEY_TYPENAME = "typename";
	private final String[] columns = { "id", "typename", "access_token",
			"expires_in", "open_id", "keyword1", "keyword2", "keyword3",
			"keyword4", "keyword5" };

	protected ShareBindHelper(Context paramContext)
	{
		super(paramContext);
	}

	private ShareBind constructShareBind(Cursor paramCursor)
	{
		ShareBind localShareBind = new ShareBind();
		localShareBind.setId(paramCursor.getInt(0));
		localShareBind.setTypename(paramCursor.getString(1));
		localShareBind.setAccess_token(paramCursor.getString(2));
		localShareBind.setExpires_in(paramCursor.getString(3));
		localShareBind.setOpen_id(paramCursor.getString(4));
		localShareBind.setKeyword1(paramCursor.getString(5));
		localShareBind.setKeyword2(paramCursor.getString(6));
		localShareBind.setKeyword3(paramCursor.getString(7));
		localShareBind.setKeyword4(paramCursor.getString(8));
		localShareBind.setKeyword5(paramCursor.getString(9));
		return localShareBind;
	}

	public static ShareBindHelper getInstance(Context paramContext)
	{
		if (dbObj == null)
			dbObj = new ShareBindHelper(paramContext);
		return dbObj;
	}

	/**
	 * 取消所有分享绑定
	 * 
	 * @return
	 */
	public boolean clearAllShareBind()
	{
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("access_token", "");
		contentvalues.put("expires_in", "");
		contentvalues.put("open_id", "");
		contentvalues.put("keyword1", "");
		contentvalues.put("keyword2", "");
		contentvalues.put("keyword3", "");
		contentvalues.put("keyword4", "");
		contentvalues.put("keyword5", "");
		boolean flag;
		if (getWritableDatabase().update("share_bind", contentvalues, "1=1",
				null) > 0)
			flag = true;
		else
			flag = false;
		close();
		return flag;
	}

	public boolean clearShareBindById(int i)
	{
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("access_token", "");
		contentvalues.put("expires_in", "");
		contentvalues.put("open_id", "");
		contentvalues.put("keyword1", "");
		contentvalues.put("keyword2", "");
		contentvalues.put("keyword3", "");
		contentvalues.put("keyword4", "");
		contentvalues.put("keyword5", "");
		boolean flag;
		if (getWritableDatabase().update("share_bind", contentvalues,
				(new StringBuilder("id=")).append(i).toString(), null) > 0)
			flag = true;
		else
			flag = false;
		close();
		return flag;
	}

	public ShareBind getShareBindById(int paramInt)
	{
		SQLiteDatabase localSQLiteDatabase = getReadableDatabase();
		Cursor localCursor = getWritableDatabase().query(true, "share_bind",
				this.columns, "id=" + paramInt, null, null, null, null, "1");
		boolean bool = localCursor.moveToFirst();
		ShareBind localShareBind = null;
		if (bool)
			localShareBind = constructShareBind(localCursor);
		localCursor.close();
		localSQLiteDatabase.close();
		return localShareBind;
	}

	public boolean updateShareBind(ShareBind sharebind)
	{
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("access_token", sharebind.getAccess_token());
		contentvalues.put("expires_in", sharebind.getExpires_in());
		contentvalues.put("open_id", sharebind.getOpen_id());
		contentvalues.put("keyword1", sharebind.getKeyword1());
		contentvalues.put("keyword2", sharebind.getKeyword2());
		contentvalues.put("keyword3", sharebind.getKeyword3());
		contentvalues.put("keyword4", sharebind.getKeyword4());
		contentvalues.put("keyword5", sharebind.getKeyword5());
		boolean flag;
		if (getWritableDatabase()
				.update("share_bind",
						contentvalues,
						(new StringBuilder("id=")).append(sharebind.getId())
								.toString(), null) > 0)
			flag = true;
		else
			flag = false;
		close();
		return flag;
	}
}