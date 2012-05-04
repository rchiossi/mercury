package com.mwr.mercury.commands;

import java.util.HashMap;

import com.mwr.mercury.commands.packages.AttackSurface;
import com.mwr.mercury.commands.packages.Info;
import com.mwr.mercury.commands.packages.Path;
import com.mwr.mercury.commands.packages.SharedUid;

public class Packages
{
	public static HashMap<String,Command> commands;
	
	static {
		commands = new HashMap<String,Command>();
		
		commands.put("attacksurface", new AttackSurface());
		commands.put("info", new Info());
		commands.put("path", new Path());
		commands.put("shareduid", new SharedUid());
	}	
}
