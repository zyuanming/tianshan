package com.tianshan.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianshan.R;
import com.tianshan.source.Core;
import com.tianshan.source.HttpRequest;

public class SelectAlbumActivity extends Activity implements
		View.OnClickListener
{
	final Handler imgHandler = new Handler()
	{
		public void handleMessage(Message message)
		{
			Bitmap localBitmap1 = BitmapFactory.decodeResource(
					SelectAlbumActivity.this.getResources(),
					R.drawable.lastpic_bg);
			switch (message.what)
			{
			default:
				super.handleMessage(message);
				return;
			case 1:
				Bitmap localBitmap3 = createNewBitmap((Bitmap) message.obj,
						localBitmap1);
				prealbumImg.setImageBitmap(localBitmap3);
				break;
			case 2:
				Bitmap localBitmap2 = createNewBitmap((Bitmap) message.obj,
						localBitmap1);
				nextalbumImg.setImageBitmap(localBitmap2);
				break;
			}
		}
	};
	private String nextAlbumId;
	private ImageView nextalbumImg;
	private String preAlbumId;
	private ImageView prealbumImg;

	private Bitmap createNewBitmap(Bitmap paramBitmap1, Bitmap paramBitmap2)
	{
		Bitmap localBitmap = Bitmap.createBitmap(paramBitmap2.getWidth(),
				paramBitmap2.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas localCanvas = new Canvas(localBitmap);
		localCanvas.drawBitmap(paramBitmap2, 0.0F, 0.0F, null);
		localCanvas.drawBitmap(paramBitmap1, 0.0F, 0.0F, null);
		localCanvas.save(31);
		localCanvas.restore();
		return localBitmap;
	}

	private void getImg(String s, final Handler handler, String s1,
			final int what)
	{
		try
		{
			final String imgPath = (new StringBuilder(String.valueOf(Core
					._getPhotoCachePath(getApplicationContext())))).append(s1)
					.append("album").toString();
			if ((new File(imgPath)).exists())
				handler.obtainMessage(
						what,
						scaleBitmap(Bitmap.createBitmap(BitmapFactory
								.decodeFile(imgPath)), 200, 150))
						.sendToTarget();
			else if (!"".equals(s))
				(new HttpRequest())._getFile(s, null, null,
						new com.tianshan.source.HttpRequest.requestCallBack()
						{

							public void download(Object obj)
							{
								try
								{
									InputStream inputstream = (InputStream) obj;
									FileOutputStream fileoutputstream = new FileOutputStream(
											imgPath);
									byte abyte0[] = new byte[1024];
									do
									{
										int i = inputstream.read(abyte0);
										if (i == -1)
										{
											fileoutputstream.close();
											inputstream.close();
											Bitmap bitmap = SelectAlbumActivity.scaleBitmap(
													Bitmap.createBitmap(BitmapFactory
															.decodeFile(imgPath)),
													200, 150);
											Log.d("bmp",
													(new StringBuilder(
															String.valueOf(bitmap
																	.getWidth())))
															.append(bitmap
																	.getHeight())
															.toString());
											if (handler != null)
												handler.obtainMessage(what,
														bitmap).sendToTarget();
											break;
										}
										fileoutputstream.write(abyte0, 0, i);
									} while (true);
								} catch (FileNotFoundException filenotfoundexception)
								{
									filenotfoundexception.printStackTrace();
								} catch (IOException ioexception)
								{
									ioexception.printStackTrace();
								}
							}
						});
		} catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

	private void initUI(Bundle bundle)
	{
		Button button = (Button) findViewById(R.id.album_back);
		this.prealbumImg = ((ImageView) findViewById(R.id.prealbum));
		this.nextalbumImg = ((ImageView) findViewById(R.id.nextalbum));
		TextView textview = (TextView) findViewById(R.id.pretext);
		TextView textview1 = (TextView) findViewById(R.id.nexttext);
		Button button1 = (Button) findViewById(R.id.prealbum_btn);
		Button button2 = (Button) findViewById(R.id.nextalbum_btn);
		this.prealbumImg.setOnClickListener(this);
		this.nextalbumImg.setOnClickListener(this);
		button.setOnClickListener(this);
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		if (bundle.getBoolean("haspre"))
		{
			textview.setText(bundle.getString("prealbumName"));
			button1.setBackgroundResource(0x7f020066);
			preAlbumId = bundle.getString("prealbumId");
			getImg(bundle.getString("prealbumImg"), imgHandler,
					bundle.getString("prealbumId"), 1);
		} else
		{
			prealbumImg.setBackgroundResource(0x7f020064);
			button1.setBackgroundResource(0x7f020065);
			textview.setText("");
			prealbumImg.setClickable(false);
			button1.setClickable(false);
		}
		if (bundle.getBoolean("hasnext"))
		{
			textview1.setText(bundle.getString("nextalbumName"));
			button2.setBackgroundResource(0x7f02005c);
			nextAlbumId = bundle.getString("nextalbumId");
			getImg(bundle.getString("nextalbumImg"), imgHandler,
					bundle.getString("nextalbumId"), 2);
		} else
		{
			nextalbumImg.setBackgroundResource(0x7f02005a);
			button2.setBackgroundResource(0x7f02005b);
			textview1.setText("");
			nextalbumImg.setClickable(false);
			button2.setClickable(false);
		}
	}

	public static Bitmap scaleBitmap(Bitmap bitmap, int i, int j)
	{
		Bitmap bitmap1;
		if (bitmap == null && i < 1 && j < 1)
		{
			bitmap1 = null;
		} else
		{
			int k = bitmap.getWidth();
			int l = bitmap.getHeight();
			float f = (1.0F * (float) j) / (float) l;
			float f1 = (1.0F * (float) i) / (float) k;
			float f2;
			Matrix matrix;
			if (f > 1.0F && f > 1.0F)
				f2 = Math.max(f, f1);
			else
				f2 = Math.min(f, f1);
			matrix = new Matrix();
			matrix.postScale(f2, f2);
			bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, k, l, matrix, true);
		}
		return bitmap1;
	}

	public void onClick(View paramView)
	{
		switch (paramView.getId())
		{
		case R.id.text:
		case R.id.albumimg:
		case R.id.pretext:
		case R.id.nexttext:
		default:
			return;
		case R.id.album_back:
			setResult(-1, new Intent(this, PhotoViewActivity.class));
			finish();
			break;
		case R.id.prealbum_btn:
			Intent localIntent4 = new Intent(this, PhotoViewActivity.class);
			Bundle localBundle4 = new Bundle();
			localBundle4.putString("albumId", this.preAlbumId);
			localIntent4.putExtras(localBundle4);
			setResult(-1, localIntent4);
			finish();
			break;
		case R.id.nextalbum_btn:
			Intent localIntent3 = new Intent(this, PhotoViewActivity.class);
			Bundle localBundle3 = new Bundle();
			localBundle3.putString("albumId", this.nextAlbumId);
			localIntent3.putExtras(localBundle3);
			setResult(-1, localIntent3);
			finish();
			break;
		case R.id.prealbum:
			Intent localIntent2 = new Intent(this, PhotoViewActivity.class);
			Bundle localBundle2 = new Bundle();
			localBundle2.putString("albumId", this.preAlbumId);
			localIntent2.putExtras(localBundle2);
			setResult(-1, localIntent2);
			finish();
			break;
		case R.id.nextalbum:
			Intent localIntent1 = new Intent(this, PhotoViewActivity.class);
			Bundle localBundle1 = new Bundle();
			localBundle1.putString("albumId", this.nextAlbumId);
			localIntent1.putExtras(localBundle1);
			setResult(-1, localIntent1);
			finish();
			break;
		}
	}

	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		getWindow().setFlags(1024, 1024);
		requestWindowFeature(1);
		setContentView(R.layout.select_album_dialog);
		initUI(getIntent().getExtras());
	}
}