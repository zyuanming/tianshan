package com.tianshan.source;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.Log;

public class HttpsRequest
{
	public static final String BOUNDARY = "7cd4a6d158c";
	public static final String DELETE = "DELETE";
	public static final String END_MP_BOUNDARY = "--7cd4a6d158c--";
	public static final String GET = "GET";
	public static final String MP_BOUNDARY = "--7cd4a6d158c";
	public static final String MULTIPART_FORM_DATA = "multipart/form-data";
	public static final String POST = "POST";
	private static final int SET_CONNECTION_TIMEOUT = 50000;
	private static final int SET_SOCKET_TIMEOUT = 200000;

	public static String encodeParameters(ArrayList arraylist)
	{
		String s1 = null;
		if (arraylist != null)
		{
			StringBuilder stringbuilder = new StringBuilder();
			int i = 0;
			int j = 0;
			do
			{
				if (j > arraylist.size())
					break;
				s1 = stringbuilder.toString();
				String s = ((BasicNameValuePair) arraylist.get(j)).getName();
				if (i != 0)
					stringbuilder.append("&");
				try
				{
					stringbuilder
							.append(URLEncoder.encode(s, "UTF-8"))
							.append("=")
							.append(URLEncoder.encode(
									((BasicNameValuePair) arraylist.get(j))
											.getValue(), "UTF-8"));
				} catch (UnsupportedEncodingException unsupportedencodingexception)
				{
					unsupportedencodingexception.printStackTrace();
				}
				i++;
				j++;
			} while (true);
		} else
		{
			s1 = "";
		}
		return s1;
	}

	public static String encodeUrl(ArrayList arraylist)
	{
		String s = null;
		if (arraylist != null)
		{
			StringBuilder stringbuilder = new StringBuilder();
			boolean flag = true;
			int i = 0;
			do
			{
				if (i > arraylist.size())
					break;
				s = stringbuilder.toString();
				if (flag)
					flag = false;
				else
					stringbuilder.append("&");
				stringbuilder.append((new StringBuilder(String
						.valueOf(URLEncoder
								.encode(((BasicNameValuePair) arraylist.get(i))
										.getName()))))
						.append("=")
						.append(URLEncoder
								.encode(((BasicNameValuePair) arraylist.get(i))
										.getValue())).toString());
				i++;
			} while (true);
		} else
		{
			s = "";
		}
		return s;
	}

	public static HttpClient getHttpClient(Context paramContext)
	{
		BasicHttpParams localBasicHttpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(localBasicHttpParams, 50000);
		HttpConnectionParams.setSoTimeout(localBasicHttpParams, 200000);
		return new DefaultHttpClient(localBasicHttpParams);
	}

	public static HttpClient getNewHttpClient()
	{
		DefaultHttpClient localDefaultHttpClient;
		try
		{
			KeyStore localKeyStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			localKeyStore.load(null, null);
			MySSLSocketFactory localMySSLSocketFactory = new MySSLSocketFactory(
					localKeyStore);
			localMySSLSocketFactory
					.setHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			BasicHttpParams localBasicHttpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(localBasicHttpParams,
					10000);
			HttpConnectionParams.setSoTimeout(localBasicHttpParams, 10000);
			HttpProtocolParams.setVersion(localBasicHttpParams,
					HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(localBasicHttpParams, "UTF-8");
			SchemeRegistry localSchemeRegistry = new SchemeRegistry();
			localSchemeRegistry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			localSchemeRegistry.register(new Scheme("https",
					localMySSLSocketFactory, 443));
			ThreadSafeClientConnManager localThreadSafeClientConnManager = new ThreadSafeClientConnManager(
					localBasicHttpParams, localSchemeRegistry);
			HttpConnectionParams.setConnectionTimeout(localBasicHttpParams,
					50000);
			HttpConnectionParams.setSoTimeout(localBasicHttpParams, 200000);
			localDefaultHttpClient = new DefaultHttpClient(
					localThreadSafeClientConnManager, localBasicHttpParams);
		} catch (Exception localException)
		{
			localDefaultHttpClient = new DefaultHttpClient();
		}
		return localDefaultHttpClient;
	}

	private static void imageContentToUpload(OutputStream outputstream,
			String s, String s1)
	{
		Bitmap bitmap;
		Bitmap bitmap1;
		byte abyte0[];
		StringBuilder stringbuilder = new StringBuilder();
		File file = new File(s1);
		bitmap = BitmapFactory.decodeFile(s1);
		bitmap1 = scaleBitmap(bitmap, 1024, 768);
		Log.d("bit",
				(new StringBuilder(String.valueOf(file.length()))).append(
						" size").toString());
		Log.d("bit", (new StringBuilder(String.valueOf(bitmap.getWidth())))
				.append("  width + height  ").append(bitmap.getHeight())
				.toString());
		Log.d("bit", (new StringBuilder(String.valueOf(bitmap1.getWidth())))
				.append("  width + height  ").append(bitmap1.getHeight())
				.toString());
		stringbuilder.append("--7cd4a6d158c").append("\r\n");
		stringbuilder
				.append((new StringBuilder(
						"Content-Disposition: form-data; name=\"")).append(s)
						.append("\"; filename=\"").toString())
				.append(file.getName()).append("\"\r\n");
		stringbuilder.append("Content-Type: ").append("image/png")
				.append("\r\n\r\n");
		abyte0 = stringbuilder.toString().getBytes();
		try
		{
			outputstream.write(abyte0);
			bitmap1.compress(android.graphics.Bitmap.CompressFormat.PNG, 100,
					outputstream);
			bitmap1.recycle();
			bitmap.recycle();
			outputstream.write("\r\n".getBytes());
			outputstream.write("\r\n--7cd4a6d158c--".getBytes());
		} catch (Exception e)
		{
			DEBUG.o(e.getMessage());
			if (outputstream != null)
			{
				try
				{
					outputstream.close();
				} catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
		}
	}

	public static String openUrl(String s, String s1, ArrayList arraylist)
	{
		return openUrl(s, s1, arraylist, "", "", "");
	}

	public static String openUrl(String s, String s1, ArrayList arraylist,
			String s2, String s3, String s4)
	{
		HttpClient httpclient = getNewHttpClient();
		Object obj = null;
		if (!TextUtils.isEmpty(s4))
		{
			CookieStore cookiestore;
			String as[];
			cookiestore = ((DefaultHttpClient) httpclient).getCookieStore();
			as = s4.split(";");
			for (int j = 0; j < as.length; j++)
			{
				String as1[] = as[j].split("=");
				Log.d("cook", as1[0]);
				Log.d("cook", as1[1]);
				BasicClientCookie basicclientcookie = new BasicClientCookie(
						as1[0], as1[1]);
				basicclientcookie.setVersion(0);
				basicclientcookie.setDomain(".xjts.cn");
				basicclientcookie.setPath("/");
				cookiestore.addCookie(basicclientcookie);
			}
			((DefaultHttpClient) httpclient).setCookieStore(cookiestore);
		}

		String s5;
		try
		{
			if (!s1.equals("GET"))
			{
				if (!s1.equals("POST"))
				{
					boolean flag = s1.equals("DELETE");
					if (flag)
						obj = new HttpDelete(s);

				} else
				{
					HttpPost httppost;
					ByteArrayOutputStream bytearrayoutputstream;
					httppost = new HttpPost(s);
					bytearrayoutputstream = new ByteArrayOutputStream(51200);
					if (TextUtils.isEmpty(s3))
					{
						httppost.setHeader("Content-Type",
								"application/x-www-form-urlencoded");
						bytearrayoutputstream.write(encodeParameters(arraylist)
								.getBytes("UTF-8"));
					} else
					{
						paramToUpload(bytearrayoutputstream, arraylist);
						httppost.setHeader("Content-Type",
								"multipart/form-data; boundary=7cd4a6d158c");
						imageContentToUpload(bytearrayoutputstream, s2, s3);
					}
					byte abyte0[] = bytearrayoutputstream.toByteArray();
					bytearrayoutputstream.close();
					httppost.setEntity(new ByteArrayEntity(abyte0));
					obj = httppost;
					((HttpUriRequest) (obj)).setHeader("HTTP_REFERER", s);

				}
			} else
			{
				obj = new HttpGet((new StringBuilder(String.valueOf(s)))
						.append("?").append(encodeUrl(arraylist)).toString());
			}

			HttpResponse httpresponse = httpclient
					.execute(((HttpUriRequest) (obj)));
			int i = httpresponse.getStatusLine().getStatusCode();
			DEBUG.o((new StringBuilder("statusCode = ")).append(i).toString());
			s5 = read(httpresponse);
		} catch (Exception e)
		{
			s5 = "";
			DEBUG.o(e.getMessage());
			return s5;
		}
		return s5;
	}

	private static void paramToUpload(OutputStream paramOutputStream,
			ArrayList<BasicNameValuePair> paramArrayList)
	{
		for (int i = 0; i < paramArrayList.size(); i++)
		{
			String str = ((BasicNameValuePair) paramArrayList.get(i)).getName();
			StringBuilder localStringBuilder = new StringBuilder(10);
			localStringBuilder.setLength(0);
			localStringBuilder.append("--7cd4a6d158c").append("\r\n");
			localStringBuilder
					.append("content-disposition: form-data; name=\"")
					.append(str).append("\"\r\n\r\n");
			localStringBuilder.append(
					((BasicNameValuePair) paramArrayList.get(i)).getValue())
					.append("\r\n");
			byte[] arrayOfByte = localStringBuilder.toString().getBytes();
			try
			{
				paramOutputStream.write(arrayOfByte);
			} catch (IOException localIOException)
			{
				DEBUG.o(localIOException.getMessage());
			}
		}
	}

	private static String read(InputStream inputstream) throws IOException
	{
		StringBuilder stringbuilder = new StringBuilder();
		BufferedReader bufferedreader = new BufferedReader(
				new InputStreamReader(inputstream), 1000);
		String s = bufferedreader.readLine();
		do
		{
			if (s == null)
			{
				inputstream.close();
				return stringbuilder.toString();
			}
			stringbuilder.append(s);
			s = bufferedreader.readLine();
		} while (true);
	}

	private static String read(HttpResponse paramHttpResponse)
	{
		HttpEntity localHttpEntity = paramHttpResponse.getEntity();
		String str = null;
		try
		{
			Object localObject = localHttpEntity.getContent();
			ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
			Header localHeader = paramHttpResponse
					.getFirstHeader("Content-Encoding");
			if ((localHeader != null)
					&& (localHeader.getValue().toLowerCase().indexOf("gzip") > -1))
				localObject = new GZIPInputStream((InputStream) localObject);
			byte[] arrayOfByte = new byte[512];
			while (true)
			{
				int i = ((InputStream) localObject).read(arrayOfByte);
				if (i == -1)
				{
					str = new String(localByteArrayOutputStream.toByteArray());
					break;
				}
				localByteArrayOutputStream.write(arrayOfByte, 0, i);
			}
		} catch (IllegalStateException localIllegalStateException)
		{
			DEBUG.o(localIllegalStateException.getMessage());
			str = "";
		} catch (IOException localIOException)
		{
			DEBUG.o(localIOException.getMessage());
		}
		return str;
	}

	public static Bitmap scaleBitmap(Bitmap bitmap, int i, int j)
	{
		if (bitmap != null || i >= 1 || j >= 1)
		{
			int k = bitmap.getWidth();
			int l = bitmap.getHeight();
			if (k > i || l > j)
			{
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
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, k, l, matrix, true);
			}
		} else
		{
			bitmap = null;
		}
		return bitmap;
	}

	public static class MySSLSocketFactory extends SSLSocketFactory
	{
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore keystore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException
		{
			super(keystore);
			X509TrustManager local1 = new X509TrustManager()
			{
				public void checkClientTrusted(
						X509Certificate[] paramAnonymousArrayOfX509Certificate,
						String paramAnonymousString)
						throws CertificateException
				{}

				public void checkServerTrusted(
						X509Certificate[] paramAnonymousArrayOfX509Certificate,
						String paramAnonymousString)
						throws CertificateException
				{}

				public X509Certificate[] getAcceptedIssuers()
				{
					return null;
				}
			};
			this.sslContext.init(null, new TrustManager[] { local1 }, null);
		}

		public Socket createSocket() throws IOException
		{
			return this.sslContext.getSocketFactory().createSocket();
		}

		public Socket createSocket(Socket paramSocket, String paramString,
				int paramInt, boolean paramBoolean) throws IOException,
				UnknownHostException
		{
			return this.sslContext.getSocketFactory().createSocket(paramSocket,
					paramString, paramInt, paramBoolean);
		}
	}
}