package com.mwr.mercury.commands.activity;

import java.util.HashMap;
import java.util.List;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.mwr.mercury.Session;
import com.mwr.mercury.commands.Command;

public class Info extends Command
{

	@Override
	public void execute(HashMap<String, String> args, Session currentSession)
	{
		//Assign filter if one came in the arguments
		String filter = args.get("filter");
		
		currentSession.startTransmission();
		currentSession.startResponse();
		currentSession.startData();
		
		//Iterate through all packages
		List<PackageInfo> packages = currentSession.applicationContext.getPackageManager().getInstalledPackages(0);
        for(PackageInfo pack : packages)
        {
        	//Get activities in package
            ActivityInfo[] activities = null;
            try
            {
            	activities = currentSession.applicationContext.getPackageManager().getPackageInfo(pack.packageName, PackageManager.GET_ACTIVITIES).activities;
            }
            catch (Exception e) {}
            
        	if (activities != null)
			{	
				for (int i = 0; i < activities.length; i++)
				{
					if (activities[i].exported == true)
					{
						boolean filterPresent = filter.length() != 0;
						boolean filterRelevant = pack.packageName.contains(filter) || activities[i].name.contains(filter);
						
						if ((filterPresent && filterRelevant) || !filterPresent)
						{
							currentSession.send("Package name: " + activities[i].packageName + "\n", true);
							currentSession.send("Activity: " + activities[i].name + "\n\n", true);
						}
					}
				}
			}
        }
		
		currentSession.endData();
		currentSession.noError();
		currentSession.endResponse();
		currentSession.endTransmission();
	}

}
