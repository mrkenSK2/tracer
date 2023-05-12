package tracer;

import java.lang.instrument.Instrumentation;

public class Agent {
    public static void premain(final String agentArgs, final Instrumentation inst) {
        Runtime.getRuntime().addShutdownHook(new Thread(Agent::onShutDown));

        inst.addTransformer(new Transformer());

        if (inst.isRetransformClassesSupported()) {
            for (final Class<?> clazz : inst.getAllLoadedClasses()) {
                try {
                    // exclude primitive/array types
                    if (!inst.isModifiableClass(clazz)) {
                        continue;
                    }
                    inst.retransformClasses(clazz);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void onLoadClass(final int classId, final String className) {
        System.err.printf("class,%d,%s\n", classId, className);
    }

    public static void onInstrumentMethod(final int classId, final int methodId, final String methodName) {
        System.err.printf("method,%d,%d,%s\n", methodId, classId, methodName);
    }

    public static void onEntry(final int methodId, final Object context, final Object[] arguments) {
        System.err.printf("enter,%d\n", methodId);
    }

    public static void onExit(final int methodId, final Object returnValue) {
        System.err.printf("exit,%d\n", methodId);
    }

    public static void onShutDown() {
        System.err.println("shutdown");
    }
}
