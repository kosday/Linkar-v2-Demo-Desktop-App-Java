package bl;
import java.util.ArrayList;
import java.util.Arrays;

import forms.LinkarDemo;
import linkar.ASCII_Chars;
import linkar.DBMV_Mark;
//import linkar.GenericError;
import linkar.LinkarClt;
import linkar.LinkarClt.DATAFORMATCRU_TYPE;
import linkar.LkException;
import linkar.SelectOptions;

public class CLkOrders extends ArrayList<CLkOrder> {
	
	//The copy of the client for operations
	private LinkarClt _LinkarClt = null;
	
	public LinkarClt getLinkarClt() {
		return _LinkarClt;
	}
	
	//When use pagination this property have count of total items
	public double totalRecords = 0.0;
	
	public ArrayList<String> LstErrors;

	//Build the object with a copy of client
    public CLkOrders(LinkarClt linkarClt)
    {
    	_LinkarClt = linkarClt;
    }
    
    // -- Customers SELECTS
    
    // Custom select operation
	public Exception FindAll(String sortClause, String var1, int numRegPag, int numPag, String fileName, String usingDict, boolean onlyIds, boolean loadITypes, boolean pagination)
	{
		if (fileName == null || fileName == "")
			fileName = CLkOrder.FILE_CLkOrder;
		
		if (sortClause == null || sortClause == "")
			sortClause = "BY CODE";
		String selectClause = "";
		selectClause = "WITH CODE = \"" + var1 + "\"";
		String dictionariesClause = "";
		dictionariesClause = "";
		String preSelectClause = "";
		return this.SelectGeneric(selectClause, sortClause, preSelectClause, loadITypes, fileName, dictionariesClause, numRegPag, numPag, usingDict, onlyIds, pagination);	
	}

	// Select operation with all options
    public Exception SelectGeneric(String selectClause, String sortClause, String preSelectClause, boolean calculated,
                                     String fileName,
                                     String dictionariesClause, int numRegPag, int numPag, String usingDict, boolean onlyIds, boolean pagination)
    {
    	Exception error = null;
    	try
    	{
    	//Fill the Select options
    	if (fileName == null || fileName == "")
    		fileName = CLkOrder.FILE_CLkOrder;    	  
    	
        boolean conversion = false;
        boolean formatSpec = false;
        boolean originalRecords = false;
        boolean onlyRecordId = onlyIds;
        //Call to sincronous session version of Select function
        SelectOptions selectOptions = new SelectOptions(onlyRecordId, pagination, numRegPag, numPag, calculated, conversion, formatSpec, originalRecords);
    		
    	String lkstring = _LinkarClt.Select_Text(fileName, selectClause, sortClause, dictionariesClause, preSelectClause, selectOptions, DATAFORMATCRU_TYPE.MV, "", 0);
    	
    		this.clear();
    		
    		if (lkstring != null && !lkstring.isEmpty())
    		{
		    	char delimiter = ASCII_Chars.FS_chr;
		    	char delimiterThisList = DBMV_Mark.AM;		    	
		    	String recordIds = "";
		    	String records = "";
		    	String recordcalculateds = "";
		    	String[] parts = lkstring.split(String.valueOf(delimiter), -1);
		        if (parts.length >= 1)
		        {
		        	String[] ThisList = parts[0].split(String.valueOf(delimiterThisList), -1);
		            int numElements = ThisList.length;
		            for (int i = 1; i < numElements; i++)
		            {             
		            	if (ThisList[i].equals("TOTAL_RECORDS"))
		            	{
		            		totalRecords = (parts[i].isEmpty() ?  0.0 : Double.valueOf(parts[i]));
		            	}
		            	if (ThisList[i].equals("RECORD_ID"))
		            	{
		            		recordIds = parts[i];
		            	}
		            	if (ThisList[i].equals("RECORD"))
		            	{
		            		records = parts[i];
		            	}
		            	if (ThisList[i].equals("CALCULATED"))
		            	{
		            		recordcalculateds = parts[i];
		            	}
		            	if (ThisList[i].equals("ERRORS"))
		            	{
		            		if (parts[i] != null && parts[i].length() > 0)
		            			this.LstErrors = new ArrayList<>(Arrays.asList(parts[i].split(DBMV_Mark.AM_str, -1)));
		            		else
		            			this.LstErrors = new ArrayList<String>();
		            	}
		            }
		        } 	        
	
		        //Fill all the items with response data
	            String[] lstids = new String[0];
	            if (recordIds != null && recordIds.length() > 0) lstids = recordIds.split(ASCII_Chars.RS_str,-1);
	            String[] lstregs = new String[0];
	            if (records != null && records.length() > 0) lstregs = records.split(ASCII_Chars.RS_str,-1);
	            String[] lstcalcs = new String[0];
	            if (recordcalculateds != null && recordcalculateds.length() > 0) lstcalcs = recordcalculateds.split(ASCII_Chars.RS_str,-1);
	                       
	            for (int i = 0; i < lstids.length; i++)
	           {
	                CLkOrder item = new CLkOrder(_LinkarClt);
	
	                if (records != null && !records.isEmpty())
	                	item.setItemOriginalContent(lstregs[i]);
	                if (recordcalculateds != null && recordcalculateds.length() > 0)
	                {
	                	item.setItemOriginalContentItypes(lstcalcs[i]);
	                	item.GetRecord(lstids[i], records != null && !records.isEmpty()? lstregs[i] : "", lstcalcs[i]);
	                }
	                else
	            		item.GetRecord(lstids[i], records != null && !records.isEmpty() ? lstregs[i] : "", "");	  
	                
		            if (this.LstErrors != null && this.LstErrors.size() > 0)
	                {
	                	for (int j = 0; j < this.LstErrors.size(); j++)
	                	{
	                		if (this.LstErrors.get(i) != null && this.LstErrors.get(i).length() > 0)
	                		{
		                		String [] errorParts = this.LstErrors.get(i).split(DBMV_Mark.VM_str, -1);
		                		if (errorParts.length > 2 && errorParts[2]  == item.getCode())
		                			item.LstErrors.add(this.LstErrors.get(i));
	                		}
	                	}
	                }
	
	                item.setStatus(CLkOrder.StatusTypes.READED);
	                this.add(item);
	            }
    		}

    	} catch (LkException e) {
    		error = e;
    	} catch (Exception e) {
    		error = e;
    	}
    	return error;
    }

    // Select all items operation
    public Exception SelectAll(String sortClause, String fileName, int numRegPag, int numPag, boolean onlyIds, boolean loadITypes, boolean pagination)
    {
    	if (fileName == null || fileName == "")
    		fileName = CLkOrder.FILE_CLkOrder;
    	
    	if (sortClause == null || sortClause == "")
            sortClause = "BY CODE";
    	
        return this.SelectGeneric("", sortClause, "", loadITypes, fileName, "", numRegPag, numPag, "", onlyIds, pagination);
    }

}

