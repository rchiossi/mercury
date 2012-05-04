package com.mwr.mercury.commands;

import java.util.HashMap;

import com.mwr.mercury.commands.broadcast.Info;
import com.mwr.mercury.commands.broadcast.Send;

public class Broadcast
{
	public static HashMap<String,Command> commands;
	
	static {
		commands = new HashMap<String,Command>();
		
		commands.put("info", new Info());
		commands.put("send", new Send());
	}	
}
