package huige233.transcend.mixinitf;

import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;

import java.util.List;

public interface ILootPoolFieldGetter {
    List<LootEntry> getEntries();

    List<LootCondition> getConditions();

    String getName();

    RandomValueRange getRolls();

    RandomValueRange getBonusRolls();
}
