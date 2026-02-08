

public class Str extends Object {
	public String p;
	public int val; 
	public Str next;
	public int ruleno=-1;
	
	final static int UNKNOWN = Infer.UNKNOWN;
	final static int TRUE = Infer.TRUE;
	final static int FALSE = Infer.FALSE;

	int n=0;
	boolean askable=true;
	
	public Str() {
	
	};
	
	public Str(String x) {
		p = x;
	}
	
	public String getString() {
		return p;
	}
	
	public void setString(String s) {
		p = s;
	}
	
	public Str getNext() {
		return next;
	}
	
	public void setVal(int v) {
		val = v;
	}
	
	public int getVal() {
		return val;
	}
	
	public boolean isTrue() {
		return val == TRUE;
	}
	
	public boolean isFalse() {
		return val == FALSE;
	}
	
	public boolean isUnknown() {
		return val == UNKNOWN;
	}
	
	public void addNext() {
		next = new Str();
	}
	
	public void addNext(String s) {
		next = new Str(s);
	}
	
	public void addNext(Str s) {
		next = s;
	}
	
	public void insertNext() {
		Str n = new Str();
		n.addNext(this.next);
		this.next = n;
	}
	
	public void insertNext(Str s) {
		s.addNext(this.next);
		this.next = s;
	}
	
	public void deleteNext() {
		this.next = this.next.next;
	}
}

