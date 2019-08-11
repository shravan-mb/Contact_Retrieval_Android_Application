package com.contact.retrieve;



import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;


public class
        SMSReceiver extends BroadcastReceiver
{
	//declaring the object NotificationManager
	NotificationManager nm;
	
	//declaring the object SmsManager
	SmsManager sms;

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		//declare the bundle and get the data from intent
		Bundle bundle = intent.getExtras();
		//A Short Message Service message array is declared
		SmsMessage[] msgs = null;
		
		//declaring the string phone number, contactname
		String str_phno;
		String str_contactname;
		String str_pass;
		
		//declaring the integer length
		int str_len;
		
		//filtering the bundle
		if (bundle != null) 
		{
			// retrieve the SMS message received
			Object[] pdus = (Object[]) bundle.get("pdus");
			
			msgs = new SmsMessage[pdus.length];
			
			//traversing the for loop
			for (int i = 0; i < msgs.length; i++) 
			{
				//getting the details from pdu object
				msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				
				//filtering the MessageBody if SEND using substring
				if(msgs[i].getMessageBody().toString().substring(0, 5).equalsIgnoreCase("SEND "))
				{
					//getting the source phone
					str_phno = msgs[i].getOriginatingAddress();
					System.err.println("phno = "+str_phno);
					
					//getting the string length of MessageBody 					
					str_len = msgs[i].getMessageBody().toString().length();
					System.err.println("len = "+str_len);
					
					int index_for_space = msgs[i].getMessageBody().toString().indexOf(" ", 5);
					System.err.println("index_for_space = "+index_for_space);
					
					//getting the string contact name form MessageBody after SEND									
					str_pass = msgs[i].getMessageBody().toString().substring(5,index_for_space); 
					System.err.println("pass = "+str_pass);
					
					if(str_pass.equalsIgnoreCase(ContactRetrievalActivity.hello))
					{
						//getting the string contact name form MessageBody after SEND									
						str_contactname = msgs[i].getMessageBody().toString().substring((index_for_space + 1), str_len);
						System.err.println("name = "+str_contactname);
						
						//initialise the intent with 2 parameters   
						intent = new Intent(context, DataSenderService.class);
						
						//create and declare the bundle
						Bundle b = new Bundle();
						
						//put the strings into the bundle
						b.putString("PHONE_NO", str_phno);
						b.putString("CONTACT_NAME", str_contactname);
						
						//put the bundle into the intent
						intent.putExtras(b);
						
						//start the service with this intent
						context.startService(intent);
						
						//call notification function 
						Notification(context);
					}
					else
					{
						sms = SmsManager.getDefault();
						sms.sendTextMessage(str_phno, null, "Password Mismatch, Enter valid Password", null, null);
					}
					
					
				}
				
			}
			
			

		}
	}

	private void Notification(Context context) 
	{
		//get NOTIFICATION_SERVICE from Android
		nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		//declare and initialise PendingIntent
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				new Intent(), 0);

		//declare and initialise Notification to be displayed on the Notification bar 
		Notification notif = new Notification(R.drawable.icon, "Contact Retrieval",
				System.currentTimeMillis());
		
		//set the Notification object with the "Contact Retrieval", "SMS Received is Valid" message 
		notif.setLatestEventInfo(context, "Contact Retrieval", "SMS Received is Valid", contentIntent);
		
		//notify the NotificationManager with ID and Notification icon and message
		nm.notify(1, notif);

		//get VIBRATOR_SERVICE from Android
		Vibrator vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		
		//set the vibrator object to vibrate for 1 sec
		vibrator.vibrate(1000);

	}

}