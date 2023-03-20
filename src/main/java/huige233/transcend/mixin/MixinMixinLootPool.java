package huige233.transcend.mixin;

import huige233.transcend.mixinitf.IMixinLootPoolFieldGetter;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(value = LootPool.class)
public class MixinMixinLootPool implements IMixinLootPoolFieldGetter {
    @Final
    @Shadow
    private List<LootEntry> lootEntries;
    @Shadow
    @Final
    private List<LootCondition> poolConditions;

    @Shadow
    @Final
    private String name;

    @Shadow
    private RandomValueRange rolls;

    @Shadow
    private RandomValueRange bonusRolls;

    @Override
    public List<LootEntry> getEntries() {
        return lootEntries;
    }

    @Override
    public List<LootCondition> getConditions() {
        return poolConditions;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public RandomValueRange getRolls() {
        return rolls;
    }

    @Override
    public RandomValueRange getBonusRolls() {
        return bonusRolls;
    }

}

