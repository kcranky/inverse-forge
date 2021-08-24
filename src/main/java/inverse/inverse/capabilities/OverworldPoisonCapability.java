package inverse.inverse.capabilities;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class OverworldPoisonCapability {
    @CapabilityInject(IOverworldPoisonManager.class)
    public static Capability<IOverworldPoisonManager> INSTANCE = null;

//    public static void register(){
//        CapabilityManager.INSTANCE.register(IOverworldPoisonManager.class);
////        RegisterCapabilitiesEvent
//    }
    public static void register(RegisterCapabilitiesEvent event){
        event.register(IOverworldPoisonManager.class);
    }
}

