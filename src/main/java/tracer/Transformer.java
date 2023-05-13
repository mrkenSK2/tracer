package tracer;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;

public class Transformer implements ClassFileTransformer {
    private int classIdCounter = 0;
    private int methodIdCounter = 0;

    private final Set<ClassLoader> classLoaders = new HashSet<>();

    private final static String[] EXCLUDED = new String[] {"java.", "javax.", "sun.", "com.sun.", "apple.", "com.apple.", "tracer."};

    public byte[] transform(final ClassLoader loader, String className, final Class<?> classBeingRedefined,
                            final ProtectionDomain protectionDomain, final byte[] classfileBuffer) {
        final int classId = ++classIdCounter;
        className = className.replace('/', '.');
        Agent.onLoadClass(classId, className);

        if (Stream.of(EXCLUDED).anyMatch(className::startsWith)) {
            return null;
        }

        try {
            // Generate compile-time class
            final ClassPool pool = ClassPool.getDefault();
            if (loader != null && !classLoaders.contains(loader)) {
                classLoaders.add(loader);
                pool.appendClassPath(new LoaderClassPath(loader));
            }
            final CtClass clazz = pool.makeClass(new ByteArrayInputStream(classfileBuffer));

            // rewrite methods
            boolean touched = false;
            for (final CtBehavior behavior : clazz.getDeclaredBehaviors()) {
                if (!behavior.isEmpty() && !Modifier.isNative(behavior.getModifiers())) {
                    final int methodId = ++methodIdCounter;
                    Agent.onInstrumentMethod(classId, methodId, behavior.getLongName());

                    final String recv = ((behavior instanceof CtMethod) && !Modifier.isStatic(behavior.getModifiers())) ? "$0" : "null";
                    behavior.insertBefore(String.format("tracer.Agent.onEntry(%d, \"%s\", %s, $args);", methodId, behavior.getLongName(), recv));
                    behavior.insertAfter(String.format("tracer.Agent.onExit(%d, ($w)$_);", methodId));
                    touched = true;
                }
            }
            return touched ? clazz.toBytecode() : null;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
