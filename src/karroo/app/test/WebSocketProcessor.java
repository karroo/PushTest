package karroo.app.test;

import java.net.URI;

import org.java_websocket.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class WebSocketProcessor implements IHandlerDelegate{
	final int  HANDER_ID=1;
	WebSocketClient socket;
	TextView urlText ;
	TextView programIdText;
	TextView websocketText;
	CheckBox onCaption;
	CheckBox onHotNews;
	public WebSocketProcessor(Activity activity){
		
		
		urlText = ((TextView)activity.findViewById(R.id.socket_url));
		programIdText = ((TextView)activity.findViewById(R.id.programId));
		websocketText = ((TextView)activity.findViewById(R.id.websocket_text));
		
		((CheckBox)activity.findViewById(R.id.onCaption)).setOnCheckedChangeListener(checkListener);
		((CheckBox)activity.findViewById(R.id.onSpotnews)).setOnCheckedChangeListener(checkListener);
		
        activity.findViewById(R.id.socket_connect).setOnClickListener(socketListener);
        activity.findViewById(R.id.socket_close).setOnClickListener(socketListener);
		
	}
	
	private Handler handler;
    public void setHandler(Handler handler){
    	this.handler = handler;
    }
	private void sendHandler(String message){
		Message msg = Message.obtain();
		msg.arg1 = HANDER_ID;
		msg.obj=message;
		handler.sendMessage(msg);
	}
	public void handleMessage(Message msg){
		if(msg.arg1 == HANDER_ID){
		
			String str = websocketText.getText().toString();
			websocketText.setText(str+"\n"+msg.obj.toString());
		
		}
	}
    
	OnCheckedChangeListener checkListener = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			int id = buttonView.getId();
			switch(id){
			case R.id.onCaption:
				if(isChecked) send("action=onCaption&programId="+programIdText.getText().toString());
				else send("action=offCaption");
				break;
			case R.id.onSpotnews:
				if(isChecked) send("action=onSpotnews");
				else send("action=offSpotnews");
				break;
			}
		}
		
	};
	
	OnClickListener socketListener = new OnClickListener(){
    	public void onClick(View v){
    		int id = v.getId();
    		switch(id){
    		case R.id.socket_connect:
    			String server = urlText.getText().toString();
    			String programId = programIdText.getText().toString();
    			
    			String url = "ws://"+server+"/WITHSocket/connect";
    			connect(url);
    			break;
    		case R.id.socket_close:
    			close();
    			break;
    		}
    	}
    };

   
	public void connect(String url){
		try{
			try{
				Log.i("WEBSOCKET", ""+socket.getConnection());
				if(socket.getConnection().isOpen()){
					sendHandler("already connected");
					return;
				}	
			}catch(Exception e){
			}
			
			
			socket = new WebSocketClient(new URI(url),new Draft_10()){
				@Override
				public void onClose(int arg0, String arg1, boolean arg2) {
					// TODO Auto-generated method stub
					Log.i("WebSocket","close");
					sendHandler("close");	
				}
	
				@Override
				public void onError(final Exception e) {
					// TODO Auto-generated method stub
					
					Log.i("WebSocket",e.getMessage());
					sendHandler(e.getMessage());
				}
	
				@Override
				public void onMessage(final String message) {
					// TODO Auto-generated method stub
					Log.i("WebSocket",message);
					sendHandler(message);
				}
	
				@Override
				public void onOpen(ServerHandshake arg0) {
					// TODO Auto-generated method stub
					Log.i("WebSocket","open");
					sendHandler("open");	
				}
				
			};
			socket.connect();
		}catch(Exception e){
			
		}
	
	}
	public void close(){
		if(socket == null) return;
		
		socket.close();
		sendHandler("close");
		socket = null;
	}
	
	public void send(String message){
		Log.i("WEBSOCKET",message);
		try{
			socket.send(message);
		}catch(Exception e){
			Log.e("WEBSOCKET",e.getMessage(),e);
		}
	}

	
}
