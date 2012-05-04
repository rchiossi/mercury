package com.mwr.mercury.commands.activity;

import java.util.HashMap;

import android.content.Intent;

import com.mwr.mercury.Session;
import com.mwr.mercury.commands.Command;

public class LaunchIntent extends Command
{

	@Override
	public void execute(HashMap<String, String> args, Session currentSession)
	{
		//Assign filter if one came in the arguments
		String packageName = args.get("packageName");
		
		Intent intent = currentSession.applicationContext.getPackageManager().getLaunchIntentForPackage(packageName);
		
		//Send intent back
		if (intent != null)
			currentSession.sendFullTransmission(intent.toString(), "");
		else
			currentSession.sendFullTransmission("", "No intent returned	");

	}

}
