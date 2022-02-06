package net.fuchsiamc.circaea.framework.util;

import java.lang.management.ManagementFactory;

public final class Debugger {

    /**
     * Checks if a debugger is attached by checking if the Java Debug Wire Protocol is active.
     * @return Whether the JDWP is active.
     */
    public static boolean isAttached() {
        return ManagementFactory.getRuntimeMXBean().getInputArguments().stream().anyMatch(arg -> arg.contains("jdwp="));
    }
}
