/*
    A basic extension of the java.applet.Applet class
 */

//import symantec.itools.awt.BorderPanel;
import java.awt.*;
import java.applet.*;
import java.net.*;



public class ExpertSystem extends Applet {

	public void init() {

	   setLayout(new BorderLayout(5,5));
		setSize(700,701);
		setBackground(new Color(16777215));

		// Top: title label
		label1 = new java.awt.Label("MicroExpert",Label.CENTER);
		label1.setFont(new Font("Helvetica", Font.PLAIN, 36));
		add(label1, BorderLayout.NORTH);

		// Center panel holds askMe + text area cards, stacked vertically
		Panel centerPanel = new Panel();
		centerPanel.setLayout(new BorderLayout(5,5));

		// AskMe panel at top of center (visible during consultation)
		askMe1 = new AskMe();
		GridBagLayout gridBagLayout;
		gridBagLayout = new GridBagLayout();
		askMe1.setLayout(gridBagLayout);
		askMe1.setVisible(false);
		askMe1.setBackground(new Color(-6697729));
		centerPanel.add(askMe1, BorderLayout.NORTH);

		// Card panel: sourceText and queryText share the same space
		textCards = new CardLayout();
		textCardPanel = new Panel(textCards) {
			public Dimension getMinimumSize() {
				return new Dimension(700, 300);
			}
			public Dimension getPreferredSize() {
				int w = Math.max(700, getParent() != null ? getParent().getWidth() : 700);
				return new Dimension(w, 500);
			}
		};

		sourceText = new java.awt.TextArea();
		sourceText.setFont(new Font("Monospaced", Font.PLAIN, 24));
		textCardPanel.add(sourceText, "source");

		queryText = new java.awt.TextArea(14,0);
		queryText.setText("Consultation");
		queryText.setFont(new Font("Times", Font.PLAIN, 28));
		textCardPanel.add(queryText, "query");

		textCards.show(textCardPanel, "source");

		// 20px left/right margins around text areas
		Panel textMarginPanel = new Panel(new BorderLayout());
		Panel leftMargin = new Panel();
		leftMargin.setPreferredSize(new Dimension(20, 0));
		Panel rightMargin = new Panel();
		rightMargin.setPreferredSize(new Dimension(20, 0));
		textMarginPanel.add(leftMargin, BorderLayout.WEST);
		textMarginPanel.add(textCardPanel, BorderLayout.CENTER);
		textMarginPanel.add(rightMargin, BorderLayout.EAST);
		centerPanel.add(textMarginPanel, BorderLayout.CENTER);

		add(centerPanel, BorderLayout.CENTER);

		// Bottom toolbar: Consult button, URL list, URL field
		Panel bottomPanel = new Panel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 3));
		consultButton = new java.awt.Button();
		consultButton.setLabel("Consult");
		consultButton.setBackground(new Color(-4533222));
		bottomPanel.add(consultButton);
		URLList = new java.awt.Choice();
		URLList.addItem("Animal");
		URLList.addItem("Izzat");
		URLList.addItem("Navigate");
		URLList.addItem("vi");
		URLList.addItem("Your Choice");
		bottomPanel.add(URLList);
		URLField = new java.awt.TextField(20);
		bottomPanel.add(URLField);
		add(bottomPanel, BorderLayout.SOUTH);
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

	
	void runButton_Clicked(Event event) {
		compileIt();
	}

	static boolean inApplet = true;
	
	public static void main(String args[])
	{
		inApplet = false; 
		AppletFrame.startApplet("ExpertSystem", "ExpertSystem", args);
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
		}
		if (aURL != null && !a.equals("Your Choice")) {
			sourceText.setText(loadURL(aURL));
			return;
		}
		if (a.equals("Your Choice")) {
			if (inApplet)
			   aURL = new URLData(theContext, URLField.getText().trim());
             else {
				  FileDialog aFD = new FileDialog(AppletFrame.ourFrame,"Which knowledge base",0);
				  aFD.setFile(".exp");
				  try { 
					 aFD.show(); 
					 aURL = new URLData(new URL("file:///"+aFD.getDirectory().substring(0,aFD.getDirectory().length()-1)+"/"),aFD.getFile());
					 System.out.println("file:///"+aFD.getDirectory().substring(0,aFD.getDirectory().length()-1)+"/");
				  }
				  catch (Exception e) {return;}
			}
			if (aURL != null) {
			   if (!aURL.done) aURL.activate();
			   
			   while (!aURL.done) {
				  try {Thread.sleep(5);} 
				  catch (Exception e) {}
			   }
			   sourceText.setText(aURL.toString());
			}
		}
	}
        
        public String loadURL(URLData aURL) {
		if (aURL != null) {
			if (!aURL.done) aURL.activate();
			
			while (!aURL.done) {
				try {Thread.sleep(5);} 
				catch (Exception e) {}
			}
			return (aURL.toString());
		}
                else return null;
        }

	//{{DECLARE_CONTROLS
	CardLayout textCards;
	Panel textCardPanel;
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


// flag for switching between edit and consult
	boolean x=true;

	void consultButton_Clicked(java.awt.event.ActionEvent event) {

		if (x) {
			consultButton.setLabel("Edit");
			compileIt();
			textCards.show(textCardPanel, "query");
			askMe1.setVisible(true);
			x=false;

		} else {
			textCards.show(textCardPanel, "source");
			askMe1.setVisible(false);
			consultButton.setLabel("Consult");
			x=true;
		}
		validate();
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
        try {
            java.io.File dir = new java.io.File(System.getProperty("user.dir"));
            // File.toURI().toURL() always produces a correct file: URL
            documentBase = dir.toURI().toURL();
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


