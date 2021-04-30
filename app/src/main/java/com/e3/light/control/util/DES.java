package com.e3.light.control.util;

import android.util.Base64;

import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class DES {
	//	private String key = "lortnoc3"; 
	private static byte[] encryptkey = {(byte)0x6c,(byte)0x6f,(byte)0x72,(byte)0x74,(byte)0x6e,(byte)0x6f,(byte)0x63,(byte)0x33};
	 
	// private String psdKye = "lrotonc3";
	public final static byte[] encryptPsdkey = { (byte) 0x6c, (byte) 0x72, (byte) 0x6f,
		(byte) 0x74, (byte) 0x6f, (byte) 0x6e, (byte) 0x63, (byte) 0x33 };
		
	public static String encryptDES(String encryptString, String encKey) throws Exception
	{
		SecretKeySpec key = new SecretKeySpec(encKey.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS7Padding","BC");
		cipher.init(Cipher.ENCRYPT_MODE, key);//, zeroIv
		byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
		return new String(Base64.encode(encryptedData, Base64.DEFAULT));
		
	}
	
	public static String encryptDES(String encryptString, byte[] encKey)
			throws Exception {
		SecretKeySpec key = new SecretKeySpec(encKey, "DES");
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS7Padding", "BC");
		cipher.init(Cipher.ENCRYPT_MODE, key);// , zeroIv
		byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
		return new String(Base64.encode(encryptedData, Base64.DEFAULT));

	}
	
	public static String encryptDES(String encryptString) throws Exception
	{
		SecretKeySpec key = new SecretKeySpec(encryptkey, "DES");
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS7Padding","BC");
		cipher.init(Cipher.ENCRYPT_MODE, key);//, zeroIv
		byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
		return new String(Base64.encode(encryptedData, Base64.DEFAULT));
		
	}
	

	private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
		DESKeySpec dks = new DESKeySpec(key);

		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(dks);

		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS7Padding", "BC");

		cipher.init(Cipher.DECRYPT_MODE, securekey);

		return cipher.doFinal(data);
	}

	public static String decrypt(String data) throws IOException,
            Exception {
		if (data == null)
			return null;
		byte[] b = Base64.decode(data.getBytes(), Base64.DEFAULT);
		byte[] bt = decrypt(b, encryptkey);

		String psd = new String(bt);

		return psd;
	}
	
	public static String decrypt(String data, byte[] dekey) throws IOException,
            Exception {
		if (data == null)
			return null;
		byte[] b = Base64.decode(data.getBytes(), Base64.DEFAULT);
		byte[] bt = decrypt(b, dekey);
		return new String(bt);
	}
	
	public static void main(String[] arg){
		String str = "HGCcTl+ggr4=";
		
		try {
			String result = decrypt(str, encryptPsdkey);
			System.out.println(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
