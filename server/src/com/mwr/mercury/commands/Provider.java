package com.mwr.mercury.commands;

import java.util.HashMap;

import com.mwr.mercury.commands.provider.Columns;
import com.mwr.mercury.commands.provider.Delete;
import com.mwr.mercury.commands.provider.Info;
import com.mwr.mercury.commands.provider.Insert;
import com.mwr.mercury.commands.provider.Query;
import com.mwr.mercury.commands.provider.Read;
import com.mwr.mercury.commands.provider.Update;

public class Provider
{
	public static HashMap<String,Command> commands;
	
	static {
		commands = new HashMap<String,Command>();
		
		commands.put("columns", new Columns());
		commands.put("delete", new Delete());
		commands.put("info", new Info());
		commands.put("insert", new Insert());
		commands.put("query", new Query());
		commands.put("read", new Read());
		commands.put("update", new Update());
	}	
}
