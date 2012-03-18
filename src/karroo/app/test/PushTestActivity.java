package karroo.app.test;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

public class PushTestActivity extends Activity {
	
	 
	View pushTest,websocketTest,snsTest;
    WebSocketProcessor websocket;
    SNSProcessor snsProcessor;
    FacebookProcessor facebookProcessor;
    C2DMProcessor c2dmProcessor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
 
        pushTest = (View)findViewById(R.id.push_test);
        websocketTest = (View)findViewById(R.id.websocket_test);
        snsTest = (View)findViewById(R.id.sns_test);
        findViewById(R.id.go_push).setOnClickListener(pageChangeListener);
        findViewById(R.id.go_websocket).setOnClickListener(pageChangeListener);
        findViewById(R.id.go_sns).setOnClickListener(pageChangeListener);
        
        
        
        websocket = new WebSocketProcessor(this);
        websocket.setHandler(handler);
        addHandlerDelegate(websocket);
        
        
        
        snsProcessor = new SNSProcessor(this);
        addActivityResultDelegate(snsProcessor);
        
        
        c2dmProcessor = new C2DMProcessor(this);
        c2dmProcessor.setHandler(handler);
        addHandlerDelegate(c2dmProcessor);
        
         
        
    }
    
    OnClickListener pageChangeListener = new OnClickListener(){
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
    
    /* onActivityResult 메소드 처리*/
    List<ActivityResultDelegate> resultDelegateList = new ArrayList<ActivityResultDelegate>();
    void addActivityResultDelegate(ActivityResultDelegate delegate){
    	resultDelegateList.add(delegate);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
      super.onActivityResult(requestCode, resultCode, data);
      
      for(ActivityResultDelegate delegate:resultDelegateList){
    	  delegate.onActivityResult(requestCode, resultCode, data);
      }
    }
    
    
    /* handler 처리*/
    List<HandlerDelegate> handlerDelegateList = new ArrayList<HandlerDelegate>();
    void addHandlerDelegate(HandlerDelegate delegate){
    	handlerDelegateList.add(delegate);
    }
    Handler handler = new Handler(){
    	public void handleMessage(Message msg){
    		super.handleMessage(msg);
    		for(HandlerDelegate delegate:handlerDelegateList){
    	    	  delegate.handleMessage(msg);
    	      }
    	}
    };
    
    
    
   
    

   
    
    
    
    
}