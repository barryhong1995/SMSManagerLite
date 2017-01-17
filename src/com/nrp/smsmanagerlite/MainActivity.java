package com.nrp.smsmanagerlite;

import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Close Button
		Button CloseBtn = (Button) findViewById(R.id.button2);
		CloseBtn.setOnClickListener(new OnClickListener(){
			
			@ Override
			public void onClick(View v){
				finish();
				System.exit(0);
			}	
		});
		this.findViewById(R.id.button1).setOnClickListener(this);
	}

    ArrayList<String> smsList = new ArrayList<String>();
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);

		int indexBody = cursor.getColumnIndex(InboxSMSDetect.BODY );
		int indexAddr = cursor.getColumnIndex(InboxSMSDetect.ADDRESS );
		
		if (indexBody < 0 || !cursor.moveToFirst()) return;
		
		smsList.clear();
		
		do
		{
			String str = null;
			try {
				str = "Sender: " + cursor.getString(indexAddr) + "\n" + AllAboutCrypt.decrypt(new String(InboxSMSDetect.PASSWORD), cursor.getString(indexBody));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			smsList.add(str);
		}
		while(cursor.moveToNext());

		ListView smsListView = (ListView) findViewById( R.id.EncryptedInbox );
		smsListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsList));
	}

}
