package com.mwr.mercury.commands;

import java.util.HashMap;

import com.mwr.mercury.commands.activity.Info;
import com.mwr.mercury.commands.activity.LaunchIntent;
import com.mwr.mercury.commands.activity.Match;
import com.mwr.mercury.commands.activity.Start;

public class Activity
{
	public static HashMap<String,Command> commands;
	
	static {
		commands = new HashMap<String,Command>();
		
		commands.put("info", new Info());
		commands.put("launchintent", new LaunchIntent());
		commands.put("match", new Match());
		commands.put("start", new Start());
	}


}
