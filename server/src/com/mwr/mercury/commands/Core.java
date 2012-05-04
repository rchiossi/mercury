package com.mwr.mercury.commands;

import java.util.HashMap;

import com.mwr.mercury.commands.core.Delete;
import com.mwr.mercury.commands.core.Download;
import com.mwr.mercury.commands.core.FileMD5;
import com.mwr.mercury.commands.core.Ping;
import com.mwr.mercury.commands.core.Strings;
import com.mwr.mercury.commands.core.Unzip;
import com.mwr.mercury.commands.core.Upload;
import com.mwr.mercury.commands.core.Version;

public class Core
{
	public static HashMap<String,Command> commands;
	
	static {
		commands = new HashMap<String,Command>();
		
		commands.put("delete", new Delete());
		commands.put("download", new Download());
		commands.put("fileMD5", new FileMD5());
		commands.put("ping", new Ping());
		commands.put("strings", new Strings());
		commands.put("unzip", new Unzip());
		commands.put("upload", new Upload());
		commands.put("version", new Version());
	}	
}
