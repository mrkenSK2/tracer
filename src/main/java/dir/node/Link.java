package dir.node;

import dir.visitor.Visitor;

public class Link extends Entry {

	private final File realSubject;
	
	public Link(final File realSubject) {
		this.realSubject = realSubject;
	}
	
	public String getContent() {
		return realSubject.getContent();
	}
	public void setContent(final String content) {
		realSubject.setContent(content);
	}
	

	public String getName() {
		return realSubject.getName();
	}

	public int getSize() {
		return 0;
	}

	public void accept(final Visitor v) {
		v.visit(this);
	}
	   
	public String toString() {                                          // 文字列表現
        return getName() + " (0)";
	}
}
