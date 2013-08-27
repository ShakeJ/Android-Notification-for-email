package com.shakej.notifier.two.androids;

import java.util.List;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.Toast;

import com.shakej.notifier.send.email.R;
import com.shakej.notifier.two.androids.service.TwoService;
import com.shakej.notifier.two.androids.utils.ContextUtil;
import com.shakej.notifier.two.androids.utils.DialogUtil;
import com.shakej.notifier.two.androids.utils.PreferenceUtil;

public class MainActivity extends Activity
{
	public static final String SERVICE_NAME = "com.shakej.notifier.two.androids.service.NotifierService";
	private Button _btnOnOff;
	private Button _btnNotification;
	
	private List<AccessibilityServiceInfo> serviceList;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		ContextUtil.CONTEXT = this;
		
		if (PreferenceUtil.getEmail() == null)
		{
			Toast.makeText(this, getString(R.string.waring_not_account), Toast.LENGTH_LONG).show();
			Intent kIntent = new Intent(this, PreferenceActivity.class);
			startActivity(kIntent);
		}
		
		_btnOnOff = (Button) findViewById(R.id.btn_on_off);
		_btnNotification = (Button) findViewById(R.id.btn_notification);
		
		if (PreferenceUtil.getFirstRun())
		{
			PreferenceUtil.setOnOff(true);
			PreferenceUtil.setFirstRun();
		}
		
		_btnOnOff.setOnClickListener(onOffClickListener);
		_btnNotification.setOnClickListener(notificationListener);
	}
	
	
	@Override
	protected void onResume()
	{
		super.onResume();
		if (PreferenceUtil.getOnOff())
		{
			_btnOnOff.setText(getString(R.string.on));
			Intent service = new Intent(this, TwoService.class);
			startService(service);
		}
		else
		{
			_btnOnOff.setText(getString(R.string.off));
		}
		
		if (isAccessbility())
		{
			_btnNotification.setText(getString(R.string.btn_noti_on));
		}
		else
		{
			_btnNotification.setText(getString(R.string.btn_noti_off));
		}
	}
	
	private OnClickListener notificationListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if (!isAccessbility())
			{
				DialogUtil.show(MainActivity.this, getString(R.string.app_name), getString(R.string.accessibility_setting_msg), msgOkListener, getString(R.string.accessibility_setting_popup_ok),
						null, getString(R.string.accessibility_setting_popup_cancel));
			}
			else
			{
				DialogUtil.show(MainActivity.this, getString(R.string.app_name), getString(R.string.accessibility_setting_msg_off), msgOkListener, getString(R.string.accessibility_setting_popup_ok),
						null, getString(R.string.accessibility_setting_popup_cancel));
			}
		}
	};
	private android.content.DialogInterface.OnClickListener msgOkListener = new DialogInterface.OnClickListener()
	{
		
		@Override
		public void onClick(DialogInterface dialog, int which)
		{
			Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
			startActivity(intent);
		}
	};
	
	
	@SuppressLint("NewApi")
	private boolean isAccessbility()
	{
		AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
		serviceList = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
		String kListString = serviceList.toString();
		boolean kIsAccessbility = kListString.contains(SERVICE_NAME);
		return kIsAccessbility;
	}
	
	private OnClickListener onOffClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View arg0)
		{
			if (PreferenceUtil.getOnOff())
			{
				PreferenceUtil.setOnOff(false);
				_btnOnOff.setText(getString(R.string.off));
				Intent kOffIntent = new Intent(TwoService.ACTION_SERVICE_OFF);
				sendBroadcast(kOffIntent);
			}
			else
			{
				PreferenceUtil.setOnOff(true);
				_btnOnOff.setText(getString(R.string.on));
				Intent service = new Intent(MainActivity.this, TwoService.class);
				startService(service);
			}
		}
	};
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0, 1, 0, "Donate");
		menu.add(0, 2, 0, "Setting");
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		
		switch (item.getItemId())
		{
			case 1:
				break;
			
			case 2:
				Intent kIntent = new Intent(MainActivity.this, PreferenceActivity.class);
				startActivity(kIntent);
				break;
			
			default:
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}
}