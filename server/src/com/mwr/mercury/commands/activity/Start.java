package com.mwr.mercury.commands.activity;

import java.util.HashMap;

import android.content.Intent;

import com.mwr.mercury.Session;
import com.mwr.mercury.commands.Command;

public class Start extends Command
{

	@Override
	public void execute(HashMap<String, String> args, Session currentSession)
	{
		//Parse intent		
		//TODO rchiossi - fix intent parsing
		Intent intent = new Intent();
		//Intent intent = Common.parseIntentGeneric(argsArray, new Intent());
		
		try
		{
			currentSession.applicationContext.startActivity(intent);
			currentSession.sendFullTransmission("Activity started with " + intent.toString(), "");
		}
		catch (Throwable t)
		{
			currentSession.sendFullTransmission("", t.getMessage());
		}
	}

}
