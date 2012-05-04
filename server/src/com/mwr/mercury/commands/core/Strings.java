package com.mwr.mercury.commands.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.mwr.mercury.Common;
import com.mwr.mercury.Session;
import com.mwr.mercury.commands.Command;

public class Strings extends Command
{

	@Override
	public void execute(HashMap<String, String> args, Session currentSession)
	{				
		ArrayList<String> lines = Common.strings(args.get("path"));
		Iterator<String> it = lines.iterator();
		
		currentSession.startTransmission();
		currentSession.startResponse();
		currentSession.startData();
		
		while (it.hasNext())
			currentSession.send(it.next() + "\n", true); //Send this with newline
		
		currentSession.endData();
		currentSession.noError();
		currentSession.endResponse();
		currentSession.endTransmission();		
	}
}
