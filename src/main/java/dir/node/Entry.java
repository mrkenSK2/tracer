package dir.node;

import java.util.Iterator;

import dir.observer.Subject;

import dir.visitor.Element;

public abstract class Entry extends Subject implements Element {

    public abstract String getName();

    public String getContent() throws FileTreatmentException {
    	throw new FileTreatmentException();
    }

    public void setContent(final String content) throws FileTreatmentException {
    	throw new FileTreatmentException();
    }
    public abstract int getSize();

    public Entry add(final Entry entry) throws FileTreatmentException {
        throw new FileTreatmentException();
    }

    public Iterator iterator() throws FileTreatmentException {
        throw new FileTreatmentException();
    }

    public String toString() {
        return getName() + " (" + getSize() + ")";
    }
}
