package com.mwr.mercury.commands.activity;

import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.mwr.mercury.Session;
import com.mwr.mercury.commands.Command;

public class Match extends Command
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
		
			//Get all activities and iterate through them
			List<ResolveInfo> activities = currentSession.applicationContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY & PackageManager.GET_ACTIVITIES & PackageManager.GET_INTENT_FILTERS & PackageManager.GET_RESOLVED_FILTER	);

			String returnVal = intent.toString() + ":\n\n";
			
			for (int i = 0; i < activities.size(); i++)
			{

				String activityPackage = activities.get(i).activityInfo.packageName;
				String activityTargetActivity = activities.get(i).activityInfo.name;

				returnVal += "Package name: " + activityPackage + "\n";
				returnVal += "Target activity: " + activityTargetActivity + "\n\n";

			}
			
			currentSession.sendFullTransmission(returnVal.trim(), "");
		
		}
		catch (Exception e)
		{
			currentSession.sendFullTransmission("", e.getMessage());
		}

	}

}
