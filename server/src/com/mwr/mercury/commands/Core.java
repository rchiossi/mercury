package com.mwr.mercury.commands;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.util.Base64;

import com.mwr.mercury.Common;
import com.mwr.mercury.Session;

public class Core extends CommandGroup
{
	private Command delete = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			// Get path from arguments
			String path = Common.getParamString(args,"path");

			try
			{
				if (new File(path).delete())
					currentSession.sendFullTransmission("", "");
				else
					currentSession.sendFullTransmission("", "Could not delete");
			}
			catch (Exception e)
			{
				currentSession.sendFullTransmission("", e.getMessage());
			}
		}
	};

	private Command download = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			// Get path from arguments
			String path = Common.getParamString(args,"path");
			Integer offset = Integer.parseInt(Common.getParamString(args,"offset"));

			// Start sending structure
			currentSession.startTransmission();
			currentSession.startResponse();
			currentSession.startData();

			File file = new File(path);
			InputStream in = null;

			int buffSize = 50 * 1024; // 50KB

			try
			{
				in = new BufferedInputStream(new FileInputStream(file));

				byte[] buffer = new byte[buffSize];

				for (int i = 0; i < offset; i++)
					in.read();

				int bytesRead = in.read(buffer, 0, buffSize);

				currentSession.send(
						new String(Base64.encode(buffer, 0, bytesRead,
								Base64.DEFAULT)) + "\n", false);

				// End data section of structure
				currentSession.endData();
				currentSession.noError();

			}
			catch (Exception e)
			{
				currentSession.endData();
				currentSession.error(e.getMessage());
			}
			finally
			{
				// Close file
				if (in != null)
				{
					try
					{
						in.close();
					}
					catch (Exception e)
					{
					}
				}

				// End transmission
				currentSession.endResponse();
				currentSession.endTransmission();
			}

		}
	};

	private Command fileSize = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			String path = Common.getParamString(args,"path");

			try
			{
				File file = new File(path);

				// Send the size of the file
				if (file.exists())
					currentSession.sendFullTransmission(
							String.valueOf(file.length()), "");
				else
					currentSession.sendFullTransmission("",
							"File does not exist");
			}
			catch (Exception e)
			{
				currentSession.sendFullTransmission("", e.getMessage());
			}
		}
	};

	private Command fileMD5 = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			// Get path from arguments
			String path = Common.getParamString(args,"path");

			try
			{
				// Send the number of bytes in the file
				currentSession
						.sendFullTransmission(Common.md5SumFile(path), "");
			}
			catch (Exception e)
			{
				currentSession.sendFullTransmission("", e.getMessage());
			}

		}
	};

	private Command ping = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			currentSession.sendFullTransmission("pong", "");
		}
	};

	private Command strings = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			// Get path from arguments
			String path = Common.getParamString(args,"path");

			ArrayList<String> lines = Common.strings(path);
			Iterator<String> it = lines.iterator();

			currentSession.startTransmission();
			currentSession.startResponse();
			currentSession.startData();

			while (it.hasNext())
				currentSession.send(it.next() + "\n", true); // Send this with
																// newline

			currentSession.endData();
			currentSession.noError();
			currentSession.endResponse();
			currentSession.endTransmission();

		}
	};

	private Command unzip = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			// Get path from arguments
			String path = Common.getParamString(args,"path");
			String destination = Common.getParamString(args,"destination");

			// Unzip file
			boolean success = Common.unzipClassesDex(path, destination);

			if (success)
				currentSession.sendFullTransmission("", "");
			else
				currentSession.sendFullTransmission("", "Unzip failed");

		}
	};

	private Command upload = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			// Get path from arguments
			String path = Common.getParamString(args,"path");
			byte[] data = Common.getParamString(args,"data").getBytes();

			File file = new File(path);
			BufferedOutputStream out = null;

			try
			{
				out = new BufferedOutputStream(new FileOutputStream(file, true));
				out.write(data);

				currentSession.sendFullTransmission("", "");

			}
			catch (Exception e)
			{
				currentSession.sendFullTransmission("", e.getMessage());
			}
			finally
			{
				// Close file
				if (out != null)
				{
					try
					{
						out.close();
					}
					catch (Exception e)
					{
					}
				}
			}

		}
	};

	private Command version = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			String version = "";
			try
			{
				version = currentSession.applicationContext
						.getPackageManager()
						.getPackageInfo(currentSession.applicationContext
										.getPackageName(),0).versionName;
			}
			catch (Exception e)
			{
			}
			currentSession.sendFullTransmission(version, "");
		}
	};

	public Core()
	{
		commands = new HashMap<String, Command>();

		commands.put("delete", delete);
		commands.put("download", download);
		commands.put("fileMD5", fileMD5);
		commands.put("fileSize", fileSize);
		commands.put("ping", ping);
		commands.put("strings", strings);
		commands.put("unzip", unzip);
		commands.put("upload", upload);
		commands.put("version", version);
	}
}
