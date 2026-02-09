// Generic AppletFrame

import java.awt.*;
import java.applet.Applet;

// Applet to Application Frame window
public class AppletFrame extends Frame {
	public static AppletFrame ourFrame=null;

	public static void startApplet(String className, String title, String args[]) {
		Applet a;
		Dimension appletSize;

		AppletFrame f = new AppletFrame(title);
		
		try {
			// create an instance of your applet class
			a = (Applet) Class.forName(className).newInstance();
		} catch (ClassNotFoundException e) {
			return;
		} catch (InstantiationException e) {
			return;
		} catch (IllegalAccessException e) {
			return;
		}

		// initialize the applet
		a.init();
		a.start();
	
		// create new application frame window
	
		// add applet to frame window
		f.add("Center", a);
	
		// resize frame window to fit applet
		// assumes that the applet sets its own size
		// otherwise, you should set a specific size here.
		f.pack();
		f.setMinimumSize(new Dimension(500, 400));
		f.setSize(500, 701);
		f.setLocation(50, 50);

		// show the window
		f.setVisible(true);
		ourFrame = f;

	}  // end startApplet()

	public static void startApplet(Applet className, String title, String args[]) {
		Applet a;

		AppletFrame f = new AppletFrame(title);

		a = className;

		// initialize the applet
		a.init();
		a.start();

		// create new application frame window

		// add applet to frame window
		f.add("Center", a);

		// resize frame window to fit applet
		f.pack();
		f.setMinimumSize(new Dimension(500, 400));
		f.setSize(500, 701);
		f.setLocation(50, 50);

		// show the window
		f.setVisible(true);
	
	}  // end startApplet()

	// constructor needed to pass window title to class Frame
	public AppletFrame(String name) {
		// call java.awt.Frame(String) constructor
		super(name);
		setLayout(new BorderLayout());
		ourFrame = this;
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
}
	public static void goFront() {
		ourFrame.toFront();
	}

	// needed to allow window close
	public boolean handleEvent(Event e) {
		// Window Destroy event
		if (e.id == Event.WINDOW_DESTROY) {
			dispose();
			return true;
		}
		
		// it's good form to let the super class look at any unhandled events
		boolean xe = super.handleEvent(e);
		return xe;
		
	}  // end handleEvent()

	//{{DECLARE_CONTROLS
	//}}
	//{{DECLARE_MENUS
	//}}
}   // end class AppletFrame

