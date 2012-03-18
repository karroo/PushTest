package karroo.app.test;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class SNSProcessor implements ActivityResultDelegate{
	Activity activity;
	EditText snsText; 
	TwitterProcessor twitterProcessor;
	FacebookProcessor facebookProcessor;
	public SNSProcessor(Activity activity) {
		this.activity = activity;
		snsText = (EditText)activity.findViewById(R.id.sns_text);
		
        activity.findViewById(R.id.sns_send).setOnClickListener(authListener);
        twitterProcessor = new TwitterProcessor(activity); 
       
        
        facebookProcessor = new FacebookProcessor(activity);
	}
	

	OnClickListener authListener = new OnClickListener(){
    	public void onClick(View v){
    		String text = snsText.getText().toString();
    		twitterProcessor.snsSend(text);
    		facebookProcessor.snsSend(text);
    	}
    };
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		twitterProcessor.onActivityResult(requestCode, resultCode, data);
		
	}
}
