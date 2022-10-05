package huige233.transcend.util.handlers;

public interface EnergeticHandler {
    default EnergyType getEnergyType() {
        return EnergyType.FORGE_ENERGY;
    }

    enum EnergyType {
        FORGE_ENERGY, EU
    }
}
