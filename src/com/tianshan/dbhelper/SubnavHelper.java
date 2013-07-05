package com.tianshan.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tianshan.model.Subnavisement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 二级导航帮助类，继承了数据库
 * 
 * @author lkh
 * 
 */
public class SubnavHelper extends DB
{
	public static final String KEY_NAV_DISPLAYORDER = "displayorder";
	public static final String KEY_NAV_FUP = "fup";
	public static final String KEY_NAV_ID = "id";
	public static final String KEY_NAV_ISSET = "isset";
	public static final String KEY_NAV_NAME = "name";
	public static final String TABLE_NAME = "subnav";
	private static String[] columns = { "id", "fup", "name", "displayorder",
			"isset" };
	private static SubnavHelper dbObj = null;

	private SubnavHelper(Context paramContext)
	{
		super(paramContext);
	}

	private List<HashMap<String, Object>> _paresData(
			ArrayList<Subnavisement> paramArrayList)
	{
		ArrayList<HashMap<String, Object>> localArrayList = new ArrayList<HashMap<String, Object>>();
		if (paramArrayList != null)
		{
			for (int j = 0; j < paramArrayList.size(); j++)
			{
				Subnavisement localSubnavisement = (Subnavisement) paramArrayList
						.get(j);
				HashMap<String, Object> localHashMap = new HashMap<String, Object>();
				localHashMap.put(KEY_NAV_ID,
						Integer.valueOf(localSubnavisement.getId()));
				localHashMap.put(KEY_NAV_FUP,
						Integer.valueOf(localSubnavisement.getFup()));
				localHashMap.put(KEY_NAV_NAME, localSubnavisement.getName());
				localHashMap.put(KEY_NAV_DISPLAYORDER,
						Integer.valueOf(localSubnavisement.getDisplayorder()));
				localHashMap.put(KEY_NAV_ISSET,
						Integer.valueOf(localSubnavisement.getIsset()));
				localArrayList.add((HashMap<String, Object>) localHashMap);
			}
		}
		return localArrayList;
	}

	private Subnavisement constructSubnavisement(Cursor paramCursor)
	{
		Subnavisement localSubnavisement = new Subnavisement();
		localSubnavisement.setId(paramCursor.getInt(0));
		localSubnavisement.setFup(paramCursor.getInt(1));
		localSubnavisement.setName(paramCursor.getString(2));
		localSubnavisement.setDisplayorder(paramCursor.getInt(3));
		localSubnavisement.setIsset(paramCursor.getInt(4));
		return localSubnavisement;
	}

	public static SubnavHelper getInstance(Context paramContext)
	{
		if (dbObj == null)
			dbObj = new SubnavHelper(paramContext);
		return dbObj;
	}

	/**
	 * 插入数据到数据库
	 * 
	 * @param subnavisement
	 * @return
	 */
	private boolean insert(Subnavisement subnavisement)
	{
		ContentValues contentvalues = new ContentValues();
		contentvalues.put(KEY_NAV_ID, Integer.valueOf(subnavisement.getId()));
		contentvalues.put(KEY_NAV_FUP,
				(new StringBuilder(String.valueOf(subnavisement.getFup())))
						.toString());
		contentvalues.put(KEY_NAV_NAME,
				(new StringBuilder(String.valueOf(subnavisement.getName())))
						.toString());
		contentvalues.put(
				KEY_NAV_DISPLAYORDER,
				(new StringBuilder(String.valueOf(subnavisement
						.getDisplayorder()))).toString());
		contentvalues.put(KEY_NAV_ISSET,
				(new StringBuilder(String.valueOf(subnavisement.getIsset())))
						.toString());
		boolean flag;
		if (getWritableDatabase().insert(TABLE_NAME, null, contentvalues) > 0L)
			flag = true;
		else
			flag = false;
		close();
		return flag;
	}

	/**
	 * 更新数据库里的数据
	 * 
	 * @param subnavisement
	 * @return
	 */
	private boolean update(Subnavisement subnavisement)
	{
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("fup",
				(new StringBuilder(String.valueOf(subnavisement.getFup())))
						.toString());
		contentvalues.put("name",
				(new StringBuilder(String.valueOf(subnavisement.getName())))
						.toString());
		contentvalues.put(
				"displayorder",
				(new StringBuilder(String.valueOf(subnavisement
						.getDisplayorder()))).toString());
		contentvalues.put("isset",
				(new StringBuilder(String.valueOf(subnavisement.getIsset())))
						.toString());
		String s = (new StringBuilder("id='")).append(subnavisement.getId())
				.append("'").toString();
		boolean flag;
		if (getWritableDatabase().update("subnav", contentvalues, s, null) > 0)
			flag = true;
		else
			flag = false;
		close();
		return flag;
	}

	/**
	 * 删除数据库中的数据
	 * 
	 * @param subnavisement
	 * @return
	 */
	public boolean delete(Subnavisement subnavisement)
	{
		String s = (new StringBuilder("id='")).append(subnavisement.getId())
				.append("'").toString();
		boolean flag;
		if (getWritableDatabase().delete("subnav", s, null) > 0)
			flag = true;
		else
			flag = false;
		close();
		return flag;
	}

	/**
	 * 查询特定内容
	 * 
	 * @param paramInt
	 *            内容ID
	 * @return
	 */
	public Subnavisement get(int paramInt)
	{
		String str = "id='" + paramInt + "'";
		Cursor localCursor = getReadableDatabase().query(true, "subnav",
				columns, str, null, null, null, null, null);
		boolean bool = localCursor.moveToFirst();
		Subnavisement localSubnavisement = null;
		if (bool)
			localSubnavisement = constructSubnavisement(localCursor);
		localCursor.close();
		close();
		return localSubnavisement;
	}

	/**
	 * 查询所有数据
	 * 
	 * @return
	 */
	public List<HashMap<String, Object>> getAll()
	{
		Cursor localCursor = getReadableDatabase().query("subnav", columns,
				null, null, null, null, null, null);
		int i = localCursor.getCount();
		ArrayList localArrayList = null;
		if (i > 0)
		{
			localArrayList = new ArrayList();
			if (localCursor.moveToFirst())
				do
					localArrayList.add(constructSubnavisement(localCursor));
				while (localCursor.moveToNext());
			localCursor.close();
			close();
		}
		return _paresData(localArrayList);
	}

	/**
	 * 得到数据总条数
	 * 
	 * @return
	 */
	public boolean getCount()
	{
		Cursor cursor = getReadableDatabase().query(false, "subnav", columns,
				null, null, null, null, null, null);
		boolean flag;
		if (cursor.getCount() > 0)
		{
			cursor.close();
			flag = true;
		} else
		{
			cursor.close();
			flag = false;
		}
		return flag;
	}

	public Subnavisement getDiyFup(int paramInt)
	{
		String str = "fup='" + paramInt + "' and isset='2'";
		Cursor localCursor = getReadableDatabase().query(true, "subnav",
				columns, str, null, null, null, null, null);
		boolean bool = localCursor.moveToFirst();
		Subnavisement localSubnavisement = null;
		if (bool)
			localSubnavisement = constructSubnavisement(localCursor);
		localCursor.close();
		close();
		return localSubnavisement;
	}

	public List<HashMap<String, Object>> getFup(int paramInt)
	{
		String str = "fup='" + paramInt + "' and (isset='1' or isset='2')";
		Cursor localCursor = getReadableDatabase().query(false, "subnav",
				columns, str, null, null, null, "displayorder desc", "4");
		int i = localCursor.getCount();
		ArrayList localArrayList = null;
		if (i > 0)
		{
			localArrayList = new ArrayList();
			if (localCursor.moveToFirst())
				do
					localArrayList.add(constructSubnavisement(localCursor));
				while (localCursor.moveToNext());
		}
		localCursor.close();
		close();
		return _paresData(localArrayList);
	}

	public List<HashMap<String, Object>> getFupByisset(int paramInt)
	{
		String str = "fup='" + paramInt + "' and isset='0'";
		Cursor localCursor = getReadableDatabase().query(false, "subnav",
				columns, str, null, null, null, "displayorder desc", null);
		int i = localCursor.getCount();
		ArrayList localArrayList = null;
		if (i > 0)
		{
			localArrayList = new ArrayList();
			if (localCursor.moveToFirst())
				do
					localArrayList.add(constructSubnavisement(localCursor));
				while (localCursor.moveToNext());
			localCursor.close();
			close();
		}
		return _paresData(localArrayList);
	}

	public int getFupCount(int paramInt)
	{
		String str = "fup='" + paramInt + "'";
		Cursor localCursor = getReadableDatabase().query(false, "subnav",
				columns, str, null, null, null, "displayorder desc", null);
		int i = localCursor.getCount();
		localCursor.close();
		return i;
	}

	public ArrayList<Subnavisement> getFupDisplay(int paramInt)
	{
		String str = "fup='" + paramInt + "'";
		Cursor localCursor = getReadableDatabase().query(false, "subnav",
				columns, str, null, null, null, "displayorder desc", null);
		int i = localCursor.getCount();
		ArrayList localArrayList = null;
		if (i > 0)
		{
			localArrayList = new ArrayList();
			if (localCursor.moveToFirst())
				do
					localArrayList.add(constructSubnavisement(localCursor));
				while (localCursor.moveToNext());
			localCursor.close();
			close();
		}
		localCursor.close();
		close();
		return localArrayList;
	}

	public boolean save(Subnavisement subnavisement)
	{
		boolean flag;
		if (get(subnavisement.getId()) == null)
			flag = insert(subnavisement);
		else
			flag = update(subnavisement);
		return flag;
	}
}