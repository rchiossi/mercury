package com.mwr.mercury.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.mwr.mercury.Common;
import com.mwr.mercury.Session;

public class Shell extends CommandGroup
{
	private Command executeMercuryShell = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			// Get all the parameters
			String shellArgs = args.get("args");

			if (Common.mercuryShell.write(shellArgs))
				currentSession.sendFullTransmission("", "");
			else
				currentSession.sendFullTransmission("", "error");

		}
	};

	private Command executeSingleCommand = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			// Get all the parameters
			String shellArgs = args.get("args");

			String returnValue = "";

			// Execute a Linux command and get result
			try
			{

				// Default working directory
				File workDir = new File("/");
				String[] env = null;

				// Executes the process using sh -c command (so that piping
				// features etc. are present)
				Process proc = Runtime.getRuntime().exec(new String[]
				{ "sh", "-c", shellArgs }, env, workDir);

				// Wait for process to finish
				try
				{
					proc.waitFor();
				}
				catch (InterruptedException e)
				{
				}

				// Read output and error streams
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(proc.getInputStream()));
				BufferedReader errorreader = new BufferedReader(
						new InputStreamReader(proc.getErrorStream()));

				String line;

				// Display output and error streams
				while ((line = errorreader.readLine()) != null)
					returnValue += line + "\n";

				while ((line = reader.readLine()) != null)
					returnValue += line + "\n";

			}
			catch (Exception e)
			{
				currentSession.sendFullTransmission(e.getMessage(), "");
			}

			currentSession.sendFullTransmission(returnValue.trim(), "");

			return;

		}
	};

	private Command newMercuryShell = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			Common.mercuryShell = new com.mwr.mercury.Shell();
			currentSession.sendFullTransmission("", "");

		}
	};

	private Command readMercuryShell = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			currentSession.sendFullTransmission(Common.mercuryShell.read(), "");
		}
	};

	public Shell()
	{
		commands = new HashMap<String, Command>();

		commands.put("executeMercuryShell", executeMercuryShell);
		commands.put("executeSingleCommand", executeSingleCommand);
		commands.put("newMercuryShell", newMercuryShell);
		commands.put("readMercuryShell", readMercuryShell);
	}
}
