// License: Refer to the README in the root directory

package com.mwr.mercury;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

//A class to wrap requests that come in
class RequestWrapper
{
	public String section;
	public String function;
	public HashMap<String,String> args;
}

public class Common
{	
	public static String LIST_DELIMITER = "#@#";
	
	//Mercury persistent shell
	public static Shell mercuryShell = null;
	
	//Get all local IP addresses - needs INTERNET permission
	public static ArrayList<String> getLocalIpAddresses()
	{		
		
		ArrayList<String> ips = new ArrayList<String>();
		
	    try
	    {
	    	//Iterate over all network interfaces
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
	        {
	        	//Get next network interface
	            NetworkInterface intf = en.nextElement();
	            
	            //Iterate over all IP addresses
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
	            {
	            	//Get next IP address
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                
	                //Add IP address if it is not a loopback address
	                if (!inetAddress.isLoopbackAddress())
	                	ips.add(inetAddress.getHostAddress().toString());
	                    
	            }
	        }
	        
	    }
	    catch (SocketException ex)
	    {
	        Log.e("getLocalIpAddress", ex.toString());
	    }
	    
	    return ips;
	}
	
	//Get md5Sum of file
	public static String md5SumFile(String path)
	{
		String md5 = "";
		
		try
		{
			MessageDigest digest = MessageDigest.getInstance("MD5");
			InputStream is = new FileInputStream(new File(path));				
			byte[] buffer = new byte[8192];
			int read = 0;
			try
			{
				while( (read = is.read(buffer)) > 0)
				{
					digest.update(buffer, 0, read);
				}	
				
				byte[] md5sum = digest.digest();
				BigInteger bigInt = new BigInteger(1, md5sum);
				md5 = bigInt.toString(16);
			}
			catch(IOException e)
			{
				throw new RuntimeException("Unable to process file for MD5", e);
			}
			finally
			{
				try
				{
					is.close();
				}
				catch(IOException e) {}
			}	
		}
		catch (Exception e) {}
		
		return md5;
	}

	//Get parameter from a List<ArgumentWrapper> in String format
		public static String getParamString(HashMap<String,String> args, String key)
		{
			String value = args.get(key);
			
			if (value == null) return "";
			
			return value;
		}

	public static List<String> getParamStringList(HashMap<String,String> args, String key)
	{
		List<String> returnValues = new ArrayList<String>();
		
		for (String item : args.get(key).split(Common.LIST_DELIMITER)) {
			returnValues.add(item);
		}		
		
		return returnValues;
	}

	
	//Convert a List to a contentvalues structure by splitting by =
	public static ContentValues listToContentValues(List<String> values, String type)
	{
		ContentValues contentvalues = new ContentValues();
	    
	    //Place values into contentvalue structure
	    for (int i = 0; i < values.size(); i++)
	    {
	    	String current = values.get(i);
	    	
	    	try
	    	{    	
		    	//Separate the value by = in order to get key:value
		    	Integer indexOfEquals = current.indexOf("=");
		    	String key = current.substring(0, indexOfEquals);
		    	String value = current.substring(indexOfEquals + 1);
		
		    	if (type.toUpperCase().equals("STRING"))
		    		contentvalues.put(key, value);
		    	
		    	if (type.toUpperCase().equals("BOOLEAN"))
		    		contentvalues.put(key, Boolean.valueOf(value));
	
		    	if (type.toUpperCase().equals("INTEGER"))
		    		contentvalues.put(key, new Integer(value));
		    	
		    	if (type.toUpperCase().equals("DOUBLE"))
		    		contentvalues.put(key, new Double(value));
		    	
		    	if (type.toUpperCase().equals("FLOAT"))
		    		contentvalues.put(key, new Float(value));
		    	
		    	if (type.toUpperCase().equals("LONG"))
		    		contentvalues.put(key, new Long(value));
		    	
		    	if (type.toUpperCase().equals("SHORT"))
		    		contentvalues.put(key, new Short(value));
	    	}
	    	catch (Exception e) 
	    	{
	    		Log.e("mercury", "Error with argument " + current);
	    	}
	    	
	    }
	    
	    return contentvalues;
	}
	
	//Get the columns of a content provider
	public static ArrayList<String> getColumns (ContentResolver resolver, String uri, String[] projectionArray)
	{
		//String returnValue = "";
		ArrayList<String> columns = new ArrayList<String>();
		
		try
		{				
	        //Issue query
	        Cursor c = resolver.query(Uri.parse(uri), projectionArray, null, null, null);
	                    		        	
	        //Get all column names and display
	        if (c != null)
	        {
	        	String [] colNames = c.getColumnNames();
	        	
	        	//Close the cursor
	        	c.close();
	        	
	        	//String columnNamesOutput = "";
	        	for (int k = 0; k < colNames.length; k++)
	        		columns.add(colNames[k]);
	        }
		}
		catch (Throwable t) {}
		
		return columns;
		

	}
	
	static {
		System.loadLibrary("common");
    }
	
	private static native String[] native_getUri(String path);
	
	public static List<String> getUri(String path) {
		List<String> uriList = new ArrayList<String>();
		
		String[] nativeList = native_getUri(path); 
		
		if (nativeList == null) return uriList;
		
		for (String uri : nativeList) {
			uriList.add(uri);
		}
		
		return uriList;
	}
	
	//Get all the printable characters in a file - like unix strings()
	public static ArrayList<String> strings (String path)
	{
		ArrayList<String> lines = new ArrayList<String>();
		
		//Buffer where string is formulated
		String buffer = "";
		
		//Keep reading chars
		boolean loop = true;
		
		//File
		InputStream is = null;
		
		try
		{
			//Open file
			File file = new File(path);
			if (file.exists())
			{
				is = new FileInputStream(file);
				
				//Read file byte for byte
				while (loop)
				{
					int tmp = is.read();
					
					//Is it a printable character - Printable chars = 32 - 126
					if ((tmp >= 32) && (tmp <= 126))
					{
						buffer += (char) tmp;
					}
					else
					{
						//If >= 4 readable chars in a row - then add as a string
						if (buffer.length() >= 4)
						{
							lines.add(buffer);
						}
						
						//Blank the buffer
						buffer = "";
					}
					
					//If read char is -1 then the eof has been reached
					if (tmp == -1)
						loop = false;
						
				}
			}
		}
		catch(IOException e)
		{
			return new ArrayList<String>();
		}
		finally
		{
			try
			{
				is.close();
			}
			catch(Exception e) {}
		}
		
		return lines;
		

	}

	//Parse a generic intent and add to given intent
		public static Intent parseIntentGeneric(HashMap<String,String> args, Intent intent)
		{		
			Intent localIntent = intent;
			
			for (String arg : args.keySet()) {								
				String[] split = args.get(arg).split("=");
				
				String key = split[0];				
				String value = split.length == 2? split[0]: split[1];
				
				//Parse arguments into Intent
				if (arg.toUpperCase().equals("ACTION"))
					localIntent.setAction(args.get(arg));
				
				if (arg.toUpperCase().equals("DATA"))
					localIntent.setData(Uri.parse(args.get(arg)));
					
				if (arg.toUpperCase().equals("MIME_TYPE"))
					localIntent.setType(args.get(arg));

				if (arg.toUpperCase().equals("CATEGORY"))
					localIntent.addCategory(args.get(arg));
					
				if (arg.toUpperCase().equals("COMPONENT"))
					localIntent.setComponent(new ComponentName(key, value));
					
				if (arg.toUpperCase().equals("FLAGS"))
					localIntent.setFlags(Integer.parseInt(args.get(arg)));
					
				if (arg.toUpperCase().equals("EXTRA-BOOLEAN"))
					localIntent.putExtra(key, Boolean.parseBoolean(value));
					
				if (arg.toUpperCase().equals("EXTRA-BYTE"))
					localIntent.putExtra(key, Byte.parseByte(value));
					
				if (arg.toUpperCase().equals("EXTRA-DOUBLE"))
					localIntent.putExtra(key, Double.parseDouble(value));
					
				if (arg.toUpperCase().equals("EXTRA-FLOAT"))
					localIntent.putExtra(key, Float.parseFloat(value));
					
				if (arg.toUpperCase().equals("EXTRA-INTEGER"))
					localIntent.putExtra(key, Integer.parseInt(value));
					
				if (arg.toUpperCase().equals("EXTRA-LONG"))
					localIntent.putExtra(key, Long.parseLong(value));
					
				if (arg.toUpperCase().equals("EXTRA-SERIALIZABLE"))
					localIntent.putExtra(key, Serializable.class.cast(value));
					
				if (arg.toUpperCase().equals("EXTRA-SHORT"))
					localIntent.putExtra(key, Short.parseShort(value));
					
				if (arg.toUpperCase().equals("EXTRA-STRING"))
					localIntent.putExtra(key, value);
			}
			
			return localIntent;
		}

	//Extract the src file to dest - return success
	public static boolean unzipClassesDex(String src, String dest)
	{
		final int BUFFER_SIZE = 4096;
		boolean success = false;
		  
		BufferedOutputStream bufferedOutputStream = null;
		FileInputStream fileInputStream;
		try
		{
			fileInputStream = new FileInputStream(src);
			ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream));
			ZipEntry zipEntry;
		      
			while ((zipEntry = zipInputStream.getNextEntry()) != null)
			{
				String zipEntryName = zipEntry.getName();
				if (zipEntryName.toUpperCase().equals("CLASSES.DEX"))
				{
					File file = new File(dest + zipEntryName);
					byte buffer[] = new byte[BUFFER_SIZE];
					FileOutputStream fileOutputStream = new FileOutputStream(file);
					bufferedOutputStream = new BufferedOutputStream(fileOutputStream, BUFFER_SIZE);
					int count;

					while ((count = zipInputStream.read(buffer, 0, BUFFER_SIZE)) != -1)
					{
						bufferedOutputStream.write(buffer, 0, count);
					}

					bufferedOutputStream.flush();
					bufferedOutputStream.close();
					
					success = true;
				}

			}
			zipInputStream.close();
		}
		catch (Exception e)
		{
			Log.e("mercury", e.getMessage());
		}

			   
		return success;

	}


}
