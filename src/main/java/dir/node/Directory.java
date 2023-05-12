package dir.node;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

import dir.visitor.Visitor;

public class Directory extends Entry {
    private final String name;

    private final List<Entry> dir = new ArrayList<>();

    public Directory(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        int size = 0;
        final Iterator<Entry> it = dir.iterator();
        while (it.hasNext()) {
            final Entry entry = it.next();
            size += entry.getSize();
        }
        return size;
    }

    public Entry add(final Entry entry) {
        dir.add(entry);
        return this;
    }

    public Iterator<Entry> iterator() {
        return dir.iterator();
    }

    public void accept(final Visitor v) {
        v.visit(this);
    }
}
