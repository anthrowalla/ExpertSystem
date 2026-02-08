/*
    A basic extension of the java.applet.Applet class
 */

//import symantec.itools.awt.BorderPanel;
import java.awt.*;
import java.applet.*;
import AskMe;
import java.net.*;
import URLData;
import InferCompiler;

public class ExpertZaurus extends Applet implements Runnable {

	public void init() {
		//super.init();

		// Take out this line if you don't use symantec.itools.net.RelativeURL, or symantec.itools.awt.util.StatusScroller
	    // symantec.itools.lang.Context.setApplet(this); 

		//{{INIT_CONTROLS
		setLayout(new BorderLayout(1,1));
		setSize(240,300);
                Font sFont = new Font("Serif", Font.PLAIN, 10);
                Font ssFont = new Font("SansSerif", Font.BOLD, 10);
		borderPanel1 = new java.awt.Panel();
		borderPanel1.setLayout(null);
		borderPanel1.setBounds(0,0,240,280);
		borderPanel1.setBackground(new Color(-3291198));
		add("Center", borderPanel1);
		panel1 = new java.awt.Panel();
		panel1.setLayout(null);
		panel1.setBounds(0,0,240,280);
		panel1.setBackground(new Color(16777215));
                panel1.setFont(sFont);
		borderPanel1.add(panel1);
		//label1 = new java.awt.Label("MicroExpert",Label.CENTER);
		//label1.setBounds(134,-4,269,48);
		//label1.setFont(new Font("Helvetica", Font.PLAIN, 36));
		//panel1.add(label1);
		consultButton = new java.awt.Button();
		consultButton.setLabel("Consult");
		consultButton.setBounds(2,260,50,20);
		consultButton.setBackground(new Color(-4533222));
                consultButton.setFont(ssFont);
		panel1.add(consultButton);
		askMe1 = new AskMe();
		GridBagLayout gridBagLayout;
		gridBagLayout = new GridBagLayout();
		askMe1.setLayout(gridBagLayout);
		askMe1.setVisible(false);
		askMe1.setBounds(1,1,238,100);
		askMe1.setBackground(new Color(-6697729));
		panel1.add(askMe1);
		queryText = new java.awt.TextArea("",14,0,TextArea.SCROLLBARS_VERTICAL_ONLY);
		queryText.setText("Consultation");
		queryText.setVisible(false);
		queryText.setBounds(1,101,238,150);
		queryText.setFont(sFont);
		panel1.add(queryText);
		URLField = new java.awt.TextField();
		URLField.setBounds(130,260,108,20);
		panel1.add(URLField);
		URLList = new java.awt.Choice();
		URLList.addItem("Animal");
		URLList.addItem("Izzat");
		URLList.addItem("Navigate");
		URLList.addItem("vi");
		URLList.addItem("Choice");
                URLList.setFont(ssFont);
		panel1.add(URLList);
		URLList.setBounds(56,260,80,20);
		sourceText = new java.awt.TextArea("",14,0,TextArea.SCROLLBARS_VERTICAL_ONLY);
		sourceText.setBounds(1,1,238,258);
               // sourceText.setOffset(2);
		panel1.add(sourceText);
		//}}
		
		theContext = getDocumentBase();
		ourCompiler = new InferCompiler();
		askMe1.init();
		Vi_URL = new URLData(theContext, "vi.exp");
		sourceText.setText(Vi_URL.theURL.toString());
			//$$ Vi_URL.move(0,0);
		Marriage_URL = new URLData(theContext, "izzat.exp");
			//$$ Marriage_URL.move(0,0);
		Navigate_URL = new URLData(theContext, "gilbert.exp");
			//$$ Navigate_URL.move(0,0);
		Animal_URL = new URLData(theContext, "animal.exp");
		sourceText.setText(loadURL(Animal_URL));
	
		//{{REGISTER_LISTENERS
		SymItem lSymItem = new SymItem();
		URLList.addItemListener(lSymItem);
		SymAction lSymAction = new SymAction();
		consultButton.addActionListener(lSymAction);
		//}}
	}

	public void run() {
		ourThread = new Thread(this);
		ourThread.start();
		for(;;) try {ourThread.sleep(25);} catch (Exception e) {}
	}
	
	 //-------------------------------------------------------------
 // This file has been migrated from the 1.0 to 1.1 event model.
 // This method is not used with the new 1.1 event model. You can 
 // move any code you need to keep, then remove this method.
 //-------------------------------------------------------------
 // public boolean handleEvent(Event event) {
 // 		if (event.target == URLList && event.id == Event.ACTION_EVENT) {
 // 			URLList_Action(event);
 // 			return true;
 // 		}
 // 		if (event.target == consultButton && event.id == Event.ACTION_EVENT) {
 // 			consultButton_Clicked(event);
 // 			return true;
 // 		} else try {
 // 			return super.handleEvent(event);
 // 		} catch (Exception e) {return false;};
 //-------------------------------------------------------------

	
	void runButton_Clicked(Event event) {

		compileIt();

	}

	static boolean inApplet = true;
	
	public static void main(String args[])
	{
		inApplet = false; 
		AppletFrame.startApplet("ExpertZaurus", "ExpertSystem", args);
	}

	void compileIt() {
		ourCompiler = new InferCompiler();
		ourCompiler.setAsker(askMe1);
		ourCompiler.setConsole(queryText);
		ourCompiler.setProg(sourceText);
 
		ourCompiler.doCompile();
		ourCompiler.start();
	}

	void URLList_Action(java.awt.event.ItemEvent event) {
		URLData aURL=null;
		String a = event.getItem().toString();
		if (a.equals("Animal")) {
			aURL = Animal_URL;
		} else if (a.equals("Izzat")) {
			aURL = Marriage_URL;
		} else if (a.equals("Navigate")) {
			aURL = Navigate_URL;
		} else if (a.equals("vi")) {
			aURL = Vi_URL;
		}  else if (a.equals("Your Choice")) {
			aURL = new URLData(theContext, URLField.getText().trim());;
		} 
		if (aURL != null) {
			if (!aURL.done) aURL.start();
			
			while (!aURL.done) {
				try {ourThread.sleep(5);} 
				catch (Exception e) {}
			}
			sourceText.setText(aURL.toString());
		}
	}
        
        public String loadURL(URLData aURL) {
		if (aURL != null) {
			if (!aURL.done) aURL.start();
			
			while (!aURL.done) {
				try {ourThread.sleep(5);} 
				catch (Exception e) {}
			}
			return (aURL.toString());
		}
                else return null;
        }
/*
	public void stop() {
		ourThread.stop();
	}
	
*/
/*	void URLList_Action(Event event) {

			 
		URLField.setText(URLList.getSelectedItem());
	}

*/

	//{{DECLARE_CONTROLS
	java.awt.Panel borderPanel1;
	java.awt.Panel panel1;
	java.awt.Label label1;
	java.awt.Button consultButton;
	AskMe askMe1;
	java.awt.TextArea queryText;
	java.awt.TextField URLField;
	java.awt.Choice URLList;
	java.awt.TextArea sourceText;
	//}}

	Thread ourThread;
	URL theContext;

	InferCompiler ourCompiler=null;
	URLData Vi_URL;
	URLData Marriage_URL;
	URLData Navigate_URL;
	URLData Animal_URL;

	boolean x=true;
			 


	void consultButton_Clicked(java.awt.event.ActionEvent event) {
		
		//{{CONNECTION
//		if (consultButton.getLabel().equals("Consult")) {
		if (x) {
			consultButton.setLabel("Edit");
			compileIt();
			sourceText.hide();
			queryText.show();
			askMe1.show();
			x=false;
	
		} else {
			queryText.hide();
			askMe1.hide();
			sourceText.show();
			consultButton.setLabel("Consult");
			x=true;
		}
		//}}
	}


	class SymItem implements java.awt.event.ItemListener
	{
		public void itemStateChanged(java.awt.event.ItemEvent event)
		{
			Object object = event.getSource();
			if (object == URLList)
				URLList_Action(event);
		}
	}

	class SymAction implements java.awt.event.ActionListener
	{
		public void actionPerformed(java.awt.event.ActionEvent event)
		{
			Object object = event.getSource();
			if (object == consultButton)
				consultButton_Clicked(event);
		}
	}

	static java.net.URL documentBase=null;
	static java.net.URL codeBase=null;

	private static void initializeApp()
	{
        StringBuffer p = new StringBuffer(System.getProperty("user.dir"));
        int         pl = p.length();

        // If the system file separator isn't the URL file separator convert it.
        try
        {
            char ps = (System.getProperty("file.separator")).charAt(0);
            if(ps != '/')
                for(int counter = 0; counter < pl; counter++)
                {
                    if(ps == p.charAt(counter)) p.setCharAt(counter, '/');
                }
        } catch(StringIndexOutOfBoundsException e) {}

        try {
            documentBase = new URL("file:///" + p + "/");
        } catch (java.net.MalformedURLException e) {
        }
	}
	
	public URL getDocumentBase() {
		if (inApplet) return (documentBase = super.getDocumentBase());
		else try {
			if (documentBase == null) initializeApp();
			return documentBase;
		} catch (Exception e) {return null;}
	}
	
	public URL getCodeBase() {
		if (inApplet) return (codeBase = super.getCodeBase());
		else try {
			if (documentBase == null) initializeApp();
			return (codeBase = documentBase);
		} catch (Exception e) {return null;}
	}

	public Image getImage(URL u, String x)
        // throws AWTException
    {
    	if (inApplet)
    		return super.getImage(u,x);
    	// Remove old images
	    else {
	    	URL url=null;
	    	try {
	    	 url = new URL( u,x);
	    	}  catch (java.net.MalformedURLException e) {
	        } 
	        Image image = getToolkit().getImage(url);
	        if (image != null)
	        {
		        MediaTracker mt = new MediaTracker(this);
				if (mt != null)
				{
			        try
			        {
			            mt.addImage(image, 0);
			            mt.waitForAll();
			        }
			        catch (InterruptedException ie)
			        {
			        }
					
			        if (mt.isErrorAny())
			        {
			            System.err.println("Error loading image " + image.toString());
			            return null;
			        }
					
			       return image;
			       
			
			        //resize(image.getWidth(this) + bevel * 3 + 2, image.getHeight(this) + bevel * 3 + 2);
				}
	        }
	        return null;
		}
    }


}


