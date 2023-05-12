package dir.node;
import dir.visitor.Visitor;

public class File extends Entry {
    private final String name;

    private int size;

    private String content;

    public File(final String name, final int size) {
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void accept(final Visitor v) {
        v.visit(this);
    }

    public void setSize(final int size) {
    	this.size = size;
    	notifyObservers();
    }

    public void setContent(final String content) {
    	this.content = content;
    }

    public String getContent() {
    	return content;
    }
}
