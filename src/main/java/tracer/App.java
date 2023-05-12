package tracer;

import javassist.*;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

public class App implements Callable<Integer> {
    @SuppressWarnings("unused")
    @Option(names = "--help", usageHelp = true)
    boolean helpRequested;

    @SuppressWarnings("unused")
    @Option(names = "--version", versionHelp = true)
    boolean versionInfoRequested;

    @Parameters(index = "0", paramLabel = "<file>", description = "source class file")
    Path source;

    @Parameters(index = "1", paramLabel = "<dir>", description = "target directory to save")
    Path target;

    @Override
    public Integer call() throws IOException, CannotCompileException {
        final ClassPool pool = ClassPool.getDefault();
        final CtClass cc = pool.makeClass(new FileInputStream(source.toString()));
        for (final CtMethod cm : cc.getDeclaredMethods()) {
            cm.insertBefore("System.err.println(\"" + cm.getName() + "\");");
            System.out.println("Inserted to " + cm.getName());
        }
        cc.writeFile(target.toString());
        return 0;
    }

    public static void main(final String[] args) {
        final App app = new App();
        final CommandLine cmdline = new CommandLine(app);
        cmdline.setExpandAtFiles(false);
        final int status = cmdline.execute(args);
        if (status != 0) {
            System.exit(status);
        }
    }
}
