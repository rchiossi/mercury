package com.mwr.mercury.commands;

import java.util.HashMap;

import com.mwr.mercury.Session;

public abstract class CommandGroup
{	
	public static final HashMap<String,CommandGroup> cmdList;
	
	static
	{
		cmdList = new HashMap<String,CommandGroup>();		
	
		cmdList.put("activity", new Activity());		
		cmdList.put("broadcast", new Broadcast());
		cmdList.put("core", new Core());
		cmdList.put("debuggable", new Debuggable());
		cmdList.put("packages", new Packages());
		cmdList.put("provider", new Provider());
		cmdList.put("service", new Service());
		cmdList.put("shell", new Shell());		
	}	
	
	public interface Command
	{	
		public void execute(HashMap<String,String> args, Session currentSession);
	}
	
	protected HashMap<String,Command> commands;
	
	public void execute(String command, HashMap<String,String> args, Session currentSession){
		Command cmd = commands.get(command);
		
		if (cmd == null)
			currentSession.sendFullTransmission("", "Command not found on Mercury server");
		else
			cmd.execute(args, currentSession);	
	}
}
