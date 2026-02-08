

import java.io.*;
import java.awt.Frame;
import java.awt.FileDialog;
import java.lang.*;

 public class XFile extends Object{
    	public File aFile;
    	DataInputStream diStream;
    	DataOutputStream doStream;
    	
    	String errMessage;
    	
    	public int Delimiter = ',';
    	
    	int xMode=-1; // 0 = read 1 = write
    	int NFields=0;
    	int[] theFields;
    	boolean isOpen=false;
    	
    	XFile() {
    		xMode = -1;
    	}
    	
    	XFile(File someFile) {
    		aFile = someFile;
    		xMode = -1;
		}	
    	
    	
    	public boolean Open() {
    		if (xMode == -1) {
    			errMessage = "Must set mode using setMode(read=0,write=1)";
    			return false;
    		}
    		
    		return(Open(xMode));
    	}
    	
    	public boolean Open(int omode) {
    		
    		xMode = omode;
    		if (xMode == 0) {
    			FileInputStream theStream;
    		
	    	  if (aFile.canRead()) {
					try {
						theStream = new FileInputStream(aFile);
					} catch (Exception e) {
						errMessage = "File is not Available";
						return(false);
			 		}
					try {
			     		diStream = new DataInputStream(theStream);
			  		} catch (Exception e) {
						errMessage = "Stream is not Available";
						return(false);
			 		}
			 		isOpen = true;
			 		return true;
			 	}
			 	return false;
		 	} else {
		 		FileOutputStream theStream;
    		
	    	  //
					try {
						if (aFile.exists())
							theStream = new FileOutputStream(aFile);
						else 
							theStream = new FileOutputStream(aFile.getPath());
					} catch (Exception e) {
						errMessage = "Write File is not Available";
						return(false);
			 		}
			 		// if (aFile.canWrite()) {
					try {
			     		doStream = new DataOutputStream(theStream);
			  		} catch (Exception e) {
						errMessage = "Stream is not Available";
						return(false);
			 		}
			 		isOpen = true;
			 		return true;
			 //	}
		 		//return false;
		 	}
	 	}

/*
	Countfields takes a string and counts how many instances of Delimiter occur in the string
	plus one.	
*/

		public int CountFields(String aLine) {
	 		int p,q=0;
	 		int tokenIndex;
	 		tokenIndex = 0;
			do {
 				p = aLine.indexOf(Delimiter,tokenIndex);
 				q++;
				if (p <= 0) {
 					return(q);
 				} else {
 					tokenIndex = p+2;
 				}
 			} while (tokenIndex <= aLine.length());
 			return(q);
 		}


/*
	BreakFields calls Countfields if necessary to initialize the theFields array, which is
	an int array of starting points for substrings in the String aLine. Breakfields then
	fills in this array for subsequent lines.
	
	At the moment it assumes all lines have the same number of fields as the first one
	it is intialized with. Beware blank initial lines as well...and blank lines in general
	I guess!!!
*/
	 	public int BreakFields(String aLine) {
	 		int p,q=-1;
	 		int tokenIndex;
			int[] xxfld;
			
	 		if (NFields == 0) {
	 			NFields = CountFields(aLine);
	 			xxfld = theFields = new int[NFields+1];
	 			theFields[0] = NFields;
	 		}
 			tokenIndex = 0;
			 		
			do {
 				p = aLine.indexOf(Delimiter,tokenIndex);
 				q++;
 				if (q < NFields) {
					if (p <= 0) {
						theFields[q]=tokenIndex;
						theFields[q+1] = aLine.length()+2;
	 					return(q+1);
	 				} else {
	 					theFields[q] = tokenIndex;
	 					tokenIndex = p+2;
	 				}
 				} else {
 					q--; // get back to last legal value
 					theFields[q+1] = aLine.length()+2; // skip to end of line
 					break; // get out of here
 				}
 			} while (tokenIndex <= aLine.length());
 			return(q+1);
 		}

/*	ReadFormat expects a reference to an array of field numbers. Elements contains the 
	appropriate field number. They need not be in the array in the order they  appear in the 
	file. ReadFormat returns a String array with  each element with a string representing the
	corresponding format[i].
	
	If format[0] is -1 or format is null then all fields are returned in a string array. 
	
*/
 	public String[] ReadFormat(int format[]) {
	 		int i,nf;
	 		String aLine = this.ReadLineNN();
	 		if (aLine == null) {
	 			errMessage = "EOF";
	 			return null;
	 		} else {
	 			nf = BreakFields(aLine);
	 			if (format == null || format[0] == -1) {
	 				String[] retStrs = new String[nf];
					for(i=0;i<nf;i++) {
		 				retStrs[i] = aLine.substring(theFields[i],theFields[i+1]-2);
		 			}
		 			return retStrs;
	 			} else {
	 				String[] retStrs = new String[format.length];
					for(i=0;i<format.length;i++) {
		 				if (format[i] < nf) 
		 					retStrs[i] = aLine.substring(theFields[format[i]],theFields[format[i]+1]-2);
		 				else {
		 					errMessage = "Variable out of bounds";
		 					retStrs[i] = "";
		 				}
		 			}
		 			return retStrs;
		 		}
	 		}
	 	}
	 	
	 	public String ReadLine() {
	 		if (diStream != null) {
	   			try {
	   				return(diStream.readLine());
	   			} catch (Exception e) {
	 				errMessage = "Probably at End of File";
					return(null);
				}
			} else {
		 		errMessage = "Stream not open";
				return(null);
		
			}
   		}
   	
  	 	public String ReadLineNN() {
  	 		String aLine;
  	 		do {
  	 			aLine = this.ReadLine();
  	 			if (aLine != null) {
  	 				if (!aLine.equals("")) break;
  	 			}
  	 		} while (aLine != null);
  	 		return(aLine);
  	 	}
   	
   		public int CountLines(boolean doEmpty) {
   			boolean wasOpen = isOpen;
   			int count=0;
   			String x;
   			
   			if (!wasOpen) {
   				if (!Open()) return -1;
   			}
   			if (doEmpty) {
	   			do {
	   				x = ReadLine();
	   				count++;
	   			} while (x != null);
	   		} else {
		   			do {
	   				x = ReadLineNN();
	   				count++;
	   			} while (x != null);
     		}
   			count --;
   			if (!wasOpen) Close();
   			return(count);
   		}
   	
   		public boolean isOpen() {
   			return isOpen;
   		}
   		
   		public boolean Choose(int omode) {
   			xMode = omode;
   			String aMessage;
   			if (xMode == -1) return false;
	   		Frame aFrame = new Frame();
	   		if (xMode == 1) aMessage = "Write to file ...";
	   		else aMessage = "Read from file ...";
	    	// FileDialog aFD = new FileDialog(aFrame,aMessage,omode); // should be this but a bug in read mode
                        FileDialog aFD = new FileDialog(aFrame,aMessage,xMode); // bug workaround ... user must type in
                        aFD.setFile(".log");
                        try { 
                                aFD.show(); 
                                
                                aFile = new File(aFD.getDirectory().substring(0,aFD.getDirectory().length()-1),aFD.getFile());
                        }
	  		catch (Exception e) {errMessage="Cancelled or error";return false;}
	  		return true;
		}
		
   		public boolean Close() {
	 		isOpen = false;
	 			if (xMode == 0) {
	    		try {
	     			diStream.close();
	     			diStream = null;
	     		} catch (Exception e) {
	     			errMessage = "Can not Close Read Stream";
	     			return false;
	     		}
	     	} else {
		    		try {
	     			doStream.close();
	     			doStream = null;
	     		} catch (Exception e) {
	     			errMessage = "Can not Close Write Stream";
	     			return false;
	     		}
	     	}
      		return true;
		}
		
		public boolean WriteLine(String line) {
			try {
			doStream.writeChars(line);
			WriteByte('\r'); // fix for UNIX PC via system property!!!!!!
			return true;
			} catch (Exception e) {
				errMessage = "Can not Write Line";
				return false;}
		}

		public boolean WriteString(String line) {
			try {
			doStream.writeChars(line);
			return true;
			} catch (Exception e) {
				errMessage = "Can not Write Line";
				return false;}
		}

		public boolean WriteByte(int bite) {
		try {
				doStream.writeByte(bite);
			return true;
			} catch (Exception e) {errMessage = "Can not Write Byte";
			return false;}
		}
   	public boolean WriteNumber(Number num) {
		try {
				doStream.writeChars(num.toString());
				return true;
			} catch (Exception e) {errMessage = "Can not Write Number";
				return false;}
	}
 
    
}
