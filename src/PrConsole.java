

class PrConsole extends PrintFormat {
	java.awt.TextArea consoleText=null;
	
	void setConsole(java.awt.TextArea c) {
		consoleText = c;
	}
	
	java.awt.TextArea getConsole() {
		return(consoleText);
	}
	
	public String printOut(boolean t) {
		if (t) return printOut() ;
		else return super.printOut();
	}
	
	public String printOut() {
		if (consoleText != null) {
                    consoleText.appendText(super.printOut());
                    consoleText.setSelectionStart(consoleText.getText().length()-1);
		} else super.printOut();
		
		return theText.toString();
	}

	String printfld(String a) {
		printf("%s",a);
		return(printOut());
	}

	String printfld(String a, String b) {
		printf(a,b);
		return(printOut());
	}

	String printfld(String a, int b) {
		printf(a,b);
		return(printOut());
	}

	String printfld(String a, String b, int c) {
		printf(a,b);
		printF(c);
		return(printOut());
	}
	String printfld(String a, String b, String c) {
		printf(a,b);
		printF(c);
		return(printOut());
	}
	String printfld(String a, String b, String c, String d) {
		printf(a,b);
		printF(c);
		printF(d);
		return(printOut());
	}
	String printfld(String a, int b, String c) {
		printf(a,b);
		printF(c);
		return(printOut());
	}
	
}