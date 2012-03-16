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
     
    @Override
    public void onReceive(Context context, Intent intent) {
        // 리시버로 받은 데이터가 Registration ID이면
        if (intent.getAction().equals(
                "com.google.android.c2dm.intent.REGISTRATION")) {
 
            handleRegistration(context, intent);
        }
        // 리시버가 받은 데이터가 메세지이면
        else if (intent.getAction().equals(
                "com.google.android.c2dm.intent.RECEIVE")) {
 
            // 추출
            c2dm_msg = intent.getStringExtra("msg");
 
            // 출력
            Log.v("C2DM", "C2DM Message : " + c2dm_msg);
            
            Toast toast = Toast.makeText(context, c2dm_msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 150);
            toast.show();
        }
    }
 
    public void handleRegistration(Context context, Intent intent) {
 
        registration_id = intent.getStringExtra("registration_id");
 
        Log.v("C2DM", "Get the Registration ID From C2DM");
        Log.v("C2DM", "Registration ID : " + registration_id);
 
        // 받은 메세지가 error일 경우
        if (intent.getStringExtra("error") != null) {
            Log.v("C2DM", "C2DM REGISTRATION : Registration failed,"
                    + "should try again later");
        }
        // 받은 메세지가 unregistered일 경우
        else if (intent.getStringExtra("unregistered") != null) {
            Log.v("C2DM", "C2DM REGISTRATION : unregistration done, "
                    + "new messages from the authorized "
                    + "sender will be rejected");
        }
        // 받은 메세지가 Registration ID일 경우
        else if (registration_id != null) {
            Log.v("C2DM", "Registration ID complete!");
 
            // Registration ID 저장
            SharedPreferences shrdPref = PreferenceManager
                    .getDefaultSharedPreferences(context);
 
            SharedPreferences.Editor editor = shrdPref.edit();
            editor.putString("registration_id", registration_id);
            editor.commit();
        }
    }
}