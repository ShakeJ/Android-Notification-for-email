package com.shakej.notifier.two.androids;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.shakej.notifier.send.email.R;
import com.shakej.notifier.two.androids.utils.ContextUtil;
import com.shakej.notifier.two.androids.utils.PreferenceUtil;

public class PreferenceActivity extends Activity
{
	private EditText _editEmail, _password, _passwordRe, _phoneName;
	private Button _btnSend;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_preference);
		ContextUtil.CONTEXT = this;
		
		_phoneName = (EditText) findViewById(R.id.edit_nickname);
		_editEmail = (EditText) findViewById(R.id.edit_mail);
		_password = (EditText) findViewById(R.id.edit_pwd);
		_passwordRe = (EditText) findViewById(R.id.edit_pwd_re);
		_btnSend = (Button) findViewById(R.id.btn_email_ok);
		
		setContent();
		
		_btnSend.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (TextUtils.isEmpty(_phoneName.getText().toString()))
				{
					Toast.makeText(PreferenceActivity.this, getString(R.string.nickname_empty), Toast.LENGTH_LONG).show();
					return;
				}
				if (TextUtils.isEmpty(_editEmail.getText().toString()))
				{
					Toast.makeText(PreferenceActivity.this, getString(R.string.email_empty), Toast.LENGTH_LONG).show();
					return;
				}
				if (TextUtils.isEmpty(_password.getText().toString()) || TextUtils.isEmpty(_passwordRe.getText().toString()))
				{
					Toast.makeText(PreferenceActivity.this, getString(R.string.password_empty), Toast.LENGTH_LONG).show();
					return;
				}
				
				if (!_password.getText().toString().equals(_passwordRe.getText().toString()))
				{
					Toast.makeText(PreferenceActivity.this, getString(R.string.password_diffrent), Toast.LENGTH_LONG).show();
					return;
				}
				
				PreferenceUtil.setEmail(_editEmail.getText().toString());
				PreferenceUtil.setPassword(_password.getText().toString());
				PreferenceUtil.setPhoneName(_phoneName.getText().toString());
				finish();
			}
		});
	}
	
	
	private void setContent()
	{
		if (PreferenceUtil.getEmail() != null)
		{
			_editEmail.setText(PreferenceUtil.getEmail());
		}
		if (PreferenceUtil.getPassword() != null)
		{
			_password.setText(PreferenceUtil.getPassword());
			_passwordRe.setText(PreferenceUtil.getPassword());
		}
		if (PreferenceUtil.getPhoneName() != null)
		{
			_phoneName.setText(PreferenceUtil.getPhoneName());
		}
	}
}
