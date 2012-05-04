package com.mwr.mercury.commands;

import java.util.HashMap;
import java.util.List;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;

import com.mwr.mercury.Common;
import com.mwr.mercury.Session;

public class Service extends CommandGroup
{
	private Command info = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			// Assign filter and permissions if they came in the arguments
			String filter = Common.getParamString(args,"filter");
			String permissions = Common.getParamString(args,"permissions");

			currentSession.startTransmission();
			currentSession.startResponse();
			currentSession.startData();

			// Get all packages from packagemanager
			PackageManager pm = currentSession.applicationContext
					.getPackageManager();
			List<PackageInfo> packages = pm
					.getInstalledPackages(PackageManager.GET_SERVICES
							| PackageManager.GET_PERMISSIONS);

			// Iterate through packages
			for (PackageInfo package_ : packages)
			{
				ServiceInfo[] services = package_.services;

				if (services != null)
				{
					for (int i = 0; i < services.length; i++)
					{
						boolean relevantFilter = false;
						boolean relevantPermissions = false;
						boolean noFilters = false;
						boolean bothFiltersRelevant = false;

						// Check if a filter was used
						if (filter.length() > 0)
							relevantFilter = package_.packageName
									.contains(filter)
									|| services[i].name.contains(filter);

						// Check if a permission filter was used
						try
						{
							if (permissions.length() > 0)
							{
								if (permissions.toUpperCase().equals("NULL"))
									relevantPermissions = (services[i].permission == null);
								else
									relevantPermissions = services[i].permission
											.contains(permissions);
							}
						}
						catch (Throwable t)
						{
						}

						// Check if no parameters were given
						if (filter.length() == 0 && permissions.length() == 0)
							noFilters = true;

						boolean bothFiltersPresent = false;
						if ((filter != "") && (permissions != ""))
							bothFiltersPresent = true;

						if (bothFiltersPresent && relevantFilter
								&& relevantPermissions)
							bothFiltersRelevant = true;

						// Apply filter and only look @ exported providers
						if (((bothFiltersPresent && bothFiltersRelevant)
								|| (!bothFiltersPresent && (relevantFilter || relevantPermissions)) || (!bothFiltersPresent && noFilters))
								&& services[i].exported)
						{
							currentSession.send("Package name: "
									+ services[i].packageName + "\n", true);
							currentSession.send("Service: " + services[i].name
									+ "\n", true);
							currentSession.send("Required Permission: "
									+ services[i].permission + "\n\n", true);
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

	private Command start = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			// Parse intent
			Intent intent = Common.parseIntentGeneric(args, new Intent());
			

			try
			{
				ComponentName service = currentSession.applicationContext
						.startService(intent);
				currentSession.sendFullTransmission(
						"Service started with " + intent.toString() + " - "
								+ service.flattenToString(), "");
			}
			catch (Throwable t)
			{
				currentSession.sendFullTransmission("", t.getMessage());
			}

		}
	};

	private Command stop = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			// Parse intent
			Intent intent = Common.parseIntentGeneric(args, new Intent());


			try
			{
				boolean stopped = currentSession.applicationContext
						.stopService(intent);
				if (stopped)
					currentSession.sendFullTransmission("Service stopped with "
							+ intent.toString(), "");
				else
					currentSession.sendFullTransmission(
							"Stopping service failed", "");
			}
			catch (Throwable t)
			{
				currentSession.sendFullTransmission("", t.getMessage());
			}

		}
	};

	public Service()
	{
		commands = new HashMap<String, Command>();

		commands.put("info", info);
		commands.put("start", start);
		commands.put("stop", stop);
	}
}
