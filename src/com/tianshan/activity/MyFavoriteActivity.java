package com.tianshan.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.tianshan.R;
import com.tianshan.ZhangWoApp;
import com.tianshan.activity.tab.SecondNavManager;
import com.tianshan.adapter.PhotoListAdapter;
import com.tianshan.dbhelper.FavoriteHelper;
import com.tianshan.model.PhotoInfo;
import com.tianshan.source.Core;
import com.tianshan.source.ShowMessage;
import com.tianshan.source.SiteTools;

public class MyFavoriteActivity extends Activity
{
	public static final int SHOWPiC = 111;
	public static final String TAG = "MyFavorite";
	private MyFavAdapter adapter = null;
	private Button btnBack = null;
	private int currentType = 1;
	private Cursor cursor = null;
	final FavoriteHelper dbHelper = FavoriteHelper.getInstance(this);
	private GridView grid;
	private AdapterView.OnItemClickListener griditemonclicklistener = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> paramAnonymousAdapterView,
				View paramAnonymousView, int paramAnonymousInt,
				long paramAnonymousLong)
		{
			Intent localIntent = new Intent();
			localIntent.setClass(
					MyFavoriteActivity.this.getApplicationContext(),
					PhotoViewActivity.class);
			localIntent.putExtra("pos", paramAnonymousInt);
			localIntent.putExtra("from", "save");
			localIntent.putExtra("type", MyFavoriteActivity.this.currentType);
			localIntent.putExtra("uid", MyFavoriteActivity.this.uid);
			MyFavoriteActivity.this.startActivityForResult(localIntent, 111);
		}
	};
	private ListView list;
	private boolean loadbit = false;
	private PhotoListAdapter mAdapter;
	private SecondNavManager.OnSecondNavBtnClick onSecondNavBtnClick = new SecondNavManager.OnSecondNavBtnClick()
	{
		public void onBtnMoreClick(int paramAnonymousInt)
		{}

		public void onClick(int i, int j)
		{
			if ((MyFavoriteActivity.this.cursor != null)
					&& (!MyFavoriteActivity.this.cursor.isClosed()))
				MyFavoriteActivity.this.cursor.close();
			if (j != 2)
			{
				currentType = j;
				MyFavoriteActivity myfavoriteactivity1 = MyFavoriteActivity.this;
				SQLiteDatabase sqlitedatabase1 = dbHelper.getWritableDatabase();
				String as1[] = new String[2];
				as1[0] = (new StringBuilder()).append(currentType).toString();
				as1[1] = (new StringBuilder()).append(uid).toString();
				myfavoriteactivity1.cursor = sqlitedatabase1.query("favorites",
						null, "news_type = ? AND news_uid = ?", as1, null,
						null, null);
				list.setVisibility(0);
				grid.setVisibility(8);
				if (cursor.getCount() > 0)
				{
					adapter = new MyFavAdapter(MyFavoriteActivity.this,
							0x7f030007, cursor, new String[0], new int[0]);
					list.setAdapter(adapter);
					list.setOnItemClickListener(onitemclicklistener);
				} else
				{
					list.setAdapter(null);
					ShowMessage.getInstance(MyFavoriteActivity.this)
							._showToast("您还没有收藏过该分类下的内容", 2);
				}
			} else
			{
				if (!loadbit)
				{
					currentType = j;
					MyFavoriteActivity myfavoriteactivity = MyFavoriteActivity.this;
					SQLiteDatabase sqlitedatabase = dbHelper
							.getWritableDatabase();
					String as[] = new String[2];
					as[0] = (new StringBuilder()).append(currentType)
							.toString();
					as[1] = (new StringBuilder()).append(uid).toString();
					myfavoriteactivity.cursor = sqlitedatabase.query(
							"favorites", null,
							"news_type = ? AND news_uid = ?", as, null, null,
							null);
					Log.d("sizie",
							(new StringBuilder(
									String.valueOf(cursor.getCount()))).append(
									"  =cursorsize").toString());
					if (cursor.getCount() > 0)
					{
						photos.clear();
						do
						{
							if (!cursor.moveToNext())
							{
								Log.d("sizie",
										(new StringBuilder(String
												.valueOf(photos.size())))
												.append("  = size").toString());
								cursor.close();
								list.setVisibility(8);
								grid.setVisibility(0);
								int k = getWindowManager().getDefaultDisplay()
										.getWidth();
								mAdapter = new PhotoListAdapter(
										getApplicationContext(), photos, k);
								grid.setAdapter(mAdapter);
								grid.setOnItemClickListener(griditemonclicklistener);
								loadbit = true;
							}
							String s = cursor.getString(cursor
									.getColumnIndex("news_id"));
							String s1 = cursor.getString(cursor
									.getColumnIndex("news_summary"));
							String s2 = (new StringBuilder(
									String.valueOf(Core
											._getPhotoCachePath(getApplicationContext()))))
									.append(s).append(".jpg").toString();
							PhotoInfo photoinfo = new PhotoInfo();
							photoinfo.setId(s);
							photoinfo.setTitle(s1);
							photoinfo.setImgurl(s2);
							photos.add(photoinfo);
						} while (true);
					}
					ShowMessage.getInstance(MyFavoriteActivity.this)
							._showToast("您还没有收藏过该分类下的内容", 2);
				} else
				{
					currentType = j;
					list.setVisibility(8);
					grid.setVisibility(0);
				}
			}
		}
	};
	private AdapterView.OnItemClickListener onitemclicklistener = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> paramAnonymousAdapterView,
				View paramAnonymousView, int paramAnonymousInt,
				long paramAnonymousLong)
		{
			int i = cursor.getPosition();
			cursor.moveToPosition(paramAnonymousInt);
			int j = cursor.getInt(cursor.getColumnIndex("news_type"));
			String str1 = cursor.getString(cursor.getColumnIndex("news_id"));
			String str2 = cursor.getString(cursor
					.getColumnIndex("news_summary"));
			cursor.moveToPosition(i);
			Intent localIntent = new Intent();
			switch (j)
			{
			case 2:
			default:
				return;
			case 1:
				localIntent.setClass(MyFavoriteActivity.this,
						NewsViewActivity.class);
				localIntent.putExtra("params", "{\"id\":" + str1 + "}");
				localIntent.putExtra("from", "save");
				localIntent.putExtra("currentType", currentType);
				localIntent.putExtra("position", paramAnonymousInt);
				String[] arrayOfString5 = new String[1];
				arrayOfString5[0] = ("ac=news&op=view&id=" + str1);
				localIntent.putExtra("url",
						SiteTools.getSiteUrl(arrayOfString5));
				startActivity(localIntent);
				break;
			case 3:
				localIntent.setClass(MyFavoriteActivity.this,
						VideoViewActivity.class);
				localIntent.putExtra("params", "{\"id\":" + str1 + "}");
				localIntent.putExtra("from", "save");
				localIntent.putExtra("currentType", currentType);
				localIntent.putExtra("position", paramAnonymousInt);
				String[] arrayOfString4 = new String[1];
				arrayOfString4[0] = ("ac=video&op=view&id=" + str1);
				localIntent.putExtra("url",
						SiteTools.getSiteUrl(arrayOfString4));
				startActivity(localIntent);
				break;
			case 4:
				localIntent.setClass(MyFavoriteActivity.this,
						ViewthreadViewActivity.class);
				localIntent.putExtra("params", "{\"tid\":" + str1 + "}");
				localIntent.putExtra("from", "save");
				localIntent.putExtra("currentType", currentType);
				localIntent.putExtra("position", paramAnonymousInt);
				String[] arrayOfString3 = new String[1];
				arrayOfString3[0] = ("ac=viewthread&tid=" + str1);
				localIntent.putExtra("url",
						SiteTools.getMobileUrl(arrayOfString3));
				startActivity(localIntent);
				break;
			case 5:
				localIntent.setClass(MyFavoriteActivity.this,
						ForumDisplayViewActivity.class);
				String[] arrayOfString1 = str1.split("\\|");
				localIntent.putExtra("params", "{\"fid\":" + arrayOfString1[0]
						+ ",\"fup\":" + arrayOfString1[1] + ",\"forumname\":"
						+ str2 + "}");
				String[] arrayOfString2 = new String[1];
				arrayOfString2[0] = ("ac=forumdisplay&fid=" + arrayOfString1[0]);
				localIntent.putExtra("url",
						SiteTools.getMobileUrl(arrayOfString2));
				startActivity(localIntent);
				break;
			}
		}
	};
	private ArrayList<PhotoInfo> photos = new ArrayList();
	private SecondNavManager secondNavManager;
	private TextView tvTitle = null;
	public long uid = 0L;

	private void _init()
	{
		findViewById(R.id.nav_commit).setVisibility(8);
		this.tvTitle = ((TextView) findViewById(R.id.nav_title));
		this.tvTitle.setText(R.string.STR_MY_SAVED);
		this.btnBack = ((Button) findViewById(R.id.nav_back));
		this.btnBack.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{
				closeMe();
			}
		});
		this.list = ((ListView) findViewById(R.id.itemlist));
		this.grid = ((GridView) findViewById(R.id.photogrid));
		this.secondNavManager = new SecondNavManager(this);
		this.secondNavManager.SetOnSecondNavBtnClick(this.onSecondNavBtnClick);
		this.secondNavManager.FillNavDataByType(6);
		this.secondNavManager.ClickBtnByIndex(0);
		this.loadbit = false;
	}

	private void closeMe()
	{
		if ((this.cursor != null) && (!this.cursor.isClosed()))
			this.cursor.close();
		finish();
	}

	protected void onActivityResult(int i, int j, Intent intent)
	{
		if (j == -1 && i == 111)
		{

			SQLiteDatabase sqlitedatabase = dbHelper.getWritableDatabase();
			String as[] = new String[2];
			as[0] = (new StringBuilder()).append(currentType).toString();
			as[1] = (new StringBuilder()).append(uid).toString();
			cursor = sqlitedatabase.query("favorites", null,
					"news_type = ? AND news_uid = ?", as, null, null, null);
			Log.d("sizie",
					(new StringBuilder(String.valueOf(cursor.getCount())))
							.append("  =cursorsize").toString());
			photos.clear();
			while (cursor.moveToNext())
			{
				String s = cursor.getString(cursor.getColumnIndex("news_id"));
				String s1 = cursor.getString(cursor
						.getColumnIndex("news_summary"));
				String s2 = (new StringBuilder(String.valueOf(Core
						._getPhotoCachePath(getApplicationContext()))))
						.append(s).append(".jpg").toString();
				PhotoInfo photoinfo = new PhotoInfo();
				photoinfo.setId(s);
				photoinfo.setTitle(s1);
				photoinfo.setImgurl(s2);
				photos.add(photoinfo);
			}
			Log.d("sizie", (new StringBuilder(String.valueOf(photos.size())))
					.append("  = size").toString());
			cursor.close();
			list.setVisibility(8);
			grid.setVisibility(0);
			int k = getWindowManager().getDefaultDisplay().getWidth();
			mAdapter = new PhotoListAdapter(getApplicationContext(), photos, k);
			grid.setAdapter(mAdapter);
			grid.setOnItemClickListener(griditemonclicklistener);
		}
		super.onActivityResult(i, j, intent);
		return;
	}

	public void onCreate(Bundle paramBundle)
	{
		if (ZhangWoApp.getInstance().isLogin())
			this.uid = ZhangWoApp.getInstance().getUserSession().getUid();
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		setContentView(R.layout.my_favorite);
		Intent localIntent = getIntent();
		if (localIntent != null)
			this.currentType = localIntent.getIntExtra("MyFavorite", 0);
		_init();
	}

	public boolean onKeyDown(int i, KeyEvent keyevent)
	{
		boolean flag;
		if (i == 4)
		{
			closeMe();
			flag = true;
		} else if (i == 82)
			flag = false;
		else
			flag = super.onKeyDown(i, keyevent);
		return flag;
	}

	protected void onStop()
	{
		recycleBitmap();
		super.onPause();
	}

	public void recycleBitmap()
	{
		int i = 0;
		do
		{
			if (i >= photos.size())
			{
				photos.clear();
				return;
			}
			if (((PhotoInfo) photos.get(i)).getBit() != null)
			{
				Log.d("recycle",
						(new StringBuilder(String.valueOf(i))).toString());
				((PhotoInfo) photos.get(i)).getBit().recycle();
				((PhotoInfo) photos.get(i)).setBit(null);
			}
			i++;
		} while (true);
	}

	public class MyFavAdapter extends SimpleCursorAdapter
	{
		public MyFavAdapter(Context context, int i, Cursor cursor1,
				String as[], int ai[])
		{
			super(context, i, cursor1, as, ai);
		}

		public void bindView(View view, Context context, Cursor cursor1)
		{
			((TextView) view.findViewById(R.id.tv_summary)).setText(cursor1
					.getString(cursor1.getColumnIndex("news_summary")));
			if (cursor1.getPosition() % 2 == 0)
				view.setBackgroundColor(Color.rgb(245, 245, 245));
			else
				view.setBackgroundColor(Color.rgb(255, 255, 255));
		}
	}
}