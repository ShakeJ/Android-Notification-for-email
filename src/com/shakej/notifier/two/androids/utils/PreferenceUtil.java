package com.shakej.notifier.two.androids.utils;

public final class PreferenceUtil extends BasePreferenceUtil
{
	/***********************
	 * key
	 **********************/
	// 최초 실행
	private static final String KEY_IS_FIRST_RUN = "first_run";
	
	private static final String KEY_EMAIL = "email";
	private static final String KEY_EMAIL_PASSWORD = "email_password";
	private static final String KEY_THIS_PHONE_NAME = "phone_name";
	
	private static final String KEY_ON_OFF = "key_on_off_here";
	
	
	/***********************
	 * method
	 **********************/
	/**
	 * 최초 실행 후 호출하기
	 */
	public static void setFirstRun()
	{
		put(KEY_IS_FIRST_RUN, false);
	}
	
	
	/**
	 * 최초 실행인지 판단
	 * @return
	 */
	public static boolean getFirstRun()
	{
		return get(KEY_IS_FIRST_RUN, true);
	}
	
	
	/**
	 * email
	 * @return
	 */
	public static String getEmail()
	{
		return get(KEY_EMAIL);
	}
	
	
	/**
	 * email
	 */
	public static void setEmail(String $url)
	{
		put(KEY_EMAIL, $url);
	}
	
	
	/**
	 * password
	 * @return
	 */
	public static String getPassword()
	{
		return get(KEY_EMAIL_PASSWORD);
	}
	
	
	/**
	 * password
	 */
	public static void setPassword(String $url)
	{
		put(KEY_EMAIL_PASSWORD, $url);
	}
	
	
	/**
	 * user token 가져오기
	 * @return
	 */
	public static String getPhoneName()
	{
		return get(KEY_THIS_PHONE_NAME);
	}
	
	
	/**
	 * Host-Url 설정
	 */
	public static void setPhoneName(String $url)
	{
		put(KEY_THIS_PHONE_NAME, $url);
	}
	
	
	/**
	 * user token 가져오기
	 * @return
	 */
	public static boolean getOnOff()
	{
		return get(KEY_ON_OFF, false);
	}
	
	
	/**
	 * Host-Url 설정
	 */
	public static void setOnOff(boolean $url)
	{
		put(KEY_ON_OFF, $url);
	}
}
