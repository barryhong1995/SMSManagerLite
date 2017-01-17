package com.nrp.smsmanagerlite;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class InboxSMSDetect extends BroadcastReceiver 
{
	// Column Name in SMS Table
	public static final String ADDRESS = "address";
    public static final String PERSON = "person";
    public static final String DATE = "date";
    public static final String READ = "read";
    public static final String STATUS = "status";
    public static final String TYPE = "type";
    public static final String BODY = "body";
    public static final String SEEN = "seen";
    
    public static final int MESSAGE_TYPE_INBOX = 1;
    public static final int MESSAGE_TYPE_SENT = 2;
    
    public static final int MESSAGE_IS_NOT_READ = 0;
    public static final int MESSAGE_IS_READ = 1;
    
    public static final int MESSAGE_IS_NOT_SEEN = 0;
    public static final int MESSAGE_IS_SEEN = 1;
	
    // Change the password here or give a user possibility to change it
    public static final byte[] PASSWORD = new byte[]{0x20, 0x32, 0x34, 0x47, (byte) 0x84, 0x33, 0x58};
	
    public void onReceive( Context context, Intent intent ) 
	{	
    	// Detect incoming SMS
        Bundle extras = intent.getExtras();
        
        String messages = "";
        String address = "";
        String body = "";
        
        // Get current time
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy - HH:mm:ss a");
        String time = sdf.format(c.getTime());
        
        
        if ( extras != null )
        {
            // Retrieve incoming SMS
            Object[] pdus = (Object[]) extras.get("pdus");
            
         // Get ContentResolver object for pushing encrypted SMS to incoming folder
            ContentResolver contentResolver = context.getContentResolver();
            
            for (int i = 0; i < pdus.length; ++i)
            {
            	SmsMessage sms = SmsMessage.createFromPdu((byte[])pdus[i]); 
            	
            	body += sms.getMessageBody().toString();
            	address += sms.getOriginatingAddress();
                
                messages += "SMS from " + address + ": \n";                    
                messages += body + "\n";
                messages += "Time: " + time + "\n";
                
                // Encrypt incoming SMS
                EncryptandSave(contentResolver,sms);
            }      
            
            // Display SMS message
            Toast.makeText(context, messages, Toast.LENGTH_SHORT).show();
            
        }
        
        this.abortBroadcast();
        
	}
    
    // Encryption and Saving to Database
    private void EncryptandSave(ContentResolver contentResolver, SmsMessage sms)
	{
		// Create SMS row
        ContentValues values = new ContentValues();
        values.put(ADDRESS, sms.getOriginatingAddress());
        values.put(DATE, sms.getTimestampMillis());
        values.put(READ, MESSAGE_IS_NOT_READ);
        values.put(STATUS, sms.getStatus());
        values.put(TYPE, MESSAGE_TYPE_INBOX);
        values.put(SEEN, MESSAGE_IS_NOT_SEEN);
        try
        {
        	String encryptedPassword = AllAboutCrypt.encrypt(new String(PASSWORD), sms.getMessageBody().toString()); 
        	values.put(BODY, encryptedPassword);
        }
        catch (Exception e) 
        { 
        	e.printStackTrace(); 
    	}
        
        // Push row into the SMS table
        contentResolver.insert(Uri.parse("content://sms"), values);
	}
	
	// Checking for Match with Contact List
	public boolean ContactExists(Context context, String address) {
		String Address = address;
		Uri lookupUri = Uri.withAppendedPath(
			PhoneLookup.CONTENT_FILTER_URI, 
			Uri.encode(Address));
		String[] mPhoneNumberProjection = {PhoneLookup._ID, PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME};
		Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
		
		try {
			
			//Address is in Contacts
			if (cur.moveToFirst()) {
				return true;
				}
			
		} finally {
			if (cur != null)
				cur.close();
			}
		
		// Address is not in Contacts
		return false;
 	}
}