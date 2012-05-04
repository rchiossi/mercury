package com.mwr.mercury.commands;

import java.util.HashMap;
import java.util.List;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.util.Log;

import com.mwr.mercury.Common;
import com.mwr.mercury.Session;

public class Debuggable extends CommandGroup
{
	private Command info = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			// Assign filter if one came in the arguments
			String filter = Common.getParamString(args,"filter");
			
			for (String word : Common.getUri("/my/test/path")){
				Log.e("mercury","word : " + word);
			}

			// String to return at the end of function
			String returnValue = "";

			// Get all packages from packagemanager
			List<PackageInfo> packages = currentSession.applicationContext
					.getPackageManager().getInstalledPackages(0);

			// Iterate through packages
			for (PackageInfo package_ : packages)
			{
				ApplicationInfo app = package_.applicationInfo;

				// Focus on debuggable apps only and apply filter
				if (((app.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0)
						&& (app.packageName.contains(filter)
								|| app.processName.contains(filter) || filter == ""))
				{
					returnValue += "Package name: " + app.packageName + "\n";
					returnValue += "UID: " + app.uid + "\n";

					// Find all permissions that this app has
					String strPermissions = "";
					String[] permissions = package_.requestedPermissions;

					if (permissions != null)
					{
						for (String permission : permissions)
							strPermissions += permission + "; ";
					}

					returnValue += "Permissions: " + strPermissions + "\n\n";
				}
			}

			currentSession.sendFullTransmission(returnValue.trim(), "");

		}
	};

	public Debuggable()
	{
		commands = new HashMap<String, Command>();

		commands.put("info", info);
	}
}
