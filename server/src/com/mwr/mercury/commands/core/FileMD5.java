package com.mwr.mercury.commands.core;

import java.util.HashMap;

import com.mwr.mercury.Common;
import com.mwr.mercury.Session;
import com.mwr.mercury.commands.Command;

public class FileMD5 extends Command	
{

	@Override
	public void execute(HashMap<String, String> args, Session currentSession)
	{
		//Get path from arguments
		String path = args.get("path");
		
		try
		{
			//Send the number of bytes in the file
			currentSession.sendFullTransmission(Common.md5SumFile(path), "");
		}
		catch (Exception e)
		{
			currentSession.sendFullTransmission("", e.getMessage());
		}
	}
}
