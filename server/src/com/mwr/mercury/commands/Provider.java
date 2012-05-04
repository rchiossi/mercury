package com.mwr.mercury.commands;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.PathPermission;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.PatternMatcher;
import android.util.Base64;

import com.mwr.mercury.Common;
import com.mwr.mercury.Session;

public class Provider extends CommandGroup
{
	private Command columns = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			//Get list of columns
			ArrayList<String> columns = Common.getColumns(currentSession.applicationContext.getContentResolver(), args.get("uri"), null);
			
			//If there are no columns, then the URI is invalid
			if (columns.size() == 0)
				currentSession.sendFullTransmission("", "Invalid content URI specified");
			else
			{
				String columnsStr = "";
				
				//Iterate through columns
				for (int i = 0; i < columns.size(); i++)
				{
					if (i != columns.size() - 1)
						columnsStr += columns.get(i) + " | ";
					else
						columnsStr += columns.get(i);
				}
    	
				currentSession.sendFullTransmission(columnsStr, "");
			}
		}
	};

	private Command delete = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			try
			{
				//TODO rchiossi - fix string list
				List<String> selectionArgs = new ArrayList<String>(); //Common.getParamStringList(argsArray, "selectionArgs");
		        String where = args.get("where");

		        //Put selectionArgs in an array
				String[] selectionArgsArray = null;
				if (selectionArgs.size() > 0)
				{
					selectionArgsArray = new String[selectionArgs.size()];
					Iterator<String> it = selectionArgs.iterator();
					
					int i = 0;
					
					while (it.hasNext())
					{
						selectionArgsArray[i] = it.next();
						i++;
					}
				}
		        
		        //Get content resolver
		        ContentResolver r = currentSession.applicationContext.getContentResolver();

		        //Issue delete command
		        int rowsDeleted = r.delete(Uri.parse(args.get("Uri")), (where.length() > 0)? where : null, selectionArgsArray);
		       
		        //Send response
		        currentSession.sendFullTransmission(Integer.toString(rowsDeleted) + " rows have been deleted", "");
	        
			}
			catch (Throwable t) 
			{
				currentSession.sendFullTransmission("", t.getMessage());
			}
			
			currentSession.endTransmission();

		}
	};

	private Command info = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			//Assign filter and permissions if they came in the arguments	
			String filter = args.get("filter");
			String permissions = args.get("permissions");
			
			currentSession.startTransmission();
			currentSession.startResponse();
			currentSession.startData();
			
			//Get all providers and iterate through them
			List<ProviderInfo> providers = currentSession.applicationContext.getPackageManager().queryContentProviders(null, PackageManager.GET_URI_PERMISSION_PATTERNS, PackageManager.GET_URI_PERMISSION_PATTERNS);
			
			//Iterate through content providers
			for (int i = 0; i < providers.size(); i++)
			{
				//Get all relevant info from content provider
				String providerAuthority = providers.get(i).authority;
				String providerPackage = providers.get(i).packageName;
				String providerReadPermission = providers.get(i).readPermission;
				PatternMatcher[] uriPermissionPatterns = providers.get(i).uriPermissionPatterns;
				String providerWritePermission = providers.get(i).writePermission;
				PathPermission[] providerPathPermissions = providers.get(i).pathPermissions;
				boolean providerMultiprocess = providers.get(i).multiprocess;
				boolean grantUriPermissions = providers.get(i).grantUriPermissions;
				
				String pathpermissions = "";
				
				//Path permissions
				if (providerPathPermissions != null)
					for (int j = 0; j < providerPathPermissions.length; j++)
					{
						if (providerPathPermissions[j].getReadPermission() != null)
							pathpermissions += "Path Permission - Read: " + providerPathPermissions[j].getPath() + " needs " + providerPathPermissions[j].getReadPermission() + "\n";

						if (providerPathPermissions[j].getWritePermission() != null)
							pathpermissions += "Path Permission - Write: " + providerPathPermissions[j].getPath() + " needs " + providerPathPermissions[j].getWritePermission() + "\n";
					}
				
				String uriPermissions = "";
				
				//URI Permission Patterns
				if (uriPermissionPatterns != null)
					for (int k = 0; k < uriPermissionPatterns.length; k++)
					{
						if (uriPermissionPatterns[k].getPath() != null)
							uriPermissions += "URI Permission Pattern: " + uriPermissionPatterns[k].getPath() + "\n";
						
					}

				boolean relevantFilter = false;
				boolean relevantPermissions = false;
				boolean noFilters = false;
				boolean bothFiltersRelevant = false;
				
				//Check if a filter was used
				if (filter.length() > 0)
					relevantFilter = providerAuthority.toUpperCase().contains(filter.toUpperCase()) || providerPackage.toUpperCase().contains(filter.toUpperCase());
				
				//Check if a permission filter was used
				try
				{
					if (permissions.length() > 0)
					{
						if (permissions.toUpperCase().equals("NULL"))
							relevantPermissions = (providerReadPermission == null) || (providerWritePermission == null);
						else
							relevantPermissions = providerReadPermission.contains(permissions) || providerWritePermission.contains(permissions);
					}
				} catch (Throwable t) {}
				
				//Check if no parameters were given
				if (filter.length() == 0 && permissions.length() == 0)
					noFilters = true;
				
				boolean bothFiltersPresent = false;
				if ((filter != "") && (permissions != ""))
					bothFiltersPresent = true;
				
				if (bothFiltersPresent && relevantFilter && relevantPermissions)
					bothFiltersRelevant = true;
				
				//Apply filter and only look @ exported providers
				if (((bothFiltersPresent && bothFiltersRelevant) || (!bothFiltersPresent && (relevantFilter || relevantPermissions)) || (!bothFiltersPresent && noFilters)) && providers.get(i).exported)
				{
					currentSession.send("Package name: " + providerPackage + "\n", true);
					currentSession.send("Authority: " + providerAuthority + "\n", true);
					currentSession.send("Required Permission - Read: " + providerReadPermission + "\n", true);
					currentSession.send("Required Permission - Write: " + providerWritePermission + "\n", true);
					currentSession.send((pathpermissions.length() > 0)? pathpermissions: "", true);
					currentSession.send((uriPermissions.length() > 0)? uriPermissions: "", true);
					currentSession.send("Grant Uri Permissions: " + grantUriPermissions + "\n", true);
					currentSession.send("Multiprocess allowed: " + providerMultiprocess + "\n\n", true);
				}

			}
			
			currentSession.endData();
			currentSession.noError();
			currentSession.endResponse();
			currentSession.endTransmission();		

		}
	};

	private Command insert = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			try
			{					        
		        ContentValues contentvalues = new ContentValues();
		        
		        //TODO rchiossi - fix string list
		        
		        //Place values into contentvalue structure
		        List<String> strings = new ArrayList<String>(); //Common.getParamStringList(argsArray, "string");
		        if (strings != null)
			        contentvalues.putAll(Common.listToContentValues(strings, "string"));
		        
		        List<String> booleans = new ArrayList<String>(); //Common.getParamStringList(argsArray, "boolean");
		        if (booleans != null)
			        contentvalues.putAll(Common.listToContentValues(booleans, "boolean"));
		        
		        List<String> integers = new ArrayList<String>(); //Common.getParamStringList(argsArray, "integer");
		        if (integers != null)
			        contentvalues.putAll(Common.listToContentValues(integers, "integer"));
		        
		        List<String> doubles = new ArrayList<String>(); //Common.getParamStringList(argsArray, "double");
		        if (doubles != null)
			        contentvalues.putAll(Common.listToContentValues(doubles, "double"));
		        
		        List<String> floats = new ArrayList<String>(); //Common.getParamStringList(argsArray, "float");
		        if (floats != null)
			        contentvalues.putAll(Common.listToContentValues(floats, "float"));
		        
		        List<String> longs = new ArrayList<String>(); //Common.getParamStringList(argsArray, "long");
		        if (longs != null)
			        contentvalues.putAll(Common.listToContentValues(longs, "long"));
		        
		        List<String> shorts = new ArrayList<String>(); //Common.getParamStringList(argsArray, "short");
		        if (shorts != null)
			        contentvalues.putAll(Common.listToContentValues(shorts, "short"));

		        //Get content resolver
		        ContentResolver r = currentSession.applicationContext.getContentResolver();
		        
		        //Issue insert command
		        Uri c = r.insert(Uri.parse(new String(args.get("Uri"))), contentvalues);
		       
		        currentSession.sendFullTransmission(c.toString(), "");
	        
			}
			catch (Throwable t) 
			{
				currentSession.sendFullTransmission("", t.getMessage());
			}

		}
	};

	private Command query = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			currentSession.startTransmission();
			currentSession.startResponse();
			currentSession.startData();
			
			try
			{				
				//Get content provider and cursor
		        ContentResolver r = currentSession.applicationContext.getContentResolver();
		        
		        //Get all the parameters
		        //TODO rchiossi - fix ArrayList
		        List<String> projection = new ArrayList<String>(); //Common.getParamStringList(argsArray, "projection");
		        String selection = args.get("selection");
		        List<String> selectionArgs = new ArrayList<String>(); //>Common.getParamStringList(argsArray, "selectionArgs");
		        String sortOrder = args.get("sortOrder");
		        String showColumns = args.get("showColumns");
		        
		        //Put projection in an array
		        String[] projectionArray = null;
		        if (projection.size() > 0)
		        {
		        	projectionArray = new String[projection.size()];
		        	Iterator<String> it = projection.iterator();
		        	
		        	int i = 0;
		        	
		        	while (it.hasNext())
		        	{
		        		projectionArray[i] = it.next();
		        		i++;
		        	}
		        }
		        
		        //Put selectionArgs in an array
				String[] selectionArgsArray = null;
				if (selectionArgs.size() > 0)
				{
					selectionArgsArray = new String[selectionArgs.size()];
					Iterator<String> it = selectionArgs.iterator();
					
					int i = 0;
					
					while (it.hasNext())
					{
						selectionArgsArray[i] = it.next();
						i++;
					}
				}
		        
		        //Issue query
		        Cursor c = r.query(Uri.parse(args.get("Uri")), projectionArray, (selection.length() > 0)? selection : null, selectionArgsArray, (sortOrder.length() > 0)? sortOrder : null);
		        
		        //Check if query failed
		        if (c != null)
		        {
			        //Display the columns
			        if (showColumns.length() == 0 || showColumns.toUpperCase().contains("TRUE"))
			        {
			        	ArrayList<String> cols = Common.getColumns(r, args.get("Uri"), projectionArray);
			        	Iterator<String> it = cols.iterator();
			        	String columns = "";
			        	
			        	while (it.hasNext())
			        		columns += it.next() + " | ";
			 
			        	currentSession.send(columns.substring(0, columns.length()-3), true);
			        	currentSession.send("\n.....\n\n", true);
			        }
			        	
		        	//Get all rows of data
		        	for (c.moveToFirst();!c.isAfterLast();c.moveToNext())
		        	{	
		        		int numOfColumns = c.getColumnCount();
		        		String data = "";
		        		
		        		//Iterate through columns
		        		for (int l = 0; l < numOfColumns; l++)
		        		{
		        			
		        			//Get string - if there is an error try retrieve as a blob
		        			try
		        			{
		        				data += c.getString(l);
		        			}
		        			catch (Exception e)
		        			{
		        				//Base64 encode blobs and prepend with (blob)
		        				data += "(blob) " + Base64.encodeToString(c.getBlob(l), Base64.DEFAULT);
		        			}
		        			
		        			//Check if a column separator should be added or not
		        			if (l != (numOfColumns - 1))
		        				data += " | ";
		        		}

		        		currentSession.send(data + "\n\n", true);
		        	}
		        	
		        	currentSession.endData();
		        	currentSession.noError();
		        }
		        else
		        {
		        	currentSession.endData();
					currentSession.error("Query failed");
		        }
	        
			}
			catch (Throwable t) 
			{
				currentSession.endData();
				currentSession.error(t.getMessage());
			}
			
			currentSession.endResponse();
			currentSession.endTransmission();
		}
	};

	private Command read = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			//Start transmission
        	currentSession.startTransmission();
        	currentSession.startResponse();
        	currentSession.startData();
        	
            try
            {
                Uri uri = Uri.parse(args.get("Uri"));
                ContentResolver r = currentSession.applicationContext.getContentResolver();
                InputStream is = r.openInputStream(uri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = -1;
                do
                {
                    byte[] buf = new byte[1024];
                    len = is.read(buf);
                    if (len > 0)
                    	baos.write(buf, 0, len);
                    
                } while(len != -1);
                
                byte[] buf = baos.toByteArray();
                String b64 = Base64.encodeToString(buf, 0);
                
                //Send response
                currentSession.send(b64, false);
                currentSession.endData();
            	currentSession.noError();
            }
            catch (Throwable t)
            {
            	currentSession.endData();
            	currentSession.error(t.getMessage());
            }
        	finally
        	{
        		currentSession.endResponse();
        		currentSession.endTransmission();
        	}

		}
	};

	private Command update = new Command()
	{

		@Override
		public void execute(HashMap<String, String> args, Session currentSession)
		{
			try
			{					        
		        ContentValues contentvalues = new ContentValues();
		        
		        //TODO rchiossi - fix string list
		        //Place values into contentvalue structure
		        List<String> strings = new ArrayList<String>(); //Common.getParamStringList(argsArray, "string");
		        if (strings != null)
			        contentvalues.putAll(Common.listToContentValues(strings, "string"));
		        
		        List<String> booleans = new ArrayList<String>(); //Common.getParamStringList(argsArray, "boolean");
		        if (booleans != null)
			        contentvalues.putAll(Common.listToContentValues(booleans, "boolean"));
		        
		        List<String> integers = new ArrayList<String>(); //Common.getParamStringList(argsArray, "integer");
		        if (integers != null)
			        contentvalues.putAll(Common.listToContentValues(integers, "integer"));
		        
		        List<String> doubles = new ArrayList<String>(); //Common.getParamStringList(argsArray, "double");
		        if (doubles != null)
			        contentvalues.putAll(Common.listToContentValues(doubles, "double"));
		        
		        List<String> floats = new ArrayList<String>(); //Common.getParamStringList(argsArray, "float");
		        if (floats != null)
			        contentvalues.putAll(Common.listToContentValues(floats, "float"));
		        
		        List<String> longs = new ArrayList<String>(); //Common.getParamStringList(argsArray, "long");
		        if (longs != null)
			        contentvalues.putAll(Common.listToContentValues(longs, "long"));
		        
		        List<String> shorts = new ArrayList<String>(); //Common.getParamStringList(argsArray, "short");
		        if (shorts != null)
			        contentvalues.putAll(Common.listToContentValues(shorts, "short"));

		        List<String> selectionArgs = new ArrayList<String>(); //Common.getParamStringList(argsArray, "selectionArgs");
		        String where = args.get("where");

		        //Put selectionArgs in an array
				String[] selectionArgsArray = null;
				if (selectionArgs.size() > 0)
				{
					selectionArgsArray = new String[selectionArgs.size()];
					Iterator<String> it = selectionArgs.iterator();
					
					int i = 0;
					
					while (it.hasNext())
					{
						selectionArgsArray[i] = it.next();
						i++;
					}
				}
		        
		        //Get content resolver
		        ContentResolver r = currentSession.applicationContext.getContentResolver();
		        
		        //Issue update command
		        Integer c = r.update(Uri.parse(args.get("Uri")), contentvalues, (where.length() > 0)? where : null, selectionArgsArray);
	        
		        //Send response
		        currentSession.sendFullTransmission(c.toString() + " rows have been updated.", "");
		        
			}
			catch (Throwable t) 
			{
				currentSession.sendFullTransmission("", t.getMessage());
			}

		}
	};

	public Provider()
	{
		commands = new HashMap<String, Command>();

		commands.put("columns", columns);
		commands.put("delete", delete);
		commands.put("info", info);
		commands.put("insert", insert);
		commands.put("query", query);
		commands.put("read", read);
		commands.put("update", update);
	}
}
