

class Keytab extends Object {
	public String    name;
	public int   type;
	public int   newstate;
	
	Keytab(String n,int t,int s) {
		name = n;
		type = t;
		newstate = s;
	}
 } 
 

class InferCompiler extends Infer {
	
	
	int Curline;
	int State;
	
	Keytab[] keytab = null ;
    java.awt.TextArea ourProg;
      
    InferCompiler() {
    }
   
    InferCompiler(java.awt.TextArea c) {
    	fld = new PrConsole();
    	fld.setConsole(c);
    }
    
    void setConsole(java.awt.TextArea c) {
    	fld = new PrConsole();
    	fld.setConsole(c);
    }
    
    void setProg(java.awt.TextArea c) {
    	ourProg = c;
    }
 
    java.awt.TextArea getConsole() {
    	if (fld != null) 
    		return fld.getConsole();
    	else return null;
    }
    
   java.awt.TextArea getProg() {
    	return ourProg;
    }
 
	void init_keytab()  {    
		
		if (keytab == null) {
			keytab = new Keytab[50];
		}
	    keytab[0] = new Keytab("THEN",STRING,CON);
	   
		keytab[1] = new Keytab("THENNOT",STRING|NOT,CON);
    
		keytab[2] = new Keytab("CONCLUDENOT",STRING|NOT,CON);
    	
		keytab[3] = new Keytab("AND",STRING,ANY);
		  
		keytab[4] = new Keytab("IF",STRING,ANT);
		  
		keytab[5] = new Keytab("WHEN",STRING,ANT);
	
		keytab[6] = new Keytab("ANDRUN",ROUTINE,ANT);
		  
		keytab[7] = new Keytab("ANDNOT",STRING|NOT,ANT);
		  		
		keytab[8] = new Keytab("THENHYP",STRING|HYP,CON);
		
		keytab[9] = new Keytab("CONCLUDE",STRING|HYP,CON);

		keytab[10] = new Keytab("IFRUN",ROUTINE,ANT);
		  
		keytab[11] = new Keytab("ANDIF",STRING,ANT);

		keytab[12] = new Keytab("ANDWHEN",STRING,ANT);

		keytab[13] = new Keytab("IFNOT",STRING|NOT,ANT);
		  
		keytab[14] = new Keytab("WHENNOT",STRING|NOT,ANT);

		keytab[15] = new Keytab("THENRUNHYP",ROUTINE|HYP,CON);

		keytab[16] = new Keytab("THENRUN",ROUTINE,CON);
	
		keytab[17] = new Keytab("ANDIFRUN",ROUTINE,ANT);
	
		keytab[18] = new Keytab("ANDNOTRUN",ROUTINE|NOT,ANT);

		keytab[19] = new Keytab("IFNOTRUN",ROUTINE|NOT,ANT);
	
		keytab[20] = new Keytab("ANDTHEN",STRING,CON);

		keytab[21] = new Keytab("ANDTHENNOT",STRING|NOT,CON);
		
		keytab[22] = new Keytab("ANDTHENHYP",STRING|HYP,CON);

		keytab[23] = new Keytab("ANDTHENRUN",ROUTINE,CON);

		keytab[24] = new Keytab("ANDTHENRUNHYP",ROUTINE|HYP,CON);
		
		keytab[25] = new Keytab("ASSERT",STRING,CON); // just for prettyness

		keytab[26] = new Keytab("ASSERTNOT",STRING|NOT,CON);
	
		keytab[27] = new Keytab("ASK",STRING|ASK,ANT);	
	
		keytab[28] = new Keytab("ASKNOT",STRING|NOT|ASK,ANT);	
	
		keytab[29] = new Keytab("THENASK",STRING|ASK,CON);	
	
		keytab[30] = new Keytab("THENASKNOT",STRING|NOT|ASK,CON);	
		
		keytab[31] = new Keytab(null,0,0);
	
	}

void doCompile() {
	if (ourProg != null) {
		compile(ourProg.getText());
	}
}

void compile (String fp) {
    int key;
    String line;
    StringStream prog;
 //    Str savestr ();
	
	init_keytab();
    Curline = 0;
    State = ANT;
    prog = new StringStream();
    prog.setStream(fp,prog.TOUPPER); // change to all upper case
    
    rulealloc ();
    line = prog.sfgets(prog.TRIM,prog.NOBLANKS);
	fld.printfld("Consultation: %s\n\n",line);
	progname = line;
	theAsker.setTitle(progname);
	theAsker.repaint();
	while ((line = prog.sfgets(prog.TRIM,prog.NOBLANKS)) != null) {
  		Curline++;
	  	if (line.equals("") || line.length() == 0) continue;
			if (Verbose != 0)
	  	  fld.printfld ("%4d    %s\n", Curline, line);
		key = getkey (line);
		if (key == NONE) 
	   		fld.printfld ("%s: no keyword found on line %d\n", line, Curline);
		else if (key == COMMENT);
		else
	    	push (key, savestr (line));
    }
}

/* newstate --- if switching from CON to ANT, start a new rule */

void newstate (int newState) {
    if (newState != ANT && newState != CON) {     /* paranoia */
		fld.printfld ("%s: bad new val: %d\n", progname, newState);
		doexit ();
    }

    if (State != newState) {
		if (State == CON)
		    rulealloc ();
		State = newState;
    }
}

/* push --- add an element to this rule */

void push (int type, Str ptr) {
    ELEMENT_T e, last;

    if (ptr == null)   /* keyword only, ignore */
		return;
    e = new ELEMENT_T();
    e.str = ptr;
    e.type = type;
    e.next = null;
    e.done = false;
    if (State == CON) {
		if (Rule[nrules-1].con == null) /* first element */
		    Rule[nrules-1].con = e;
		else                          /* place on end */
		    for (last = Rule[nrules-1].con; last != null; last = last.next)
			if (last.next == null) {
			    last.next = e;
			    break;
			}
	    }
    else {
		if (Rule[nrules-1].ant == null)
		    Rule[nrules-1].ant = e;
		else
		    for (last = Rule[nrules-1].ant; last != null; last = last.next)
				if (last.next == null) {
				    last.next = e;
				    break;
		}
    }
}

/* savestr --- skip 1st word, save rest of line in str buffer */

Str savestr (String line) {
    String s;
    Str sp;
	int ndx;
	/*
	ndx = line.indexOf(" ");
	if (ndx != -1) {
		s = line.substring(ndx);
		s = s.trim();
	} else {
		s = "";
	}
   */
   s = new String(line);
   s = s.trim();
   	ndx = s.indexOf(" ");
	if (ndx != -1) {
		s = s.substring(ndx);
		s = s.trim();
	} else {
		s = "";
	}
    if (s.equals("")) {
		fld.printfld ("%s: line %d has nothing but a keyword\n",
	  		progname, Curline);
		return (null);
    }
   //  fld.printfld ("\nS: %s\n\n",s);
	if (s.startsWith("NOT ")) s = s.substring(4).trim(); // Check this index start !!!!!!
    for (sp = SP; sp != null; sp = sp.next)    /* is string already present? */
		if (sp.p.equals(s)) {
			sp.n++;
			return (sp);
		}

    sp = new Str(); /* new string */
    sp.p = s;
    sp.val = UNKNOWN;
    sp.next = SP;      /* place at head */
    SP = sp;
    return (sp);
}

/* getkey --- determine the keyword on this line */

int getkey (String line) {
     int i, so;
     String s, p;
    String word;
    
    if (line.equals("") || line.length() == 0) return COMMENT;
    if (line.charAt(0) == COMMENT_CHAR) return (COMMENT);

	i = line.indexOf(" ");
	if (i == -1) return (COMMENT);
	
	word = line.substring(0,i);
	so = ++i;
 
    for (i = 0; keytab[i].name != null ; i++)    /* look for match */
		if (word.equals(keytab[i].name)) {
			if (keytab[i].newstate != ANY)
		    	newstate (keytab[i].newstate);
			if (line.startsWith("NOT ",so)) {
						return(keytab[i].type ^ NOT); // possible flake here!!!!!!!
					}
		    return (keytab[i].type);
		}

    return (NONE);
}


/* rulealloc --- allocate a new rule, and advance the pointer */

	void rulealloc () {
		
	    if (nrules >= MAXRULE) {
			fld.printfld ("%s: too many rules! %d rules\n", progname, nrules);
			doexit ();
	    }
	
	    Rule[nrules] = new RULE_T();
	    Rule[nrules].ant = null;
	    Rule[nrules].con = null;
	    Rule[nrules].vfy = false;
	    nrules++;
	}

}

