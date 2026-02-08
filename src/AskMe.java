// import symantec.itools.awt.BorderPanel;
import java.awt.*;


class PLabel extends Label {
	public Dimension prefSize;
	public Insets prefInset;
	
	public Dimension preferredSize() {
		if (prefSize == null) prefSize = new Dimension(150,15);
		return prefSize;
	}
	public Insets Insets() {
		if (prefInset == null) prefInset = new Insets(5,5,5,5);
		return prefInset;
	}
	public void setInsets(int t, int l, int b, int r) {
		prefInset = new Insets(t,l,b,r);
	}
	public void setPrefSize(int h, int v) {
		prefSize = new Dimension(h,v);
	}
}

// public class AskMe extends symantec.itools.awt.BorderPanel
public class AskMe extends Panel
{
	boolean inited=false;
	GridBagLayout gLayout;
	PLabel label3D1;
	PLabel titleLabel;
	java.awt.Button yesButton;
	java.awt.Button noButton;
	java.awt.Button whyButton;
	java.awt.Button quitButton;
	java.awt.Choice optionList;

	//insert class definition here
	void init() {
		//super();
		/*this.setPaddingTop(0);
		this.setPaddingBottom(0);
		this.setPaddingLeft(0);
		this.setPaddingRight(0);*/
		// this.setBorderColor(new Color(4210752));
		// this.setLayout(null);
		//this.reshape(6,274,429,154);
		// this.setBackground(new Color(16777108));
		gLayout = new GridBagLayout();
		setLayout(gLayout);

		titleLabel = new PLabel();
		titleLabel.setText("Consultation: Select from Menu");
		titleLabel.setAlignment(titleLabel.CENTER);
		titleLabel.setPrefSize(350,20);
		this.add(titleLabel);
		
		GridBagConstraints gcon = new GridBagConstraints();
		gcon.gridheight = 1;
		gcon.gridwidth = 4;
		gcon.gridx = 0;
		gcon.gridy = 0;
		gcon.ipady = 5;
		gcon.weightx = .5;
		gcon.weighty = .5;
		gcon.fill = gcon.HORIZONTAL;
		gLayout.setConstraints(titleLabel,gcon);


		label3D1 = new PLabel();
		label3D1.setText("                    Ask and Receive                      ");
	//	label3D1.reshape(16,6,393,50);
		label3D1.setBackground(new Color(16777075));
		label3D1.setPrefSize(300,40);
		label3D1.setInsets(5,15,5,15);
		this.add(label3D1);
		
		gcon = new GridBagConstraints();
		gcon.gridheight = 2;
		gcon.gridwidth = 2;
		gcon.gridx = 1;
		gcon.gridy = gcon.RELATIVE;
		gcon.ipady = 5;
		gcon.fill = gcon.HORIZONTAL;
		gcon.fill = gcon.BOTH;
		gcon.weightx = .5;
		gcon.weighty = .5;
		gLayout.setConstraints(label3D1,gcon);

		optionList = new java.awt.Choice();
		optionList.setVisible(true);
		this.add(optionList);
		
		gcon = new GridBagConstraints();
		gcon.gridheight = 1;
		gcon.gridwidth = 2;
		gcon.gridx = 1;
		gcon.gridy = gcon.RELATIVE;
		gcon.ipady = 5;
		gcon.weightx = .5;
		gcon.weighty = .5;
		gcon.fill = gcon.HORIZONTAL;
		gLayout.setConstraints(optionList,gcon);


		yesButton = new java.awt.Button("True ");
	//	yesButton.reshape(54,100,60,23);
		yesButton.setBackground(new Color(11468718));
		this.add(yesButton);
		gcon = new GridBagConstraints();
		gcon.gridheight = 1;
		gcon.gridwidth = 1;
		gcon.gridx = 0;
		gcon.gridy = 4;
		gcon.ipady = 5;
		gcon.ipadx = 5;
		gcon.weightx = .5;
		gcon.weighty = .5;
		gLayout.setConstraints(yesButton,gcon);

		noButton = new java.awt.Button("False");
	//	noButton.reshape(150,100,60,23);
		noButton.setBackground(new Color(11206570));
		this.add(noButton);
		gcon = new GridBagConstraints();
		gcon.gridheight = 1;
		gcon.gridwidth = 1;
		gcon.gridx = gcon.RELATIVE;
		gcon.gridy = 4;
		gcon.ipady = 5;
		gcon.weightx = .5;
		gcon.weighty = .5;
		gLayout.setConstraints(noButton,gcon);

		whyButton = new java.awt.Button("Why");
	//	whyButton.reshape(250,100,60,23);
		whyButton.setBackground(new Color(11206570));
		this.add(whyButton);
		gcon = new GridBagConstraints();
		gcon.gridheight = 1;
		gcon.gridwidth = 1;
		gcon.gridx = gcon.RELATIVE;
		gcon.gridy = 4;
		gcon.ipady = 5;
		gcon.ipadx = 5;
		gcon.weightx = .5;
		gcon.weighty = .5;
		gLayout.setConstraints(whyButton,gcon);
	
		quitButton = new java.awt.Button("Stop");
	//	quitButton.reshape(320,100,60,23);
		quitButton.setBackground(new Color(11206570));
		this.add(quitButton);
		gcon = new GridBagConstraints();
		gcon.gridheight = 1;
		gcon.gridwidth = 1;
		gcon.gridx = gcon.RELATIVE;
		gcon.gridy = 4;
		gcon.ipady = 5;
		gcon.ipadx = 5;
		gcon.weightx = .5;
		gcon.weighty = .5;
		gLayout.setConstraints(quitButton,gcon);
	
		yesButton.disable();
		noButton.disable();
		validate();
		inited = true;
		SymItem lSymItem = new SymItem();
		optionList.addItemListener(lSymItem);

	}
	
	
	// symantec.itools.awt.Label3D label3D1;


	String theTitle;

	public void setTitle(String theTitle) {
		this.theTitle = theTitle;
		titleLabel.setText(theTitle);
		// this.setLabel(theTitle);
	}

	public String getTitle() {
		return theTitle;
	}

	String theAnswer;

	public void setAnswer(String theAnswer) {
		this.theAnswer = theAnswer;
	}

	public String getAnswer() {
		yesButton.disable();
		noButton.disable();
		return theAnswer;
	}

	String theQuestion;

	public void setQuestion(String theQuestion) {
		setQuestion(theQuestion,"True","False");
	}

	public void setQuestion(String theQuestion, String bTrue, String bFalse) {
		this.theQuestion = theQuestion;
		label3D1.setText(theQuestion);
		optionList.disable();
		optionList.removeAll();
		theAnswer = null;
		yesButton.enable();
		yesButton.setLabel(bTrue);
		noButton.enable();
		noButton.setLabel(bFalse);
		this.repaint();
	}

	public void setQuestion(String theQuestion, java.util.Vector choices) {
		this.theQuestion = theQuestion;
		label3D1.setText(theQuestion);
		theAnswer = null;
		yesButton.disable();
		yesButton.setLabel("Use menu");
		noButton.disable();
		noButton.setLabel("use menu");
		this.repaint();
		optionList.removeAll();
		optionList.enable();
		for(int i=0;i<choices.size();i++) {
			optionList.addItem((String) choices.elementAt(i));
		}
	}

	public String getQuestion() {
		return theQuestion;
	}
	public boolean ReadyBegin() {
		if (theQuestion != null && theAnswer == null) return true;
		else return false;
	}
	public boolean ReadyEnd() {
		if (theQuestion != null && theAnswer != null) return true;
		else return false;
	}
	
	void choice_Action(java.awt.event.ItemEvent event) {
		theAnswer = event.getItem().toString();
	}

	public boolean handleEvent(Event event) {
		if (event.target == yesButton && event.id == Event.ACTION_EVENT) {
			yesButton_Clicked(event);
			return true;
		} else 	if (event.target == noButton && event.id == Event.ACTION_EVENT) {
			noButton_Clicked(event);
			return true;
		} else 	if (event.target == whyButton && event.id == Event.ACTION_EVENT) {
			whyButton_Clicked(event);
			return true;
		}else 	if (event.target == quitButton && event.id == Event.ACTION_EVENT) {
			quitButton_Clicked(event);
			return true;
		}/* else 	if (event.target == optionList && event.id == Event.ACTION_EVENT) {
			choice_Action((java.awt.event.ItemEvent) event);
			return true;
		}*/

		return super.handleEvent(event);
	}
	
	void yesButton_Clicked(Event event) {
		//{{CONNECTION
		theAnswer = "Yes";
		//}}
	}
	void noButton_Clicked(Event event) {

			 
		//{{CONNECTION
		theAnswer = "No";
		//}}
	}
	void whyButton_Clicked(Event event) {

			 
		//{{CONNECTION
		theAnswer = "Why";
		//}}
	}
	void quitButton_Clicked(Event event) {

			 
		//{{CONNECTION
		theAnswer = "Quit";
		//}}
	}

	public void update() {
		if (!inited) init();
		validate();
		paint(getGraphics());
	}
	public void paint(java.awt.Graphics g)
	{
		// to do: place event handler code here.
		validate();
		super.paint(g);
	}
	
	public Dimension preferredSize() {
		return new Dimension(350,60);
	}
	
		class SymItem implements java.awt.event.ItemListener
	{
		public void itemStateChanged(java.awt.event.ItemEvent event)
		{
			Object object = event.getSource();
			if (object == optionList)
				choice_Action(event);
		}
	}

}