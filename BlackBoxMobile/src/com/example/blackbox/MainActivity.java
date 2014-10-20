package com.example.blackbox;


import com.example.blackbox.R;
import com.example.blackbox.R.layout;
import com.example.blackbox.R.menu;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	EditText mdp = null;
	EditText message = null;
	EditText adresse = null;
	EditText pseudo = null;
	TextView chatBox = null;
	Button send = null;
	Client client = null;
	ToggleButton connect = null;
	static MainActivity mApp = null;
	
	private boolean connected;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mdp = (EditText)findViewById(R.id.mdp);
        message = (EditText)findViewById(R.id.message);
        adresse = (EditText)findViewById(R.id.adresse);
        pseudo = (EditText)findViewById(R.id.name);
        chatBox = (TextView)findViewById(R.id.ChatRoom);
        send = (Button) findViewById(R.id.send);
        connect = (ToggleButton)findViewById(R.id.connect);
        
        mApp = this;
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy); 
        
        connect.setOnCheckedChangeListener( new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                setState(connect.isChecked()) ;
            }
        }) ;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }
    
    public String getPassword(){
    	return mdp.getText().toString();
    }//getPWD
    
    public void append(String str) {
		chatBox.append(str + "\n");
	}//append
    
    public void connectionFailed(){
    	send.setClickable(false);
		connected = false;
    }//connectionFailed
    
    public static Context context()
    {
        return mApp.getApplicationContext();
    }
    
    private void setState(boolean isChecked){
    	if(isChecked) {
    		// ok it is a connection request
    		String username = pseudo.getText().toString().trim();
    		// empty username ignore it
    		if(username.length() == 0 || username.contains(" ")){
    			connect.setChecked(false);
    			return;
    		}
    		// empty serverAddress ignore it
    		String server = adresse.getText().toString().trim();
    		if(server.length() == 0){
    			connect.setChecked(false);
    			return;
    		}
    		// try creating a new Client with GUI
    		client = new Client(server, 1664, username, this);
    		// test if we can start the Client
    		client.run();
    		if(!client.getSuccess()){
    			connect.setChecked(false);
    			return;
    		}
    		message.setText("");
    		connected = true;
    		// Action listener for when the user enter a message
    		send.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, message.getText().toString()));
                    message.setText("");
                }
            });
    	}else if(!isChecked){
//    		client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
			return;
    	}
    }//setState
}
