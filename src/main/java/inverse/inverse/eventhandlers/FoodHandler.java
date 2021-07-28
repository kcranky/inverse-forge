package inverse.inverse.eventhandlers;

import inverse.inverse.capabilities.OverworldPoisonCapability;
import inverse.inverse.init.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod.EventBusSubscriber
public class FoodHandler {
    private static final Logger LOGGER = LogManager.getLogger(OverworldDamageHandler.class);

    @SubscribeEvent
    public static void getAppleCore(LivingEntityUseItemEvent.Finish event) {
        Level level = event.getEntityLiving().getCommandSenderWorld();

        if (!level.isClientSide()) {
            List<? extends Player> players = level.players();

            players.forEach(player -> {
                player.getCapability(OverworldPoisonCapability.INSTANCE).ifPresent(owph -> {
                    if (event.getItem().getItem() == ModItems.IODISED_APPLE_ITEM.get()) {
                        LOGGER.debug("Player eats an iodised apple");
                        owph.eatApple();
                    }
                });
            });
        }

    }

}

