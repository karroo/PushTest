package karroo.app.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class C2DMProcessor  implements IHandlerDelegate,IReceiverDelegate{
	final int HANDLER_ID = 2; 
	String registration_id = null;
    String authToken = null;
	Activity activity;
	TextView receicePush;
	Button authPuth ;
	TextView registId;
	
	public C2DMProcessor(Activity activity){
		this.activity = activity;
		

      receicePush = (TextView) activity.findViewById(R.id.receivePush);
      registId = (TextView)activity.findViewById(R.id.regist_id);
//      passText = (EditText)activity.findViewById(R.id.mail_pass);
//      pushText = (EditText)activity.findViewById(R.id.pushMsg);
      
      activity.findViewById(R.id.authPuth).setOnClickListener(clickListener);
//      activity.findViewById(R.id.sendPush).setOnClickListener(clickListener);
      
      
	}
	
	OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // 메세지를 보낸다.
            try {
            	int id = v.getId();
            	switch(id){
            	case R.id.authPuth:
            		requestRegistrationId("mozinski@gmail.com");
            		break;
//            	case R.id.sendPush:
//            		sender(pushText.getText().toString());
//            		break;
            	}
//                sender(msg_text.getText().toString());
//                Log.v("C2DM", "Send Message : "+ msg_text.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };
    private Handler handler;
    public void setHandler(Handler handler){
    	this.handler = handler;
    }
    private void sendHandler(String message){
		Message msg = Message.obtain();
		msg.arg1 = HANDLER_ID;
		msg.obj=message;
		handler.sendMessage(msg);
	}
    
    public void handleMessage(Message msg){
		if(msg.arg1 ==HANDLER_ID){
		
			String str = receicePush.getText().toString();
			receicePush.setText(str+"\n"+msg.obj.toString());
			if(registration_id != null) registId.setText(registration_id);
		}
	}
    
    
    
    public void registReceiver(){
    	 IntentFilter iFilter = new IntentFilter();
    	 iFilter.addAction("karroo.app.test.C2DM" ); 
    	 activity.registerReceiver(receiver, iFilter);
    }
    public void unregistReceiver(){
    	activity.unregisterReceiver(receiver);
    	
    }
    BroadcastReceiver receiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("C2DM",intent.getStringExtra("message"));

			sendHandler(intent.getStringExtra("message"));
		}
    	
    };
    
   
    
    

	
 
    /**
     * Request for RegistrationID to C2DM Activity 시작시 구글 C2DM으로 Registration ID
     * 발급을 요청한다. Registration ID를 발급받기 위해서는 Application ID, Sender ID가 필요.
     * Registration ID는 Device를 대표하는 ID로써 한번만 받아서 저장하면 되기 때문에 매번 실행시 체크.
     */
    public void requestRegistrationId(String mail) throws Exception{
 
        SharedPreferences shrdPref = PreferenceManager.getDefaultSharedPreferences(activity);
        registration_id = shrdPref.getString("registration_id", null);
        shrdPref = null;
 
        
        if (registration_id == null) {
            Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
            // Application ID(Package Name)
            registrationIntent.putExtra("app",PendingIntent.getBroadcast(activity, 0, new Intent(), 0));
            // Developer ID
            registrationIntent.putExtra("sender", mail);
            // Start request.
            
            activity.startService(registrationIntent);
        } 
        Log.v("C2DM","registration_id : "+registration_id);
    }
    public void handleRegistration(Context context, Intent intent) {
   	 
        registration_id = intent.getStringExtra("registration_id");
 
        Log.v("C2DM", "Get the Registration ID From C2DM");
        Log.v("C2DM", "Registration ID : " + registration_id);
 
        if (intent.getStringExtra("error") != null) {
            Log.v("C2DM", "error");
        }else if (intent.getStringExtra("unregistered") != null) {
            Log.v("C2DM", "unregistered.");
        }else if (registration_id != null) {
            Log.v("C2DM", "Registration ID complete!");
 
            // Registration ID 저장
            SharedPreferences shrdPref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = shrdPref.edit();
            editor.putString("registration_id", registration_id);
            editor.commit();
            shrdPref = null;
        }
    }
     
    /**
     * C2DM을 이용하기 위해서는 보안상 authToken(인증키)이 필요하다. 
     * authToken도 역시 한 번만 받아놓고 저장한다음 쓰면 된다.
     */
    /*
    public String getAuthToken(String mail,String pass) throws Exception {
 
        SharedPreferences shrdPref = PreferenceManager.getDefaultSharedPreferences(activity);
        String authToken = shrdPref.getString("authToken", null);
 
        Log.i("C2DM", "AuthToken : " + authToken);
 
        if (authToken == null) {
            StringBuffer postDataBuilder = new StringBuffer();
 
            postDataBuilder.append("accountType=GOOGLE");
            postDataBuilder.append("&Email="+mail);
            postDataBuilder.append("&Passwd="+pass); 
            postDataBuilder.append("&service=ac2dm");
            postDataBuilder.append("&source=PushTestApp");
 
            byte[] postData = postDataBuilder.toString().getBytes("UTF-8");
 
            URL url = new URL("https://www.google.com/accounts/ClientLogin");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
 
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length",
                    Integer.toString(postData.length));
 
            // 출력스트림을 생성하여 서버로 송신
            OutputStream out = conn.getOutputStream();
            out.write(postData);
            out.close();
 
            // 서버로부터 수신받은 스트림 객체를 버퍼에 넣어 읽는다.
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
 
            String sIdLine = br.readLine();
            String lsIdLine = br.readLine();
            String authLine = br.readLine();
 
            Log.i("C2DM", sIdLine);
            Log.i("C2DM", lsIdLine);
            Log.i("C2DM", authLine);
 
            authToken = authLine.substring(5, authLine.length());
 
            SharedPreferences.Editor editor = shrdPref.edit();
            editor.putString("authToken", authToken);
            editor.commit();
        }
 
        shrdPref = null;
        return authToken;
    }
    
    
    
    public void receive(String msg){
    	sendHandler(msg);
    }*/
    
    /** C2DM으로 메세지를 보내는 메소드 */
    public void sender(String msg)
            throws Exception {
 
        // collapse_key는 C2DM에서 사용자가 SEND 버튼을 실수로 여러번 눌렀을때
        // 이전 메세지 내용과 비교해서 반복전송되는 것을 막기 위해서 사용된다.
        // 여기서는 반복전송도 허용되게끔 매번 collapse_key를 랜덤함수로 뽑는다.
        String collaspe_key = String.valueOf(Math.random() % 100 + 1);
 
        // 보낼 메세지 조립
        StringBuffer postDataBuilder = new StringBuffer();
 
        postDataBuilder.append("registration_id=" + registration_id);
        postDataBuilder.append("&collapse_key=" + collaspe_key); // 중복방지 필터
        postDataBuilder.append("&delay_while_idle=1");
        postDataBuilder.append("&data.msg=" + URLEncoder.encode(msg, "UTF-8")); // 메세지                                                                          // 내용
 
        // 조립된 메세지를 Byte배열로 인코딩
        byte[] postData = postDataBuilder.toString().getBytes("UTF-8");
 
        // HTTP 프로토콜로 통신한다.
        // 먼저 해당 url 커넥션을 선언하고 연다.
        URL url = new URL("https://android.apis.google.com/c2dm/send");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
 
        conn.setDoOutput(true); // 출력설정
        conn.setUseCaches(false);
        conn.setRequestMethod("POST"); // POST 방식
        conn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length",
                Integer.toString(postData.length));
        conn.setRequestProperty("Authorization", "GoogleLogin auth="
                + authToken);
 
        // 출력스트림을 생성하여 postData를 기록.
        OutputStream out = conn.getOutputStream();
 
        // 출력(송신)후 출력스트림 종료
        out.write(postData);
        out.close();
 
        // 소켓의 입력스트림을 반환
        conn.getInputStream();
    }




	
    
}
