import java.awt.Event;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.lang.Thread;
// import symantec.itools.net.RelativeURL;
public class URLData // extends Thread
{
	//insert class definition here

	public URL theURL=null;
	BufferedInputStream theStream=null;
	StringBuffer buf=null;
	public boolean done=false;
	
	URLData(URL context, String url) {
		try {
			// RelativeURL a = 
			String relativeURL = baseContext(context)+url;
			System.out.println(relativeURL);
			// theURL =  new URL(context,url);
			theURL =  new URL(relativeURL);
		} catch (java.net.MalformedURLException e) {
			theURL = null;
                        System.out.println("Malformed URL "+url);
		}
	}
	
	public String toString() {
		if (buf != null)
			return buf.toString();
		else
			return "Empty\n\nOK";
	}

	public void openURL() {
		if (theURL != null) {
			try {
				InputStream a = theURL.openStream();
				theStream = new BufferedInputStream(a);
				
			} catch (java.io.IOException e) {
				theStream = null;
				System.out.println("URL Stream is NULL!"+theURL);
			}
		}
	}
	
	public int readByte() {
		if (theStream != null) {
			try {
				return theStream.read();
			} catch (java.io.IOException e) {return -1;}
		}
		return -1;
	}
	
	/*public void run() {
		activate();
		//deactivate();
		stop();
	}*/
	
	/*public void doexit() {
		stop();
		// deactivate();
	}*/
	
	public void readAll() {
		if (theStream != null) {
			buf = new StringBuffer(4000);
			byte[] cbuf = new byte[4000];
			int hm;
			do {
				try {
					hm=theStream.read(cbuf,0,4000);
					if (hm >0) buf.append(new String(cbuf,0,hm));
				//	try {sleep(5);} catch (InterruptedException e) {}
				} catch (java.io.IOException e) {break;}
			} while (hm > 0);
			
		} else buf = null;
		// System.out.println(buf.toString());
	}
	
	public void xdeactivate() {
		try {theStream.close();} catch (Exception e) {}
	 	theStream = null;
	 //	buf = null;
	 //	done = false;
	}
	
	public void activate() {
		//if (done == true) return;
		openURL();
		readAll();
		if (theStream != null) {
			try {
				theStream.close();
			} catch (java.io.IOException e) {}
		}
		done = true;
	}
	
	String baseContext(URL context) {
		String p = context.toString();
		// Normalize file:/ URLs to file:/// form
		if (p.startsWith("file:///")) {
			// already correct, do nothing
		} else if (p.startsWith("file:/")) {
			p = "file:///" + p.substring(6);
		}
		int k = p.lastIndexOf('/'); // change to system separater!

		return(p.substring(0,k+1));

	}
}