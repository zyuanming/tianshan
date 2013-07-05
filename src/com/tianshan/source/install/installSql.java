package com.tianshan.source.install;

import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;

public class installSql
{
	private static StringBuffer _createAdvertisement()
	{
		StringBuffer localStringBuffer = new StringBuffer();
		localStringBuffer
				.append("Create table IF NOT EXISTS advertisement ( adid integer primary key autoincrement ,")
				.append("starttime varchar,").append("endtime varchar,")
				.append("updatetime varchar").append(");");
		return localStringBuffer;
	}

	private static StringBuffer _createFavorites()
	{
		StringBuffer localStringBuffer = new StringBuffer();
		localStringBuffer
				.append("Create table IF NOT EXISTS favorites ( _id integer primary key autoincrement ,")
				.append("news_type int,").append("news_uid int,")
				.append("news_id text,").append("news_summary text")
				.append(");");
		return localStringBuffer;
	}

	private static StringBuffer _createSetting()
	{
		StringBuffer localStringBuffer = new StringBuffer();
		localStringBuffer
				.append("Create table IF NOT EXISTS common_setting ( _id integer primary key autoincrement ,")
				.append("skey text,").append("svalue text").append(");");
		localStringBuffer.append("CREATE INDEX skey on common_setting(skey);");
		return localStringBuffer;
	}

	private static StringBuffer _createShareBind()
	{
		StringBuffer localStringBuffer = new StringBuffer();
		localStringBuffer
				.append("Create table IF NOT EXISTS share_bind ( id integer primary key autoincrement ,")
				.append("typename varchar,").append("access_token varchar,")
				.append("expires_in varchar,").append("open_id varchar,")
				.append("keyword1 varchar,").append("keyword2 varchar,")
				.append("keyword3 varchar,").append("keyword4 varchar,")
				.append("keyword5 varchar").append(");");
		return localStringBuffer;
	}

	private static StringBuffer _createSubnavisement()
	{
		StringBuffer localStringBuffer = new StringBuffer();
		localStringBuffer
				.append("Create table IF NOT EXISTS subnav ( adid integer primary key autoincrement ,")
				.append("id varchar,").append("fup varchar,")
				.append("name varchar,").append("displayorder varchar,")
				.append("isset varchar").append(");");
		return localStringBuffer;
	}

	private static StringBuffer _createUserSession()
	{
		StringBuffer localStringBuffer = new StringBuffer();
		localStringBuffer
				.append("Create table IF NOT EXISTS common_usersession (_id integer primary key autoincrement ,")
				.append("uid integer,").append("username text,")
				.append("groupid integer, ").append("saltkey text,")
				.append("auth text,").append("dateline integer,")
				.append("formhash text,").append("mobile_auth text,")
				.append("webviewcookies text").append(");");
		localStringBuffer
				.append("CREATE INDEX uid on common_usersession (uid);");
		return localStringBuffer;
	}

	private static StringBuffer _fillShareBind()
	{
		StringBuffer localStringBuffer = new StringBuffer();
		localStringBuffer
				.append("insert into share_bind (typename) select('腾讯微博') ");
		localStringBuffer.append("union select('QQ空间') ");
		localStringBuffer.append("union select('新浪微博') ");
		localStringBuffer.append("union select('人人网') ");
		return localStringBuffer;
	}

	public static ArrayList<StringBuffer> _initCreate()
	{
		ArrayList localArrayList = new ArrayList();
		localArrayList.add(_createSetting());
		localArrayList.add(_createAdvertisement());
		localArrayList.add(_createSubnavisement());
		localArrayList.add(_createFavorites());
		localArrayList.add(_createUserSession());
		localArrayList.add(_createShareBind());
		localArrayList.add(_fillShareBind());
		return localArrayList;
	}

	public static ArrayList<String> _initDrop()
	{
		return new ArrayList();
	}

	public static ArrayList<String> _initMoveTable()
	{
		return null;
	}

	public static void _recover_table(SQLiteDatabase paramSQLiteDatabase)
	{}
}