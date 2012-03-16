package karroo.app.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import org.java_websocket.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PushTestActivity extends Activity {
	private static String authToken = null;
	 
	View pushTest,websocketTest,snsTest;
    EditText msg_text;
    Button msg_send;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
 
        
        
        pushTest = (View)findViewById(R.id.push_test);
        websocketTest = (View)findViewById(R.id.websocket_test);
        snsTest = (View)findViewById(R.id.sns_test);
        
        Button goPush = (Button)findViewById(R.id.go_push);
        Button goWebsocket = (Button)findViewById(R.id.go_websocket);
        Button goSns = (Button)findViewById(R.id.go_sns);
        
        OnClickListener goListener = new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				pushTest.setVisibility(View.INVISIBLE);
				websocketTest.setVisibility(View.INVISIBLE);
				snsTest.setVisibility(View.INVISIBLE);
				
				int id = v.getId();
				switch(id){
				case R.id.go_push:
					pushTest.setVisibility(View.VISIBLE);
					break;
				case R.id.go_websocket:
					websocketTest.setVisibility(View.VISIBLE);
					break;
				case R.id.go_sns:
					snsTest.setVisibility(View.VISIBLE);
					break;
						
				}
				
			}
        	
        };
        
        goPush.setOnClickListener(goListener);
        goWebsocket.setOnClickListener(goListener);
        goSns.setOnClickListener(goListener);
 
//        msg_text = (EditText) findViewById(R.id.msg_text);
//        msg_send = (Button) findViewById(R.id.msg_send);
// 
//        msg_send.setOnClickListener(new OnClickListener() {
// 
//            @Override
//            public void onClick(View v) {
//                // 메세지를 보낸다.
//                try {
//                    sender(C2dmReceiver.registration_id, authToken,
//                            msg_text.getText().toString());
//                    Log.v("C2DM", "Send Message : "
//                            + msg_text.getText().toString());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
// 
//            }
//        });
        
        
        
        
        // C2DM초기화 : C2DM 으로부터 Registration ID와 AuthToken을 발급 받는다.
        try {
            requestRegistrationId();
            authToken = getAuthToken();
        } catch (Exception e) { 
            e.printStackTrace();
        }
        /* 웹소켓 초기화*/
        try{
        	new WebSocketProcessor().connect();
        }catch(Exception e){
        	Log.e("WEBSOCKET",e.getMessage(),e);
        }
        
        
    }
 
    /** C2DM으로 메세지를 보내는 메소드 */
    public void sender(String registration_id, String authToken, String msg)
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
 
    /**
     * Request for RegistrationID to C2DM Activity 시작시 구글 C2DM으로 Registration ID
     * 발급을 요청한다. Registration ID를 발급받기 위해서는 Application ID, Sender ID가 필요.
     * Registration ID는 Device를 대표하는 ID로써 한번만 받아서 저장하면 되기 때문에 매번 실행시 체크.
     */
    public void requestRegistrationId() throws Exception{
 
        SharedPreferences shrdPref = PreferenceManager
                .getDefaultSharedPreferences(this);
        String registration_id = shrdPref.getString("registration_id", null);
        shrdPref = null;
 
        if (registration_id == null) {
            Intent registrationIntent = new Intent(
                    "com.google.android.c2dm.intent.REGISTER");
 
            // Application ID(Package Name)
            registrationIntent.putExtra("app",
                    PendingIntent.getBroadcast(this, 0, new Intent(), 0));
 
            // Developer ID
            registrationIntent.putExtra("sender", "mozinski@gmail.com");
 
            // Start request.
            ComponentName comp = startService(registrationIntent);
            Log.v("C2DM","ComponentName : "+comp.getClassName());
        } else {
            C2dmReceiver.registration_id = registration_id;
            Log.v("C2DM", "Registration ID is Exist!");
            Log.v("C2DM", "Registration ID : " + C2dmReceiver.registration_id);
        }
        
//        Toast.makeText(this, "팝업", 3).show();
    }
     
    /**
     * C2DM을 이용하기 위해서는 보안상 authToken(인증키)이 필요하다. 
     * authToken도 역시 한 번만 받아놓고 저장한다음 쓰면 된다.
     */
    public String getAuthToken() throws Exception {
 
        SharedPreferences shrdPref = PreferenceManager
                .getDefaultSharedPreferences(this);
        String authToken = shrdPref.getString("authToken", null);
 
        Log.i("C2DM", "AuthToken : " + authToken);
 
        if (authToken == null) {
            StringBuffer postDataBuilder = new StringBuffer();
 
            postDataBuilder.append("accountType=HOSTED_OR_GOOGLE");
            postDataBuilder.append("&Email=mozinski@gmail.com");
            postDataBuilder.append("&Passwd=shibba11"); 
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
    
    public static class WebSocketProcessor extends WebSocketClient{

    	public WebSocketProcessor() throws Exception{
    		super(new URI("ws://localhost:9999/test/caption?programId=111"),new Draft_10());
    		
    	}
		@Override
		public void onClose(int arg0, String arg1, boolean arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onError(Exception arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMessage(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onOpen(ServerHandshake arg0) {
			// TODO Auto-generated method stub
			
		}
    	
    }
}