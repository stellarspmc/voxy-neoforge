package me.cortex.voxy.commonImpl;

import me.cortex.voxy.common.config.Serialization;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod("voxy")
public class VoxyCommon {
    public static String MOD_VERSION = "";
    public static boolean IS_DEDICATED_SERVER = false;
    public static boolean IS_IN_MINECRAFT = false;

    public VoxyCommon(ModContainer container, IEventBus modBus) {
        IS_IN_MINECRAFT = true;

        String version = container.getModInfo().getVersion().toString();
        String commit = (String) container.getModInfo().getModProperties().getOrDefault("commit", "unknown000000");
        MOD_VERSION = version + "-" + (commit.length() >= 7 ? commit.substring(0, 7) : commit);

        IS_DEDICATED_SERVER = FMLEnvironment.dist == Dist.DEDICATED_SERVER;

        Serialization.init();
        // modBus.addListener(this::commonSetup);
    }

    //This is hardcoded like this because people do not understand what they are doing
    public static boolean isVerificationFlagOn(String name) {
        return isVerificationFlagOn(name, false);
    }

    public static boolean isVerificationFlagOn(String name, boolean defaultOn) {
        return System.getProperty("voxy."+name, Boolean.toString(defaultOn)).equals("true");
    }

    public interface IInstanceFactory {VoxyInstance create();}
    private static VoxyInstance INSTANCE;
    private static IInstanceFactory FACTORY = null;

    public static void setInstanceFactory(IInstanceFactory factory) {
        if (FACTORY != null) {
            throw new IllegalStateException("Cannot set instance factory more than once");
        }
        FACTORY = factory;
    }

    public static VoxyInstance getInstance() {
        return INSTANCE;
    }

    public static void shutdownInstance() {
        if (INSTANCE != null) {
            var instance = INSTANCE;
            INSTANCE = null;//Make it null before shutdown
            instance.shutdown();
        }
    }

    public static void createInstance() {
        if (FACTORY == null) {
            //Logger.info("Voxy factory");
            return;
        }
        if (INSTANCE != null) throw new IllegalStateException("Cannot create multiple instances");
        INSTANCE = FACTORY.create();
    }

    //Is voxy available in any capacity
    public static boolean isAvailable() {
        return FACTORY != null;
    }

    public static final boolean IS_MINE_IN_ABYSS = false;
}