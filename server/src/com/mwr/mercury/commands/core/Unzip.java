package com.mwr.mercury.commands.core;

import java.util.HashMap;

import com.mwr.mercury.Common;
import com.mwr.mercury.Session;
import com.mwr.mercury.commands.Command;

public class Unzip extends Command
{

	@Override
	public void execute(HashMap<String, String> args, Session currentSession)
	{
		//Get path from arguments
		String path = args.get("path");
		String destination = args.get("destination");
		
		//Unzip file
		boolean success = Common.unzipClassesDex(path, destination);
		
		if (success)
			currentSession.sendFullTransmission("", "");
		else
			currentSession.sendFullTransmission("", "Unzip failed");		
	}

}
