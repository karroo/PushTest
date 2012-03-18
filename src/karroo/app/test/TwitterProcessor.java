package karroo.app.test;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class TwitterProcessor implements ActivityResultDelegate,ISNSProcessor{
	public static final String LOG_TAG = "TwitterCon";
	public static final String TWITTER_CONSUMER_KEY = "cxQpGCW4YYFk3ZYkCAHVbQ";
	public static final String TWITTER_CONSUMER_SECRET = "CcgiMJoLdD34VQpKLHgtptUU4kZLlLfpLwqGbQfhu1k";
	public static final String TWITTER_CALLBACK_URL = "http://karroo.iptime.org";
	public static final String MOVE_TWITTER_LOGIN   = "com.android.twittercon.TWITTER_LOGIN";  // 인텐트 호출시 사용할 것입니다. 
	public static final int TWITTER_LOGIN_CODE = 10;
	
	
	Activity activity;
	RequestToken mRqToken;
	Twitter mTwitter;
	AccessToken mAccessToken;
	public TwitterProcessor(Activity activity) {
		this.activity = activity;
		
        activity.findViewById(R.id.authTwitter).setOnClickListener(authListener);
		
	}

	OnClickListener authListener = new OnClickListener(){
    	public void onClick(View v){
    		int id = v.getId();
    		switch(id){
    		case R.id.authTwitter:
    			login();
    			break;
    			
    		}
    		
    	}
    };
    
	public void login() {
		new Thread() {
			public void run() {
				try {
					
					mTwitter = new TwitterFactory().getInstance();
					mTwitter.setOAuthConsumer(TWITTER_CONSUMER_KEY,
							TWITTER_CONSUMER_SECRET);

					mRqToken = mTwitter
							.getOAuthRequestToken(TWITTER_CALLBACK_URL);
					Log.v(LOG_TAG, "AuthorizationURL >>>>>>>>>>>>>>> "
							+ mRqToken.getAuthorizationURL());

					Intent intent = new Intent(MOVE_TWITTER_LOGIN); 
				
					intent.putExtra("auth_url", mRqToken.getAuthorizationURL());
					intent.putExtra("callback", TWITTER_CALLBACK_URL);
					activity.startActivityForResult(intent,	TWITTER_LOGIN_CODE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data){
		try {
			if(requestCode != TWITTER_LOGIN_CODE) return;
				
			mAccessToken = mTwitter.getOAuthAccessToken(mRqToken,
					data.getStringExtra("oauth_verifier"));

			Log.v(LOG_TAG,
					"Twitter Access Token : " + mAccessToken.getToken());
			Log.v(LOG_TAG,
					"Twitter Access Token Secret : "
							+ mAccessToken.getTokenSecret());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void snsSend(String text) {
		try {
			mTwitter.setOAuthAccessToken(mAccessToken);
//			String text = snsText.getText().toString();
			Status status = mTwitter.updateStatus(text);
		} catch (Exception e) {
			Log.e("SNSSend", e.getMessage(), e);
		}
	}
}
