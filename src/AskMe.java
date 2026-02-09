// import symantec.itools.awt.BorderPanel;
import java.awt.*;


class PLabel extends Label {
	public Dimension prefSize;
	public Insets prefInset;
	
	public Dimension preferredSize() {
		if (prefSize == null) prefSize = new Dimension(225,23);
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

		Font panelFont = new Font("Helvetica", Font.PLAIN, 18);
		Font buttonFont = new Font("Helvetica", Font.BOLD, 18);

		titleLabel = new PLabel();
		titleLabel.setText("Consultation: Select from Menu");
		titleLabel.setAlignment(titleLabel.CENTER);
		titleLabel.setFont(panelFont);
		titleLabel.setPrefSize(525,30);
		this.add(titleLabel);

		GridBagConstraints gcon = new GridBagConstraints();
		gcon.gridheight = 1;
		gcon.gridwidth = 4;
		gcon.gridx = 0;
		gcon.gridy = 0;
		gcon.ipady = 8;
		gcon.weightx = .5;
		gcon.weighty = .5;
		gcon.fill = gcon.HORIZONTAL;
		gLayout.setConstraints(titleLabel,gcon);


		label3D1 = new PLabel();
		label3D1.setText("                    Ask and Receive                      ");
		label3D1.setBackground(new Color(16777075));
		label3D1.setFont(panelFont);
		label3D1.setPrefSize(450,60);
		label3D1.setInsets(8,20,8,20);
		this.add(label3D1);

		gcon = new GridBagConstraints();
		gcon.gridheight = 2;
		gcon.gridwidth = 2;
		gcon.gridx = 1;
		gcon.gridy = gcon.RELATIVE;
		gcon.ipady = 8;
		gcon.fill = gcon.HORIZONTAL;
		gcon.fill = gcon.BOTH;
		gcon.weightx = .5;
		gcon.weighty = .5;
		gLayout.setConstraints(label3D1,gcon);

		optionList = new java.awt.Choice();
		optionList.setFont(panelFont);
		optionList.setVisible(true);
		this.add(optionList);

		gcon = new GridBagConstraints();
		gcon.gridheight = 1;
		gcon.gridwidth = 2;
		gcon.gridx = 1;
		gcon.gridy = gcon.RELATIVE;
		gcon.ipady = 8;
		gcon.weightx = .5;
		gcon.weighty = .5;
		gcon.fill = gcon.HORIZONTAL;
		gLayout.setConstraints(optionList,gcon);


		yesButton = new java.awt.Button("True ");
		yesButton.setFont(buttonFont);
		yesButton.setBackground(new Color(11468718));
		this.add(yesButton);
		gcon = new GridBagConstraints();
		gcon.gridheight = 1;
		gcon.gridwidth = 1;
		gcon.gridx = 0;
		gcon.gridy = 4;
		gcon.ipady = 8;
		gcon.ipadx = 8;
		gcon.weightx = .5;
		gcon.weighty = .5;
		gLayout.setConstraints(yesButton,gcon);

		noButton = new java.awt.Button("False");
		noButton.setFont(buttonFont);
		noButton.setBackground(new Color(11206570));
		this.add(noButton);
		gcon = new GridBagConstraints();
		gcon.gridheight = 1;
		gcon.gridwidth = 1;
		gcon.gridx = gcon.RELATIVE;
		gcon.gridy = 4;
		gcon.ipady = 8;
		gcon.weightx = .5;
		gcon.weighty = .5;
		gLayout.setConstraints(noButton,gcon);

		whyButton = new java.awt.Button("Why");
		whyButton.setFont(buttonFont);
		whyButton.setBackground(new Color(11206570));
		this.add(whyButton);
		gcon = new GridBagConstraints();
		gcon.gridheight = 1;
		gcon.gridwidth = 1;
		gcon.gridx = gcon.RELATIVE;
		gcon.gridy = 4;
		gcon.ipady = 8;
		gcon.ipadx = 8;
		gcon.weightx = .5;
		gcon.weighty = .5;
		gLayout.setConstraints(whyButton,gcon);

		quitButton = new java.awt.Button("Stop");
		quitButton.setFont(buttonFont);
		quitButton.setBackground(new Color(11206570));
		this.add(quitButton);
		gcon = new GridBagConstraints();
		gcon.gridheight = 1;
		gcon.gridwidth = 1;
		gcon.gridx = gcon.RELATIVE;
		gcon.gridy = 4;
		gcon.ipady = 8;
		gcon.ipadx = 8;
		gcon.weightx = .5;
		gcon.weighty = .5;
		gLayout.setConstraints(quitButton,gcon);
	
		yesButton.setEnabled(false);
		noButton.setEnabled(false);
		validate();
		inited = true;
		SymItem lSymItem = new SymItem();
		optionList.addItemListener(lSymItem);

		// Register ActionListeners for buttons (AWT 1.1 event model)
		SymAction lSymAction = new SymAction();
		yesButton.addActionListener(lSymAction);
		noButton.addActionListener(lSymAction);
		whyButton.addActionListener(lSymAction);
		quitButton.addActionListener(lSymAction);

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
		yesButton.setEnabled(false);
		noButton.setEnabled(false);
		return theAnswer;
	}

	String theQuestion;

	public void setQuestion(String theQuestion) {
		setQuestion(theQuestion,"True","False");
	}

	public void setQuestion(String theQuestion, String bTrue, String bFalse) {
		this.theQuestion = theQuestion;
		label3D1.setText(theQuestion);
		optionList.setEnabled(false);
		optionList.removeAll();
		theAnswer = null;
		yesButton.setEnabled(true);
		yesButton.setLabel(bTrue);
		noButton.setEnabled(true);
		noButton.setLabel(bFalse);
		this.repaint();
	}

	public void setQuestion(String theQuestion, java.util.Vector choices) {
		this.theQuestion = theQuestion;
		label3D1.setText(theQuestion);
		theAnswer = null;
		yesButton.setEnabled(false);
		yesButton.setLabel("Use menu");
		noButton.setEnabled(false);
		noButton.setLabel("use menu");
		this.repaint();
		optionList.removeAll();
		optionList.setEnabled(true);
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

	void yesButton_Clicked() {
		theAnswer = "Yes";
	}
	void noButton_Clicked() {
		theAnswer = "No";
	}
	void whyButton_Clicked() {
		theAnswer = "Why";
	}
	void quitButton_Clicked() {
		theAnswer = "Quit";
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
		return new Dimension(525,90);
	}
	
	class SymAction implements java.awt.event.ActionListener
	{
		public void actionPerformed(java.awt.event.ActionEvent event)
		{
			Object object = event.getSource();
			if (object == yesButton)
				yesButton_Clicked();
			else if (object == noButton)
				noButton_Clicked();
			else if (object == whyButton)
				whyButton_Clicked();
			else if (object == quitButton)
				quitButton_Clicked();
		}
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