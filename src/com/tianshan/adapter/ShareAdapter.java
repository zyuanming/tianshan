package com.tianshan.adapter;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.tianshan.R;
import com.tianshan.activity.AuthorizeActivity;
import com.tianshan.activity.ShareActivity;
import com.tianshan.activity.ShareEditContentActivity;
import com.tianshan.dbhelper.ShareBindHelper;
import com.tianshan.exception.ShareException;
import com.tianshan.model.ShareBind;

/**
 * 处理分享帐号绑定的各个点击事件，并处理结果
 * 
 * @author lkh
 * 
 */
public class ShareAdapter
{
	private Context c;

	public ShareAdapter(Context paramContext)
	{
		this.c = paramContext;
	}

	public void createShareDialog(final Intent dataIntent)
	{
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(this.c);
		localBuilder.setTitle("分享");
		localBuilder.setNegativeButton("取消",
				new DialogInterface.OnClickListener()
				{
					public void onClick(
							DialogInterface paramAnonymousDialogInterface,
							int paramAnonymousInt)
					{
						paramAnonymousDialogInterface.cancel();
					}
				});
		try
		{
			IconListItemAdapter localIconListItemAdapter1 = new IconListItemAdapter(
					this.c, ShareActivity.ShareType.dataIdOrder,
					ShareActivity.ShareType.imageIdOrder,
					ShareActivity.ShareType.textOrder);
			localBuilder.setAdapter(localIconListItemAdapter1,
					new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialoginterface,
								int i)
						{
							ShareBindHelper sharebindhelper = ShareBindHelper
									.getInstance(c);
							switch (i)
							{
							default:
								return;
							case 0:
								dealTencentBlogClick(dataIntent,
										sharebindhelper);
								c.startActivity(dataIntent);
								break;
							case 1:
								dealQQSpaceClick(dataIntent, sharebindhelper);
								c.startActivity(dataIntent);
								break;
							case 2:
								dealSinaClick(dataIntent, sharebindhelper);
								c.startActivity(dataIntent);
								break;
							case 3:
								dealRenrenClick(dataIntent, sharebindhelper);
								c.startActivity(dataIntent);
								break;
							}
						}
					});
			localBuilder.create().show();
		} catch (Exception localException)
		{
			showToastMessage(localException.getMessage());
		}
	}

	protected void dealQQResult(JSONObject paramJSONObject, int paramInt)
			throws ShareException
	{
		try
		{
			if ((paramJSONObject.has("ret"))
					&& (paramJSONObject.getInt("ret") != 0))
				throw new ShareException(paramInt,
						paramJSONObject.getInt("ret"));
		} catch (JSONException localJSONException)
		{
			showToastMessage(localJSONException.getMessage());
		}
	}

	/**
	 * 处理QQ空间绑定事件
	 * 
	 * @param paramIntent
	 * @param paramShareBindHelper
	 */
	protected void dealQQSpaceClick(Intent paramIntent,
			ShareBindHelper paramShareBindHelper)
	{
		ShareBind localShareBind = paramShareBindHelper.getShareBindById(1);
		if (localShareBind.isBind())
		{
			paramIntent.setClass(this.c, ShareEditContentActivity.class);
			paramIntent.putExtra("access_token",
					localShareBind.getAccess_token());
			paramIntent.putExtra("open_id", localShareBind.getOpen_id());
		} else
		{
			paramIntent.setClass(this.c, AuthorizeActivity.class);
		}
		paramIntent.putExtra("sharetype", 1);
	}

	/**
	 * 处理人人绑定事件
	 * 
	 * @param intent
	 * @param sharebindhelper
	 */
	protected void dealRenrenClick(Intent intent,
			ShareBindHelper sharebindhelper)
	{
		ShareBind sharebind = sharebindhelper.getShareBindById(2);
		if (sharebind.isBind())
		{
			intent.setClass(c, ShareEditContentActivity.class);
			intent.putExtra("access_token", sharebind.getAccess_token());
		} else
		{
			intent.setClass(c, AuthorizeActivity.class);
		}
		intent.putExtra("sharetype", 2);
	}

	/**
	 * 处理人人绑定结果
	 * 
	 * @param paramJSONObject
	 * @param paramInt
	 * @throws ShareException
	 */
	protected void dealRenrenResult(JSONObject paramJSONObject, int paramInt)
			throws ShareException
	{
		try
		{
			if ((paramJSONObject.has("error_code"))
					&& (paramJSONObject.has("error_msg")))
				throw new ShareException(paramInt,
						paramJSONObject.getString("error_msg"),
						paramJSONObject.getInt("error_code"));
		} catch (JSONException localJSONException)
		{
			showToastMessage(localJSONException.getMessage());
		}
	}

	/**
	 * 处理所有绑定结果
	 * 
	 * @param paramString
	 * @param i
	 * @throws ShareException
	 */
	public void dealResult(String paramString, int i) throws ShareException
	{
		try
		{
			JSONObject jsongobject = new JSONObject(paramString);
			switch (i)
			{
			default:
				showToastMessage("这是火星发来的贺电么?!");
			case 1:
			case 4:
				dealQQResult(jsongobject, i);
				break;
			case 3:
				dealSinaResult(jsongobject, i);
				break;
			case 2:
				dealRenrenResult(jsongobject, i);
				break;
			}
		} catch (JSONException localJSONException)
		{
			localJSONException.printStackTrace();
		}
	}

	/**
	 * 处理绑定错误
	 * 
	 * @param i
	 * @param j
	 * @param intent
	 */
	public void dealResultError(int i, int j, Intent intent)
	{
		switch (i)
		{
		default:
			return;
		case 3:
			switch (j)
			{
			case 21314:
			case 21315:
			case 21316:
			case 21317:
			case 21319:
			case 21332:
				intent.setClass(c, AuthorizeActivity.class);
				c.startActivity(intent);
				break;
			}
			break;
		case 1:
		case 4:
			switch (j)
			{
			case 100013:
			case 100014:
			case 100015:
			case 100016:
			case 100030:
				intent.setClass(c, AuthorizeActivity.class);
				c.startActivity(intent);
				break;
			}
			break;
		case 2:
			switch (j)
			{
			case 200:
			case 202:
			case 2001:
			case 2002:
			case 10621:
				intent.setClass(c, AuthorizeActivity.class);
				c.startActivity(intent);
				break;
			}
		}
	}

	/**
	 * 处理分享绑定事件
	 * 
	 * @param paramInt
	 * @param paramIntent
	 */
	public void dealShareBtnClick(int paramInt, Intent paramIntent)
	{
		ShareBindHelper localShareBindHelper = ShareBindHelper
				.getInstance(this.c);
		switch (paramInt)
		{
		default:
			showToastMessage("哥们,点火星去了~!");
		case R.id.ll_sina:
			dealSinaClick(paramIntent, localShareBindHelper);
			this.c.startActivity(paramIntent);
			break;
		case R.id.ll_tencent_blog:
			dealTencentBlogClick(paramIntent, localShareBindHelper);
			this.c.startActivity(paramIntent);
			break;
		case R.id.ll_qq_space:
			dealQQSpaceClick(paramIntent, localShareBindHelper);
			this.c.startActivity(paramIntent);
			break;
		case R.id.ll_renren:
			dealRenrenClick(paramIntent, localShareBindHelper);
			this.c.startActivity(paramIntent);
			break;
		}
	}

	/**
	 * 处理新浪绑定事件
	 * 
	 * @param intent
	 * @param sharebindhelper
	 */
	protected void dealSinaClick(Intent intent, ShareBindHelper sharebindhelper)
	{
		ShareBind sharebind = sharebindhelper.getShareBindById(3);
		if (sharebind.isBind())
		{
			intent.setClass(c, ShareEditContentActivity.class);
			intent.putExtra("access_token", sharebind.getAccess_token());
		} else
		{
			intent.setClass(c, AuthorizeActivity.class);
		}
		intent.putExtra("sharetype", 3);
	}

	/**
	 * 处理新浪绑定结果
	 * 
	 * @param paramJSONObject
	 * @param paramInt
	 * @throws ShareException
	 */
	protected void dealSinaResult(JSONObject paramJSONObject, int paramInt)
			throws ShareException
	{
		int i = 0;
		if (paramJSONObject.has("error"))
			try
			{
				int j = paramJSONObject.getInt("error_code");
				i = j;
				throw new ShareException(paramInt, i);
			} catch (JSONException localJSONException)
			{
				showToastMessage(localJSONException.getMessage());
			}
	}

	/**
	 * 处理腾讯博客绑定事件
	 * 
	 * @param intent
	 * @param sharebindhelper
	 */
	protected void dealTencentBlogClick(Intent intent,
			ShareBindHelper sharebindhelper)
	{
		ShareBind sharebind = sharebindhelper.getShareBindById(4);
		if (sharebind.isBind())
		{
			intent.setClass(c, ShareEditContentActivity.class);
			intent.putExtra("access_token", sharebind.getAccess_token());
			intent.putExtra("open_id", sharebind.getOpen_id());
		} else
		{
			intent.setClass(c, AuthorizeActivity.class);
		}
		intent.putExtra("sharetype", 4);
	}

	public void showToastMessage(String paramString)
	{
		Toast.makeText(this.c, paramString, 1).show();
	}
}