package inverse.inverse.config;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public final class CommonConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final Poison POISON = new Poison(BUILDER);
    public static final Spawn SPAWN = new Spawn(BUILDER);
    public static final ForgeConfigSpec spec = BUILDER.build();

    private static boolean loaded = false;
    private static List<Runnable> loadActions = new ArrayList<>();

    public static void setLoaded() {
        if (!loaded)
            loadActions.forEach(Runnable::run);
        loaded = true;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static void onLoad(Runnable action) {
        if (loaded)
            action.run();
        else
            loadActions.add(action);
    }

    public static class Poison {
        public final ForgeConfigSpec.ConfigValue<Integer> invigorationFrequency;
        public final ForgeConfigSpec.ConfigValue<Integer> appleInvigoration;
        public final ForgeConfigSpec.ConfigValue<Integer> maxToxication;
        public final ForgeConfigSpec.ConfigValue<Integer> poisonFrequency;
        public final ForgeConfigSpec.ConfigValue<Integer> poisonMultiplier;
        public final ForgeConfigSpec.ConfigValue<Integer> poisonDuration;
        public final ForgeConfigSpec.ConfigValue<Integer> maxHealthPenalty;

        public Poison(ForgeConfigSpec.Builder builder) {
            builder.push("Inverse Poison Config");

            invigorationFrequency = builder
                    .comment("How often (in seconds) the player should regenerate toxication in the End/Nether")
                    .defineInRange("invigorationFrequency", 360, 0, Integer.MAX_VALUE);

            appleInvigoration = builder
                    .comment("How much health points [0..19] should be recovered upon eating an iodised apple")
                    .defineInRange("appleInvigoration", 2, 0, 19);

            maxToxication = builder
                    .comment("Maximum toxication, bigger means slower healthy penalty effects (both positive and negative)")
                    .defineInRange("maxToxication", 160, 0, Integer.MAX_VALUE);

            poisonFrequency = builder
                    .comment("How often (in seconds) the player should take poison damage in the Overworld")
                    .defineInRange("poisonFrequency", 60, 1, Integer.MAX_VALUE);

            poisonMultiplier = builder
                    .comment("The \"level\" (eg, 1, 2) of poison damage. In game Poison has a multiplier of 0. Poison II has a multiplier of 1, etc")
                    .defineInRange("poisonMultiplier", 0, 0, 5);

            poisonDuration  = builder
                    .comment("How long (in seconds) the poison effect lasts")
                    .defineInRange("poisonDuration", 15, 0, Integer.MAX_VALUE);

            maxHealthPenalty = builder
                    .comment("Maximum hit points [0..19] that the mod should remove. 2HP = 1 heart")
                    .defineInRange("maxHealthPenalty", 16, 0, 19);

            builder.pop();
        }
    }

    public static class Spawn {

        public final ForgeConfigSpec.ConfigValue<Boolean> spawnInNether;
        public final ForgeConfigSpec.ConfigValue<Boolean> useDeterminedSpawn;
        public final ForgeConfigSpec.ConfigValue<String> spawnCoordinates;
        public final ForgeConfigSpec.ConfigValue<String> spawnRotation;

        public Spawn(ForgeConfigSpec.Builder builder) {
            builder.push("Inverse Spawn Config");

            spawnInNether = builder
                    .comment("Should the player spawn in the nether and respawn there if they don't have a bed set?")
                    .define("spawnInNether", true);

            useDeterminedSpawn = builder
                    .comment("Set to true if you want the nether spawn to be a particular set of coordinates as defined by \"spawnCoordinates\" and \"spawnRotation\"")
                    .define("usedDeterminedSpawn", false);

            spawnCoordinates = builder
                    .comment("X, Y, Z")
                    .define("spawnCoordinates", "0.0, 64.0, 0.0");

            spawnRotation = builder
                    .comment("XRot, YRot")
                    .define("spawnRotation", "0.0, 0.0");

            builder.pop();
            }
        }


}
