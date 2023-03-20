package huige233.transcend.mixinitf;

import net.minecraft.world.storage.loot.LootPool;

import java.util.List;

public interface IMixinLootTableFieldGetter {
    List<LootPool> getLootPool();
}
