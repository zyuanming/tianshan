package com.tianshan.source;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class GPSUtil
{
	private Context context;
	private double latitude;
	private Location location;
	private final LocationListener locationListener = new LocationListener()
	{
		public void onLocationChanged(Location paramAnonymousLocation)
		{
			GPSUtil.this.updateWithNewLocation(paramAnonymousLocation);
		}

		public void onProviderDisabled(String paramAnonymousString)
		{
			GPSUtil.this.updateWithNewLocation(null);
		}

		public void onProviderEnabled(String paramAnonymousString)
		{}

		public void onStatusChanged(String paramAnonymousString,
				int paramAnonymousInt, Bundle paramAnonymousBundle)
		{}
	};
	private LocationManager locationManager;
	private double longitude;
	String provider;

	public GPSUtil(Context paramContext)
	{
		this.context = paramContext;
	}

	private void getProvider()
	{
		// Criteria：指示应用程序选择位置提供者的标准
		Criteria localCriteria = new Criteria();
		localCriteria.setAccuracy(Criteria.ACCURACY_FINE); // 为纬度和精度设置精度，精度越高，位置越精确，但是会消耗很多的电量和时间
		localCriteria.setAltitudeRequired(false); // 指示是否提供者必须提供高度信息。并不是所有的补丁都保证包含这样的信息。
		localCriteria.setBearingRequired(false); // 指示是否提供者必须提供轴承信息。并不是所有的补丁都保证包含这样的信息。
		localCriteria.setCostAllowed(true); // 指示是否允许供应商产生货币成本。
		localCriteria.setPowerRequirement(Criteria.POWER_LOW); // 显示所需的最高功率水平：NO_REQUIREMENT,
																// POWER_LOW,
																// POWER_MEDIUM,
																// POWER_HIGH.
		this.provider = "network";
	}

	private void openGPS()
	{
		if (!locationManager.isProviderEnabled("gps"))
			if (!locationManager.isProviderEnabled("network"))
				;
	}

	/**
	 * 更新位置信息
	 * 
	 * @param location1
	 *            新的位置信息
	 * @return
	 */
	private double[] updateWithNewLocation(Location location1)
	{
		double ad[];
		if (location1 != null)
		{
			latitude = location1.getLatitude();
			longitude = location1.getLongitude();
			ad = new double[2];
			ad[0] = latitude;
			ad[1] = longitude;
			Log.d("loc",
					(new StringBuilder(String.valueOf(location1.getLatitude())))
							.append(" = double  latitude").toString());
			Log.d("loc",
					(new StringBuilder(String.valueOf(location1.getLongitude())))
							.append(" = double  longitude").toString());
		} else
		{
			ad = null;
		}
		return ad;
	}

	/**
	 * 设置精度和纬度
	 * 
	 * @return
	 */
	public double[] setLatitudeAndLongitude()
	{
		this.locationManager = ((LocationManager) this.context
				.getSystemService("location"));
		getProvider();
		openGPS();
		this.location = this.locationManager
				.getLastKnownLocation(this.provider); // 立即取得当前位置
		this.locationManager.requestLocationUpdates("network", 1000L, 10.0F,
				this.locationListener);
		this.locationManager.requestLocationUpdates("gps", 1000L, 10.0F,
				this.locationListener);
		this.location = this.locationManager.getLastKnownLocation("gps");
		if (this.location == null)
			this.location = this.locationManager
					.getLastKnownLocation("network");
		if (this.location == null)
			this.location = this.locationManager.getLastKnownLocation("gps");
		if (this.location == null)
			this.location = this.locationManager
					.getLastKnownLocation("network");
		double[] arrayOfDouble = updateWithNewLocation(this.location);
		if (arrayOfDouble != null)
			this.locationManager.removeUpdates(this.locationListener);
		return arrayOfDouble;
	}
}