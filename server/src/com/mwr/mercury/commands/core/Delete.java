package com.mwr.mercury.commands.core;

import java.io.File;
import java.util.HashMap;

import com.mwr.mercury.Session;
import com.mwr.mercury.commands.Command;

public class Delete extends Command
{

	@Override
	public void execute(HashMap<String, String> args, Session currentSession)
	{		
		try
		{
			if (new File(args.get("path")).delete())
				currentSession.sendFullTransmission("", "");
			else
				currentSession.sendFullTransmission("", "Could not delete");
		}
		catch (Exception e)
		{
			currentSession.sendFullTransmission("", e.getMessage());
		}
		
	}

}
