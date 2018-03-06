package com.boyaa.luaviewer_helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.*;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.*;
import android.provider.Contacts;
import android.provider.UserDictionary;
import android.text.AndroidCharacter;
import android.util.Log;
import android.widget.Toast;

import com.boyaa.luaviewer_helper.*;

public class ViewerHelper extends Service
{
	public static final String LOG_TAG = "luaviewer_helper_test";
	public static final String AUTHORITY = "com.boyaa.test.providers.element";
//	public static final String AUTHORITY = "com.boyaa.application.providers.element";
//	public static final Uri requestUri = Uri.parse("content://"+AUTHORITY+"/getHierarchy");
    public static final Uri requestUri = Uri.parse("content://" + AUTHORITY + "/getHierarchy");
    File sdCardDir = new File(android.os.Environment.getExternalStorageDirectory().getPath());
    
	public static final String UIDUMP_DEVICE_PATH = "/mnt/sdcard/lua_uidump.json";
//    public static final String UIDUMP_DEVICE_PATH = android.os.Environment.getExternalStorageDirectory().getPath() + "/lua_uidump.json";
    
//    JSONToXML json2XML = null;
	private ContentResolver resolver;
	
	public static final Uri mCurrentURI = requestUri;
	public static Cursor cursor;
	
    @SuppressLint("NewApi")
	public void onCreate()
    {
    	String packageName = getRunningPackageName(getBaseContext());
//    	requestUri = Uri.parse("content://" + packageName + ".providers.element/getHierarchy");
//    	requestUri = Uri.parse("content://com.boyaa.sina.providers.element/getHierarchy");
    	Log.d(LOG_TAG, "CurrentPackage:"+packageName);
    	Log.d(LOG_TAG, "mCurrentURI:"+mCurrentURI);
        super.onCreate();
        resolver = getContentResolver();
        try {
        	cursor = resolver.query(mCurrentURI, null, null, null, null);
//        	resolver.query(mCurrentURI, null, null, null, null);
        } catch(NullPointerException e) {
        	throw new RuntimeException("the target APK is not running, or the provider is not found!Info: "+e);
        	
        }    
//        cursor = getContentResolver().query(mCurrentURI, null, null, null, null);
//        Log.d(LOG_TAG, "cursor:"+cursor);
         query();
		 	 // Stop yourself!
	     Handler handler = new Handler();
         handler.postDelayed(new Runnable() {
            public void run() {
            	ViewerHelper.this.stopSelf();
            }
        }, 200);
        // 
        //sendBroadcast(intent, receiverPermission);
    }
   
    private void query() {
//    	cursor = this.cursor;
//    	resolver = getContentResolver();
//    	Log.d(LOG_TAG, "mCurrentURI:" + mCurrentURI);

      try {
//          cursor = resolver.query(mCurrentURI, null, null, null, null);
          Log.d(LOG_TAG, "cursor:" + cursor.toString());
    	  Thread.sleep(6000);
          cursor.moveToFirst();
//          while (cursor!=null) {
          do {
              int count = cursor.getColumnCount();
              for (int i = 0; i < count; i++) {
                  Log.d(LOG_TAG, "cursor:" + cursor.getColumnName(i));
                  String string = cursor.getColumnName(i);
                  if(saveJson(string)==true){
                	  return;
                  };
              }
          }while (cursor.moveToNext());
		 	cursor.close();
		 	getContentResolver().delete(mCurrentURI, null, null);
//          cursor.close();
		
	} catch (Exception e) {
		// TODO: handle exception
	}

  }
    

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * get current foreground package name (by get top activity name)
	 * 
	 * @param context
	 *            context of activity
	 * @return current foreground package name
	 */
	public String getRunningPackageName(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
		if (runningTaskInfos != null) {
			String packageName = (runningTaskInfos.get(0).topActivity).getPackageName();
			Log.d(LOG_TAG, "packageName:"+packageName);
			return packageName;
		} else {
			return null;
		}
	}
	/*
	 * 创建文件   	public static final String UIDUMP_DEVICE_PATH = "/sdcard/lua_uidump.json";
	 */
	@SuppressLint("SdCardPath")
	public static void CreateFiletoSD(){
		String sDstateString = android.os.Environment.getExternalStorageState();
		if(sDstateString.equals(android.os.Environment.MEDIA_MOUNTED)){
			try{
//				File sdCardDir = new File(android.os.Environment.getExternalStorageDirectory().getPath()); //获取SDCard目录
				Log.d(LOG_TAG, UIDUMP_DEVICE_PATH);
				File destDir = new File(UIDUMP_DEVICE_PATH);
				if(!destDir.exists()){
					destDir.createNewFile();
				}
				else {
					File file = new File("lua_uidump.json");
					file.delete();
				}
			}catch(Exception e){	
				e.printStackTrace();
			}
		}
	}
	/*
	 * json保存
	 */
	
    public static boolean saveJson(String jsonString){
    	CreateFiletoSD();
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//            Toast.makeText(context, "sd卡不可用", Toast.LENGTH_SHORT).show();
            return false;
        }
        File file=new File(android.os.Environment.getExternalStorageDirectory().getPath());
        File file1 = new File(file+"/lua_uidump.json");
        try {
            FileOutputStream fos=new FileOutputStream(file1);
            String info=jsonString;
            fos.write(info.getBytes());
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
    }
	
}
