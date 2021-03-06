package inverse.inverse;

import inverse.inverse.capabilities.OverworldPoisonCapability;
import inverse.inverse.capabilities.OverworldPoisonManager;
import inverse.inverse.config.CommonConfig;
import inverse.inverse.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmlserverevents.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("inverseforge")
public class InverseForge {

    public static final String MOD_ID = "inverseforge";

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger(InverseForge.class);

    public InverseForge() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register the enqueueIMC method for modloading
        modEventBus.addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        modEventBus.addListener(this::processIMC);
        // Add any capabilities
        modEventBus.addListener(this::registerCapabilities);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Add the config
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModConfigEvent);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.spec);

        // Register custom items
        ModItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());

        // From PAM's HC 2
        // Also, see https://forums.minecraftforge.net/topic/96761-1165-properly-using-distexecutor-with-arguments/
        DistExecutor.unsafeRunForDist(() -> () -> new SideProxy.Client(), () -> () -> new SideProxy.Server());

    }

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        OverworldPoisonCapability.register(event);
    }



    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("inverseforge", "helloworld", () -> {
            LOGGER.info("Hello world from the MDK");
            return "Hello world";
        });
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m -> m.messageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
//        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
//            LOGGER.debug("HELLO from Register Block");
        }
    }

    @SubscribeEvent
    public void attachCapabilitiesEntities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            Player player = (Player) event.getObject();
            Level world = player.level;
            if (!world.isClientSide()) {
                LOGGER.info("Attaching OverworldPoisonProvider");
                event.addCapability(new ResourceLocation("inverseforge", "overworldpoison"), new OverworldPoisonManager());
            }
        }
    }

    private void onModConfigEvent(final FMLLoadCompleteEvent event) {
        if (event.equals(ModConfig.Type.COMMON)) {
            CommonConfig.setLoaded();
        }
    }
}