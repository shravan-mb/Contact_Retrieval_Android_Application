package com.contact.retrieve;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.SmsManager;


public class DataSenderService extends Service 
{
	//declaring the string phone number, contact name and contact retrieved 
	String Contact_name;
	String Phone_number;
	
	String Contact_Retrieved;
	String Contact_not_Retrieved;
	String Contact_Related;

	//declaring the integer Count_Mismatch and Count_Contacts
	int Count_Mismatch = 0;
	int Count_Contacts = 0;
	
	//declaring the object SmsManager
	SmsManager sms;
	
	@Override
	public IBinder onBind(Intent arg0) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() 
	{
		// TODO Auto-generated method stub
		super.onCreate();
		System.err.println("ONCREATE");
	}

	@Override
	public void onStart(Intent intent, int startId) 
	{
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		System.err.println("ONSTART");
		
		//declare the bundle and get the data from intent
		Bundle b = intent.getExtras();
		
		//get the strings from the bundle
		Contact_name = b.getString("CONTACT_NAME");
		System.err.println("Contact_name = "+Contact_name);
		
		Phone_number = b.getString("PHONE_NO");
		System.err.println("Phone_number = "+Phone_number);
		
		//Get the default instance of the SmsManager
		sms = SmsManager.getDefault();

		//search contact function to search the contact			
		SearchContact();
	}

	private void SearchContact() 
	{
				
		//declare and initialise contact uri
		Uri allContacts = ContactsContract.Contacts.CONTENT_URI;
		System.err.println("allContacts = "+ allContacts);

		//declare and initialise cursor to query contacts and display the name in ASC order
		Cursor c = getContentResolver().query(allContacts, null, null, null,
				ContactsContract.Contacts.DISPLAY_NAME + " ASC");
		System.err.println("c = "+c);
		
		//PrintContactsCount function to find the total number of contacts 
		PrintContactsCount(c);
		
		//PrintContacts function to find and send the particular contact
		PrintContacts(c);
	}
	
	private void PrintContactsCount(Cursor c) 
	{
		//filtering the cursor
		if (c.moveToFirst()) 
		{
			do 
			{
				//count the total number of contacts
				Count_Contacts++;
				System.err.println("Count_Contacts = "+Count_Contacts);
				
			} while (c.moveToNext());
			
		}
		
	}

	private void PrintContacts(Cursor c) 
	{
		//filtering the cursor
		if (c.moveToFirst()) 
		{
			do 
			{
				//get the contactID from the contacts uri
				String contactID = c.getString(c
						.getColumnIndex(ContactsContract.Contacts._ID));
				System.err.println("contactID = "+contactID);
				
				//get the contactDisplayName from the contacts uri
				String contactDisplayName = c
						.getString(c
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				System.err.println("contactDisplayName = "+contactDisplayName);

				//filtering the contactDisplayName 
				if(!contactDisplayName.equalsIgnoreCase(Contact_name))
				{
					//count the total number of Mismatch contacts
					Count_Mismatch++;
					System.err.println("Count_Mismatch = "+Count_Mismatch);
				}
				else
				{
					//in case of match with the contact name
					
					//if the phone number is present for the particular contact change the hasPhone to 1 else 0 get the hasPhone from uri contacts HAS_PHONE_NUMBER
					int hasPhone = c
							.getInt(c
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

					//if contacts uri has PHONE_NUMBER
					if (hasPhone == 1) 
					{
						//declare and initialise cursor to query phone number
						Cursor phoneCursor = getContentResolver().query(
								ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
								null,
								ContactsContract.CommonDataKinds.Phone.CONTACT_ID
										+ " = " + contactID, null, null);
						
						//get the phone number
						while (phoneCursor.moveToNext()) 
						{
							
							//get the phone number and place it in a string Contact_Retrieved
							Contact_Retrieved = phoneCursor.getString(phoneCursor
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							System.err.println("phone number = "+phoneCursor.getString(phoneCursor
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
					
							//send sms function to send the retrieved contact
							SendSms();
						}
						
						phoneCursor.close();
					}

				}
				
			} while (c.moveToNext());
			
		}
		
		//if equals then Contact name not found in Contacts
		if(Count_Mismatch == Count_Contacts)
		{
			//initialise the string Contact_not_Retrieved
			Contact_not_Retrieved = "Contact Name "+Contact_name+" not found in Contacts";
			
			//send text message Contact name not found in Contacts
			sms.sendTextMessage(Phone_number, null, Contact_not_Retrieved, null, null);
			
			SendRelatedContacts(c);
						
		}
		
		//finally initialise to 0 
		Count_Contacts = 0;
		Count_Mismatch = 0;
		
	}

	private void SendRelatedContacts(Cursor c) 
	{
		//filtering the cursor
		if (c.moveToFirst()) 
		{
			do 
			{
				//get the contactID from the contacts uri
				String contactID = c.getString(c
						.getColumnIndex(ContactsContract.Contacts._ID));
				System.err.println("contactID = "+contactID);
				
				//get the contactDisplayName from the contacts uri
				String contactDisplayName = c
						.getString(c
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				System.err.println("contactDisplayName = "+contactDisplayName);

				if(contactDisplayName.substring(0, 1).equalsIgnoreCase(Contact_name.substring(0, 1)))
				{
					
					//if the phone number is present for the particular contact change the hasPhone to 1 else 0 get the hasPhone from uri contacts HAS_PHONE_NUMBER
					int hasPhone = c
							.getInt(c
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

					//if contacts uri has PHONE_NUMBER
					if (hasPhone == 1) 
					{
						//declare and initialise cursor to query phone number
						Cursor phoneCursor = getContentResolver().query(
								ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
								null,
								ContactsContract.CommonDataKinds.Phone.CONTACT_ID
										+ " = " + contactID, null, null);
						
						//get the phone number
						while (phoneCursor.moveToNext()) 
						{
							
							//get the phone number and place it in a string Contact_Retrieved
							Contact_Related = phoneCursor.getString(phoneCursor
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							System.err.println("phone number = "+phoneCursor.getString(phoneCursor
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
					
							//sendsms function to send the retrieved contact
							SendRelatedSms(contactDisplayName);
						}
						
						phoneCursor.close();
					}

				}
				
			} while (c.moveToNext());
			
		}
		
	}

	private void SendRelatedSms(String contactDisplayName) 
	{
		//initialise the string Contact_Retrieved
		Contact_Related = ""+contactDisplayName+" Phone Number is : "+Contact_Related+"";
				
		//send text message Phone Number is :
		sms.sendTextMessage(Phone_number, null, Contact_Related, null, null);
		
	}

	private void SendSms() 
	{
		//initialise the string Contact_Retrieved
		Contact_Retrieved = ""+Contact_name+" Phone Number is : "+Contact_Retrieved+"";
		
		//send text message Phone Number is :
		sms.sendTextMessage(Phone_number, null, Contact_Retrieved, null, null);
				
	}


}
