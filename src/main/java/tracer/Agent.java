package tracer;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Agent {
    private static class FuncInfo {
        int id;
        String name;
        FuncInfo(int id, String name) {
            this.id = id;
            this.name = name;
        }
        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof FuncInfo)) return false;
            FuncInfo funcinfo = (FuncInfo) obj;
            return funcinfo.id == this.id && funcinfo.name == this.name;
        }
        @Override
        public int hashCode() {
            return id + name.hashCode();
        } 
    }

    private static class CallRel {
        FuncInfo caller;
        FuncInfo callee;
        int count = 1;
        CallRel(FuncInfo caller, FuncInfo callee) {
            this.caller = caller;
            this.callee = callee;
        }
    }

    static int callerId = 0;
    static String callerName = "callRoot";
    static ArrayList<CallRel> callRelList = new ArrayList<>();
    static HashMap<String, Integer> call_count = new HashMap<>();

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

    public static void onEntry(final int methodId, final String methodName, final Object context, final Object[] arguments) {
        if (!call_count.containsKey(methodName)) {
            call_count.put(methodName, 1);
        } else {
            call_count.put(methodName, call_count.get(methodName) + 1);
        }
        FuncInfo caller = new FuncInfo(callerId, callerName);
        FuncInfo callee = new FuncInfo(methodId, methodName);
        if (callRelList == null || callRelList.size() == 0) {
            CallRel newCallRel = new CallRel(caller, callee);
            callRelList.add(newCallRel);
        } else {
            int index = -1;
            boolean flag = false;
            for (int i = 0; i < callRelList.size(); i++) {
                CallRel callrel = callRelList.get(i);
                if (callrel.caller.equals(caller) && callrel.callee.equals(callee)) {
                    flag = true;
                    index = i;
                    break;
                } 
            }
            if (flag) {
                callRelList.get(index).count += 1;
                flag = false;
            } else {
                CallRel newCallRel = new CallRel(caller, callee);
                callRelList.add(newCallRel);
            }
        }
        callerId = methodId;
        callerName = methodName;
        System.err.printf("enter,%d\n", methodId);
    }

    public static void onExit(final int methodId, final Object returnValue) {
        System.err.printf("exit,%d\n", methodId);
    }
    
    private static void createDot() {
        File dir = new File("build");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try{
            File file = new File("build/callGraph.dot");
            FileWriter filewriter = new FileWriter(file);
            filewriter.write("strict digraph G {\n");
            for (CallRel callRel : callRelList) {
                if (callRel.caller.name == "callRoot") continue;
                String content = "    \"" + callRel.caller.name + "\" -> \"" + callRel.callee.name + 
                "\" [label=" + callRel.count + "]\n";
                filewriter.write(content);
            }
            filewriter.write("}\n");
            filewriter.close();
        }catch(IOException e){
            System.out.println(e);
        }
    }

    public static void onShutDown() {
        for(String methodName: call_count.keySet()) System.err.printf("name: %s, called: %d\n", methodName, call_count.get(methodName));
        createDot();
        System.err.println("shutdown");
    }
}
