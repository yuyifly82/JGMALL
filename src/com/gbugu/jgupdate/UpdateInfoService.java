package com.gbugu.jgupdate;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.external.activeandroid.util.Log;
import com.insthub.ecmobile.activity.EcmobileMainActivity;
import com.insthub.ecmobile.activity.GalleryImageActivity;
import com.insthub.jgmall.R.string;

import android.R.integer;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

public class UpdateInfoService {
	ProgressDialog progressDialog;
	Handler handler;
	Context context;
	UpdateInfo updateInfo;
	
	public UpdateInfoService(Context context){
		this.context=context;
	}
	
	public UpdateInfo getUpDateInfo() throws Exception {
		String serverJson = null;
		byte[] buffer = new byte[128];
		
		try {
			URL serverURL = new URL("http://www.gbugu.com/app/version.htm");
			HttpURLConnection connect = (HttpURLConnection) serverURL.openConnection();
			connect.setRequestMethod("GET");
			InputStream in=connect.getInputStream();
			BufferedReader reader=new BufferedReader(new InputStreamReader(in));
			StringBuilder response=new StringBuilder();
			String line;
			while ( (line = reader.readLine()) != null){
				response.append(line);
			}
			
			serverJson=response.toString();
			/*BufferedInputStream bis = new BufferedInputStream(connect.getInputStream());
			
			int n = 0;
			while((n = bis.read(buffer))!= -1){
				serverJson = new String(buffer);
			}*/
		} catch (Exception e) {
				System.out.println("获取服务器版本号异常！"+e);
		}
		
		JSONArray array = new JSONArray(serverJson);
		JSONObject object = array.getJSONObject(0);
		String getDescription = object.getString("description");
		Integer getServerVersion = object.getInt("version");
		String getApkUrl = object.getString("apkurl");
		UpdateInfo updateInfo = new UpdateInfo();
		updateInfo.setVersion(getServerVersion);
		updateInfo.setDescription(getDescription);
		updateInfo.setUrl(getApkUrl);
		this.updateInfo=updateInfo;
		return updateInfo;
	}

	
	public boolean isNeedUpdate(){
			int new_version = updateInfo.getVersion(); // ���°汾�İ汾��
			//��ȡ��ǰ�汾��
			int now_version=0;
			try {
				PackageManager packageManager = context.getPackageManager();
				PackageInfo packageInfo = packageManager.getPackageInfo(
						context.getPackageName(), 0);
				now_version= packageInfo.versionCode;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			if (new_version<=now_version) {
				return false;
			} else {
				return true;
			}
	}
	
	
	public void downLoadFile(final String url,final ProgressDialog pDialog,Handler h){
		progressDialog=pDialog;
		handler=h;
		new Thread() {
			public void run() {        
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					int length = (int) entity.getContentLength();   //��ȡ�ļ���С
                                        progressDialog.setMax(length);                            //���ý�������ܳ���
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						File file = new File(
								Environment.getExternalStorageDirectory(),
								"JGMALL.apk");
						fileOutputStream = new FileOutputStream(file);
						//����ǻ�����һ�ζ�ȡ10�����أ���Ū��С�˵㣬��Ϊ�ڱ��أ�������ֵ̫��һ�¾���������,
						//������progressbar��Ч��
                        byte[] buf = new byte[1024];   
						int ch = -1;
						int process = 0;
						while ((ch = is.read(buf)) != -1) {       
							fileOutputStream.write(buf, 0, ch);
							process += ch;
							progressDialog.setProgress(process);       //������ǹؼ��ʵʱ���½���ˣ�
						}

					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					down();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}.start();
	}
	
	void down() {
		handler.post(new Runnable() {
			public void run() {
				progressDialog.cancel();
				update();
			}
		});
	}

	void update() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), "JGMALL.apk")),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	
}
