package com.insthub.ecmobile.activity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/*
 *@author Eric 
 *@2015-11-7����8:03:31
 */
public class UpdateManager {
	private static UpdateManager manager = null;
	private UpdateManager(){}
	public static UpdateManager getInstance(){
		manager = new UpdateManager();
		return manager;
	}
	
	//��ȡ�汾��
	public int getVersion(Context context){
		int version = 0;
		try {  
			version = context.getPackageManager().getPackageInfo(  
                    "com.insthub.jgmall", 0).versionCode;  
        } catch (Exception e) {  
        	 System.out.println("��ȡ�汾���쳣��");
        }  
		return version;
	}
	
	//��ȡ�汾��
	public String getVersionName(Context context){
		String versionName = null;
		try {
			versionName = context.getPackageManager().getPackageInfo(
					"com.insthub.jgmall", 0).versionName;
		} catch (Exception e) {
			 System.out.println("��ȡ�汾���쳣��");
		}
		return versionName;
	}
	
	//��ȡ�������汾��
	public String getServerVersion(){
		String serverJson = null;
		byte[] buffer = new byte[128];
		
		try {
			URL serverURL = new URL("http://gbugu.com/app/version.htm");
			HttpURLConnection connect = (HttpURLConnection) serverURL.openConnection();
			BufferedInputStream bis = new BufferedInputStream(connect.getInputStream());
			int n = 0;
			while((n = bis.read(buffer))!= -1){
				serverJson = new String(buffer);
			}
		} catch (Exception e) {
			System.out.println("发生错误："+e);
		}
		
		return serverJson;
	}	
	
	//�ȽϷ������汾�뱾�ذ汾�����Ի���
	public boolean compareVersion(Context context){
		
		final Context contextTemp = context;
		
		new Thread(){
			public void run() {
				Looper.prepare();
				String serverJson = manager.getServerVersion();
				
				//����Json���
				try {
					JSONArray array = new JSONArray(serverJson);
					JSONObject object = array.getJSONObject(0);
					String getServerVersion = object.getString("version");
					String getServerVersionName = object.getString("versionName");
					
					EcmobileMainActivity.serverVersion = Integer.parseInt(getServerVersion);
					EcmobileMainActivity.serverVersionName = getServerVersionName;
					
					if(EcmobileMainActivity.version < EcmobileMainActivity.serverVersion){
						//����һ���Ի���
			            AlertDialog.Builder builder  = new Builder(contextTemp);  
			            builder.setTitle("�汾����" ) ;  
			            builder.setMessage("��ǰ�汾��"+EcmobileMainActivity.versionName
			            		+"\n"+"�������汾��"+EcmobileMainActivity.serverVersionName ) ;  
			            builder.setPositiveButton("��������",new DialogInterface.OnClickListener() {  
			                   @Override  
			                   public void onClick(DialogInterface dialog, int arg1) { 
			                       //�����߳�����apk
			                	   new Thread(){
			                		   public void run() {
			                			   Looper.prepare();
			                			   downloadApkFile(contextTemp);
			                			   Looper.loop();
			                		   };
			                	   }.start();
			                   }  
			               });  
			            builder.setNegativeButton("�´���˵", null);  
			            builder.show();
					}else{
			            AlertDialog.Builder builder  = new Builder(contextTemp);  
			            builder.setTitle("�汾��Ϣ" ) ;  
			            builder.setMessage("��ǰ�Ѿ������°汾" ) ;  
			            builder.setPositiveButton("ȷ��",null);  
			            builder.show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					System.out.println("��ȡ�������汾�߳��쳣��"+e);
				}
				
				Looper.loop();
			};
			
		}.start();
		
		
		
		
		
		return false;
	}
	
	
	//����apk�ļ�
	public void downloadApkFile(Context context){
		String savePath = Environment.getExternalStorageDirectory()+"/JGMALL.apk";
		String serverFilePath = "http://gbugu.com/app/JGMALL.apk";
		try {
			if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){  
				URL serverURL = new URL(serverFilePath);
				HttpURLConnection connect = (HttpURLConnection) serverURL.openConnection();
				BufferedInputStream bis = new BufferedInputStream(connect.getInputStream());
				File apkfile = new File(savePath);
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(apkfile));
				
				int fileLength = connect.getContentLength();
				int downLength = 0;
				int progress = 0;
				int n;
				byte[] buffer = new byte[1024];
				while((n=bis.read(buffer, 0, buffer.length))!=-1){
					bos.write(buffer, 0, n);
					downLength +=n;
					progress = (int) (((float) downLength / fileLength) * 100);
					Message msg = new Message();
					msg.arg1 = progress;
					EcmobileMainActivity.handler2.sendMessage(msg);
					//System.out.println("����"+progress);
				}
				bis.close();
				bos.close();
				connect.disconnect();
	        } 
			
		} catch (Exception e) {
			System.out.println("���س��?"+e);
		}
		

		/*AlertDialog.Builder builder  = new Builder(context);  
        builder.setTitle("����apk" ) ;  
        builder.setMessage("��������" ) ;  
        builder.setPositiveButton("ȷ��",null);  
        builder.show();*/
		
		
		
	}
}
