package com.shakej.notifier.two.androids.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;

import com.shakej.notifier.send.email.R;
import com.shakej.notifier.two.androids.MainActivity;
import com.shakej.notifier.two.androids.utils.ContextUtil;
import com.shakej.notifier.two.androids.utils.GmailSender;
import com.shakej.notifier.two.androids.utils.PreferenceUtil;
import com.shakej.notifier.two.androids.utils.StringConst;

@SuppressLint("NewApi")
public class TwoService extends Service
{
	private int ID_REMOTSERVICE = 102;
	final static private String ACTION_MMS_RECEIVED = "android.provider.Telephony.WAP_PUSH_RECEIVED";
	final static public String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	final static public String ACTION_SERVICE_OFF = "com.shakej.service.off";
	final static public String ACTION_SERVICE_NOTIFICATION = "com.shakej.notification";
	
	Notification n;
	
	
	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}
	
	
	@Override
	public void onCreate()
	{
		Log.w("TwoService | onCreate()", "Start Service");
		ContextUtil.CONTEXT = this;
		
		showNotify();
		registerReceiverFilter();
	}
	
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
	
	
	private void showNotify()
	{
		PendingIntent contentIntent = PendingIntent.getActivity(this, ID_REMOTSERVICE, new Intent(this, MainActivity.class), 0);
		
		@SuppressWarnings("unused")
		NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Resources res = this.getResources();
		Notification.Builder builder = new Notification.Builder(this);
		
		builder.setContentIntent(contentIntent).setSmallIcon(R.drawable.ic_launcher).setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher))
				.setTicker(res.getString(R.string.app_name)).setWhen(System.currentTimeMillis()).setAutoCancel(false).setContentTitle(res.getString(R.string.app_name))
				.setContentText("Detect New activity...");
		builder.setOngoing(true);
		n = builder.build();
		
		startForeground(ID_REMOTSERVICE, n);
	}
	
	
	/*
	 * ////////////////////////////////////////////////////////////////////////
	 * Intent Filter 등록
	 */////////////////////////////////////////////////////////
	private void registerReceiverFilter()
	{
		IntentFilter filter = new IntentFilter(ACTION_SMS_RECEIVED);
		filter.setPriority(2147483647);
		this.registerReceiver(mReceiver, filter);
		filter = new IntentFilter(ACTION_MMS_RECEIVED);
		filter.setPriority(2147483647);
		this.registerReceiver(mReceiver, filter);
		filter = new IntentFilter(ACTION_SERVICE_OFF);
		this.registerReceiver(mReceiver, filter);
		filter = new IntentFilter(ACTION_SERVICE_NOTIFICATION);
		this.registerReceiver(mReceiver, filter);
	}
	
	/*
	 * /////////////////////////////////////////////////////////
	 * BroadCastReceiver
	 */////////////////////////////////////////////////////////
	private final BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			Log.w("BTReader", "서비스 브로드 캐스트에서 받은 액션 :" + action);
			
			if (action.equals("android.provider.Telephony.SMS_RECEIVED"))
			{
				try
				{
					Bundle bundle = intent.getExtras();
					
					String lastMessage = "";
					String lastNumber = "";
					
					Object[] pdusObj = (Object[]) bundle.get("pdus");
					if (pdusObj == null)
					{
						return;
					}
					
					SmsMessage[] messages = new SmsMessage[pdusObj.length];
					for (int i = 0; i < messages.length; i++)
					{
						messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
						
						String incomingNumber = messages[i].getDisplayOriginatingAddress();
						if (incomingNumber != null)
						{
							String[] incomingNumbers = incomingNumber.split(";");
							lastNumber = incomingNumbers[0];
						}
						else
							return;
						
						lastMessage = lastNumber + "님에게서 온 SMS : " + lastMessage + " " + messages[i].getMessageBody();
					}
					try
					{
						GmailSender sender = new GmailSender(PreferenceUtil.getEmail(), PreferenceUtil.getPassword());
						sender.sendMail(lastMessage, StringConst.TYPE_SMS);
					} catch (Exception e)
					{
						
					}
				} catch (Exception e)
				{
					Log.e("SendMail", e.getMessage(), e);
				}
			}
			else if (action.equals(ACTION_MMS_RECEIVED))
			{
				String incomingNumber = "";
				Bundle bundle = intent.getExtras();
				if (bundle != null)
				{
					byte[] buffer = bundle.getByteArray("data");
					String kIncomingNumber = new String(buffer);
					int indx = kIncomingNumber.indexOf("/TYPE");
					if (indx > 0 && (indx - 15) > 0)
					{
						int newIndx = indx - 15;
						kIncomingNumber = kIncomingNumber.substring(newIndx, indx);
						indx = kIncomingNumber.indexOf("+");
						if (indx > 0)
						{
							kIncomingNumber = kIncomingNumber.substring(indx);
						}
						incomingNumber = kIncomingNumber;
					}
				}
				
				final String kNumber = incomingNumber;
				
				Handler mHandler = new Handler();
				mHandler.postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						String body = "";
						Cursor cursor = getContentResolver().query(Uri.parse("content://mms/part"), null, null, null, null);
						try
						{
							cursor.moveToLast();
							String partId = cursor.getString(cursor.getColumnIndex("_id"));
							String type = cursor.getString(cursor.getColumnIndex("ct"));
							if ("text/plain".equals(type))
							{
								String data = cursor.getString(cursor.getColumnIndex("_data"));
								
								if (data != null)
								{
									body = getMmsText(partId);
								}
								else
								{
									body = cursor.getString(cursor.getColumnIndex("text"));
								}
							}
							cursor.close();
							
						} catch (Exception e)
						{
							Log.w("BTReaderReceiver | onReceive()", "MMS Parse Error : " + e);
						}
						
						try
						{
							body = kNumber + "님에게 온 MMS : " + body;
							GmailSender sender = new GmailSender(PreferenceUtil.getEmail(), PreferenceUtil.getPassword());
							sender.sendMail(body, StringConst.TYPE_SMS);
						} catch (Exception e)
						{
							
						}
					}
				}, 3000);
			}
			else if (action.equals(ACTION_SERVICE_OFF))
			{
				stopForeground(true);
				TwoService.this.stopSelf();
			}
			if (action.equals(ACTION_SERVICE_NOTIFICATION))
			{
				try
				{
					String msg = intent.getStringExtra("message");
					String appname = intent.getStringExtra("appname");
					msg = "From " + appname + "\nApplication Notification ::\n\n" + msg;
					GmailSender sender = new GmailSender(PreferenceUtil.getEmail(), PreferenceUtil.getPassword());
					sender.sendMail(msg, StringConst.TYPE_NOTI);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	};
	
	
	//MMS GET
	public String getMmsText(String id)
	{
		Uri partURI = Uri.parse("content://mms/part/" + id);
		InputStream is = null;
		StringBuilder sb = new StringBuilder();
		try
		{
			is = getContentResolver().openInputStream(partURI);
			if (is != null)
			{
				InputStreamReader isr = new InputStreamReader(is, "UTF-8");
				BufferedReader reader = new BufferedReader(isr);
				String temp = reader.readLine();
				while (temp != null)
				{
					sb.append(temp);
					temp = reader.readLine();
					Log.w("BTReaderReceiver | getMmsText()", "temp : " + temp);
				}
			}
		} catch (IOException e)
		{
		} finally
		{
			if (is != null)
			{
				try
				{
					is.close();
				} catch (IOException e)
				{
				}
			}
		}
		return sb.toString();
	}
}
