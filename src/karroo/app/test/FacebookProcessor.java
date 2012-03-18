package karroo.app.test;

import karroo.app.test.facebook.DialogError;
import karroo.app.test.facebook.Facebook;
import karroo.app.test.facebook.FacebookError;
import karroo.app.test.facebook.Facebook.DialogListener;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class FacebookProcessor implements ISNSProcessor{
	final String appId = "203631409736689";
	Activity activity ;
	Facebook facebook;
	public FacebookProcessor(Activity activity) {
		this.activity = activity;
		
        activity.findViewById(R.id.authFacebook).setOnClickListener(authListener);
		
	}
	

	OnClickListener authListener = new OnClickListener(){
    	public void onClick(View v){
    		int id = v.getId();
    		switch(id){
    		case R.id.authFacebook:
    			login();
    			break;
    		}
    	}
    };
    
    public void login() {
    	facebook = new Facebook(appId);
    	facebook.authorize(activity, dialogListener);
	}
 // Facebook 인증후 처리를 위한 callback class
    DialogListener dialogListener = new  DialogListener()
    {
      @Override
      public void onCancel()
      {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void onComplete(Bundle values)
      {
        // TODO Auto-generated method stub
    	  Log.v("Facebook", "::: onComplete :::");
      }

      @Override
      public void onError(DialogError e)
      {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void onFacebookError(FacebookError e)
      {
        // TODO Auto-generated method stub
        
      }
    };
    
    
    public void snsSend(String text){
    	try{
	    	Bundle params = new Bundle();
	        params.putString("message", text);
	        params.putString("name", "testkarroo");
	        params.putString("link", "");
	        params.putString("description", "FacebookCon을통해 포스트됨.");
	        params.putString("picture", "");
	    	facebook.request("me/feed", params, "POST");
    	}catch(Exception e){
    		Log.e("Facebook", e.getMessage(), e);
    	}
    }
}
