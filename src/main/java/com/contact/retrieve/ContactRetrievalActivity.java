package com.contact.retrieve;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ContactRetrievalActivity extends Activity 
{
	
	Button Save;
	EditText Pass;
	static String hello;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    
        Save = (Button) findViewById(R.id.button1);
        
        Pass = (EditText) findViewById(R.id.editText1);
        
        Save.setOnClickListener(new OnClickListener() 
        {
			
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				hello = Pass.getText().toString();
				
				System.err.println("password = "+hello);
				Toast.makeText(getApplicationContext(), "Application Deployed", Toast.LENGTH_LONG).show();
				finish();
			}
		});
        
        //Call this when your activity is done and should be closed
        //finish();
    }
}