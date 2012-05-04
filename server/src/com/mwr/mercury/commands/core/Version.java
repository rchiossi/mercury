package com.mwr.mercury.commands.core;

import java.util.HashMap;

import com.mwr.mercury.Session;
import com.mwr.mercury.commands.Command;

public class Version extends Command
{
	@Override
	public void execute(HashMap<String, String> args, Session currentSession)
	{
		String version = "";
		try
		{
			version = currentSession.applicationContext.getPackageManager().getPackageInfo(currentSession.applicationContext.getPackageName(), 0).versionName;
		}
		catch (Exception e) {}
		currentSession.sendFullTransmission(version, "");		
	}

}
