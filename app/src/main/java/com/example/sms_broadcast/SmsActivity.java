package com.example.sms_broadcast;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class SmsActivity extends Activity implements OnItemClickListener {

    private static SmsActivity inst;
    ArrayList<String> smsMessagesList = new ArrayList<String>();
    ListView smsListView;
    ArrayAdapter arrayAdapter;

    public static SmsActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;

        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(SmsActivity.this, new String[]{"android.permission.READ_SMS"},999);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        smsListView = (ListView) findViewById(R.id.SMSList);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        smsListView.setAdapter(arrayAdapter);
        smsListView.setOnItemClickListener(this);

        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(SmsActivity.this, new String[]{"android.permission.READ_SMS"},999);


        refreshSmsInbox();
    }

    public void refreshSmsInbox() {

     if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED)
        ActivityCompat.requestPermissions(SmsActivity.this, new String[]{"android.permission.READ_SMS"},999);

        ContentResolver contentResolver = getContentResolver();

        if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {


            Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
            int indexBody = smsInboxCursor.getColumnIndex("body");
            int indexAddress = smsInboxCursor.getColumnIndex("address");
            if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
            arrayAdapter.clear();
            do {
                String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
                        "\n" + smsInboxCursor.getString(indexBody) + "\n";
                arrayAdapter.add(str);
            } while (smsInboxCursor.moveToNext());

        }
        else
        {
           ActivityCompat.requestPermissions(SmsActivity.this, new String[]{"android.permission.READ_SMS"},999);
        }
    }

    public void updateList(final String smsMessage) {
        arrayAdapter.insert(smsMessage, 0);
        arrayAdapter.notifyDataSetChanged();
    }

    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        try {
            String[] smsMessages = smsMessagesList.get(pos).split("\n");
            String address = smsMessages[0];
            String smsMessage = "";
            for (int i = 1; i < smsMessages.length; ++i) {
                smsMessage += smsMessages[i];
            }

            String smsMessageStr = address + "\n";
            smsMessageStr += smsMessage;
            Toast.makeText(this, smsMessageStr, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
