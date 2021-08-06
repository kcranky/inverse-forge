package inverse.inverse.eventhandlers;

import inverse.inverse.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
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
            LOGGER.debug("New player - moving to nether");
            respawnInNether(player);
        }
    }

    @SubscribeEvent
    public static void playerRespawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        LOGGER.debug("Player died - respawning in nether");
        Player player = event.getPlayer();
        ServerPlayer serverPlayer = (ServerPlayer) player;
        ServerLevel level = (ServerLevel) player.level;
        MinecraftServer minecraftServer = level.getServer();

        // check "player.java" for details on respawn mechanics
        if (serverPlayer.getRespawnDimension() == Level.OVERWORLD) {
            if (CommonConfig.SPAWN.allowBedRespawn.get()) { // if beds are allowed
                ServerLevel overworld = minecraftServer.getLevel(ServerLevel.OVERWORLD);
                BlockPos respawnBlock = serverPlayer.getRespawnPosition();
                serverPlayer.teleportTo(overworld, respawnBlock.getX(), respawnBlock.getY(), respawnBlock.getZ(), 0.0f, 0.0f);
            }
            else {
                Component component = (new TextComponent("The overworld rejects your presence.")).withStyle(ChatFormatting.RED);
                serverPlayer.sendMessage(component, serverPlayer.getUUID());
                respawnInNether(player);
            }
        }
        else if (serverPlayer.getRespawnDimension() == Level.NETHER) {
            if (CommonConfig.SPAWN.allowAnchorRespawn.get()){
                ServerLevel nether = minecraftServer.getLevel(ServerLevel.NETHER);
                BlockPos respawnBlock = serverPlayer.getRespawnPosition();
                BlockState blockstate = nether.getBlockState(respawnBlock);
                if (blockstate.getBlock() instanceof RespawnAnchorBlock && blockstate.getValue(RespawnAnchorBlock.CHARGE) > 0) {
                    serverPlayer.teleportTo(nether, respawnBlock.getX(), respawnBlock.getY(), respawnBlock.getZ(), 0.0f, 0.0f);
                }
            }
            else{
                respawnInNether(player);
            }

        }
        else {
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
                if (CommonConfig.SPAWN.useDeterminedSpawn.get()){
                    String[] spawnCoordsStr = CommonConfig.SPAWN.spawnCoordinates.get().split(",");
                    String[] spawnRotStr = CommonConfig.SPAWN.spawnRotation.get().split(",");
                    double x = Double.parseDouble(spawnCoordsStr[0]);
                    double y = Double.parseDouble(spawnCoordsStr[1]);
                    double z = Double.parseDouble(spawnCoordsStr[2]);
                    float xRot = Float.parseFloat(spawnRotStr[0]);
                    float yRot = Float.parseFloat(spawnRotStr[1]);
                    serverPlayer.teleportTo(nether, x, y, z, xRot, yRot);
                    LOGGER.debug("Teleported player to location specified in config");
                }
                else {
                    // "sea level" in the nether is at y=31
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
                                boolean canStand = !nether.getBlockState(standingBlock).isAir();
                                boolean isAir = nether.getBlockState(standingBlockLegs).isAir() && nether.getBlockState(standingBlockHead).isAir();
                                if (canStand && isAir) {
                                    serverPlayer.teleportTo(nether, xnew, ynew + 1, znew, (float)0.0, (float)0.0);
                                    LOGGER.debug("Teleported player to \"random\" nether location");
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
