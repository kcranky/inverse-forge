package inverse.inverse.eventhandlers;

import inverse.inverse.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        respawnInNether(player);
    }

    // Helper for spawning into the nether
    public static void respawnInNether(Player player){
        ServerPlayer serverPlayer = (ServerPlayer) player;

        if (player.level instanceof ServerLevel) {
            ServerLevel overworld = (ServerLevel) player.level;
            MinecraftServer minecraftServer = overworld.getServer();
            ServerLevel nether = minecraftServer.getLevel(ServerLevel.NETHER);



            if (nether != null && minecraftServer.isNetherEnabled() && CommonConfig.SPAWN.spawnInNether.get()) {
                LOGGER.info(CommonConfig.SPAWN.useDeterminedSpawn.get());
                if (CommonConfig.SPAWN.useDeterminedSpawn.get().equals(true)){
                    String[] spawnCoordsStr = CommonConfig.SPAWN.spawnCoordinates.get().split(",");
                    double x = Double.parseDouble(spawnCoordsStr[0]);
                    double y = Double.parseDouble(spawnCoordsStr[1]);
                    double z = Double.parseDouble(spawnCoordsStr[2]);

                    String[] spawnRotStr = CommonConfig.SPAWN.spawnRotation.get().split(",");
                    float xRot = Float.parseFloat(spawnRotStr[0]);
                    float yRot = Float.parseFloat(spawnRotStr[1]);
                    serverPlayer.teleportTo(nether, x, y, z, xRot, yRot);
                }
                else {
                    // "sea level" in the nether is at y=31
                    // Start at 0, 31, 0.
                    BlockPos.MutableBlockPos standingBlock = new BlockPos.MutableBlockPos();
                    BlockPos.MutableBlockPos standingBlockLegs = new BlockPos.MutableBlockPos();
                    BlockPos.MutableBlockPos standingBlockHead = new BlockPos.MutableBlockPos();

                    BlockPos someBlock = new BlockPos(0,0,0);

                    findsafe:
                    for (int ynew = 32; ynew < 119; ++ynew) { // 121 is where bedrock starts forming
                        for (int xnew = -8; xnew < 9; ++xnew) {
                            for (int znew = -8; znew < 9; ++znew) {
                                standingBlock.set(xnew, ynew, znew);
                                standingBlockLegs.set(xnew, ynew + 1, znew);
                                standingBlockHead.set(xnew, ynew + 2, znew);

                                // TODO Keegan, yoh man. (BlockState has isValidSpawn method)

                                boolean canStand = !nether.getBlockState(standingBlock).isAir();
                                boolean isAir = nether.getBlockState(standingBlockLegs).isAir() && nether.getBlockState(standingBlockHead).isAir();
                                if (canStand && isAir) {
                                    LOGGER.info("foundsafe");
                                    serverPlayer.teleportTo(nether, xnew, ynew + 1, znew, (float)0.0, (float)0.0);
                                    LOGGER.info(xnew);
                                    LOGGER.info(ynew + 1);
                                    LOGGER.info(znew);
                                    break findsafe;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
