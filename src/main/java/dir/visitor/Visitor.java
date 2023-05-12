package dir.visitor;

import dir.node.Directory;
import dir.node.File;
import dir.node.Link;

public abstract class Visitor {
    public abstract void visit(final File file);

    public abstract void visit(final Directory directory);

    public abstract void visit(final Link link);
}
