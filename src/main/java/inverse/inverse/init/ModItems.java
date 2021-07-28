package inverse.inverse.init;

import inverse.inverse.InverseForge;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, InverseForge.MOD_ID);

    public static final RegistryObject<Item> IODISED_APPLE_ITEM = ITEMS.register("iodisedappleitem",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().alwaysEat().nutrition(4).saturationMod(0.3F).build()).tab(CreativeModeTab.TAB_FOOD)));
}
