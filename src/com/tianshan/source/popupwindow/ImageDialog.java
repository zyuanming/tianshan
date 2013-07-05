package com.tianshan.source.popupwindow;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.tianshan.R;

public class ImageDialog extends Dialog implements View.OnTouchListener
{
	static final int CLICK = 3;
	static final int DRAG = 1;
	static final float MAX_SCALE = 4.0F;
	static final int NONE = 0;
	static final int ZOOM = 2;
	Bitmap bitmap;
	float dist = 1.0F;
	DisplayMetrics dm;
	private float firstMotionX;
	ImageView imgView;
	Matrix matrix = new Matrix();
	PointF mid = new PointF();
	float minScaleR;
	int mode = 0;
	PointF prev = new PointF();
	Matrix savedMatrix = new Matrix();

	public ImageDialog(Context paramContext, Bitmap paramBitmap)
	{
		super(paramContext, 16973831);
		this.bitmap = paramBitmap;
	}

	private void CheckView()
	{
		float[] arrayOfFloat = new float[9];
		this.matrix.getValues(arrayOfFloat);
		if (this.mode == 2)
		{
			if (arrayOfFloat[0] < this.minScaleR)
				this.matrix.setScale(this.minScaleR, this.minScaleR);
			if (arrayOfFloat[0] > 4.0F)
				this.matrix.set(this.savedMatrix);
		}
		center();
	}

	private void center()
	{
		center(true, true);
	}

	private void init()
	{
		setContentView(R.layout.image_popup);
		this.imgView = ((ImageView) findViewById(R.id.img));
		this.imgView.setImageBitmap(this.bitmap);
		this.imgView.setOnTouchListener(this);
		this.dm = new DisplayMetrics();
		getOwnerActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(this.dm);
		minZoom();
		center();
		this.imgView.setImageMatrix(this.matrix);
	}

	private void midPoint(PointF paramPointF, MotionEvent paramMotionEvent)
	{
		float f1 = paramMotionEvent.getX(0) + paramMotionEvent.getX(1);
		float f2 = paramMotionEvent.getY(0) + paramMotionEvent.getY(1);
		paramPointF.set(f1 / 2.0F, f2 / 2.0F);
	}

	private void minZoom()
	{
		this.minScaleR = Math.min(this.dm.widthPixels / this.bitmap.getWidth(),
				this.dm.heightPixels / this.bitmap.getHeight());
		if (this.minScaleR < 1.0D)
			this.matrix.postScale(this.minScaleR, this.minScaleR);
	}

	private float spacing(MotionEvent paramMotionEvent)
	{
		float f1 = paramMotionEvent.getX(0) - paramMotionEvent.getX(1);
		float f2 = paramMotionEvent.getY(0) - paramMotionEvent.getY(1);
		return FloatMath.sqrt(f1 * f1 + f2 * f2);
	}

	protected void center(boolean flag, boolean flag1)
	{
		Matrix matrix1 = new Matrix();
		matrix1.set(matrix);
		RectF rectf = new RectF(0.0F, 0.0F, bitmap.getWidth(),
				bitmap.getHeight());
		matrix1.mapRect(rectf);
		float f = rectf.height();
		float f1 = rectf.width();
		float f2 = 0.0F;
		float f3;
		if (flag1)
		{
			int k = dm.heightPixels;
			if (f < (float) k)
				f2 = ((float) k - f) / 2.0F - rectf.top;
			else if (rectf.top > 0.0F)
			{
				f2 = -rectf.top;
			} else
			{
				boolean l = rectf.bottom != (float) k;
				f2 = 0.0F;
				if (l)
					f2 = (float) imgView.getHeight() - rectf.bottom;
			}
		}
		f3 = 0.0F;
		if (flag)
		{
			int i = dm.widthPixels;
			if (f1 < (float) i)
				f3 = ((float) i - f1) / 2.0F - rectf.left;
			else if (rectf.left > 0.0F)
			{
				f3 = -rectf.left;
			} else
			{
				boolean j = rectf.right != (float) i;
				f3 = 0.0F;
				if (j)
					f3 = (float) i - rectf.right;
			}
		}
		matrix.postTranslate(f3, f2);
	}

	public void onClick()
	{
		dismiss();
	}

	public boolean onTouch(View paramView, MotionEvent motionevent)
	{
		float f1 = motionevent.getX();
		switch (0xFF & motionevent.getAction())
		{
		case 3:
		case 4:
		default:
			this.imgView.setImageMatrix(this.matrix);
			CheckView();
		case 0:

			this.firstMotionX = f1;
			this.savedMatrix.set(this.matrix);
			this.prev.set(motionevent.getX(), motionevent.getY());
			this.mode = 1;
			break;
		case 5:
			dist = spacing(motionevent);
			if (spacing(motionevent) > 10F)
			{
				savedMatrix.set(matrix);
				midPoint(mid, motionevent);
				mode = 2;
			}
			break;
		case 1:
			firstMotionX = f1;
			savedMatrix.set(matrix);
			prev.set(motionevent.getX(), motionevent.getY());
			mode = 1;
			break;
		case 6:
			mode = 0;
			break;
		case 2:
			if (mode == 1)
			{
				matrix.set(savedMatrix);
				matrix.postTranslate(motionevent.getX() - prev.x,
						motionevent.getY() - prev.y);
			} else if (mode == 2)
			{
				float f2 = spacing(motionevent);
				if (f1 > 10F)
				{
					matrix.set(savedMatrix);
					float f3 = f1 / dist;
					matrix.postScale(f3, f3, mid.x, mid.y);
				}
			}
			break;
		}
		return true;
	}

	public void show()
	{
		init();
		super.show();
	}
}