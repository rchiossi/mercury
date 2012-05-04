// License: Refer to the README in the root directory

package com.mwr.mercury;
import java.util.ArrayList;

import android.util.Log;

import com.mwr.mercury.commands.CommandGroup;

class SessionThread extends Thread
{
	Session currentSession;
  
	//Server version info
	String version_info = "Mercury v0.1";
  
	//Assign session variables
	SessionThread(Session session)
	{
		currentSession = session;
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		// TODO Auto-generated method stub
		super.finalize();
		Log.e("mercury", "Closing thread");
	}
  
	//This thread receives commands from the client and handles command
	public void run()
	{
		while (currentSession.connected)
		{
			//Pass off command to be handled
			handleCommand(currentSession.receive());
		}
		Log.e("mercury", "Exiting thread");
	}
  
  
	//Redirect commands to be handled by different functions
	public void handleCommand(String xmlInput)
	{
		//Create an array of commands from xml request received
		ArrayList<RequestWrapper> parsedCommands = new XML(xmlInput).getCommands();
	
		for (RequestWrapper request : parsedCommands) {
			CommandGroup group = CommandGroup.cmdList.get(request.section);
			
			if (group == null) {
				currentSession.sendFullTransmission("", "Command not found on Mercury server");
			} else {
				group.execute(request.function, request.args , currentSession);
			}
		}
  }
  
  
}
