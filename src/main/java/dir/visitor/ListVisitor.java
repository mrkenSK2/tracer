package dir.visitor;
import dir.node.Directory;
import dir.node.Entry;
import dir.node.File;
import dir.node.Link;

import java.util.Iterator;

public class ListVisitor extends Visitor {
    private String currentdir = "";

    public void visit(final File file) {
        System.out.println(currentdir + "/" + file);
    }

    public void visit(final Directory directory) {
        System.out.println(currentdir + "/" + directory);
        final String savedir = currentdir;
        currentdir += "/" + directory.getName();
        final Iterator<Entry> it = directory.iterator();
        while (it.hasNext()) {
            final Entry entry = it.next();
            entry.accept(this);
        }
        currentdir = savedir;
    }
	public void visit(final Link link) {
		System.out.println(currentdir + "/" + link.getName() + "@" + link);
	}
}
