package inverse.inverse.eventhandlers;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Stats;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import inverse.inverse.config.CommonConfig;

import java.util.Optional;

@Mod.EventBusSubscriber
public class NetherSpawn {

    private static final Logger LOGGER = LogManager.getLogger(OverworldDamageHandler.class);

    @SubscribeEvent
    public static void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event){
        Player player = event.getPlayer();
        ServerPlayer serverPlayer = (ServerPlayer) player;

        int timePlayed = serverPlayer.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));

        // Teleport to Nether if first time joining world
        if (timePlayed < 1 && CommonConfig.SPAWN.spawnInNether.get()) {
            respawnInNether(player);
        }
    }

    @SubscribeEvent
    public static void playerRespawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        ServerPlayer serverPlayer = (ServerPlayer) player;

        BlockPos respawnPosition = serverPlayer.getRespawnPosition();

        if (respawnPosition != null) {
            respawnInNether(player);
        }
    }

    // Helper for spawning into the nether
    public static void respawnInNether(Player player){
        ServerPlayer serverPlayer = (ServerPlayer) player;

        if (player.level instanceof ServerLevel) {
            ServerLevel overworld = (ServerLevel) player.level;
            MinecraftServer minecraftServer = overworld.getServer();
            ServerLevel nether = minecraftServer.getLevel(ServerLevel.NETHER);

            if (nether != null && minecraftServer.isNetherEnabled() && CommonConfig.SPAWN.spawnInNether.get()) {
                // "sea level" in the nether is at y=31
                // Start at 0, 31, 0.
                BlockPos.MutableBlockPos standingBlock = new BlockPos.MutableBlockPos();
                BlockPos.MutableBlockPos standingBlockLegs = new BlockPos.MutableBlockPos();
                BlockPos.MutableBlockPos standingBlockHead = new BlockPos.MutableBlockPos();

                findsafe:
                for (int ynew = 32; ynew < 119; ++ynew) { // 121 is where bedrock starts forming
                    for (int xnew = -8; xnew < 9; ++xnew) {
                        for (int znew = -8; znew < 9; ++znew) {
                            standingBlock.set(xnew, ynew, znew);
                            standingBlockLegs.set(xnew, ynew + 1, znew);
                            standingBlockHead.set(xnew, ynew + 2, znew);

                            // TODO Keegan, yoh man. (BlockState has isValidSpawn method)
                            boolean canStand = nether.getBlockState(standingBlock).isAir();
                            boolean isAir = nether.getBlockState(standingBlockLegs).isAir() && nether.getBlockState(standingBlockHead).isAir();
                            if (canStand && isAir) {
                                serverPlayer.teleportTo(nether, xnew, ynew + 1, znew, player.getYRot(), player.getXRot());
                                LOGGER.debug(xnew);
                                LOGGER.debug(ynew + 1);
                                LOGGER.debug(znew);
                                break findsafe;
                            }
                        }
                    }
                }

            }
        }
    }
}
