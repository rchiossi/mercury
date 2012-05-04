package com.mwr.mercury.commands.core;

import java.io.File;
import java.util.HashMap;

import com.mwr.mercury.Session;
import com.mwr.mercury.commands.Command;

public class FIleSize extends Command	
{
	@Override
	public void execute(HashMap<String, String> args, Session currentSession)
	{
		//Get path from arguments
		String path = new String(args.get("path"));
		
		try
		{
			File file = new File(path);
			
			//Send the size of the file
			if (file.exists())
				currentSession.sendFullTransmission(String.valueOf(file.length()), "");
			else
				currentSession.sendFullTransmission("", "File does not exist");
		}
		catch (Exception e)
		{
			currentSession.sendFullTransmission("", e.getMessage());
		}		
	}
}
