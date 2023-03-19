package huige233.transcend.util.handlers;

import huige233.transcend.mixinitf.ILootPoolFieldGetter;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber()
public abstract class LootEvent {
    private static final HashMap<String, Object> reversedLootPool = new HashMap<>();

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        LootTable table = event.getTable();
        ArrayList<?> lootPools = new ArrayList<>();
        lootPools.forEach(pool -> {
            ILootPoolFieldGetter fg = ((ILootPoolFieldGetter) (pool));
            if (reversedLootPool.containsKey(fg.getName())) {
                reversedLootPool.put(fg.getName(), reverseLootPool(fg));
            }
        });
    }

    public static Object get(String name) {
        return reversedLootPool.get(name);
    }


    public static Object reverseLootPool(ILootPoolFieldGetter pool) {
        List<LootEntry> lootEntries = ((ILootPoolFieldGetter) pool).getEntries();
        ArrayList<Integer> weights = new ArrayList<>();
        lootEntries.forEach(lootEntry -> {
            if (weights.contains(lootEntry.weight)) weights.add(lootEntry.weight);
        });
        List<LootEntry> reversedEntries = new ArrayList<>();
        lootEntries.forEach(lootEntry -> {
            reversedEntries.add(reversedEntry(lootEntry, weights));
        });
        return new DataContainer(reversedEntries.toArray(new LootEntry[]{}), ((ILootPoolFieldGetter) pool).getConditions().toArray(new LootCondition[]{}), pool.getRolls(), pool.getBonusRolls(), pool.getName());
    }

    private static LootEntry reversedEntry(LootEntry entry, ArrayList<Integer> weights) {
        if (entry instanceof LootEntryItem) {
            LootEntryItem item = (LootEntryItem) entry;
            int weight = weights.get(weights.size() - weights.indexOf(item.weight) - 1);
            return new LootEntryItem(item.item, weight, item.quality, item.functions, item.conditions, item.getEntryName());
        } else if (entry instanceof LootEntryTable) {
            LootEntryTable table = (LootEntryTable) entry;
            int weight = weights.get(weights.size() - weights.indexOf(table.weight) - 1);
            return new LootEntryTable(table.table, weight, table.quality, table.conditions, table.getEntryName());
        }
        return null;
    }

    private static class DataContainer {
        private final LootCondition[] lootConditions;
        private final LootEntry[] lootEntries;
        private final String name;
        private final RandomValueRange bonusRolls;
        private final RandomValueRange rolls;

        public DataContainer(LootEntry[] lootEntries, LootCondition[] lootConditions, RandomValueRange rolls, RandomValueRange bonusRolls, String name) {
            this.lootEntries = lootEntries;
            this.lootConditions = lootConditions;
            this.rolls = rolls;
            this.bonusRolls = bonusRolls;
            this.name = name;
        }
    }
}
