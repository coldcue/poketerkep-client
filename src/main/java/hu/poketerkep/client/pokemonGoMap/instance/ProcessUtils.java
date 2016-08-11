package hu.poketerkep.client.pokemonGoMap.instance;


import java.lang.reflect.Field;

class ProcessUtils {
    static void killProcess(Process process) {
        if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
            // get the PID on unix/linux systems
            try {
                Field f = process.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                int pid = f.getInt(process);
                Runtime.getRuntime().exec("kill -KILL " + pid);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
