package com.tianshan.adapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.tianshan.model.PhotoInfo;
import com.tianshan.source.BitmapUtil;
import com.tianshan.source.Core;
import com.tianshan.source.HttpRequest;
import com.tianshan.source.activity.BaseActivity;
import com.tianshan.source.popupwindow.ImageDialog;

public class PhotoPagerAdapter extends PagerAdapter implements
		View.OnClickListener
{
	private final int FINISH = 0;
	private BaseActivity context;
	private HashMap<String, SoftReference<Bitmap>> imageList;
	private List<PhotoInfo> photoList;
	private boolean show = false;
	private Bitmap showbit;
	private ArrayList<ImageView> viewList;

	public PhotoPagerAdapter(BaseActivity paramBaseActivity)
	{
		this.context = paramBaseActivity;
		this.viewList = new ArrayList();
		this.photoList = new ArrayList();
		this.imageList = new HashMap();
	}

	private void getImg(PhotoInfo photoinfo, final Handler handler)
	{
		final String photoId;
		final String imgPath;
		try
		{
			System.gc();
			System.gc();
			System.gc();
			photoId = photoinfo.getId();
			if (imageList.containsKey(photoId))
			{
				SoftReference softreference = (SoftReference) imageList
						.get(photoId);
				if (softreference.get() != null)
					handler.obtainMessage(0, softreference.get())
							.sendToTarget();
			}
			imgPath = (new StringBuilder(String.valueOf(Core
					._getPhotoCachePath(context)))).append(photoinfo.getId())
					.append(".jpg").toString();
			Log.d("path", (new StringBuilder("show path = ")).append(imgPath)
					.toString());
			if ((new File(imgPath)).exists())
			{
				Bitmap bitmap = BitmapUtil.freeBit(imgPath);
				imageList.put(photoId, new SoftReference(bitmap));
				handler.obtainMessage(0, bitmap).sendToTarget();
			}
			if (!"".equals(photoinfo.getImgurl()))
			{
				(new File(imgPath)).createNewFile();
				(new HttpRequest())._getFile(photoinfo.getImgurl(), null, null,
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
											Bitmap bitmap1 = BitmapUtil
													.freeBit(imgPath);
											imageList.put(photoId,
													new SoftReference(bitmap1));
											if (handler != null)
											{
												handler.obtainMessage(0,
														bitmap1).sendToTarget();
											}
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
			}
		} catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

	public void destroyItem(View paramView, int paramInt, Object paramObject)
	{
		Log.e("----", "destroyItem:" + paramInt);
		System.gc();
		System.gc();
		System.gc();
		((ViewPager) paramView).removeView((View) this.viewList.get(paramInt));
	}

	public void finishUpdate(View paramView)
	{}

	public int getCount()
	{
		int i;
		if (photoList == null)
			i = 0;
		else
			i = photoList.size();
		return i;
	}

	public List<PhotoInfo> getPhotoList()
	{
		return this.photoList;
	}

	public View getView(int paramInt)
	{
		return (View) this.viewList.get(paramInt);
	}

	public ArrayList<ImageView> getViewList()
	{
		return this.viewList;
	}

	public Object instantiateItem(View view, int i)
	{
		Log.d("page", (new StringBuilder("instantiateItem:--------------"))
				.append(i).toString());
		final PhotoInfo info = (PhotoInfo) photoList.get(i);
		final ImageView imageView = (ImageView) viewList.get(i);
		imageView
				.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(
						-1, -1));
		imageView.setTag(info.getId());
		((ViewPager) view).addView(imageView, 0);
		(new Thread(new Runnable()
		{

			public void run()
			{
				System.gc();
				System.gc();
				getImg(info, null);
			}
		})).start();
		return imageView;
	}

	public boolean isViewFromObject(View view, Object obj)
	{
		boolean flag;
		if (view == obj)
			flag = true;
		else
			flag = false;
		return flag;
	}

	public ImageView makeView()
	{
		ImageView localImageView = new ImageView(this.context);
		localImageView.setOnClickListener(this);
		localImageView.setBackgroundColor(-16777216);
		localImageView
				.setLayoutParams(new FrameLayout.LayoutParams(-1, -1, 17));
		return localImageView;
	}

	public void onClick(View paramView)
	{
		Log.d("xxx", "onClick");
		String str = (String) paramView.getTag();
		Log.d("xxx", "onClick" + this.imageList);
		SoftReference localSoftReference = (SoftReference) this.imageList
				.get(str);
		if (localSoftReference != null)
		{
			Bitmap localBitmap = (Bitmap) localSoftReference.get();
			ImageDialog localImageDialog = new ImageDialog(this.context,
					localBitmap);
			localImageDialog.setOwnerActivity(this.context);
			localImageDialog.show();
		}
	}

	public void recycleBitmap()
	{
		for (int i = 0; i < this.photoList.size(); i++)
		{
			Log.d("index", " photo list  i = " + i);
			if (this.imageList.containsKey(((PhotoInfo) photoList.get(i))
					.getId()))
			{
				Log.d("index", " image list  i = " + i);
				((Bitmap) ((SoftReference) this.imageList
						.get(((PhotoInfo) this.photoList.get(i)).getId()))
						.get()).recycle();
			}
		}
	}

	public void restoreState(Parcelable paramParcelable,
			ClassLoader paramClassLoader)
	{}

	public Parcelable saveState()
	{
		return null;
	}

	public void setPhotoList(List<PhotoInfo> paramList)
	{
		if (paramList != null)
		{
			this.photoList = paramList;
			for (int j = 0; j < paramList.size(); j++)
			{
				this.viewList.add(makeView());
			}
		}

	}

	public void setViewList(ArrayList<ImageView> paramArrayList)
	{
		this.viewList = paramArrayList;
	}

	public void startUpdate(View paramView)
	{}
}