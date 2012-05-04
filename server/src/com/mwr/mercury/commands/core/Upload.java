package com.mwr.mercury.commands.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import com.mwr.mercury.Session;
import com.mwr.mercury.commands.Command;

public class Upload extends Command
{

	@Override
	public void execute(HashMap<String, String> args, Session currentSession)
	{			
		File file = new File(args.get("path"));
		BufferedOutputStream out = null;
		
		try
		{
			out = new BufferedOutputStream(new FileOutputStream(file,true)); 
			out.write(args.get("data").getBytes());
			
			currentSession.sendFullTransmission("", "");
		
		}
		catch (Exception e)
		{
			currentSession.sendFullTransmission("", e.getMessage());
		}
		finally
		{
			//Close file
			if (out != null)
			{
				try
				{
					out.close(); 
				}
				catch (Exception e) {}
			}
		}		
	}
}
