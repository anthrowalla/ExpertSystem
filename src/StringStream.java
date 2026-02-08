class StringStream extends Object {

	String prog=null;
	int offset=0;
	
	public final boolean TOLOWER=true;
	public final boolean TOUPPER=false;
	public final boolean TRIM=true;
	public final boolean NOTRIM=false;
	public final boolean NOBLANKS=true;
	public final boolean BLANKS=false;

	void setStream(String s) {
		prog = new String(s);

	}

	void setStream(String s, boolean upper_lower) {
		if (upper_lower) 
			setStream(s.toLowerCase());
		else setStream(s.toUpperCase());
	}
	
	String getStream() {
		return prog;
	}
	
	int getOffset() {
		return offset;
	}
	
	void setOffset(int o) {
		offset = o;
	}
	
	String sfgets() { // prog is line offSet pointer
		return sfgets(false); // don't trim
	}	
	
	String subit(String a, int s, int e) {
		StringBuffer k = new StringBuffer(e-s+1);
		
		for (int i = s;i<e;i++) k.append(a.charAt(i));
	
		return k.toString();
		
	}
	
	String sfgets(boolean trim) { // prog is line offSet pointer
												// return next offset.
		int pr;
		String xline=null;
		
		if (prog == null) return null;
		if (prog.length() <= this.offset) return(null); // empty
		if ((pr = prog.indexOf('\n',this.offset)) == -1) pr = prog.indexOf('\r',this.offset);
/*		if (pr == -1) {
			line = prog.substring(this.offset,prog.length());
		} else {
			line = prog.substring(this.offset,pr);
		}*/
		if (pr == -1) {
			xline = new String(prog.substring(this.offset,prog.length()));
		} else {
			xline = new String(prog.substring(this.offset,pr));
		}
		//line = "47";
		this.offset = pr+1;
		if (trim) return xline.trim();
		else return xline;
	}

	String sfgets(boolean trim, boolean noblanks) {
		if (noblanks) {
			String line;
			do 
				line = sfgets(trim);
			while (line != null && line.equals(""));
			return line;
		} else return sfgets(trim);
	}
	
}