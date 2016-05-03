package com.insthub.ecmobile.activity;
//
//                       __
//                      /\ \   _
//    ____    ____   ___\ \ \_/ \           _____    ___     ___
//   / _  \  / __ \ / __ \ \    <     __   /\__  \  / __ \  / __ \
//  /\ \_\ \/\  __//\  __/\ \ \\ \   /\_\  \/_/  / /\ \_\ \/\ \_\ \
//  \ \____ \ \____\ \____\\ \_\\_\  \/_/   /\____\\ \____/\ \____/
//   \/____\ \/____/\/____/ \/_//_/         \/____/ \/___/  \/___/
//     /\____/
//     \/___/
//
//  Powered by BeeFramework
//

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Build;
import com.insthub.ecmobile.fragment.D0_CategoryFragment;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.gbugu.jgupdate.UpdateInfo;
import com.gbugu.jgupdate.UpdateInfoService;
import com.iflytek.cloud.SpeechUtility;
import com.insthub.BeeFramework.BeeFrameworkApp;
import com.insthub.BeeFramework.model.BeeQuery;
import com.insthub.BeeFramework.view.ToastView;
import com.insthub.ecmobile.EcmobileManager;
import com.insthub.jgmall.R;
import com.insthub.ecmobile.protocol.FILTER;
import com.umeng.analytics.MobclickAgent;


public class EcmobileMainActivity extends FragmentActivity
{

    public static final String RESPONSE_METHOD = "method";
    public static final String RESPONSE_CONTENT = "content";
    public static final String RESPONSE_ERRCODE = "errcode";
    protected static final String ACTION_LOGIN = "com.baidu.pushdemo.action.LOGIN";
    public static final String ACTION_MESSAGE = "com.baiud.pushdemo.action.MESSAGE";
    public static final String ACTION_RESPONSE = "bccsclient.action.RESPONSE";
    public static final String ACTION_PUSHCLICK = "bccsclient.action.PUSHCLICK";
    public static final String ACTION_SHOW_MESSAGE = "bccsclient.action.SHOW_MESSAGE";
    protected static final String EXTRA_ACCESS_TOKEN = "access_token";
    public static final String EXTRA_MESSAGE = "message";
    public static final String CUSTOM_CONTENT ="CustomContent";
    

    

	
    // 在百度开发者中心查询应用的API Key
    public static String API_KEY ;

    public void onCreate(Bundle savedInstanceState)
    {
		SpeechUtility.createUtility(EcmobileMainActivity.this, "appid=" + EcmobileManager.getXunFeiCode(EcmobileMainActivity.this));
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	    checkUpdate();//检查是否有更新
	    Intent intent = new Intent();
		intent.setAction("com.BeeFramework.NetworkStateService");
		startService(intent);
		
		
		if(getIntent().getStringExtra(CUSTOM_CONTENT) != null) {
			pushIntent(getIntent().getStringExtra(CUSTOM_CONTENT));
		}
	    
    }
    
   

    @Override
    protected void onNewIntent(Intent intent) {
        // 如果要统计Push引起的用户使用应用情况，请实现本方法，且加上这一个语句
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();

        if (ACTION_RESPONSE.equals(action))
        {

        } else if (ACTION_LOGIN.equals(action))
        {

        }
        else if (ACTION_MESSAGE.equals(action))
        {

        }
        else if (ACTION_PUSHCLICK.equals(action))
        {
            String message = intent.getStringExtra(CUSTOM_CONTENT);
            pushIntent(message);
        }
    }
    
    public void pushIntent(String message) {
    	if(message != null) {
        	try
            {
                JSONObject jsonObject = new JSONObject(message);
                String actionString = jsonObject.optString("a");
                if (0 == actionString.compareTo("s")) {
                    String parameter = jsonObject.optString("k");
                    if (null != parameter && parameter.length() > 0) {
                    	try {
							parameter = URLDecoder.decode(parameter,"UTF-8");
						} catch (UnsupportedEncodingException e1) {
							 
							e1.printStackTrace();
						} 
                        Intent it = new Intent(this, B1_ProductListActivity.class);
                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        FILTER filter = new FILTER();
                        filter.keywords =parameter;
                        try {
                            it.putExtra(B1_ProductListActivity.FILTER,filter.toJson().toString());
                        } catch (JSONException e) {
                        	e.printStackTrace();
                        }
                        startActivity(it);
                    }
                } else if(0 == actionString.compareTo("w")) {
                	String parameter = jsonObject.optString("u");
                	if (null != parameter && parameter.length() > 0) {
                		try {
							parameter = URLDecoder.decode(parameter,"UTF-8");
						} catch (UnsupportedEncodingException e1) {
							 
							e1.printStackTrace();
						}
                        Intent it = new Intent(this, BannerWebActivity.class);
                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        it.putExtra("url",parameter);
                        startActivity(it);
                    }
                }
            } catch (JSONException e) {

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        
         {
        	API_KEY = EcmobileManager.getPushKey(this);
            PushManager.activityStarted(this);
            PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, API_KEY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(EcmobileManager.getUmengKey(this)!=null){
            MobclickAgent.onResume(this, EcmobileManager.getUmengKey(this), "");
        }

    }

    private boolean isExit = false;
    //退出操作
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {        
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(isExit==false){
                isExit=true;
                Resources resource = (Resources) getBaseContext().getResources();
                String exit=resource.getString(R.string.again_exit);                
                ToastView toast = new ToastView(getApplicationContext(), exit);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                handler.sendEmptyMessageDelayed(0, 3000);
                if(BeeQuery.environment() == BeeQuery.ENVIROMENT_DEVELOPMENT)
                {
                    BeeFrameworkApp.getInstance().showBug(this);
                }

                return true;
            } else {
            	Intent intent = new Intent();
        		intent.setAction("com.BeeFramework.NetworkStateService");
        		stopService(intent);
                finish();
                ToastView.cancel();
                return false;
            }
        }
        return true;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit=false;
        }
    };


    @Override
    protected void onStop() {
        super.onStop();
        PushManager.activityStoped(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(EcmobileManager.getUmengKey(this)!=null){
            MobclickAgent.onPause(this);
        }
    }
    
    
    private UpdateInfo info;
    private UpdateInfoService updateInfoService;
    private ProgressDialog progressDialog;
    private void checkUpdate(){
		// 自动检查有没有新版本 如果有新版本就提示更新
		new Thread() {
			@Override
			public void run() {
				try {
					updateInfoService = new UpdateInfoService(EcmobileMainActivity.this);
					info = updateInfoService.getUpDateInfo();
					handler1.sendEmptyMessage(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
   }
   
	@SuppressLint("HandlerLeak")
	private Handler handler1 = new Handler() {
		public void handleMessage(Message msg) {
			// 如果有更新就提示
			if (updateInfoService.isNeedUpdate()) {
				showUpdateDialog();
			}
		};
	};

	
	//显示是否要更新的对话框
	private void showUpdateDialog() {
		Activity activity = this; 
        while (activity.getParent() != null) {  
            activity = activity.getParent();  
            System.out.println(activity.getLocalClassName());
        }  
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle("请升级APP至版本" + info.getVersion());
		builder.setMessage(info.getDescription());
		builder.setCancelable(false);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					downFile(info.getUrl());
				} else {
					Toast.makeText(EcmobileMainActivity.this, "SD卡不可用，请插入SD卡",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
	}

	void downFile(final String url) { 
		progressDialog = new ProgressDialog(EcmobileMainActivity.this);    //进度条，在下载的时候实时更新进度，提高用户友好度
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setTitle("正在下载");
		progressDialog.setMessage("请稍候...");
		progressDialog.setProgress(0);
		progressDialog.show();
		updateInfoService.downLoadFile(url, progressDialog,handler1);
	}
}
