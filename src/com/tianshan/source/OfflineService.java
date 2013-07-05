package com.tianshan.source;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Display;

import com.tianshan.activity.DownloadAllMsg;
import com.tianshan.dbhelper.SubnavHelper;
import com.tianshan.model.DownLoadModel;
import com.tianshan.setting.Setting;
import com.tianshan.setting.WoPreferences;
import com.tianshan.task.BaseAsyncTask;
import com.tianshan.task.DownloadTask;

public class OfflineService extends Service
{
	private static Context mContext;
	private static OnDownloadDoneListener mdownloaddoneListener;
	private BaseAsyncTask.TaskResultListener DownloadListener = new BaseAsyncTask.TaskResultListener()
	{
		public void onTaskResult(boolean flag, DownLoadModel downloadmodel)
		{
			if (!flag)
			{
				OfflineService offlineservice = OfflineService.this;
				offlineservice.curDownpos = 1 + offlineservice.curDownpos;
				if (curDownpos < downloadlist.size())
				{
					if (Setting.hasTaskRunning)
					{
						DownloadTask downloadtask = new DownloadTask(
								OfflineService.mContext, DownloadListener);
						String as[] = new String[3];
						as[0] = ((DownLoadModel) downloadlist.get(curDownpos))
								.getUrl();
						as[1] = ((DownLoadModel) downloadlist.get(curDownpos))
								.getName();
						as[2] = ((DownLoadModel) downloadlist.get(curDownpos))
								.getType();
						downloadtask.execute(as);
					}
				} else if (curType.equals("topnews"))
				{
					OfflineService offlineservice9 = OfflineService.this;
					offlineservice9.curDownloadTypePos = 1 + offlineservice9.curDownloadTypePos;
					curType = cate[curDownloadTypePos];
					initDownload();
					Subnewspos = 0;
					getNewsId();
					downloadnews(Subnewspos);
				} else if (curType.equals("news") && Subnewspos == 0)
				{
					initDownload();
					OfflineService offlineservice8 = OfflineService.this;
					offlineservice8.Subnewspos = 1 + offlineservice8.Subnewspos;
					downloadnews(Subnewspos);
				} else if (curType.equals("news") && Subnewspos == 1)
				{
					OfflineService offlineservice7 = OfflineService.this;
					offlineservice7.curDownloadTypePos = 1 + offlineservice7.curDownloadTypePos;
					curType = cate[curDownloadTypePos];
					initDownload();
					downloadtopics();
				} else if (curType.equals("topics"))
				{
					OfflineService offlineservice6 = OfflineService.this;
					offlineservice6.curDownloadTypePos = 1 + offlineservice6.curDownloadTypePos;
					curType = cate[curDownloadTypePos];
					initDownload();
					Subvideopos = 0;
					getVideoId();
					downloadvideo(Subvideopos);
				} else if (curType.equals("video") && Subvideopos == 0)
				{
					initDownload();
					OfflineService offlineservice5 = OfflineService.this;
					offlineservice5.Subvideopos = 1 + offlineservice5.Subvideopos;
					downloadvideo(Subvideopos);
				} else if (curType.equals("video") && Subvideopos != 0
						&& Subvideopos != 3)
				{
					initDownload();
					OfflineService offlineservice4 = OfflineService.this;
					offlineservice4.Subvideopos = 1 + offlineservice4.Subvideopos;
					downloadvideo(Subvideopos);
				} else if (curType.equals("video") && Subvideopos == 3)
				{
					OfflineService offlineservice3 = OfflineService.this;
					offlineservice3.curDownloadTypePos = 1 + offlineservice3.curDownloadTypePos;
					curType = cate[curDownloadTypePos];
					initDownload();
					Subimgpos = 0;
					getPicId();
					downloadpic(Subimgpos);
				} else if (curType.equals("pic") && Subimgpos == 0)
				{
					initDownload();
					OfflineService offlineservice2 = OfflineService.this;
					offlineservice2.Subimgpos = 1 + offlineservice2.Subimgpos;
					downloadpic(Subimgpos);
				} else if (curType.equals("pic") && Subimgpos != 0
						&& Subimgpos != 3)
				{
					initDownload();
					OfflineService offlineservice1 = OfflineService.this;
					offlineservice1.Subimgpos = 1 + offlineservice1.Subimgpos;
					downloadpic(Subimgpos);
				} else
				{
					OfflineService.mContext.getSharedPreferences("tianshan", 0)
							.edit().clear();
					OfflineService.mdownloaddoneListener.ondownloaddone(1);
				}
			} else
			{
				SharedPreferences sharedpreferences;
				android.content.SharedPreferences.Editor editor;
				sharedpreferences = OfflineService.mContext
						.getSharedPreferences("tianshan", 0);
				editor = sharedpreferences.edit();
				if (!downloadmodel.getType().equals("html"))
				{
					if (downloadmodel.getType().equals("img"))
					{
						OfflineManager.setContext(getApplicationContext());
						OfflineManager.writeImg(downloadmodel);
					}
				} else
				{
					int i;
					int j;
					try
					{
						OfflineManager.setContext(getApplicationContext());
						OfflineManager.writeFile(downloadmodel);
						editor.putInt("count",
								1 + sharedpreferences.getInt("count", 0));
					} catch (IOException ioexception)
					{
						ioexception.printStackTrace();
					}
					i = (int) Math
							.ceil(downloadmodel.getContent().length / 1024);
					j = Math.round((100 * (1 + curDownpos))
							/ downloadlist.size());
					editor.putInt("size",
							i + sharedpreferences.getInt("size", 0));
					editor.putInt("progress", j);
					editor.commit();
				}
			}
		}
	};
	private BaseAsyncTask.TaskResultListener DownloadimgListener = new BaseAsyncTask.TaskResultListener()
	{
		public void onTaskResult(boolean flag, DownLoadModel downloadmodel)
		{
			if (flag)
			{
				JSONObject jsonobject;
				JSONArray jsonarray;
				String s;
				int j;
				DownLoadModel downloadmodel2;
				DownloadTask downloadtask;
				String as[];
				JSONObject jsonobject1;
				try
				{
					OfflineManager.setContext(getApplicationContext());
					OfflineManager.writeFile(downloadmodel);
					int k = (int) Math
							.ceil(downloadmodel.getContent().length / 1024);
					SharedPreferences sharedpreferences = OfflineService.mContext
							.getSharedPreferences("tianshan", 0);
					int l = sharedpreferences.getInt("size", 0);
					android.content.SharedPreferences.Editor editor = sharedpreferences
							.edit();
					editor.putInt("size", k + l);
					editor.commit();
				} catch (IOException ioexception)
				{
					ioexception.printStackTrace();
					Setting.hasTaskRunning = false;
				}
				s = (new StringBuilder(String.valueOf(Setting.downloadUrl)))
						.append("pic&subtypeid=").append(imgSubId[Subimgpos])
						.append("&system=android")
						.append(OfflineService._addUrlParams()).toString();
				try
				{
					jsonobject1 = OfflineManager.getSubDownloadObj(s);
					jsonobject = jsonobject1;
					if (jsonobject.getJSONObject("msg").getString("msgvar")
							.equals("list_sucess"))
					{
						jsonarray = jsonobject.getJSONObject("res")
								.getJSONArray("list");
						j = jsonarray.length();
						for (int i = 0; i < jsonarray.length(); i++)
						{
							String s1 = ((JSONObject) jsonarray.get(i))
									.optString("filename", "null");
							if (!s1.equals("null") && !s1.equals(""))
							{
								DownLoadModel downloadmodel1 = new DownLoadModel();
								downloadmodel1.setType("img");
								downloadmodel1.setUrl((new StringBuilder(String
										.valueOf(Setting.downloadimgUrl)))
										.append(s1).toString());
								downloadlist.add(downloadmodel1);
							}
						}
						downloadmodel2 = (DownLoadModel) downloadlist
								.get(curDownpos);
						if (Setting.hasTaskRunning)
						{
							downloadtask = new DownloadTask(
									OfflineService.mContext, DownloadListener);
							as = new String[3];
							as[0] = downloadmodel2.getUrl();
							as[1] = downloadmodel2.getName();
							as[2] = downloadmodel2.getType();
							downloadtask.execute(as);
						}
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	};
	private BaseAsyncTask.TaskResultListener DownloadnewslistListener = new BaseAsyncTask.TaskResultListener()
	{
		public void onTaskResult(boolean flag, DownLoadModel downloadmodel)
		{
			if (flag)
			{
				String s;
				try
				{
					OfflineManager.setContext(getApplicationContext());
					OfflineManager.writeFile(downloadmodel);
					int i = (int) Math
							.ceil(downloadmodel.getContent().length / 1024);
					SharedPreferences sharedpreferences = OfflineService.mContext
							.getSharedPreferences("tianshan", 0);
					int j = sharedpreferences.getInt("size", 0);
					android.content.SharedPreferences.Editor editor = sharedpreferences
							.edit();
					editor.putInt("size", i + j);
					editor.commit();
				} catch (IOException ioexception)
				{
					ioexception.printStackTrace();
					Setting.hasTaskRunning = false;
				}
				if (!curType.equals("topnews"))
				{
					boolean flag1 = curType.equals("news");
					s = null;
					if (flag1)
						s = (new StringBuilder(
								String.valueOf(Setting.downloadUrl)))
								.append("news&subtypeid=")
								.append(newsSubId[Subnewspos])
								.append("&system=android")
								.append(OfflineService._addUrlParams())
								.toString();
					downloadtopAndnews(s);
				} else
				{
					s = (new StringBuilder(String.valueOf(Setting.downloadUrl)))
							.append("topnews&subtypeid=1")
							.append("&system=android")
							.append(OfflineService._addUrlParams()).toString();
				}
			}

		}
	};
	private BaseAsyncTask.TaskResultListener DownloadtopicsListener = new BaseAsyncTask.TaskResultListener()
	{
		public void onTaskResult(boolean flag, DownLoadModel downloadmodel)
		{
			if (flag)
			{
				JSONObject jsonobject;
				JSONArray jsonarray;
				String s;
				DownLoadModel downloadmodel5;
				DownloadTask downloadtask;
				String as[];
				JSONObject jsonobject3;
				try
				{
					OfflineManager.setContext(getApplicationContext());
					OfflineManager.writeFile(downloadmodel);
					int k = (int) Math
							.ceil(downloadmodel.getContent().length / 1024);
					SharedPreferences sharedpreferences = OfflineService.mContext
							.getSharedPreferences("tianshan", 0);
					int l = sharedpreferences.getInt("size", 0);
					android.content.SharedPreferences.Editor editor = sharedpreferences
							.edit();
					editor.putInt("size", k + l);
					editor.commit();
				} catch (IOException ioexception)
				{
					ioexception.printStackTrace();
					Setting.hasTaskRunning = false;
				}
				s = (new StringBuilder(String.valueOf(Setting.downloadUrl)))
						.append("topics&subtypeid=1").append("&system=android")
						.append(OfflineService._addUrlParams()).toString();
				try
				{
					jsonobject3 = OfflineManager.getSubDownloadObj(s);
					jsonobject = jsonobject3;
					if (jsonobject.getJSONObject("msg").getString("msgvar")
							.equals("list_sucess"))
					{
						jsonarray = jsonobject.getJSONObject("res")
								.getJSONArray("list");
						for (int i = 0; i < jsonarray.length(); i++)
						{
							JSONObject jsonobject1;
							jsonobject1 = (JSONObject) jsonarray.get(i);
							String s1 = jsonobject1.getString("cid");
							DownLoadModel downloadmodel1 = new DownLoadModel();
							downloadmodel1.setName((new StringBuilder(
									"news_special_")).append(s1)
									.append(".html").toString());
							downloadmodel1.setType("html");
							downloadmodel1.setUrl((new StringBuilder(String
									.valueOf(Setting.topicsSublistUrl)))
									.append(s1).append("&system=android")
									.append(OfflineService._addUrlParams())
									.toString());
							downloadlist.add(downloadmodel1);
							String s2 = jsonobject1.optString("filename",
									"null");
							if (!s2.equals("null") && !s2.equals(""))
							{
								DownLoadModel downloadmodel4 = new DownLoadModel();
								downloadmodel4.setType("img");
								downloadmodel4.setUrl((new StringBuilder(String
										.valueOf(Setting.downloadimgUrl)))
										.append(s2).toString());
								downloadlist.add(downloadmodel4);
							}
							JSONArray jsonarray2 = jsonobject1
									.getJSONArray("sonlist");
							JSONArray jsonarray1 = jsonarray2;
							if (jsonarray1 != null)
							{
								for (int j = 0; j < jsonarray1.length(); j++)
								{
									JSONObject jsonobject2 = (JSONObject) jsonarray1
											.get(j);
									String s3 = jsonobject2.getString("aid");
									DownLoadModel downloadmodel2 = new DownLoadModel();
									downloadmodel2.setName((new StringBuilder(
											"news_view_")).append(s3)
											.append(".html").toString());
									downloadmodel2.setType("html");
									downloadmodel2.setUrl((new StringBuilder(
											String.valueOf(Setting.newsUrl)))
											.append(s3)
											.append("&system=android")
											.append(OfflineService
													._addUrlParams())
											.toString());
									downloadlist.add(downloadmodel2);
									String s4 = jsonobject2.optString(
											"filename", "null");
									if (!s4.equals("null") && !s4.equals(""))
									{
										DownLoadModel downloadmodel3 = new DownLoadModel();
										downloadmodel3.setType("img");
										downloadmodel3
												.setUrl((new StringBuilder(
														String.valueOf(Setting.downloadimgUrl)))
														.append(s4).toString());
										downloadlist.add(downloadmodel3);
									}
								}
							}
						}
						downloadmodel5 = (DownLoadModel) downloadlist
								.get(curDownpos);
						if (Setting.hasTaskRunning)
						{
							downloadtask = new DownloadTask(
									OfflineService.mContext, DownloadListener);
							as = new String[3];
							as[0] = downloadmodel5.getUrl();
							as[1] = downloadmodel5.getName();
							as[2] = downloadmodel5.getType();
							downloadtask.execute(as);
						}
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	};
	private BaseAsyncTask.TaskResultListener DownloadvideoListener = new BaseAsyncTask.TaskResultListener()
	{
		public void onTaskResult(boolean flag, DownLoadModel downloadmodel)
		{
			if (flag)
			{
				JSONObject jsonobject;
				JSONArray jsonarray;
				String s;
				DownLoadModel downloadmodel4;
				DownloadTask downloadtask;
				String as[];
				JSONObject jsonobject2;
				try
				{
					OfflineManager.setContext(getApplicationContext());
					OfflineManager.writeFile(downloadmodel);
					int j = (int) Math
							.ceil(downloadmodel.getContent().length / 1024);
					SharedPreferences sharedpreferences = OfflineService.mContext
							.getSharedPreferences("tianshan", 0);
					int k = sharedpreferences.getInt("size", 0);
					android.content.SharedPreferences.Editor editor = sharedpreferences
							.edit();
					editor.putInt("size", j + k);
					editor.commit();
				} catch (IOException ioexception)
				{
					ioexception.printStackTrace();
					Setting.hasTaskRunning = false;
				}
				s = (new StringBuilder(String.valueOf(Setting.downloadUrl)))
						.append("video&subtypeid=")
						.append(videoSubId[Subvideopos])
						.append("&system=android")
						.append(OfflineService._addUrlParams()).toString();
				try
				{
					jsonobject2 = OfflineManager.getSubDownloadObj(s);
					jsonobject = jsonobject2;
					if (jsonobject.getJSONObject("msg").getString("msgvar")
							.equals("list_sucess"))
					{
						jsonarray = jsonobject.getJSONObject("res")
								.getJSONArray("list");
						for (int i = 0; i < jsonarray.length(); i++)
						{
							JSONObject jsonobject1 = (JSONObject) jsonarray
									.get(i);
							String s1 = jsonobject1.getString("vid");
							DownLoadModel downloadmodel1 = new DownLoadModel();
							downloadmodel1.setName((new StringBuilder(
									"video_view_")).append(s1).append(".html")
									.toString());
							downloadmodel1.setType("html");
							downloadmodel1.setUrl((new StringBuilder(String
									.valueOf(Setting.videoUrl))).append(s1)
									.append("&system=android")
									.append(OfflineService._addUrlParams())
									.toString());
							downloadlist.add(downloadmodel1);
							String s2 = jsonobject1.optString("imgurl", "null");
							if (!s2.equals("null") && !s2.equals(""))
							{
								DownLoadModel downloadmodel3 = new DownLoadModel();
								downloadmodel3.setType("img");
								downloadmodel3.setUrl((new StringBuilder(String
										.valueOf(Setting.downloadimgUrl)))
										.append(s2).toString());
								downloadlist.add(downloadmodel3);
							}
							String s3 = jsonobject1.optString("bigimg", "null");
							if (!s3.equals("null") && !s3.equals(""))
							{
								DownLoadModel downloadmodel2 = new DownLoadModel();
								downloadmodel2.setType("img");
								downloadmodel2.setUrl((new StringBuilder(String
										.valueOf(Setting.downloadimgUrl)))
										.append(s3).toString());
								downloadlist.add(downloadmodel2);
							}
						}
						downloadmodel4 = (DownLoadModel) downloadlist
								.get(curDownpos);
						downloadtask = new DownloadTask(
								OfflineService.mContext, DownloadListener);
						as = new String[3];
						as[0] = downloadmodel4.getUrl();
						as[1] = downloadmodel4.getName();
						as[2] = downloadmodel4.getType();
						downloadtask.execute(as);
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	};
	private int Subimgpos = 0;
	private int Subnewspos = 0;
	private int Subvideopos = 0;
	private String[] cate = { "topnews", "news", "topics", "video", "pic" };
	private int curDownloadTypePos = 0;
	private int curDownpos = 0;
	private String curType;
	String dirurl = null;
	private ArrayList<Integer> downloaddone = new ArrayList();
	private ArrayList<DownLoadModel> downloadlist = new ArrayList();
	private ArrayList<Integer> downloadres = new ArrayList();
	String foldername = null;
	private Handler handler = new Handler(new Handler.Callback()
	{
		public boolean handleMessage(Message paramAnonymousMessage)
		{
			OfflineService.this.downloadres.clear();
			OfflineService.this.startDownloadTask();
			return false;
		}
	});
	private int imgID = 4;
	private int[] imgSubId = new int[4];
	private List<HashMap<String, Object>> imgSubdata;
	private int newID = 2;
	private int[] newsSubId = new int[2];
	private List<HashMap<String, Object>> newsSubdata;
	String targetPath = null;
	private boolean updateCss = false;
	private boolean updateJs = false;
	private boolean updatePic = false;
	private int videoID = 3;
	private int[] videoSubId = new int[4];
	private List<HashMap<String, Object>> videoSubdata;

	private static String _addUrlParams()
	{
		Core localCore = new Core(mContext);
		Display localDisplay = localCore._getDisplay();
		int i = WoPreferences.queryDownloadImgMode();
		return "&display=" + localDisplay.getWidth() + "*"
				+ localDisplay.getHeight() + "&imei=" + localCore._getIMEI()
				+ "&phone=" + localCore._getPhoneNumber() + "&clientversion="
				+ localCore._getVersionCode() + "&downloadimgmode=" + i;
	}

	private void _checkDownloadCssJsPic()
	{
		HttpRequest httprequest = new HttpRequest();
		String s = SiteTools.getApiUrl(new String[] { "ac=fileversion" });
		try
		{
			String s1 = httprequest._get(s);
			JSONObject jsonobject = new JSONObject(s1);
			DEBUG.o(s1);
			if (jsonobject != null)
			{
				JSONObject jsonobject1 = jsonobject.optJSONObject("res");
				if (jsonobject1 != null)
				{
					JSONArray jsonarray;
					int i;
					jsonarray = jsonobject1.optJSONArray("list");
					i = jsonarray.length();
					if (i > 0)
					{
						DEBUG.o((new StringBuilder("local css version : "))
								.append(WoPreferences.getDownloadCss())
								.toString());
						DEBUG.o((new StringBuilder("local js version : "))
								.append(WoPreferences.getDownloadJs())
								.toString());
						DEBUG.o((new StringBuilder("local pic version : "))
								.append(WoPreferences.getDownloadPic())
								.toString());
						for (int j = 0; j < i; j++)
						{
							JSONObject jsonobject2;
							jsonobject2 = jsonarray.optJSONObject(j);
							if ("css".equals(jsonobject2.optString("vname"))
									&& WoPreferences.getDownloadCss() != jsonobject2
											.optInt("version"))
							{
								updateCss = true;
								downloadres.add(Integer.valueOf(0));
								WoPreferences.setDownloadCss(jsonobject2
										.optInt("version"));
								DEBUG.o((new StringBuilder("up css, version:"))
										.append(jsonobject2.optInt("version"))
										.toString());
							}
							if ("js".equals(jsonobject2.optString("vname"))
									&& WoPreferences.getDownloadJs() != jsonobject2
											.optInt("version"))
							{
								updateJs = true;
								downloadres.add(Integer.valueOf(1));
								WoPreferences.setDownloadJs(jsonobject2
										.optInt("version"));
								DEBUG.o((new StringBuilder("up js, version:"))
										.append(jsonobject2.optInt("version"))
										.toString());
							}
							if ("pic".equals(jsonobject2.optString("vname"))
									&& WoPreferences.getDownloadPic() != jsonobject2
											.optInt("version"))
							{
								updatePic = true;
								downloadres.add(Integer.valueOf(2));
								WoPreferences.setDownloadPic(jsonobject2
										.optInt("version"));
								DEBUG.o((new StringBuilder("up pic, version:"))
										.append(jsonobject2.optInt("version"))
										.toString());
							} else
							{
								DEBUG.o((new StringBuilder("no "))
										.append(jsonobject2.optString("vname"))
										.append(" download online version : ")
										.append(jsonobject2.optInt("version"))
										.toString());
							}
						}

						if (downloadres.size() != 0)
						{
							downloadCssJsPic();
						} else
						{
							mdownloaddoneListener.ondownloaddone(2);
							Message message = new Message();
							handler.sendMessage(message);
						}
					}
				}
			}
		} catch (JSONException je)
		{
			DEBUG.o("_checkDownloadCssJsPic error");
			je.printStackTrace();
		} catch (Exception e)
		{
			DEBUG.o("_checkDownloadCssJsPic error");
			e.printStackTrace();
		}
	}

	private void checkDir()
	{
		File localFile1 = new File(
				"/sdcard/tianshan/html/template/default/css/");
		if (!localFile1.exists())
			localFile1.mkdirs();
		File localFile2 = new File(
				"/sdcard/tianshan/html/template/default/js/video-js/skins/");
		if (!localFile2.exists())
			localFile2.mkdirs();
		File localFile3 = new File(
				"/sdcard/tianshan/html/template/default/images/");
		if (!localFile3.exists())
			localFile3.mkdirs();
	}

	private void downloadAll(String s)
	{
		try
		{
			HttpRequest httprequest = new HttpRequest();
			String s1 = httprequest._get(s);
			DEBUG.o(s);
			JSONObject jsonobject = new JSONObject(s1);
			DEBUG.o(s1);
			if (jsonobject != null)
			{
				JSONObject jsonobject1 = jsonobject.optJSONObject("res");
				if (jsonobject1 != null)
				{
					JSONArray jsonarray;
					int i;
					jsonarray = jsonobject1.optJSONArray("list");
					i = jsonarray.length();
					if (i > 0)
					{
						for (int j = 0; j < i; j++)
						{
							JSONObject jsonobject2 = jsonarray.optJSONObject(j);
							String s2 = jsonobject2.optString("path");
							URL url = new URL(s2);
							Log.d("aaaa", (new StringBuilder("path  = "))
									.append(url.getPath()).toString());
							File file;
							String s3;
							HttpRequest.requestCallBack requestcallback;
							if (url.getPath().contains("admin"))
							{
								dirurl = url.getPath().substring(7,
										url.getPath().length());
								Log.d("aaaa", (new StringBuilder("path  = "))
										.append(dirurl).toString());
							} else
							{
								dirurl = url.getPath();
							}
							if (!Setting.IsCanUseSdCard())
							{
								foldername = getApplicationContext()
										.getFilesDir().getAbsolutePath();
							} else
							{
								foldername = Environment
										.getExternalStorageDirectory()
										.getPath();
							}

							targetPath = (new StringBuilder(
									String.valueOf(foldername))).append("/")
									.append("tianshan").append("/html/")
									.append(dirurl).toString();
							file = new File(targetPath);
							s3 = String.valueOf(file.lastModified());
							if (!file.exists()
									|| s3 == null
									|| !jsonobject2.optString("updatetime")
											.equals(s3.substring(0, 10)))
							{
								requestcallback = new HttpRequest.requestCallBack()
								{

									public void download(Object obj)
									{
										try
										{
											InputStream inputstream = (InputStream) obj;
											FileOutputStream fileoutputstream = new FileOutputStream(
													targetPath);
											byte abyte0[] = new byte[1024];
											do
											{
												int k = inputstream
														.read(abyte0);
												if (k == -1)
												{
													fileoutputstream.close();
													break;
												}
												fileoutputstream.write(abyte0,
														0, k);
											} while (true);
										} catch (FileNotFoundException filenotfoundexception)
										{
											filenotfoundexception
													.printStackTrace();
										} catch (IOException ioexception)
										{
											ioexception.printStackTrace();
										}
									}
								};
								httprequest._getFile(s2, null, null,
										requestcallback);
							}
							if (file.exists())
							{
								DEBUG.o((new StringBuilder("====="))
										.append(file.getPath()).append("=====")
										.toString());
								DEBUG.o((new StringBuilder("online up time: "))
										.append(jsonobject2
												.optLong("updatetime"))
										.toString());
								DEBUG.o((new StringBuilder(
										"local file up time: ")).append(s3)
										.toString());
								file.setLastModified(1000L * jsonobject2
										.optLong("updatetime"));
							}
						}

						downloaddone.add(Integer.valueOf(1));
						if (downloaddone.size() == downloadres.size())
						{
							mdownloaddoneListener.ondownloaddone(2);
							Message message = new Message();
							handler.sendMessage(message);
						}
					}
				}
			}
		} catch (JSONException je)
		{
			DEBUG.o("downloadAll error");
			je.printStackTrace();
		} catch (Exception e)
		{
			DEBUG.o("downloadAll error");
			e.printStackTrace();
		}
	}

	private void downloadCssJsPic()
	{
		checkDir();
		if (this.downloadres.size() > 0)
		{
			for (int i = 0; i < this.downloadres.size(); i++)
			{
				if (((Integer) this.downloadres.get(i)).intValue() == 0)
					downloadAll(SiteTools.getApiUrl(new String[] {
							"ac=resourcelist", "type=css" }));
				if (((Integer) this.downloadres.get(i)).intValue() == 1)
					downloadAll(SiteTools.getApiUrl(new String[] {
							"ac=resourcelist", "type=js" }));
				else if (((Integer) this.downloadres.get(i)).intValue() == 2)
					downloadAll(SiteTools.getApiUrl(new String[] {
							"ac=resourcelist", "type=images" }));
			}
		}
	}

	private void downloadnews(int i)
	{
		android.content.SharedPreferences.Editor editor = mContext
				.getSharedPreferences("tianshan", 0).edit();
		editor.putString("type",
				(String) ((HashMap) newsSubdata.get(i + 2)).get("name"));
		editor.putInt("progress", 0);
		editor.commit();
		if (Setting.hasTaskRunning)
		{
			DownloadTask downloadtask = new DownloadTask(mContext,
					DownloadnewslistListener);
			String as[] = new String[3];
			as[0] = (new StringBuilder(String.valueOf(Setting.newslistUrl)))
					.append(newsSubId[i]).append("&system=android")
					.append(_addUrlParams()).toString();
			as[1] = (new StringBuilder("news_list_")).append(newsSubId[i])
					.append(".html").toString();
			as[2] = "html";
			downloadtask.execute(as);
		}
	}

	private void downloadpic(int i)
	{
		android.content.SharedPreferences.Editor editor = mContext
				.getSharedPreferences("tianshan", 0).edit();
		editor.putString("type",
				(String) ((HashMap) imgSubdata.get(i)).get("name"));
		editor.putInt("progress", 0);
		editor.commit();
		if (Setting.hasTaskRunning)
		{
			DownloadTask downloadtask = new DownloadTask(mContext,
					DownloadimgListener);
			String as[] = new String[3];
			as[0] = (new StringBuilder(String.valueOf(Setting.imglistUrl)))
					.append(imgSubId[i]).append("&system=android")
					.append(_addUrlParams()).toString();
			as[1] = (new StringBuilder("pic_list_")).append(imgSubId[i])
					.append(".html").toString();
			as[2] = "html";
			downloadtask.execute(as);
		}
	}

	private void downloadtopAndnews(String s)
	{
		try
		{
			JSONObject jsonobject = OfflineManager.getSubDownloadObj(s);
			if (jsonobject.getJSONObject("msg").getString("msgvar")
					.equals("list_sucess"))
			{
				JSONObject jsonobject1 = jsonobject.getJSONObject("res")
						.getJSONObject("list");
				if (jsonobject1 != null)
				{

					for (int i = 0; i < 30; i++)
					{

						JSONObject jsonobject3 = jsonobject1
								.getJSONObject((new StringBuilder(String
										.valueOf(i))).toString());
						String s4 = jsonobject3.getString("aid");
						DownLoadModel downloadmodel5 = new DownLoadModel();
						downloadmodel5
								.setName((new StringBuilder("news_view_"))
										.append(s4).append(".html").toString());
						downloadmodel5.setType("html");
						downloadmodel5.setUrl((new StringBuilder(String
								.valueOf(Setting.newsUrl))).append(s4)
								.append("&system=android")
								.append(_addUrlParams()).toString());
						downloadlist.add(downloadmodel5);
						String s5 = jsonobject3.optString("filename", "null");
						if (!s5.equals("null") && !s5.equals(""))
						{
							DownLoadModel downloadmodel8 = new DownLoadModel();
							downloadmodel8.setType("img");
							downloadmodel8.setUrl((new StringBuilder(String
									.valueOf(Setting.downloadimgUrl))).append(
									s5).toString());
							downloadlist.add(downloadmodel8);
						}
						String s6 = jsonobject3.optString("content", "null");
						if (!s6.equals("null") && !s6.equals(""))
						{
							String as2[] = s6.split("\\|");
							if (as2.length > 1)
							{
								for (int i1 = 0; i1 < as2.length; i1++)
								{
									DownLoadModel downloadmodel7 = new DownLoadModel();
									downloadmodel7.setType("img");
									downloadmodel7
											.setUrl((new StringBuilder(
													String.valueOf(Setting.downloadimgUrl)))
													.append(as2[i1]).toString());
									downloadlist.add(downloadmodel7);
								}

							} else
							{
								DownLoadModel downloadmodel6 = new DownLoadModel();
								downloadmodel6.setType("img");
								downloadmodel6.setUrl((new StringBuilder(String
										.valueOf(Setting.downloadimgUrl)))
										.append(s6).toString());
								downloadlist.add(downloadmodel6);
							}
						}
					}
					JSONArray jsonarray = jsonobject1.getJSONArray("flashlist");
					if (jsonarray != null)
					{
						for (int j = 0; j < jsonarray.length(); j++)
						{
							JSONObject jsonobject2 = jsonarray.getJSONObject(j);
							String s1 = jsonobject2.getString("aid");
							DownLoadModel downloadmodel1 = new DownLoadModel();
							downloadmodel1.setName((new StringBuilder(
									"news_view_")).append(s1).append(".html")
									.toString());
							downloadmodel1.setType("html");
							downloadmodel1.setUrl((new StringBuilder(String
									.valueOf(Setting.newsUrl))).append(s1)
									.append("&system=android")
									.append(_addUrlParams()).toString());
							downloadlist.add(downloadmodel1);
							String s2 = jsonobject2.optString("filename",
									"null");
							if (!s2.equals("null") && !s2.equals(""))
							{
								DownLoadModel downloadmodel4 = new DownLoadModel();
								downloadmodel4.setType("img");
								downloadmodel4.setUrl((new StringBuilder(String
										.valueOf(Setting.downloadimgUrl)))
										.append(s2).toString());
								downloadlist.add(downloadmodel4);
							}
							String s3 = jsonobject2
									.optString("content", "null");
							if (!s3.equals("null") && !s3.equals(""))
							{
								String as1[] = s3.split("\\|");
								if (as1.length > 1)
								{
									for (int l = 0; l < as1.length; l++)
									{
										DownLoadModel downloadmodel3 = new DownLoadModel();
										downloadmodel3.setType("img");
										downloadmodel3
												.setUrl((new StringBuilder(
														String.valueOf(Setting.downloadimgUrl)))
														.append(as1[l])
														.toString());
										downloadlist.add(downloadmodel3);
									}
								} else
								{
									DownLoadModel downloadmodel2 = new DownLoadModel();
									downloadmodel2.setType("img");
									downloadmodel2
											.setUrl((new StringBuilder(
													String.valueOf(Setting.downloadimgUrl)))
													.append(s3).toString());
									downloadlist.add(downloadmodel2);
								}
							}
						}
					}
				}
			}
		} catch (JSONException je)
		{
			Setting.hasTaskRunning = false;
			je.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		DownLoadModel downloadmodel = (DownLoadModel) downloadlist
				.get(curDownpos);
		if (Setting.hasTaskRunning)
		{
			DownloadTask downloadtask = new DownloadTask(mContext,
					DownloadListener);
			String as[] = new String[3];
			as[0] = downloadmodel.getUrl();
			as[1] = downloadmodel.getName();
			as[2] = downloadmodel.getType();
			downloadtask.execute(as);
		}
	}

	private void downloadtopics()
	{
		android.content.SharedPreferences.Editor editor = mContext
				.getSharedPreferences("tianshan", 0).edit();
		editor.putString("type", "\u4E13\u9898");
		editor.putInt("progress", 0);
		editor.commit();
		String s = Setting.topicslistUrl;
		if (Setting.hasTaskRunning)
		{
			DownloadTask downloadtask = new DownloadTask(mContext,
					DownloadtopicsListener);
			String as[] = new String[3];
			as[0] = (new StringBuilder(String.valueOf(s)))
					.append("&system=android").append(_addUrlParams())
					.toString();
			as[1] = "news_specialindex.html";
			as[2] = "html";
			downloadtask.execute(as);
		}
	}

	private void downloadvideo(int i)
	{
		android.content.SharedPreferences.Editor editor = mContext
				.getSharedPreferences("tianshan", 0).edit();
		editor.putString("type",
				(String) ((HashMap) videoSubdata.get(i)).get("name"));
		editor.putInt("progress", 0);
		editor.commit();
		if (Setting.hasTaskRunning)
		{
			DownloadTask downloadtask = new DownloadTask(mContext,
					DownloadvideoListener);
			String as[] = new String[3];
			as[0] = (new StringBuilder(String.valueOf(Setting.videolistUrl)))
					.append(videoSubId[i]).append("&system=android")
					.append(_addUrlParams()).toString();
			as[1] = (new StringBuilder("video_list_")).append(videoSubId[i])
					.append(".html").toString();
			as[2] = "html";
			downloadtask.execute(as);
		}
	}

	public static Context getContext()
	{
		return mContext;
	}

	private void getNewsId()
	{
		this.newsSubdata = SubnavHelper.getInstance(mContext)
				.getFup(this.newID);
		for (int i = 2;; i++)
		{
			if (i >= 4)
				return;
			if (this.newsSubdata.get(i) != null)
				this.newsSubId[(i - 2)] = ((Integer) ((HashMap) this.newsSubdata
						.get(i)).get("id")).intValue();
		}
	}

	private void getPicId()
	{
		this.imgSubdata = SubnavHelper.getInstance(mContext).getFup(this.imgID);
		for (int i = 0;; i++)
		{
			if (i >= 4)
				return;
			if (this.imgSubdata.get(i) != null)
				this.imgSubId[i] = ((Integer) ((HashMap) this.imgSubdata.get(i))
						.get("id")).intValue();
		}
	}

	private void getVideoId()
	{
		this.videoSubdata = SubnavHelper.getInstance(mContext).getFup(
				this.videoID);
		for (int i = 0;; i++)
		{
			if (i >= 4)
				return;
			if (this.videoSubdata.get(i) != null)
				this.videoSubId[i] = ((Integer) ((HashMap) this.videoSubdata
						.get(i)).get("id")).intValue();
		}
	}

	private void init()
	{
		this.curDownpos = 0;
		this.curDownloadTypePos = 0;
		this.Subnewspos = 0;
		this.Subvideopos = 0;
		this.Subimgpos = 0;
		this.downloadlist.clear();
		SharedPreferences.Editor localEditor = mContext.getSharedPreferences(
				"tianshan", 0).edit();
		localEditor.clear();
		localEditor.commit();
	}

	private void initDownload()
	{
		this.curDownpos = 0;
		this.downloadlist.clear();
	}

	public static void setContext(Context paramContext)
	{
		mContext = paramContext;
	}

	public static void setOnDownloadDoneListener(
			OnDownloadDoneListener paramOnDownloadDoneListener)
	{
		mdownloaddoneListener = paramOnDownloadDoneListener;
	}

	private void startDownloadTask()
	{
		this.curType = this.cate[this.curDownloadTypePos];
		SharedPreferences.Editor localEditor = mContext.getSharedPreferences(
				"tianshan", 0).edit();
		localEditor.putString("type", "头条");
		localEditor.putInt("progress", 0);
		localEditor.commit();
		if (this.curType.equals("topnews"))
		{
			String str = SiteTools.getSiteUrl(new String[] { "ac=news",
					"op=toplist" });
			if (Setting.hasTaskRunning)
				new DownloadTask(mContext, this.DownloadnewslistListener)
						.execute(new String[] { str, "news_toplist.html",
								"html" });
		}
	}

	public IBinder onBind(Intent paramIntent)
	{
		return null;
	}

	public void onCreate()
	{
		super.onCreate();
		setForeground(true);
	}

	public void onStart(Intent paramIntent, int paramInt)
	{
		super.onStart(paramIntent, paramInt);
		if (!Setting.hasTaskRunning)
		{
			Setting.hasTaskRunning = true;
			DownloadAllMsg.downloadswitch = true;
			if (Setting.IsCanUseSdCard())
			{
				File localFile1 = new File(Environment
						.getExternalStorageDirectory().getPath()
						+ "/"
						+ "tianshan" + "/html");
				if (localFile1.exists())
					localFile1.delete();
			} else
			{
				File localFile2 = new File(getApplicationContext()
						.getFilesDir().getAbsolutePath()
						+ "/"
						+ "tianshan"
						+ "/html");
				if (localFile2.exists())
					localFile2.delete();
			}
			init();
			mdownloaddoneListener.ondownloaddone(0);
			new Thread(new Runnable()
			{
				public void run()
				{
					OfflineService.this.downloadres.clear();
					OfflineService.this._checkDownloadCssJsPic();
				}
			}).start();
		}
	}

	public static abstract interface OnDownloadDoneListener
	{
		public abstract void ondownloaddone(int paramInt);
	}
}