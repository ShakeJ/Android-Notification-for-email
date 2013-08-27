package com.shakej.notifier.two.androids.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

public class NotifierService extends AccessibilityService
{
	Context _context;
	
	
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event)
	{
		System.out.println("onAccessibilityEvent");
		if (event.getPackageName().toString().equals("com.google.android.gm"))
		{
			return;
		}
		else if (event.getPackageName().toString().equals("com.shakej.notifier.two.androids"))
		{
			return;
		}
		else if (event.getPackageName().toString().equals("android"))
		{
			return;
		}
		else
		{
			Intent noti = new Intent(TwoService.ACTION_SERVICE_NOTIFICATION);
			noti.putExtra("appname", event.getPackageName());
			noti.putExtra("message", event.getText().toString());
			this.sendBroadcast(noti);
		}
	}
	
	
	@Override
	protected void onServiceConnected()
	{
		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
		info.notificationTimeout = 0;
		info.feedbackType = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
		setServiceInfo(info);
	}
	
	
	@Override
	public void onInterrupt()
	{
	}
}