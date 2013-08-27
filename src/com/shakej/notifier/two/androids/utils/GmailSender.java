package com.shakej.notifier.two.androids.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import android.os.AsyncTask;
import android.util.Log;

public class GmailSender extends javax.mail.Authenticator
{
	private String mailhost = "smtp.gmail.com";
	private String user;
	private String password;
	private Session session;
	
	private String _subject, _body, _sender, _recipients;
	
	
	public GmailSender(String user, String password)
	{
		this.user = user;
		this.password = password;
		
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.host", mailhost);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.quitwait", "false");
		
		session = Session.getDefaultInstance(props, this);
	}
	
	
	protected PasswordAuthentication getPasswordAuthentication()
	{
		return new PasswordAuthentication(user, password);
	}
	
	
	public synchronized void sendMail(String body, String type) throws Exception
	{
		Log.w("GmailSender | sendMail()", "sendMail Start " + type);
		if (PreferenceUtil.getEmail() == null)
		{
			return;
		}
		
		if (type.equals(StringConst.TYPE_CALL))
		{
			_subject = "[" + PreferenceUtil.getPhoneName() + " CALL] Android Notification";
			;
		}
		else if (type.equals(StringConst.TYPE_NOTI))
		{
			_subject = "[" + PreferenceUtil.getPhoneName() + " NOTI] Android Notification";
		}
		else if (type.equals(StringConst.TYPE_SMS))
		{
			_subject = "[" + PreferenceUtil.getPhoneName() + " SMS] Android Notification";
		}
		
		_body = body;
		_sender = "AndroidNotifier@gmail.com";
		_recipients = PreferenceUtil.getEmail();
		
		new GmailAsyn().execute("");
	}
	
	public class ByteArrayDataSource implements DataSource
	{
		private byte[] data;
		private String type;
		
		
		public ByteArrayDataSource(byte[] data, String type)
		{
			super();
			this.data = data;
			this.type = type;
		}
		
		
		public ByteArrayDataSource(byte[] data)
		{
			super();
			this.data = data;
		}
		
		
		public void setType(String type)
		{
			this.type = type;
		}
		
		
		public String getContentType()
		{
			if (type == null)
				return "application/octet-stream";
			else
				return type;
		}
		
		
		public InputStream getInputStream() throws IOException
		{
			return new ByteArrayInputStream(data);
		}
		
		
		public String getName()
		{
			return "ByteArrayDataSource";
		}
		
		
		public OutputStream getOutputStream() throws IOException
		{
			throw new IOException("Not Supported");
		}
	}
	
	public class GmailAsyn extends AsyncTask<String, Void, String[]>
	{
		
		@Override
		protected String[] doInBackground(String... params)
		{
			try
			{
				MimeMessage message = new MimeMessage(session);
				if (_body != null)
				{
					DataHandler handler = new DataHandler(new ByteArrayDataSource(_body.getBytes(), "text/plain"));
					message.setSender(new InternetAddress(_sender));
					message.setSubject(_subject);
					message.setDataHandler(handler);
					if (_recipients.indexOf(',') > 0)
						message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(_recipients));
					else
						message.setRecipient(Message.RecipientType.TO, new InternetAddress(_recipients));
					Transport.send(message);
				}
			} catch (AddressException e)
			{
				e.printStackTrace();
			} catch (MessagingException e)
			{
				e.printStackTrace();
			}
			
			return null;
		}
		
		
		@Override
		protected void onPostExecute(String[] result)
		{
			super.onPostExecute(result);
		}
	}
}