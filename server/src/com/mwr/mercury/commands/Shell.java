package com.mwr.mercury.commands;

import java.util.HashMap;

import com.mwr.mercury.commands.shell.ExecuteMercuryShell;
import com.mwr.mercury.commands.shell.ExecuteSingleCommand;
import com.mwr.mercury.commands.shell.NewMercuryShell;
import com.mwr.mercury.commands.shell.ReadMercuryShell;

public class Shell
{
	public static HashMap<String,Command> commands;
	
	static {
		commands = new HashMap<String,Command>();
		
		commands.put("executeMercuryShell", new ExecuteMercuryShell());
		commands.put("executeSingleCommand", new ExecuteSingleCommand());
		commands.put("newMercuryShell", new NewMercuryShell());
		commands.put("readMercuryShell", new ReadMercuryShell());
	}	
}
