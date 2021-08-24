package inverse.inverse.eventhandlers;

import inverse.inverse.capabilities.OverworldPoisonCapability;
import inverse.inverse.config.CommonConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod.EventBusSubscriber
public class OverworldDamageHandler {
    private static final Logger LOGGER = LogManager.getLogger(OverworldDamageHandler.class);

    /**
     * This is purely used for debugging on player interaction.
     */
    @SubscribeEvent
    public static void playerRightClick(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Level world = player.level;
        if (!world.isClientSide) {
            player.getCapability(OverworldPoisonCapability.INSTANCE).ifPresent(owph -> {
                LOGGER.debug("Inverse mod debug: {}", owph);
            });
        }
    }

    @SubscribeEvent
    public static void playerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getPlayer();
        final Level world = player.level;
        if (!world.isClientSide) {
            player.getCapability(OverworldPoisonCapability.INSTANCE).ifPresent(owph -> {
                owph.setTicksInDimension(0);
            });
        }
    }

    @SubscribeEvent
    public static void playerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player player = event.getPlayer();
        original.reviveCaps();
        original.getCapability(OverworldPoisonCapability.INSTANCE).ifPresent(owph -> {
            LOGGER.debug("Trying to move capability data to player clone...");
            player.getCapability(OverworldPoisonCapability.INSTANCE).ifPresent(newOwph -> {
                newOwph.transferDataFrom(owph);
                LOGGER.debug("Successfully moved capability data to player clone");
            });
        });
        original.invalidateCaps();
    }

    @SubscribeEvent
    public static void handleOverworldPoisonUpdate(TickEvent.WorldTickEvent event) {
        Level world = event.world;
        if (!world.isClientSide && event.phase == TickEvent.Phase.START) {

            List<? extends Player> players = world.players();

            int duration = CommonConfig.POISON.poisonDuration.get() * 20;
            long poisonFrequency = CommonConfig.POISON.poisonFrequency.get() * 20;
            int amplifier = CommonConfig.POISON.poisonMultiplier.get();
            long invigorateFrequency = CommonConfig.POISON.invigorationFrequency.get() * 20;

            players.forEach(player -> {
                player.getCapability(OverworldPoisonCapability.INSTANCE).ifPresent(owph -> {
                    owph.incrementTicksInDimension();
                    float newPenalty = owph.getHealthPenalty();
                    player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(Math.min(20, 20-newPenalty));

                    if (player.level.dimension().equals(Level.OVERWORLD)) {
                        if (owph.getTicksInDimension() > poisonFrequency) {
                            LOGGER.debug("Applying poison effect");
                            player.addEffect(new MobEffectInstance(MobEffects.POISON, duration, amplifier));
                            owph.incrementToxication();
                            owph.setTicksInDimension(0);
                        }
                    } else {
                        if (owph.getTicksInDimension() > invigorateFrequency) {
                            LOGGER.debug("Player feels slightly invigorated by the dark energies in their surroundings");
                            owph.decrementToxication();
                            owph.setTicksInDimension(0);
                        }
                    }
                });
            });
        }
    }
}
