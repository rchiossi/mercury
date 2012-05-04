package com.mwr.mercury.commands.core;

import java.util.HashMap;

import com.mwr.mercury.Session;
import com.mwr.mercury.commands.Command;

public class Ping extends Command
{
	@Override
	public void execute(HashMap<String, String> args, Session currentSession)
	{
		currentSession.sendFullTransmission("pong", "");		
	}
}
