/* infer --- inference engine 
 *
 *		XCMD interface for infer expert system.
 *
 */
 
import Str;
import PrConsole;
import AskMe;

class RULE_T extends Object {
	public ELEMENT_T   ant;           /* Head of antecedents for this rule */
	public ELEMENT_T   con;           /* Head of consequences for this rule */ 
	public boolean       vfy;            /* to detect circular logic */ 
	public boolean proved = false;
}

class ELEMENT_T extends Object {
	public final static int UNKNOWN = 42	;/*  the answer to Life, the Universe, etc  */
	public final static int TRUE = -1	;/*  all binary ones (most places)  */
	public final static int FALSE = 0	;/*  all zeros (most places)  */

	public int type;
	public Str str;
	public ELEMENT_T next;
	
	boolean t() {if ((type & Infer.NOT) == 0) return str.isTrue(); else return str.isFalse();}
	boolean f() {if ((type & Infer.NOT) == 0) return str.isFalse(); else return str.isTrue();}
	boolean u() {return str.isUnknown();}
	
	int tv() {
		return u() ? UNKNOWN :  t() ? TRUE : FALSE;
	}

	void setTV() {
		str.val = UNKNOWN;
	}

	void setTV(boolean stv) {
		if ((type & Infer.NOT) == 0) str.val = (stv ? TRUE : FALSE);
		else str.val = (stv ? FALSE : TRUE);
	}
	
	void setTV(int stv) {
		if (!(stv == UNKNOWN)) {
			setTV(stv == TRUE);
		}
	}
	
	int run() {
		int ret = UNKNOWN;
		try {
		//	return( (Integer) (Infer.externs.getClass().getMethod(str.sval,null).invoke(Infer.externs,null))).intValue();
			return TRUE;
		} catch (Exception e) {
			return UNKNOWN;
		}
	}
}


class Infer extends Thread {
	
	//static Externals externs = new Externals();
	public boolean terminated = false;

	public String progname;
	public RULE_T Rule[];
	public int nrules = 0;
	public RULE_T why[];
	public int nwhy = 0;
	public Str SP = null;
	public int Verbose=0;
	public int verbose=0;
	
	public final int	ERRORFLAG	= -1;
	public final String	USAGE = "Usage: infer(field name, program text)";
	
	public String[] outstr;
	public String fp;
	
	public PrConsole fld;

	public final static int UNKNOWN = ELEMENT_T.UNKNOWN; // 42	;/*  the answer to Life, the Universe, etc  */
	public final static int TRUE = ELEMENT_T.TRUE; // -1	;/*  all binary ones (most places)  */
	public final static int FALSE = ELEMENT_T.FALSE; // 0	;/*  all zeros (most places)  */

	
	public final static int STRING = 000	;/*  display this string  */
	public final static int ROUTINE = 001	;/*  run this string via the shell  */
	public final static int NOT = 002	;/*  invert truth-value of assoc. string  */
	public final static int HYP = 004	;/*  if TRUE, exit  */
	public final static int COMMENT = 8	;/*  this line is a comment  */
	public final static int NONE = 16	;/*  no keyword recognized  */
	public final static int ASK = 32	;/*  no keyword recognized  */

	
	public final static int ANT = 1	;/*  in IF part of rule  */
	public final static int CON = 2	;/*  in THEN part of rule  */
	public final static int ANY = 3	;/*  either part of rule take State */
	public final static int COMMENT_CHAR = '!'	;/*  ignore lines beginning with this  */
	public final static int MAXRULE = 1000	;/*  plenty  */
	public final static int MAXWHY = 100	;/*  things proven true, plus current  */

	
	int TRUTHVALUE(ELEMENT_T E) {
		return E.u() ? UNKNOWN : E.t() ? TRUE : FALSE;
	}
	
	Infer() {
		init();
	}
	
	void init () {
		Rule = new RULE_T[MAXRULE];
		why = new RULE_T[MAXWHY];
	
	}


	void doexit()
	{
		this.stop();
		terminated = true;
	}

/* Main loop

*/

	public void run () {
	    int i;
	    boolean proved = false;
	
		if (theAsker == null) doexit(); // or false?!
		try {
	    for (i = 0; i < nrules; i++) {      /* verify each CON */
	    	if (Rule[i].con == null) {
			    fld.printfld ("%s: RULE %d has no THENs:\n", progname, i);
			    prrule (Rule[i], fld.printOut(false));
			    doexit(); // treat as error 
			}
			if (Rule[i].proved) continue; // already done
			
			if (verify (Rule[i]) == true) {
				Rule[i].proved = true;
			    proved = true;
			    setConclusions(Rule[i]);
			}
	    }
	    } catch (Exception e) {
	    
	    }
	    if (!proved) {
			fld.printfld ("I can't prove anything!!!\n");
			fld.printfld("\n");
		}
	    doexit();
	}
	
	public void setConclusions(RULE_T rule) {
		ELEMENT_T f;
		boolean didhyp=false;
		for (f = rule.con; f != null; f = f.next) {
			if (!f.u())  continue;
			if ((f.type & ROUTINE) != 0) {
			    f.str.val = (f.run());  // adjust to account for java run ;
			} else { // else not a routine
			    f.setTV(true);
			}
			if ((f.type & HYP) != 0) {
				didhyp = true;
				if (f.str.val == TRUE) fld.printfld ("\nI conclude: %s\n", f.str.p);
				else fld.printfld ("\nI conclude: %s is FALSE\n", f.str.p);
			}  else {
				if (f.str.val == TRUE) fld.printfld ("\nI infer: %s\n", f.str.p);
				else fld.printfld ("\nI infer: %s is FALSE\n", f.str.p);
			}
		}
		if (didhyp) doexit();
	}

	/* verify --- verify a CON.  May be called recursivly */
	
	boolean verify (RULE_T rule)
	{
	    ELEMENT_T e;
	    int i;
	
		if (rule.proved) return true; // already done
	    if (verbose != 0)
			prrule (rule, fld.printOut(false));
		
		if (rule.ant == null) {
			fld.printfld ("%s: RULE has no IFs:\n", progname);
			prrule (rule, fld.printOut(false));
			return (true);
	    }
		if (TRUTHVALUE(rule.con) == TRUE) return true;
		if (TRUTHVALUE(rule.con) == FALSE) return false;
	    pushwhy (rule);
	
	
		// might do some analysis for first question to ask
		// right now just does first in the list
		
	    for (e = rule.ant; e != null; e = e.next) {   /* for each ANT */

			// find out if this condition is a conclusion of any rule
			// should improve the analysis here to optimize
			// probably should elaborate Str to include list of rules where in conclusion
			for (i = 0; i < nrules; i++)
			    if (e.str == Rule[i].con.str)    /* this ANT is a CON */
					break;
					
			if (i == nrules) {      /* not in a CON */
				e.str.val = prove(e);
			    if (e.t())
					continue;
			    else {
					popwhy ();
					return (false);
			    }
			}
			// i contains  rule number that contains ANT as CON, so don't initialise i
		    int anytrue = 0;
		    for (; i < nrules; i++) {
			   if (e.str == Rule[i].con.str) {  /* match */
				    boolean ret;
				    if (Rule[i].vfy) {
						fld.printfld ("%s: Circular logic in Rules! Rules are:\n", progname);
						prcirc ();
						doexit ();
				    }
				    Rule[i].vfy = true;
				    ret = verify (Rule[i]);
				    Rule[i].vfy = false;
					if (ret == true) {
						anytrue++;
						setConclusions(Rule[i]);
						e.str.val = prove(e);
						if (e.t()) {
							break;
						} else {
							popwhy ();
							return (false);
						}
					}
				}
		    }
		    if (anytrue == 0) {
				popwhy ();
			    return (false);
			}
	    }               /* don't pop the why stack if Const.TRUE */
	    return (true);
	}

	/* prove --- prove this ANT (may be already proven) */
	
	int prove (ELEMENT_T e) {
		if (!e.u()) return e.str.val;
		if ((e.type & ROUTINE) != 0)
			return (e.run());
		else
			return (askval (e));
	}
	
	/* askval --- get truth from user */
	
	String ask(String question, String b1, String b2) {
		theAsker.setQuestion(question,b1,b2);
		try {
			sleep(100);
			while (theAsker.ReadyEnd() == false) sleep(10);
		} catch (Exception e){};
		return theAsker.getAnswer();
	}
	
	int askval (ELEMENT_T e) {
	    String line="";
	    int value;
	    
	    value = UNKNOWN;
	    for (;;) {
	    	fld.printfld("%s?", e.str.p);
			line = fld.printOut(false);
			if ((line = ask (line, "True","False")) == null)
			    line = "q";
			fld.printfld(" %s\n",line);
			line = line.toLowerCase();
			switch (line.charAt(0)) {
			case 'y':
			case 't':
			    value = TRUE;
			    break;
			case 'n':
			case 'f':
			    value = FALSE;
			    break;
			case 'q':
			    fld.printfld ("OK, Bye!\n");
			    doexit();
			    break;
			case 'w':
			    showwhy ();
			    continue;
			}
			if (value != UNKNOWN)
			    break;
			fld.printfld ("Didn't recognize answer! Try again.\n\n");
	    }
	    e.str.val = value;
	    return (e.str.val);
	}
	
	/* prrule --- print this rule in a readable form */
	
	void prrule (RULE_T rule, String fp) {
	    ELEMENT_T e;
	 //   String prval (), prtype ();
	
	    for (e = rule.ant; e != null; e = e.next)
	    fld.printfld ("%s %s (%s)\n", prtype (e.type, "IF"), e.str.p,
		  prval (e));
		fp = fld.printOut(false);
	    for (e = rule.con; e != null; e = e.next)
	    fld.printfld ("%s %s (%s)\n", prtype (e.type, "THEN"), e.str.p,
		  prval (e));
		fp = fld.printOut(false);
	}
	
	/* prval --- return the char representation of this value */
	
	String prval (ELEMENT_T e) {
	
	    if (e.str.val == UNKNOWN)
			return ("Unknown");
	    else if (e.str.val == TRUE || e.str.val == FALSE) {
			if (TRUTHVALUE (e) == TRUE)
			    return ("True");
			else
			    return ("False");
	   	} else
			return ("Bad Value");
	}
	
	/* prtype --- return the char representation of this type */
	
	String prtype (int type, String word) {
	  
	    String str_type;
	
	    str_type = word;
	    if ((type & NOT) != 0)
			str_type = str_type + "NOT";
	    if ((type & ROUTINE) != 0)
			str_type = str_type + "RUN";
	    if ((type & HYP) != 0)
			str_type = str_type + "HYP";
	    return (str_type);
	}
	
	/* pushwhy --- push this rule onto the "why" stack */
	
	void pushwhy (RULE_T r) {
	    if (nwhy >= MAXWHY) {
			fld.printfld ("%s: blew why stack!\n", progname);
			doexit ();
	    }
	    why[nwhy++] = r;
	}
	
	/* popwhy --- pop a value off of the "why" stack */
	
	void popwhy ()
	{
	    if (--nwhy < 0) {
			fld.printfld ("%s: why stack underflow!\n", progname);
			doexit ();
	    }
	}
	
	/* showwhy --- print the details of the "why" stack */
	
	void showwhy ()
	{
	    int i;
	    
	    for (i = 0; i < nwhy; i++)
			prrule (why[i], fld.printOut(false));
	}
	
	/* prcirc --- print the details of the circular loop */
	
	void prcirc ()
	{
	    int i;
	    
	    for (i = 0; i < nrules; i++)
		if (Rule[i].vfy)
		    prrule (Rule[i], fld.printOut(false));
	}
	
	
	public PrConsole getFld() {
		return fld;
	}


	public void setConsole(PrConsole fld) {
		this.fld = fld;
	}


	AskMe theAsker;

	public void setAsker(AskMe theAsker) {
		this.theAsker = theAsker;
	}

	public AskMe getAsker() {
		return theAsker;
	}
}