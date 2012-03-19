package karroo.app.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class C2dmReceiver  extends BroadcastReceiver  {
	static String registration_id = null;
    static String c2dm_msg = "";
    public C2dmReceiver(){
    	
    }
    @Override
    public void onReceive(Context context, Intent intent) {
    	
        // 리시버로 받은 데이터가 Registration ID이면
        if (intent.getAction().equals(
                "com.google.android.c2dm.intent.REGISTRATION")) {
 
            handleRegistration(context, intent);
        }else if (intent.getAction().equals(
                "com.google.android.c2dm.intent.RECEIVE")) {
 
            // 추출
            c2dm_msg = intent.getStringExtra("msg");
 
            // 출력
            Log.v("C2DM", "C2DM Message : " + c2dm_msg);
            
            
            
            Intent newIntent = new Intent();
            newIntent.setAction("karroo.app.test.C2DM");
            newIntent.putExtra("message", c2dm_msg);
            context.sendBroadcast(newIntent);
            Toast toast = Toast.makeText(context, c2dm_msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 150);
            toast.show();
        }
    }
 
    public void handleRegistration(Context context, Intent intent) {
 
    	
        registration_id = intent.getStringExtra("registration_id");
 
        Log.v("C2DM", "Get the Registration ID From C2DM");
        Log.v("C2DM", "Registration ID : " + registration_id);
 
        String broadmessage = "";
        if (intent.getStringExtra("error") != null) {
            Log.v("C2DM", "error");
            broadmessage = "registration error";
        }else if (intent.getStringExtra("unregistered") != null) {
            Log.v("C2DM", "unregistered.");
            broadmessage = "unregistered";
        }else if (registration_id != null) {
            Log.v("C2DM", "Registration ID complete!");
 
            // Registration ID 저장
            SharedPreferences shrdPref = PreferenceManager
                    .getDefaultSharedPreferences(context);
 
            SharedPreferences.Editor editor = shrdPref.edit();
            editor.putString("registration_id", registration_id);
            editor.commit();
            broadmessage = registration_id;
        }
        
        Intent newIntent = new Intent();
        newIntent.setAction("karroo.app.test.C2DM");
        newIntent.putExtra("message", broadmessage);
        context.sendBroadcast(newIntent);
    }
}
