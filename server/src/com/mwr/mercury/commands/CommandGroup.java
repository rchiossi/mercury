package com.mwr.mercury.commands;

import java.util.HashMap;

import com.mwr.mercury.Session;

public abstract class CommandGroup
{
	public static final HashMap<String,CommandGroup> cmdList;
	
	static
	{
		cmdList = new HashMap<String,CommandGroup>();
		/*
		cmdList.put("activity", Activity);
		cmdList.put("broadcast", Broadcast);		
		cmdList.put("core", Core);
		cmdList.put("debuggable", Debuggable);
		cmdList.put("packages", Packages);
		cmdList.put("provider", Provider);
		cmdList.put("service", Service);
		cmdList.put("shell", Shell);
		*/		
	}
	
	public abstract void execute(String command, HashMap<String,String> args, Session currentSession);
}
