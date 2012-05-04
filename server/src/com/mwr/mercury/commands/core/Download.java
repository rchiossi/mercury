package com.mwr.mercury.commands.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

import android.util.Base64;

import com.mwr.mercury.Session;
import com.mwr.mercury.commands.Command;

public class Download extends Command	
{
	@Override
	public void execute(HashMap<String, String> args,
			Session currentSession)
	{
		//Get path from arguments
		String path = args.get("path");
		Integer offset = Integer.parseInt(args.get("offset"));
		
		//Start sending structure
		currentSession.startTransmission();
		currentSession.startResponse();
		currentSession.startData();
		
		File file = new File(path);
		InputStream in = null;
		
		int buffSize = 50 * 1024; //50KB
		
		try
		{
			in = new BufferedInputStream(new FileInputStream(file));
			
			byte[] buffer = new byte[buffSize];
			
			for (int i = 0; i < offset; i++)
				in.read();
			
			int bytesRead = in.read(buffer, 0, buffSize);
			
			currentSession.send(new String(Base64.encode(buffer, 0, bytesRead, Base64.DEFAULT)) + "\n", false);
			
			//End data section of structure
		    currentSession.endData();
		    currentSession.noError();
		
		}
		catch (Exception e)
		{
			currentSession.endData();
			currentSession.error(e.getMessage());
		}
		finally
		{
			//Close file
			if (in != null)
			{
				try
				{
					in.close(); 
				}
				catch (Exception e) {}
			}
		     
		     //End transmission
		     currentSession.endResponse();
		     currentSession.endTransmission();
		}		
	}
}
