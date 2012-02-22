package com.pjgatt.android.lolwotd;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class lolwotdActivity extends Activity
{
	TextView day, hour, min, sec;
	int iDay, iHour, iMin, iSec;
	MyCount counter;
	
	Date endDate = null;
	Date startDate = null;
	
	Time now = null;
	Time end = null;
	
	NumberFormat myFormat = NumberFormat.getInstance();
	
	private static final int ID_My_Notification = 1;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss");
        
        hour = (TextView)findViewById(R.id.hour);
        min = (TextView)findViewById(R.id.min);
        sec = (TextView)findViewById(R.id.sec);
        
        myFormat.setMinimumIntegerDigits(2);
        
        Button startbtn = (Button)findViewById(R.id.startbtn);
        startbtn.setOnClickListener(new View.OnClickListener()
        	{
        		public void onClick(View arg0)
        		{
        			startTimer();
        		};
        	}
        		
        );
        
        Button stopbtn = (Button)findViewById(R.id.stopbtn);
        stopbtn.setOnClickListener(new View.OnClickListener()
        	{
        		public void onClick(View arg0)
        		{
        			stopTimer();
        		}
        	}
        );
        
        Button resetbtn = (Button)findViewById(R.id.resetbtn);
        resetbtn.setOnClickListener(new View.OnClickListener()
        	{
        		public void onClick(View arg0)
        		{
        			stopTimer();
        			startTimer();
        		}
        	}
        );
        
    }
	
	public void startTimer()
	{
		if(now == null)
		{
			now = new Time();
			end = new Time();
			
			// put get start time here
			now.setToNow();
			// End time is now + 22 hours
			end.set(now.toMillis(false) + 22 * 60 * 60 * 1000);
			
			// change this to simply 22 hours in hours/min/sec
			long diffInMis = end.toMillis(false) - now.toMillis(false);
			
			long diff = TimeUnit.MILLISECONDS.toSeconds(diffInMis);
			
			iHour = (int) (diff/3600);
			long lhour = (diff % (3600));
			
			iMin = (int) (lhour/60);
			long lmin = (lhour % (60));
			
			iSec = (int) (lmin);
			
			hour.setText(String.valueOf(myFormat.format(iHour)).toString());
			min.setText(":" + String.valueOf(myFormat.format(iMin)).toString());
			sec.setText(":" + String.valueOf(myFormat.format(iSec)).toString());
			
			counter = new MyCount(iSec*1000, 1000);
		}
		
		counter.start();
	}
	
	public void stopTimer()
	{
		counter.cancel();
		now = null;
		end = null;
		hour.setText("00");
		min.setText(":00");
		sec.setText(":00");
	}
	
	public void setNotification()
	{
		//Get a reference to the NotificationManager
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		   
		//Instantiate the Notification
		int icon = android.R.drawable.ic_dialog_alert;
		CharSequence tickerText = "LoL - WotD Available!";
		long when = System.currentTimeMillis();
		
		Notification notification = new Notification(icon, tickerText, when);
		   
		//Define the Notification's expanded message and Intent
		Context context = getApplicationContext();
		CharSequence contentTitle = "LoL Win of the Day";
		CharSequence contentText = "Win of the Day Available!";
		//Intent notificationIntent = new Intent(AndroidStatusBarNotifications.this, AndroidStatusBarNotifications.class);
		Intent notificationIntent = new Intent(getBaseContext(), lolwotdActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(lolwotdActivity.this, 0, notificationIntent, 0);
		
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		   
		//Pass the Notification to the NotificationManager
		mNotificationManager.notify(ID_My_Notification, notification);
	}
	
	public class MyCount extends CountDownTimer
	{
		public MyCount(long millisInFuture, long countDownInterval)
		{
			super(millisInFuture, countDownInterval);
		}
		
		@Override
		public void onFinish()
		{
			counter = new MyCount(60000, 1000);
			counter.start();
			
			iMin -= 1;
			
			if(iMin > -1)
			{
				min.setText(":" + String.valueOf(myFormat.format(iMin)).toString());
			}
			else
			{
				iMin = 59;
				min.setText(":" + String.valueOf(myFormat.format(iMin)).toString());
				iHour -= 1;
				
				if(iHour > -1)
				{
					hour.setText(String.valueOf(myFormat.format(iHour)).toString());
				}
				else
				{
					iHour = 11;
					hour.setText(String.valueOf(myFormat.format(iHour)).toString());
					iDay -= 1;
					
					if(iDay < 0)
					{
						stopTimer();
						setNotification();
					}
				}
			}
		}
		
		@Override
		public void onTick(long millisUntilFinished)
		{
			sec.setText(":" + String.valueOf(myFormat.format(millisUntilFinished/1000)));
		}
	}
}