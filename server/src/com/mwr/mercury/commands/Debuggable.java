package com.mwr.mercury.commands;

import java.util.HashMap;

import com.mwr.mercury.commands.debuggable.Info;

public class Debuggable
{
	public static HashMap<String,Command> commands;
	
	static {
		commands = new HashMap<String,Command>();
		
		commands.put("info", new Info());
	}	
}
