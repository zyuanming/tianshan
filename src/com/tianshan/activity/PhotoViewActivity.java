package com.tianshan.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tianshan.R;
import com.tianshan.ZhangWoApp;
import com.tianshan.adapter.PhotoPagerAdapter;
import com.tianshan.adapter.ShareAdapter;
import com.tianshan.dbhelper.FavoriteHelper;
import com.tianshan.model.PhotoInfo;
import com.tianshan.source.Core;
import com.tianshan.source.DEBUG;
import com.tianshan.source.HttpRequest;
import com.tianshan.source.SiteTools;
import com.tianshan.source.activity.BaseActivity;
import com.tianshan.source.view.PhotoNavbar;

public class PhotoViewActivity extends BaseActivity implements
		PhotoNavbar.OnPhotoNavigateListener, View.OnClickListener
{
	public static int SELECTALBUM = 110;
	private PhotoPagerAdapter adapter;
	private String albumId;
	private int backToNav = 0;
	private ImageView btnFavImageView;
	private View btnFavourite;
	private View btnSave;
	private View btnShare;
	private Bundle bundle;
	final FavoriteHelper dbHelper = FavoriteHelper.getInstance(this);
	private boolean dialogshow = false;
	private int down_x;
	private boolean first = false;
	private String from = "";
	private boolean hasnext = false;
	private boolean haspre = false;
	private ViewPager.OnPageChangeListener intfacePageChange;
	private boolean isFavourite = false;
	private JSONArray list;
	private int move_x;
	private PhotoNavbar navbar;
	protected LinearLayout navbarbox;
	private JSONObject nextInfo;
	private ViewPager photoPager;
	private ProgressBar photoProgress;
	private int photoSize;
	private TextView photoTitle;
	private List<PhotoInfo> photos;
	private FrameLayout photoview;
	private JSONObject preInfo;
	private boolean second = false;
	private int selected_id = 0;
	protected LinearLayout toolbox;
	private long uid;

	private List _getPhotoList(String s)
	{
		ArrayList arraylist = null;
		try
		{
			HttpRequest httprequest = new HttpRequest();
			String s1 = httprequest._get(url);
			DEBUG.o("*****get photo list****");
			if (s1 != null)
			{
				JSONObject jsonobject;
				Log.d("json",
						(new StringBuilder(String.valueOf(s1))).append(
								"  = result").toString());
				jsonobject = new JSONObject(s1);
				if (jsonobject != null)
				{
					JSONArray jsonarray;
					JSONObject jsonobject1 = jsonobject.optJSONObject("res");
					list = jsonobject1.optJSONArray("list");
					preInfo = jsonobject1.optJSONObject("preinfo");
					nextInfo = jsonobject1.optJSONObject("nextinfo");
					jsonarray = list;
					if (jsonarray != null)
					{
						ArrayList arraylist1 = new ArrayList();
						try
						{
							photoSize = list.length();
							for (int i = 0; i < photoSize; i++)
							{
								arraylist1.add(new PhotoInfo(list
										.optJSONObject(i)));
							}
							arraylist = arraylist1;
						} catch (Exception e)
						{
							arraylist = arraylist1;
							e.printStackTrace();
						}
						return arraylist;
					}

				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return arraylist;
	}

	private void _initProgress()
	{
		this.photoview = ((FrameLayout) findViewById(R.id.zhangwobox));
		this.photoview.setBackgroundColor(0);
		this.photoProgress = new ProgressBar(this);
		this.photoProgress.setBackgroundColor(0);
		this.photoProgress.setIndeterminateDrawable(getResources().getDrawable(
				R.drawable.progress_blue_move));
		this.photoProgress.setIndeterminate(false);
		FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(
				-2, -2, 17);
		this.photoview.addView(this.photoProgress, localLayoutParams);
		this.photoProgress.setVisibility(8);
	}

	private boolean getDirection(int i, int j)
	{
		boolean flag;
		if (i > j)
			flag = true;
		else
			flag = false;
		return flag;
	}

	private void goToNav()
	{
		Intent localIntent = new Intent();
		localIntent.setClass(this, NavigationBar.class);
		localIntent.setFlags(335544320);
		PendingIntent localPendingIntent = PendingIntent.getActivity(this, 100,
				localIntent, 134217728);
		try
		{
			localPendingIntent.send();
			finish();
		} catch (PendingIntent.CanceledException localCanceledException)
		{
			localCanceledException.printStackTrace();
		}
	}

	private void popupLoginDialog()
	{
		new AlertDialog.Builder(this)
				.setTitle(R.string.message_notice_nologin)
				.setPositiveButton(R.string.nav_btn_login,
						new DialogInterface.OnClickListener()
						{
							public void onClick(
									DialogInterface paramAnonymousDialogInterface,
									int paramAnonymousInt)
							{
								DEBUG.o("ok");
								Intent localIntent = new Intent(
										PhotoViewActivity.this,
										LoginActivity.class);
								startActivity(localIntent);
							}
						})
				.setNegativeButton(17039360,
						new DialogInterface.OnClickListener()
						{
							public void onClick(
									DialogInterface paramAnonymousDialogInterface,
									int paramAnonymousInt)
							{
								paramAnonymousDialogInterface.dismiss();
								DEBUG.o("no");
							}
						}).create().show();
	}

	protected void _initNavBar(boolean paramBoolean)
	{
		this.navbarbox = ((LinearLayout) findViewById(R.id.navbar_box));
		this.navbarbox.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{}
		});
		if (paramBoolean)
			this.navbarbox.setVisibility(0);
		else
			this.navbarbox.setVisibility(4);
		this.navbar = new PhotoNavbar(this, this.navbarbox);
		this.navbar.setCounterVisibility(true);
		this.navbar.setOnPhotoNavigate(this);
		this.intfacePageChange.onPageScrolled(0, 0.0F, 0);
		this.navbar.setCounter(1 + this.selected_id, this.photoSize);
		this.photoTitle.setText(((PhotoInfo) this.photos.get(this.selected_id))
				.getTitle());
	}

	protected void _initToolBar(boolean paramBoolean)
	{
		this.toolbox = ((LinearLayout) findViewById(R.id.tool_box));
		this.toolbox.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View paramAnonymousView)
			{}
		});
		if (paramBoolean)
			this.toolbox.setVisibility(0);
		else
			this.toolbox.setVisibility(4);
		View localView = LayoutInflater.from(this).inflate(
				R.layout.photo_detail_bottom, null);
		this.photoTitle = ((TextView) localView.findViewById(R.id.photo_title));
		this.btnSave = localView.findViewById(R.id.btn_save);
		this.btnShare = localView.findViewById(R.id.btn_share);
		this.btnFavourite = localView.findViewById(R.id.btn_favourite);
		this.btnFavImageView = ((ImageView) localView
				.findViewById(R.id.btn_fav_image));
		this.btnSave.setClickable(true);
		this.btnShare.setClickable(true);
		this.btnFavourite.setClickable(true);
		this.btnSave.setOnClickListener(this);
		this.btnShare.setOnClickListener(this);
		this.btnFavourite.setOnClickListener(this);
		if ((this.from.equals("news")) || (this.from.equals("board"))
				|| (this.from.equals("forum")))
		{
			this.btnSave.setVisibility(4);
			this.btnFavourite.setVisibility(4);
			((ImageView) this.btnShare.findViewById(R.id.save_img))
					.setImageResource(R.drawable.nav_save);
			((TextView) this.btnShare.findViewById(R.id.save_text))
					.setText("保存");
			this.btnShare.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View paramAnonymousView)
				{
					saveImg(selected_id);
				}
			});
		}
		FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(
				-1, -2);
		this.toolbox.addView(localView, localLayoutParams);
	}

	public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
	{
		switch (paramMotionEvent.getAction())
		{
		case 1:
		default:
			break;
		case 0:
			this.down_x = ((int) paramMotionEvent.getX());
			break;
		case 2:
			this.move_x = ((int) paramMotionEvent.getX());
			break;
		}
		return super.dispatchTouchEvent(paramMotionEvent);
	}

	public boolean getisFavourite()
	{
		if (ZhangWoApp.getInstance().isLogin())
		{
			Log.i("****uid*****", "uid=" + this.uid);
			Log.i("****selected_id*****", "selected_id=" + this.selected_id);
			Log.i("****photos size*****", "photos size=" + this.photos.size());
			if (this.dbHelper.isInFavorites(2,
					((PhotoInfo) this.photos.get(this.selected_id)).getId(),
					this.uid))
			{
				this.isFavourite = true;
				this.btnFavImageView.setImageResource(R.drawable.nav_collect4);
			}
		}
		if ((!ZhangWoApp.getInstance().isLogin()) || (!this.isFavourite))
		{
			this.isFavourite = false;
			this.btnFavImageView.setImageResource(R.drawable.nav_collect2);
		}
		return this.isFavourite;
	}

	protected void initWidget()
	{
		super.initWidget();
		this.adapter = new PhotoPagerAdapter(this);
		if (this.photos != null)
			this.adapter.setPhotoList(this.photos);
		this.photoPager = ((ViewPager) findViewById(R.id.view_box));
		this.photoPager.setAdapter(this.adapter);
		if (this.from.equals("save"))
			this.photoPager.setCurrentItem(this.selected_id);
		else
			this.photoPager.setCurrentItem(0);
		this.photoPager.setOnPageChangeListener(this.intfacePageChange);
	}

	protected void onActivityResult(int i, int j, Intent intent)
	{
		super.onActivityResult(i, j, intent);
		if (j == -1 && i == SELECTALBUM)
		{
			Bundle bundle1 = intent.getExtras();
			if (bundle1 == null)
			{
				if (photoSize > 1)
				{
					second = false;
					dialogshow = false;
				} else if (photoSize == 1)
				{
					first = false;
					second = false;
					dialogshow = false;
				}
			} else
			{
				String s = bundle1.getString("albumId");
				Intent intent1 = new Intent();
				intent1.setClass(this, PhotoViewActivity.class);
				intent1.putExtra("album_id",
						(new StringBuilder(String.valueOf(s))).toString());
				intent1.putExtra("from", "album");
				startActivity(intent1);
				finish();
			}
		}
	}

	public void onBack()
	{
		if (backToNav == 1)
			goToNav();
		else if (from.equals("save"))
		{
			setResult(-1, new Intent(getApplicationContext(),
					MyFavoriteActivity.class));
			finish();
		} else
		{
			finish();
		}
	}

	public void onClick(View paramView)
	{
		switch (paramView.getId())
		{
		default:
			return;
		case R.id.btn_save:
			saveImg(this.selected_id);
			break;
		case R.id.btn_share:
			Intent localIntent = new Intent();
			localIntent.putExtra("type", "图片");
			localIntent.putExtra("title",
					((PhotoInfo) this.photos.get(this.selected_id)).getTitle());
			localIntent
					.putExtra("pcurl", ((PhotoInfo) this.photos
							.get(this.selected_id)).getImgurl());
			new ShareAdapter(this).createShareDialog(localIntent);
			break;
		case R.id.btn_favourite:
			if (getisFavourite())
				setisFavourite(false);
			else
				setisFavourite(true);
			break;
		}
	}

	public void onCommit()
	{}

	protected void onCreate(Bundle bundle1)
	{
		super.onCreate(bundle1);
		getWindow().setFlags(1024, 1024);
		requestWindowFeature(1);
		setContentView(0x7f030015);
		_initProgress();
		if (ZhangWoApp.getInstance().isLogin())
			uid = ZhangWoApp.getInstance().getUserSession().getUid();
		bundle = getIntent().getExtras();
		(new Thread(new Runnable()
		{

			public void run()
			{
				photoProgress.setVisibility(0);
				if (bundle != null)
				{
					Cursor cursor;
					if ("pushmsg".equals(bundle.getString("from")))
					{
						backToNav = 1;
						DEBUG.o(bundle.getString("from"));
					}
					if (!from.equals("save"))
						albumId = bundle.getString("album_id");
					from = bundle.getString("from");
					Message message;
					int i;
					long l;
					SQLiteDatabase sqlitedatabase;
					String as1[];
					if (from.equals("news"))
					{
						PhotoViewActivity photoviewactivity3 = PhotoViewActivity.this;
						String as4[] = new String[2];
						as4[0] = "ac=newsalbum";
						as4[1] = (new StringBuilder("id=")).append(albumId)
								.toString();
						photoviewactivity3.url = SiteTools.getApiUrl(as4);
					} else if (from.equals("album"))
					{
						PhotoViewActivity photoviewactivity2 = PhotoViewActivity.this;
						String as3[] = new String[2];
						as3[0] = "ac=album";
						as3[1] = (new StringBuilder("id=")).append(albumId)
								.toString();
						photoviewactivity2.url = SiteTools.getApiUrl(as3);
					} else if (from.equals("board"))
					{
						PhotoViewActivity photoviewactivity1 = PhotoViewActivity.this;
						String as2[] = new String[2];
						as2[0] = "ac=boardpic";
						as2[1] = (new StringBuilder("id=")).append(albumId)
								.toString();
						photoviewactivity1.url = SiteTools.getApiUrl(as2);
					} else if (from.equals("forum"))
					{
						PhotoViewActivity photoviewactivity = PhotoViewActivity.this;
						String as[] = new String[2];
						as[0] = "ac=threadpic";
						as[1] = (new StringBuilder("id=")).append(albumId)
								.toString();
						photoviewactivity.url = SiteTools.getApiUrl(as);
					}
					if (from.equals("save"))
					{
						i = bundle.getInt("type");
						l = bundle.getLong("uid");
						selected_id = Integer.parseInt(bundle.getString("pos"));
						Log.d("sizie", (new StringBuilder(String.valueOf(i)))
								.append("  =type").toString());
						Log.d("sizie", (new StringBuilder(String.valueOf(l)))
								.append("  =uid").toString());
						sqlitedatabase = dbHelper.getWritableDatabase();
						as1 = new String[2];
						as1[0] = (new StringBuilder()).append(i).toString();
						as1[1] = (new StringBuilder()).append(l).toString();
						cursor = sqlitedatabase.query("favorites", null,
								"news_type = ? AND news_uid = ?", as1, null,
								null, null);
						Log.d("sizie",
								(new StringBuilder(String.valueOf(cursor
										.getCount()))).append("  =cursorsize")
										.toString());
						photos = new ArrayList();
						photos.clear();

						while (cursor.moveToNext())
						{
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
						}
						photos = _getPhotoList(albumId);
						message = new Message();
						message.what = 1;
						handler.sendMessage(message);
						cursor.close();
						photoSize = photos.size();
					}
				}
			}
		})).start();
	}

	protected void onDestroy()
	{
		super.onDestroy();
		this.adapter.recycleBitmap();
	}

	public boolean onKeyDown(int i, KeyEvent keyevent)
	{
		boolean flag = true;
		if (i != 4 || keyevent.getRepeatCount() != 0)
		{
			if (i == 82)
				flag = false;
		} else
		{
			if (backToNav == 1)
				goToNav();
			else if (from.equals("save"))
			{
				setResult(-1, new Intent(getApplicationContext(),
						MyFavoriteActivity.class));
				finish();
			} else
			{
				finish();
			}
		}
		return flag;
	}

	public void saveImg(int i)
	{
		PhotoInfo photoinfo = (PhotoInfo) photos.get(i);
		String s = (new StringBuilder(String.valueOf((new StringBuilder(String
				.valueOf(Core._getPhotosPath(this)))).append(photoinfo.getId())
				.toString()))).append(".jpg").toString();
		Log.d("save", s);
		if (!(new File(s)).exists())
		{
			File file = new File((new StringBuilder(String.valueOf(Core
					._getPhotoCachePath(this)))).append(photoinfo.getId())
					.append(".jpg").toString());
			try
			{
				FileInputStream fileinputstream = new FileInputStream(file);
				FileOutputStream fileoutputstream = new FileOutputStream(s);
				byte abyte0[] = new byte[1024];
				do
				{
					int j = fileinputstream.read(abyte0);
					if (j == -1)
					{
						Toast.makeText(this, "保存成功", 0).show();
						fileoutputstream.close();
						fileinputstream.close();
						break;
					}
					fileoutputstream.write(abyte0, 0, j);
				} while (true);
			} catch (FileNotFoundException filenotfoundexception)
			{
				filenotfoundexception.printStackTrace();
			} catch (IOException ioexception)
			{
				ioexception.printStackTrace();
			}
		} else
		{
			Toast.makeText(this, "该图片已经保存过了", 0).show();
		}
	}

	public void setisFavourite(boolean flag)
	{
		if (ZhangWoApp.getInstance().isLogin())
		{
			isFavourite = flag;
			String s = ((PhotoInfo) photos.get(selected_id)).getId();
			if (isFavourite)
			{
				btnFavImageView.setImageResource(0x7f020043);
				String s1 = ((PhotoInfo) photos.get(selected_id)).getTitle();
				Log.d("title", s1);
				dbHelper.insert(2, s, s1, uid);
			} else
			{
				btnFavImageView.setImageResource(0x7f020042);
				dbHelper.delete(2, s, uid);
			}
		} else
		{
			popupLoginDialog();
		}
	}
}