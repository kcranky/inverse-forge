package inverse.inverse.capabilities;

import inverse.inverse.config.CommonConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class OverworldPoisonManager implements IOverworldPoisonManager, ICapabilityProvider, INBTSerializable<CompoundTag> {

    public final LazyOptional<IOverworldPoisonManager> holder = LazyOptional.of(() -> this);

    /**
     * The maximum health penalty. Currently represented in hit points
     */
    private static final long MAX_HEALTH_PENALTY = CommonConfig.POISON.maxHealthPenalty.get();

    long ticksInDimension;
    long toxication;

    @Override
    public long getTicksInDimension() {
        return ticksInDimension;
    }

    @Override
    public void setTicksInDimension(long value) {
        ticksInDimension = value;
    }

    @Override
    public void incrementTicksInDimension() {
        ticksInDimension += 1;
    }

    @Override
    public long getToxication() {
        return toxication;
    }

    @Override
    public void incrementToxication() {
        setToxication(toxication + 1);
    }

    @Override
    public void setToxication(long value) {
        toxication = Math.min(Math.max(0, value), CommonConfig.POISON.maxToxication.get());
    }

    @Override
    public void decrementToxication() {
        setToxication(toxication - 1);
    }

    private int getToxicationPerLostHP(){
        return Math.floorDiv(CommonConfig.POISON.maxToxication.get(), (int)MAX_HEALTH_PENALTY);
    }

    /**
     * This value is calculated directly from the {@link #toxication} value. The {@link #toxication} increases in the
     * Overworld and decreases (more slowly) in the other dimensions.
     */
    @Override
    public int getHealthPenalty() {
        if (getToxicationPerLostHP() > 0) {
            return Math.floorDiv((int)toxication, getToxicationPerLostHP());
        }
        return 0;
    }

    @Override
    public void eatApple(){
        this.toxication = Math.max(0, toxication - (long) getToxicationPerLostHP() * CommonConfig.POISON.appleInvigoration.get());
    }

    @Override
    public void transferDataFrom(IOverworldPoisonManager other) {
        this.ticksInDimension = other.getTicksInDimension();
        this.toxication = other.getToxication();
    }

    @Override
    public String toString() {
        return "OverworldPoisonHandler{" +
                "ticksInDimension=" + ticksInDimension +
                ", toxication=" + toxication +
                '}';
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == OverworldPoisonCapability.INSTANCE){ return OverworldPoisonCapability.INSTANCE.orEmpty(cap, this.holder); }
        else return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("ticksInDimension", ticksInDimension);
        tag.putLong("toxication", toxication);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ticksInDimension = nbt.getLong("ticksInDimension");
        toxication = nbt.getLong("toxication");
    }
}
