package com.mwr.mercury.commands;

import java.util.HashMap;

import com.mwr.mercury.commands.service.Info;
import com.mwr.mercury.commands.service.Start;
import com.mwr.mercury.commands.service.Stop;

public class Service
{
	public static HashMap<String,Command> commands;
	
	static {
		commands = new HashMap<String,Command>();
		
		commands.put("info", new Info());
		commands.put("start", new Start());
		commands.put("stop", new Stop());
	}	
}
