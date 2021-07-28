package inverse.inverse.capabilities;

public interface IOverworldPoisonManager {

    long getTicksInDimension();

    void incrementTicksInDimension();

    void setTicksInDimension(long value);

    long getToxication();

    void incrementToxication();

    void decrementToxication();

    void setToxication(long value);

    int getHealthPenalty();

    void eatApple();

    void transferDataFrom(IOverworldPoisonManager other);
}
