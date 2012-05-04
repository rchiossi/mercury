package com.mwr.mercury.commands;

import java.util.HashMap;

import com.mwr.mercury.Session;

public abstract class Command
{
	public static final HashMap<String,HashMap<String,Command>> cmdList;
	
	static
	{
		cmdList = new HashMap<String,HashMap<String,Command>>();	
		cmdList.put("activity", Activity.commands);
		cmdList.put("broadcast", Broadcast.commands);		
		cmdList.put("core", Core.commands);
		cmdList.put("debuggable", Debuggable.commands);
		cmdList.put("packages", Packages.commands);
		cmdList.put("provider", Provider.commands);
		cmdList.put("service", Service.commands);
		cmdList.put("shell", Shell.commands);		
	}
	
	public abstract void execute(HashMap<String,String> args, Session currentSession);
}
