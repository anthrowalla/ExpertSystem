 /* infer --- inference engine 
 *
 *		XCMD interface for infer expert system.
 *
 */
 



import java.util.Hashtable;


class PropHash extends Hashtable {
	
	PropHash(int cap,float load) {
		super(cap,load);
	}
	
	public Str putProp(Str s) {
		Str ret=null;
		if (containsKey(s.p)) {
			ret = getProp(s.p);
			ret.n++;
			return ret;
		} else {
			s.n=1;
			this.put(s.p, s);
			return s;
		}
	}
	
	public Str getProp(String s) {
		return (Str) this.get(s);
	}
	
}


class RULE_T extends Object {
   public static final int UNKNOWN = 42	;/*  the answer to Life, the Universe, etc  */
   public static final int TRUE = -1	;/*  all binary ones (most places)  */
   public static final int FALSE = 0	;/*  all zeros (most places)  */
   public final int STRING = 000	;/*  display this string  */
   public final int ROUTINE = 001	;/*  run this string via the shell  */
   public static final int NOT = 002	;/*  invert truth-value of assoc. string  */

   public final int HYP = 004	;/*  if TRUE, exit  */
   public final int ASK = 020	;/*  either part of rule take State */
   public final int ALT = 100	;/*  either part of rule take State */
   public final int COMMENT = 010	;/*  this line is a comment  */
   public final int NONE = 040	;/*  no keyword recognized  */
								 
    public ELEMENT_T   ant;           /* Head of antecedents for this rule */
	public ELEMENT_T   con;           /* Head of consequences for this rule */ 

	public boolean	   vfy;            /* to detect circular logic */
	public boolean proved=false; // once proved, set true;

	static public RULE_T Rule[] = new RULE_T[100];
	static public int nrules = 0;

	
	public boolean isKnown() {
	   ELEMENT_T e;
		for (e = ant; e != null; e = e.next) { // check all conclusions.
			if (e.truthValue() == e.UNKNOWN) return false; // must go on  to find a value
		}
		return true;
	}

	public boolean isProved() {
	   return proved;
	}
	
	public boolean isTrue() { // undefined for notKnown
		ELEMENT_T e;
		for (e = ant; e != null; e = e.next) { // check all conclusions.
			if (e.truthValue() != e.TRUE) return false; // must go on  to find a value
		}
		return true;
	}
	
	public boolean isFalse() { // undefined for notKnown
		ELEMENT_T e;
		for (e = ant; e != null; e = e.next) { // check all conclusions.
			if (e.truthValue() == e.FALSE) return true; // must go on  to find a value
		}
		return false;
	}

	public boolean inAnt(ELEMENT_T f) {
		ELEMENT_T e;
		for (e = ant; e != null; e = e.next) { // check all conclusions.
			if (e.str == f.str) return true; // must go on  to find a value
		}
		return false;
	}

	public boolean inCon(ELEMENT_T f) {
		ELEMENT_T e;
		for (e = con; e != null; e = e.next) { // check all conclusions.
			if (e.str == f.str) return true; // must go on  to find a value
		}
		return false;
	}

	public ELEMENT_T unknownAnt() {
		ELEMENT_T e;
		for (e = ant; e != null; e = e.next) { // check all conclusions.
			if (e.str.val == e.UNKNOWN) return e; // must go on  to find a value
		}
		return null;
	}
	
	public boolean setConclusions(PrConsole fld) {
	   if (proved == false) {
		  ELEMENT_T e;

		  for (e = con; e != null; e = e.next) {
			   if ((e.type & ROUTINE) != 0) { // all of this clause needs redoing
				  if (e.str.val != UNKNOWN)
					 continue;
				  if (e.runx() == TRUE) { // adjust to account for java run ;
					 e.str.val = TRUE; // this will need to change when runx works
					 if ((e.type & HYP) != 0) {
						fld.printfld ("\n\nConclusion\n");
						return true; // exit
					 }
				  } else {
					 e.str.val = FALSE; // this will need to change when runx works
				  }
			   } else { // all the conclusions have a truthvalue of true
				  if (e.str.val == e.UNKNOWN) {
					 e.str.val = e.TRUE;
					 if ((e.type & e.NOT) != 0) e.str.val = e.FALSE;
					
					 if (e.str.val == TRUE)
						//fld.printfld ("I infer that: %s is TRUE\n", e.str.p);
						fld.printfld ("I infer that: %s.\n", e.str.p);
					 else
						fld.printfld ("I infer that: %s is False.\n", e.str.p);
					 for (int j=0;j<nrules;j++) {
						if (!Rule[j].isProved() && Rule[j].inAnt(e)) {
						   if (Rule[j].isTrue()) {
							  Rule[j].setConclusions(fld);
						   }
						}
					 }
				  }
				  if ((e.type & HYP) != 0) {
					 fld.printfld ("\n--> Conclusion\n");
					 return true; // exit
				  }
			}
		 }
		  proved=true;
	   }
	   return false; // don't exit
	}
}

class ELEMENT_T extends Object {
	
	public final int UNKNOWN = 42	;/*  the answer to Life, the Universe, etc  */
	public final int TRUE = -1	;/*  all binary ones (most places)  */
	public final int FALSE = 0	;/*  all zeros (most places)  */
	public final int NOT = 002	;/*  invert truth-value of assoc. string  */

	public int type;
	public Str str;
	public boolean done;
	public ELEMENT_T next;
	
	int truthValue() {
		if (this.str.val == UNKNOWN) return UNKNOWN;
		if ((this.type & NOT) != 0) 
			if (str.val == TRUE) return FALSE ;
			else return TRUE;
		else
			return str.val;
	}

	boolean tv(int tf) {
	   if (this.str.val == UNKNOWN) return tf == UNKNOWN;
	   if ((this.type & NOT) != 0)
		  if (str.val == TRUE) return tf == FALSE ;
		  else return tf == TRUE;
	   else
		  return str.val == tf;
	}
	
	public void setTrue() {
		str.val = TRUE;
		if ((type & NOT) != 0) str.val = FALSE;
	}
	
	
	public void setAskable() {
		str.askable = true;
		if ((type & NOT) != 0) str.askable = false;
	}

	public boolean isAskable() {
		return str.askable;
	}

	int runx () {
	   int value;
	   int ret;

	   str.val = TRUE; // patch
	   return(truthValue());
     }
	int run () {
	   int value;
	   int ret;

	   str.val = TRUE; // patch
	   return(truthValue());
	}
	   
	public void reportInference(PrConsole fld) {
		if (str.val == TRUE)
			fld.printfld ("I infer that: %s.\n", str.p);
		else
			fld.printfld ("I infer that: %s is False.\n", str.p);		
	}

}


public class Infer extends Thread {

	public String progname;
	public RULE_T Rule[];
	public int nrules = 0;
	public RULE_T why[];
	public PropHash pHash;

	public volatile boolean exitRequested = false;

	public int nwhy = 0;
	public Str SP = null;
	public int Verbose=0;
	public int verbose=0;

	public final int	ERRORFLAG	= -1;
	public final String	USAGE = "Usage: infer(field name, program text)";
	
	public String[] outstr;
	public String fp;
	
	public PrConsole fld;

	public static final int UNKNOWN = 42	;/*  the answer to Life, the Universe, etc  */
	public static final int TRUE = -1	;/*  all binary ones (most places)  */
	public static final int FALSE = 0	;/*  all zeros (most places)  */

	
	public final int STRING = 000	;/*  display this string  */
	public final int ROUTINE = 001	;/*  run this string via the shell  */
	public static final int NOT = 002	;/*  invert truth-value of assoc. string  */
	public final int HYP = 004	;/*  if TRUE, exit  */
	public final int ASK = 020	;/*  either part of rule take State */
	public final int ALT = 100	;/*  either part of rule take State */
	public final int COMMENT = 010	;/*  this line is a comment  */
	public final int NONE = 040	;/*  no keyword recognized  */

	
	public final int ANT = 1	;/*  in IF part of rule  */
	public final int CON = 2	;/*  in THEN part of rule  */
	public final int ANY = 3	;/*  either part of rule take State */
	public final int COMMENT_CHAR = '!'	;/*  ignore lines beginning with this  */
	public final int MAXRULE = 2000	;/*  plenty  */
	public final int MAXWHY = 200	;/*  things proven true, plus current  */
	public final int STRSIZE = 512	;/*  should be plenty  */


	
	int TRUTHVALUE(ELEMENT_T E) {
		if (E.str.val == UNKNOWN) return UNKNOWN;
		return (((E.type & NOT) != 0) ? (E.str.val == TRUE) ? FALSE : TRUE : (E.str.val));
	}
	
	Infer() {
		init();
	}
	
	void init () {
		RULE_T.Rule = new RULE_T[MAXRULE];
	   Rule = RULE_T.Rule;
		why = new RULE_T[MAXWHY];
		pHash = new PropHash(MAXRULE, (float) .7);
	
	}


	void doexit()
	{
		exitRequested = true;
	}

	
	public void run () {
	    int i;
	    int proved= FALSE;
		ELEMENT_T e;
		RULE_T.nrules = nrules;
		if (theAsker == null) { doexit(); return; } // or false?!
		// super.start();
	    for (i = 0; i < nrules && !exitRequested; i++) {      /* verify each CON */
			if (Rule[i] == null || Rule[i].con == null) {
				if (Rule[i] == null) {
					fld.printfld ("%s: RULE %d is null!!\n", progname, i);
					doexit(); break;
				}
				if (Rule[i].ant == null) {
				    fld.printfld ("%s: RULE %d has no Conditions and draws no Conclusions:\n", progname, i);
				    prrule (Rule[i], fld.printOut(false));
				    doexit(); break;
				}
				if ((Rule[i].ant.type & ASK) != 0) {
					for (e = Rule[i].ant; e != null; e = e.next) {
						e.setAskable(); // a way of setting askables?? Otherwise ...
					}
				} else {
					fld.printfld ("%s: RULE %d draws no Conclusions:\n", progname, i);
				    prrule (Rule[i], fld.printOut(false));
				    doexit(); break;
				}
				continue;
			}
			if (Rule[i].ant == null) {
			   Rule[i].setConclusions(fld);
			/*	for (e = Rule[i].con; e != null; e = e.next) { // check all conclusions.
					if ((e.type & ASK) != 0) {
						e.setAskable();
					}
					if (e.truthValue() == UNKNOWN) {
						e.setTrue();
						proved = e.TRUE;
						e.reportInference(fld);
						for (int j=0;j<nrules;j++) {
							if (Rule[j].inAnt(e)) {
							    if (Rule[j].isTrue()) Rule[j].setConclusions(fld);
							}
						}
						if ((e.type & HYP) != 0) {
							fld.printfld ("\n\nConclusion\n");
							doexit();
						}
					}
				} */
				continue;
			}
		//	if (TRUTHVALUE (Rule[i].con) == TRUE) // assume this rule has flown before
			//    continue; // Change to allCONS! some may be undone!!!!!!
			if (false) { // no op
				for (e = Rule[i].con; e != null; e = e.next) { // check all conclusions.
					if (e.str.val == UNKNOWN) break; // must go on  to find a value
				}
				if (e == null) {
					continue; // all CONs are known
				}
			}

			if(verify (Rule[i]) == true) { // if all the ANTs are true
			   // Rule[i].proved = true;
			    proved = TRUE;
			    Rule[i].setConclusions(fld);
			}
	    }

	    if (!exitRequested && proved == FALSE) {
			fld.printfld ("I can't prove anything!!!\n");
			fld.printfld("\n");
		}
	    doexit();
	}
	
	/* verify --- verify a CON.  May be called recursivly */
	
	boolean verify (RULE_T rule)
	{
	    ELEMENT_T e,h;
	    int i;

	if (exitRequested) return false;
	if (rule == null) return false;
		// have already checked to see rule is worth doing. some unknown consequence
		
 		pushwhy (rule); // in case someone asks
	  
	   
	    if (verbose != 0) // This is not currently set. nor is Verbose.
	    					// some opportunities here for the visible rulebase
			prrule (rule, fld.printOut(false));
		
		if (rule.ant == null) { // no conditions e.g. assert!!!
			if (Verbose == 0) fld.printfld ("%s: RULE has no Conditions:\n", progname);
			prrule (rule, fld.printOut(false));
			popwhy ();
			return (true); // results in asserting these CONs with truthvalue true
	    }
		for (e = rule.ant; e != null; e = e.next) {   /* for each ANT */
	    	if (e.truthValue()  == e.TRUE) continue; // this part is true
			else if (e.truthValue() == e.FALSE) {
				popwhy();
				return false; // rule is false
			}
			if ((e.type & ASK) != 0) {
					e.setAskable();
					continue;
			} // set askable false??
			for (i = 0; i < nrules; i++)
			   if (Rule[i].inCon(e)) break;

			if (i == nrules) {      /* not in a CON */
			   prove(e);
			   if (e.truthValue() == TRUE)
				  continue;
			   else {
				  popwhy ();
				  return (false);
			   }
			}

int anytrue = 0;

			for (; i < nrules; i++)  {// find first rule that will prove this ant
				if (Rule[i].inCon(e)) {
				   boolean ret;
					if (Rule[i].vfy) {
						fld.printfld ("%s: Circular logic in Rules! Rules are:\n", progname);
						prcirc ();
						fld.printfld("\n");
						return false;
						// doexit ();
					}
					Rule[i].vfy = true;
					ret = verify (Rule[i]);
					Rule[i].vfy = false;
					if (ret == true) {
					   anytrue++;
						if (e.truthValue() == e.TRUE)
							break; // this e's done
						else return false; 
					}
				}
			}
		    if (anytrue == 0) {// other means worked. Do next ANT
				popwhy ();         // clean up stack on fail.
				return (false); // other means didn't - one ANT false -> rule false
			}
		}               /* don't pop the why stack if Const.TRUE */
		rule.setConclusions(fld);
	    return (true);
	}

	/* prove --- prove this ANT (may be already proven) */
	
	int prove (ELEMENT_T e) {
		if (e.truthValue() != UNKNOWN)
			return (e.truthValue());
		if ((e.type & ROUTINE) != 0)
			return (runx (e));
		else if (e.isAskable())
			return (askval (e));
		else return e.FALSE;
	}
	
	/* askval --- get truth from user */
	
	String ask(String question, String b1, String b2) {
		theAsker.setQuestion(question,b1,b2);
		try {
			sleep(100);
			while (theAsker.ReadyEnd() == false && !exitRequested) sleep(10);
		} catch (Exception e){};
		return theAsker.getAnswer();
	}
	
	int askval (ELEMENT_T e) {
	    String line="";
	    int value;

	    value = UNKNOWN;
	    for (;!exitRequested;) {
	    	fld.printfld("%s?", e.str.p);
			line = fld.printOut(false);
			// fld.printfld(line);
			if ((line = ask (line, "True","False")) == null)
			    line = "q";
			fld.printfld(" %s\n",line);
			line = line.toLowerCase();
			switch (line.charAt(0)) {
			case 'y':
			case 't':
				//if (e.type & NOT)  value = FALSE;
			   // else
			    value = TRUE;
			    break;
			case 'n':
			case 'f':
			  //  if (e.type & NOT) value = TRUE;
			  //  else
			  value = FALSE;
			    break;
			case 'q':
			    fld.printfld ("OK, Bye!\n");
			    doexit();
			    return FALSE;
			case 'w':
			    showwhy ();
			    continue;
			}
			if (value != UNKNOWN)
			    break;
			fld.printfld ("Didn't recognize answer! Try again.\n\n");
	    }
	    e.str.val = value; // this is how you set a value for an ANT condition;
	    return (e.truthValue());
	}
	
	/* run --- execute this string */
	
	int runx (ELEMENT_T e) {
	    int value;
	    int ret;
	    
	    e.str.val = TRUE; // patch 
	    return(e.truthValue());
	    /*if ((ret = system (e.str.p)) < 0) {
		printfld (fld,"%s: can't execute %s  returning Const.TRUE\n", progname,
		  e.str.p);
		value = Const.TRUE;
	    }
	    else if (ret == 0)
		value = Const.TRUE;
	    else
		value = Const.FALSE;
	
	    e.str.val = value;
	    return (TRUTHVAL (e));*/
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
	    // fld.printfld ("%s\n",fp);
	}
	
	/* prval --- return the char representation of this value */
	
	String prval (ELEMENT_T e) {
	
	    if (e.str.val == UNKNOWN)
			return ("Unknown");
	    else if (e.str.val == TRUE || e.str.val == FALSE) {
			if (e.truthValue() == TRUE)
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