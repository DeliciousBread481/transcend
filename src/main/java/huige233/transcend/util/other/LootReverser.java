package huige233.transcend.util.other;

import com.google.common.collect.Lists;
import huige233.transcend.mixinitf.IMixinLootPoolFieldGetter;
import huige233.transcend.mixinitf.IMixinLootTableFieldGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public abstract class LootReverser {
    private static final HashMap<LootTable, LootTable> cachedReversedTables = new HashMap<>();

    private static LootTable reversedLootTable(LootTable table) {
        ArrayList<LootPool> reversedLootPools = new ArrayList<>();

        Map<String, LootPool> poolMap = new HashMap<>();
        List<Pair<String, RandomValueRange>> sortedRoll = new ArrayList<>();
        List<Pair<String, RandomValueRange>> sortedBoundsRoll = new ArrayList<>();
//        (IMixinLootTableFieldGetter) table).getLootPool().stream()
//                .map(pool11 -> Pair.of(pool11.getName(), pool11))
//                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        for (LootPool lootPool : ((IMixinLootTableFieldGetter) table).getLootPool()) {
            poolMap.put(lootPool.getName(), lootPool);
            RandomValueRange rolls = lootPool.getRolls();
            RandomValueRange bonusRolls = lootPool.getBonusRolls();
            sortedRoll.add(Pair.of(lootPool.getName(), rolls));
            sortedBoundsRoll.add(Pair.of(lootPool.getName(), bonusRolls));
        }
        sortedRoll.sort(Comparator.comparingDouble(randomValueRange -> (randomValueRange.getValue().getMax() + randomValueRange.getValue().getMin()) / 2.0));
        sortedBoundsRoll.sort(Comparator.comparingDouble(randomValueRange -> (randomValueRange.getValue().getMax() + randomValueRange.getValue().getMin()) / 2.0));
//        ((IMixinLootTableFieldGetter) table).getLootPool()
//                .stream()
//                .map(pool11 -> Pair.of(pool11.getName(), pool11))
//                .map(pool -> Pair.of(pool.getKey(), pool.getValue().getRolls()))
//                .sorted(Comparator.comparingDouble(randomValueRange -> (randomValueRange.getValue().getMax() + randomValueRange.getValue().getMin()) / 2.0))
//                .collect(Collectors.toList());
//        ((IMixinLootTableFieldGetter) table).getLootPool()
//                .stream()
//                .map(pool1 -> Pair.of(pool1.getName(), pool1))
//                .map(pool -> Pair.of(pool.getKey(), pool.getValue().getBonusRolls()))
//                .sorted(Comparator.comparingDouble(randomValueRange -> (randomValueRange.getValue().getMax() + randomValueRange.getValue().getMin()) / 2.0)).collect(Collectors.toList());

        for (String name : poolMap.keySet()) {
            IMixinLootPoolFieldGetter fieldGetter = (IMixinLootPoolFieldGetter) poolMap.get(name);

            RandomValueRange reversedRoll = sortedRoll.get(sortedRoll.size() - 2 - sortedRoll.indexOf(name)).getValue();
            RandomValueRange reversedBoundsRoll = sortedBoundsRoll.get(sortedBoundsRoll.size() - 2 - sortedBoundsRoll.indexOf(name)).getValue();

            reversedLootPools.add(new LootPool(reversedLootEntries(fieldGetter.getEntries()), fieldGetter.getConditions().toArray(new LootCondition[0]), reversedRoll, reversedBoundsRoll, name));
        }

        return new LootTable(reversedLootPools.toArray(new LootPool[0]));
    }

    private static LootEntry[] reversedLootEntries(List<LootEntry> lootEntries) {
        ArrayList<LootEntry> processed = new ArrayList<>();
//        lootEntries.stream().filter(LootEntryTable.class::isInstance).collect(Collectors.toList())
//        List<Integer> weights = lootEntries.stream()
//                .filter(entry -> !(processed.contains(entry) && entry instanceof LootEntryEmpty))
//                .map(entry -> ((LootEntryItem) entry))
//                .mapToInt(stringLootEntryPair -> stringLootEntryPair.weight)
//                .collect()
        for (LootEntry lootEntry : lootEntries) {
            if (lootEntry instanceof LootEntryTable) {
                processed.add(lootEntry);
            }
        }
        HashSet<Integer> cache = new HashSet<>();
        for (LootEntry lootEntry : lootEntries) {
            if (!(processed.contains(lootEntry) || lootEntry instanceof LootEntryEmpty)) {
                cache.add(((LootEntryItem) lootEntry).weight);
            }
        }
        ArrayList<Integer> weights = new ArrayList<>();
        for (Integer integer : cache) {
            weights.add(integer);
        }
        weights.sort(Comparator.comparingInt(Integer::intValue));
        for (int weight : weights) {
//            lootEntries.stream().filter(entry -> !(processed.contains(entry) && entry instanceof LootEntryEmpty))
//                    .map(entry -> ((LootEntryItem) entry))
//                    .filter(stringLootEntryPair -> stringLootEntryPair.weight == weight && !stringLootEntryPair.item.equals(ItemStack.EMPTY.getItem()))
//                    .forEach(pair -> processed.add(new LootEntryItem(
//                    pair.item,
//                    weights.get(weights.size() - 1 - weights.indexOf(weight)),
//                    pair.quality,
//                    pair.functions,
//                    pair.conditions,
//                    pair.getEntryName()
//            )));

            for (LootEntry lootEntry : lootEntries) {
                if (!(processed.contains(lootEntry) || lootEntry instanceof LootEntryEmpty)) {
                    LootEntryItem lootEntryItem = (LootEntryItem) lootEntry;
                    if (lootEntryItem.weight == weight && !lootEntryItem.item.equals(ItemStack.EMPTY.getItem())) {
                        processed.add(new LootEntryItem(
                                lootEntryItem.item,
                                weights.get(weights.size() - 1 - weights.indexOf(weight)),
                                lootEntryItem.quality,
                                lootEntryItem.functions,
                                lootEntryItem.conditions,
                                lootEntryItem.getEntryName()
                        ));
                    }
                }
            }

        }
        return processed.toArray(new LootEntry[0]);
    }

    public static List<ItemStack> getReversedTableAndGenerate(LootTable table, Random pGenerateLootForPools1, LootContext context) {
        return generate(pGenerateLootForPools1, context, cachedReversedTables.computeIfAbsent(table, LootReverser::reversedLootTable));

    }

    public static List<ItemStack> generate(Random rand, LootContext context, LootTable table) {
        List<ItemStack> list = Lists.<ItemStack>newArrayList();

        if (context.addLootTable(table)) {
            for (LootPool lootpool : ((IMixinLootTableFieldGetter) table).getLootPool()) {
                lootpool.generateLoot(list, rand, context);
            }

            context.removeLootTable(table);
        } else {
//            LOGGER.warn("Detected infinite loop in loot tables");
        }

        return list;
    }

}
