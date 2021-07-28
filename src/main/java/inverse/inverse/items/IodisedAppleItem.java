package inverse.inverse.items;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class IodisedAppleItem extends Foods {

    public IodisedAppleItem(FoodProperties properties) {
        // set to be the same as a regular apple
        (new FoodProperties.Builder()).nutrition(4).saturationMod(0.3F).build();
    }

//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
//        tooltip.add(ITextComponent.getTextComponentOrEmpty("A rather crunchy apple..."));
//        tooltip.add(ITextComponent.getTextComponentOrEmpty("Or crapple, if you will"));
//        super.addInformation(stack, worldIn, tooltip, flagIn);
//    }

//    @OnlyIn(Dist.CLIENT)
//    public boolean hasEffect(FoodProperties item){
//        return true; // give it an enchant glow
//    }
}
