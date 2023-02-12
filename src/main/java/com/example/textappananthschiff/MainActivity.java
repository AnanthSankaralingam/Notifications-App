package com.example.textappananthschiff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.telephony.SmsManager;
import android.widget.TextView;
import android.Manifest;

import android.content.pm.PackageManager;
import android.os.Build;

import android.os.Handler;

import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;

import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    int counter;
    TextView number, mess, state;
    String msg, num = "";
    String sentMessage;
    Message sms;
    boolean states;
    IntentFilter filter;
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    public static final int request = 1;
    boolean state1, state2, state3, state4 = false;
    String[]greeting, goodbye;
    Random random, r1;
    int st = 0;
    boolean a;
    boolean b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE}, request);

        }

        number = findViewById(R.id.number);
        mess = findViewById(R.id.message);
        state = findViewById(R.id.state);
        greeting = new String[]{"Hello, how can I help?", "Hi, what can I help you with?", "What's Up?", "Hey!", "How are you?"};
        goodbye = new String[]{"GoodBye", "Have a nice day", "Bye. Thanks!",  "See you!"};
        random = new Random();
        r1 = new Random();

        sms = new Message();
        filter = new IntentFilter(SMS_RECEIVED);
        registerReceiver(sms, filter);
    }

    public class Message extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Tag", "Message Received");
            Bundle bundle = intent.getExtras();
            if(bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] message = new SmsMessage[pdus.length];

                for (int i = 0; i < pdus.length; i++) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String format = bundle.getString("format");
                        message[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                    } else {
                        message[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                    msg = message[i].getDisplayMessageBody();
                    num = message[i].getOriginatingAddress();

                }
                int greet = random.nextInt(greeting.length);
                int b = r1.nextInt(goodbye.length);
                number.setText(num);
                mess.setText(msg);
                if(st == 0){
                    if (msg.toLowerCase().contains("hi") || msg.toLowerCase().contains("hello") || msg.toLowerCase().contains("hey") || msg.toLowerCase().contains("whats")) {
                        states = true;
                        counter++;
                        state1 = true;
                         if(counter == 1) {
                            a = true;
                            sentMessage = "Hello! Welcome to the TD bank! "+greeting[greet];
                            sendSMS(sentMessage);
                            state1 = false;
                            state.setText("State 0");
                        }
                    }

                    else if(a==true&&(msg.toLowerCase().equals("good")||msg.toLowerCase().contains("great")||msg.toLowerCase().contains("bad")||msg.toLowerCase().contains("fine"))){
                        states = true;
                        sentMessage = "Okay me too!";
                        sendSMS(sentMessage);
                        sentMessage = "Would you like to check your balance?";
                        sendSMS(sentMessage);
                        st = 1;
                        state.setText("State 1");
                    }
                    
                    else{
                        sentMessage = "Sorry I did not understand you.";
                        sendSMS(sentMessage);
                        st = 0;
                        state.setText("State 0");
                    }
                    
                }
                
                else if (st == 1)
                {
                    if(msg.toLowerCase().contains("no"))
                    {
                        st = 3;
                        state.setText("State 0");
                        sentMessage = "Oh okay! Have a nice day!";
                        sendSMS(sentMessage);
                    }
                    else if(msg.toLowerCase().contains("ye"))
                    {
                        sentMessage = "Great, what's your account number?";
                        sendSMS(sentMessage);
                        st = 2;
                        state.setText("State 2");
                    }
                    else
                    {
                        sentMessage = "Sorry, I did not understand, please try again.";
                        sendSMS(sentMessage);
                        st = 1;
                        state.setText("State 1");
                    }
                }
                else if(st == 2)
                {
                    if(msg.length()==4 && digits(msg,msg.length()))
                    {
                        sentMessage = "Great! Your account looks good! You're all set!";
                        sendSMS(sentMessage);
                        sentMessage = "Thank you for your time!";
                        sendSMS(sentMessage);
                        st = 3;
                        state.setText("State 3");
                    }
                    else
                    {
                        sentMessage = "That account number was invalid! Try again";
                        sendSMS(sentMessage);
                        st = 2;
                    }
                }
                else if(st == 3){
                    if (msg.toLowerCase().contains("bye") ||  msg.toLowerCase().contains("have a good day") || msg.toLowerCase().contains("night")) {
                        state4 = true;

                        if(state4) {
                            sentMessage = goodbye[b];
                            sendSMS(sentMessage);
                            st = 0;
                            state.setText("State 0");
                        }
                        else{
                            sentMessage = "Sorry I did not understand you. Please try again.";
                            sendSMS(sentMessage);
                            st = 0;
                            state.setText("State 0");
                        }
                    }else{
                        sentMessage = "Sorry I did not understand you. Please try again.";
                        sendSMS(sentMessage);
                        state.setText("State 1");
                    }
                }
            }
        }
    }
    public void sendSMS(final String str){
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(num, null, str, null, null);
                Toast.makeText(MainActivity.this, "Message SENT", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Message FAILED", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            Log.d("TAG","sendSMS");
        }, 4000);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case request:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE}, request);
                    }
                }
                break;
            default:
                break;
        }
 }
    public static boolean digits(String str, int length)
    {
        for (int i = 0; i < length; i++) {

            if (str.charAt(i) >= '0' && str.charAt(i) <= '5') {
                return true;
            }
            else {
                return false;
            }
        }
        return false;
    }

}

