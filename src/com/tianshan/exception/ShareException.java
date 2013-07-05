package com.tianshan.exception;

import java.util.HashMap;

public class ShareException extends Exception
{
	private static final long serialVersionUID = -5654394285242608040L;
	private int err_code;
	protected HashMap<Integer, String> qqErrorMsg = null;
	private int sharetype;
	protected HashMap<Integer, String> sinaErrorMsg = null;

	public ShareException()
	{}

	public ShareException(int paramInt1, int paramInt2)
	{
		this.sharetype = paramInt1;
		this.err_code = paramInt2;
		switch (this.sharetype)
		{
		case 2:
		default:
			return;
		case 3:

			this.sinaErrorMsg = new HashMap();
			fillSinaErrorMsg();
			break;
		case 1:
		case 4:
			this.qqErrorMsg = new HashMap();
			fillQQErrorMsg();
			break;
		}
	}

	public ShareException(int paramInt1, String paramString, int paramInt2)
	{
		super(paramString);
		this.sharetype = paramInt1;
		this.err_code = paramInt2;
	}

	public ShareException(String paramString)
	{
		super(paramString);
	}

	public ShareException(String paramString, Throwable paramThrowable)
	{
		super(paramString, paramThrowable);
	}

	public ShareException(Throwable paramThrowable)
	{
		super(paramThrowable);
	}

	private void fillQQErrorMsg()
	{
		this.qqErrorMsg.put(Integer.valueOf(100000),
				"缺少参数response_type或response_type非法");
		this.qqErrorMsg.put(Integer.valueOf(100001), "缺少参数client_id");
		this.qqErrorMsg.put(Integer.valueOf(100002), "缺少参数client_secret");
		this.qqErrorMsg.put(Integer.valueOf(100003),
				"http head中缺少Authorization");
		this.qqErrorMsg.put(Integer.valueOf(100004),
				"缺少参数grant_type或grant_type非法");
		this.qqErrorMsg.put(Integer.valueOf(100005), "缺少参数code");
		this.qqErrorMsg.put(Integer.valueOf(100006), "缺少refresh token");
		this.qqErrorMsg.put(Integer.valueOf(100007), "缺少access token");
		this.qqErrorMsg.put(Integer.valueOf(100008), "该appid不存在");
		this.qqErrorMsg
				.put(Integer.valueOf(100009), "client_secret（即appkey）非法");
		this.qqErrorMsg.put(Integer.valueOf(100010), "回调地址不合法");
		this.qqErrorMsg.put(Integer.valueOf(100011), "APP不处于上线状态");
		this.qqErrorMsg.put(Integer.valueOf(100012), "HTTP请求非post方式");
		this.qqErrorMsg.put(Integer.valueOf(100013), "access token非法");
		this.qqErrorMsg.put(Integer.valueOf(100014), "access token过期");
		this.qqErrorMsg.put(Integer.valueOf(100015), "access token废除");
		this.qqErrorMsg.put(Integer.valueOf(100016), "access token验证失败");
		this.qqErrorMsg.put(Integer.valueOf(100017), "获取appid失败");
		this.qqErrorMsg.put(Integer.valueOf(100018), "获取code值失败");
		this.qqErrorMsg.put(Integer.valueOf(100019), "用code换取access token值失败");
		this.qqErrorMsg.put(Integer.valueOf(100020), "code被重复使用");
		this.qqErrorMsg.put(Integer.valueOf(100021), "获取access token值失败");
		this.qqErrorMsg.put(Integer.valueOf(100022), "获取refresh token值失败");
		this.qqErrorMsg.put(Integer.valueOf(100023), "获取app具有的权限列表失败");
		this.qqErrorMsg.put(Integer.valueOf(100024), "获取某OpenID对某appid的权限列表失败");
		this.qqErrorMsg.put(Integer.valueOf(100025), "获取全量api信息、全量分组信息失败");
		this.qqErrorMsg.put(Integer.valueOf(100026), "设置用户对某app授权api列表失败");
		this.qqErrorMsg.put(Integer.valueOf(100027), "设置用户对某app授权时间失败");
		this.qqErrorMsg.put(Integer.valueOf(100028), "缺少参数which");
		this.qqErrorMsg.put(Integer.valueOf(100029), "错误的http请求");
		this.qqErrorMsg.put(Integer.valueOf(100030),
				"用户没有对该api进行授权，或用户在腾讯侧删除了该api的权限,请用户重新走登录、授权流程，对该api进行授权");
		this.qqErrorMsg.put(Integer.valueOf(100031), "第三方应用没有对该api操作的权限");
	}

	private void fillSinaErrorMsg()
	{
		this.sinaErrorMsg.put(Integer.valueOf(10001), "系统错误");
		this.sinaErrorMsg.put(Integer.valueOf(10002), "服务暂停");
		this.sinaErrorMsg.put(Integer.valueOf(10003), "远程服务错误");
		this.sinaErrorMsg.put(Integer.valueOf(10004), "IP限制不能请求该资源");
		this.sinaErrorMsg.put(Integer.valueOf(10005), "该资源需要appkey拥有授权");
		this.sinaErrorMsg.put(Integer.valueOf(10006), "缺少source (appkey) 参数");
		this.sinaErrorMsg.put(Integer.valueOf(10007), "不支持的MediaType");
		this.sinaErrorMsg.put(Integer.valueOf(10008), "参数错误，请参考API文档");
		this.sinaErrorMsg.put(Integer.valueOf(10009), "任务过多，系统繁忙");
		this.sinaErrorMsg.put(Integer.valueOf(10010), "任务超时");
		this.sinaErrorMsg.put(Integer.valueOf(10011), "RPC错误");
		this.sinaErrorMsg.put(Integer.valueOf(10012), "非法请求");
		this.sinaErrorMsg.put(Integer.valueOf(10013), "不合法的微博用户");
		this.sinaErrorMsg.put(Integer.valueOf(10014), "应用的接口访问权限受限");
		this.sinaErrorMsg.put(Integer.valueOf(10016), "缺失必要参数 ，请参考API文档");
		this.sinaErrorMsg.put(Integer.valueOf(10017), "参数值非�?，请参考API文档");
		this.sinaErrorMsg.put(Integer.valueOf(10018), "请求长度超过限制");
		this.sinaErrorMsg.put(Integer.valueOf(10020), "接口不存在");
		this.sinaErrorMsg.put(Integer.valueOf(10021), "请求的HTTP方法不支持");
		this.sinaErrorMsg.put(Integer.valueOf(10022), "IP请求频次超过上限");
		this.sinaErrorMsg.put(Integer.valueOf(10023), "用户请求频次超过上限");
		this.sinaErrorMsg.put(Integer.valueOf(10024), "用户请求特殊接口频次超过上限");
		this.sinaErrorMsg.put(Integer.valueOf(21314), "Token已经被使用");
		this.sinaErrorMsg.put(Integer.valueOf(21315), "Token已经过期");
		this.sinaErrorMsg.put(Integer.valueOf(21316), "Token不合法");
		this.sinaErrorMsg.put(Integer.valueOf(21317), "Token不合法");
		this.sinaErrorMsg.put(Integer.valueOf(21319), "授权关系已经被解除");
		this.sinaErrorMsg.put(Integer.valueOf(21332), "Token已经失效,请重新绑定账号");
	}

	public int getErrorCode()
	{
		return this.err_code;
	}

	public String getMessage()
	{
		String str1 = "无类型错误";
		switch (this.sharetype)
		{
		default:
			return str1;
		case 3:
			if (this.sinaErrorMsg.containsKey(Integer.valueOf(this.err_code)))
				str1 = (String) this.sinaErrorMsg.get(Integer
						.valueOf(this.err_code));
			break;
		case 1:
		case 4:
			if (this.qqErrorMsg.containsKey(Integer.valueOf(this.err_code)))
				str1 = (String) this.qqErrorMsg.get(Integer
						.valueOf(this.err_code));
			break;
		case 2:
			str1 = super.getMessage();
			break;
		}
		return str1;
	}

	public int getShareType()
	{
		return this.sharetype;
	}
}