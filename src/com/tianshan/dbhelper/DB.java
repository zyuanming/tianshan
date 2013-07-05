package com.tianshan.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.tianshan.source.install.installSql;
import java.util.ArrayList;

public abstract class DB extends SQLiteOpenHelper
{
	public static final String DB_NAME = "tianshan.db";
	public static final int VERSION = 4;

	protected DB(Context paramContext)
	{
		super(paramContext, "tianshan.db", null, 4);
	}

	private void onUpgrade_save_cache(SQLiteDatabase paramSQLiteDatabase)
	{
		ArrayList localArrayList = installSql._initMoveTable();
		if (localArrayList != null)
		{
			for (int i = 0; i < localArrayList.size(); i++)
			{
				paramSQLiteDatabase.execSQL((String) localArrayList.get(i));
			}
		}
	}

	public void onCreate(SQLiteDatabase paramSQLiteDatabase)
	{
		ArrayList localArrayList = installSql._initCreate();
		if (localArrayList != null)
		{
			for (int i = 0; i < localArrayList.size(); i++)
			{
				paramSQLiteDatabase.execSQL(((StringBuffer) localArrayList
						.get(i)).toString());
			}
		}
	}

	public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1,
			int paramInt2)
	{
		onUpgrade_save_cache(paramSQLiteDatabase);
		ArrayList localArrayList = installSql._initDrop();
		if (localArrayList != null)
		{
			for (int i = 0; i < localArrayList.size(); i++)
			{
				paramSQLiteDatabase.execSQL((String) localArrayList.get(i));
			}
		}
		onCreate(paramSQLiteDatabase);
		installSql._recover_table(paramSQLiteDatabase);
	}
}