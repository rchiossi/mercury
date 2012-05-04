package com.mwr.mercury.commands;

import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.mwr.mercury.Session;

public class Broadcast extends CommandGroup
{
	private Command info = new Command()
	{
		
		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			//Assign filter if one came in the arguments
			String filter = args.get("filter");
			
			currentSession.startTransmission();
			currentSession.startResponse();
			currentSession.startData();
			
			//Get all packages from packagemanager
			PackageManager pm = currentSession.applicationContext.getPackageManager();
			List <PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_RECEIVERS | PackageManager.GET_PERMISSIONS);
			
			//Iterate through packages
			for (PackageInfo package_:packages)
			{
				
				ActivityInfo[] receivers = package_.receivers;			
				
				if (receivers != null)
				{	
					for (int i = 0; i < receivers.length; i++)
					{
						if (receivers[i].exported == true)
						{
							boolean filterPresent = filter.length() != 0;
							boolean filterRelevant = package_.packageName.contains(filter) || receivers[i].name.contains(filter);
							
							if ((filterPresent && filterRelevant) || !filterPresent)
							{
								currentSession.send("Package name: " + receivers[i].packageName + "\n", true);
								currentSession.send("Receiver: " + receivers[i].name + "\n\n", true);
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
	};

	private Command send = new Command()
	{
		
		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			//Parse intent
			//Intent intent = Common.parseIntentGeneric(argsArray, new Intent());
			//TODO rchiossi fix parse
			Intent intent = new Intent();
			
			try
			{
				currentSession.applicationContext.sendBroadcast(intent);
				currentSession.sendFullTransmission("Broadcast sent with " + intent.toString(), "");
			}
			catch (Throwable t)
			{
				currentSession.sendFullTransmission("", t.getMessage());
			}
		}
	};
	
	public Broadcast() {
		commands = new HashMap<String, Command>();
		
		commands.put("info", info);
		commands.put("send", send);
	}
}
