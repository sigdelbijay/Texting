package com.example.texting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    EditText phoneNoEditText;
    EditText messageEditText;
    Button sendBtn;

    static String messages = "";
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        phoneNoEditText = (EditText) findViewById(R.id.phoneNoEditText);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        sendBtn = (Button) findViewById(R.id.sendBtn);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(5000);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(messages);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public void sendMessage(View view) {

        String phoneNum = phoneNoEditText.getText().toString();
        String message = messageEditText.getText().toString();

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNum, null, message, null, null);
            Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException ex) {
            Log.e("TEXTING", "Destination Address or data empty");
            Toast.makeText(this, "Enter a phone number and message", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        } catch(Exception ex) {
            Toast.makeText(this, "Message not sent", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }

        messages = messages + "You : " + message + "\n";
    }

    public static class SmsReceiver extends BroadcastReceiver {

        final SmsManager smsManager = SmsManager.getDefault();

        public SmsReceiver() {}

        @Override
        public void onReceive(Context context, Intent intent) {

            final Bundle bundle = intent.getExtras();

            try {
                if (bundle != null) {
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");
                    for (int i = 0; i < pdusObj.length; i++) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = smsMessage.getDisplayOriginatingAddress();
                        String message = smsMessage.getDisplayMessageBody();
                        messages = messages + phoneNumber + " : " + message + "\n";

                    }
                }
            } catch (Exception ex) {
                Log.e("SmsReceiver", "Exception smsReceiver" + ex);
            }
        }
    }

    public class MMSReceiver extends BroadcastReceiver {
        public MMSReceiver() { }
        @Override
        public void onReceive(Context context, Intent intent) {
            throw new UnsupportedOperationException("Not Implemented Yet");
        }
    }

    public class HeadlessSmsSendService extends  BroadcastReceiver {

        public HeadlessSmsSendService() {}
        @Override
        public void onReceive(Context context, Intent intent) {
            throw new UnsupportedOperationException("Not Implemented Yet");

        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
