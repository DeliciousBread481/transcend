package huige233.transcend.util.other;

import huige233.transcend.mixinitf.ILootPoolFieldGetter;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class LootReverser {
    private static final HashMap<LootTable, LootTable> cachedReversedTables = new HashMap<>();

    private static LootTable reversedLootTable(LootTable table) {
        ArrayList<LootPool> reversedLootPolls = new ArrayList<>();
        Stream<Pair<String, LootPool>> stream = table.pools.stream().map(pool -> Pair.of(pool.getName(), pool));

        Map<String, LootPool> poolMap = stream.collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        List<Pair<String, RandomValueRange>> sortedRoll = stream.map(pool -> Pair.of(pool.getKey(), pool.getValue().getRolls())).sorted(Comparator.comparingDouble(randomValueRange -> (randomValueRange.getValue().getMax() + randomValueRange.getValue().getMin()) / 2.0)).collect(Collectors.toList());
        List<Pair<String, RandomValueRange>> sortedBoundsRoll = stream.map(pool -> Pair.of(pool.getKey(), pool.getValue().getBonusRolls())).sorted(Comparator.comparingDouble(randomValueRange -> (randomValueRange.getValue().getMax() + randomValueRange.getValue().getMin()) / 2.0)).collect(Collectors.toList());

        for (String name : poolMap.keySet()) {
            ILootPoolFieldGetter fieldGetter = (ILootPoolFieldGetter) poolMap.get(name);

            RandomValueRange reversedRoll = sortedRoll.get(sortedRoll.size() - 1 - sortedRoll.indexOf(name)).getValue();
            RandomValueRange reversedBoundsRoll = sortedBoundsRoll.get(sortedBoundsRoll.size() - 1 - sortedBoundsRoll.indexOf(name)).getValue();

            reversedLootPolls.add(new LootPool(reversedLootEntries(fieldGetter.getEntries()), fieldGetter.getConditions().toArray(new LootCondition[0]), reversedRoll, reversedBoundsRoll, name));
        }

        return new LootTable(reversedLootPolls.toArray(new LootPool[0]));
    }

    private static LootEntry[] reversedLootEntries(List<LootEntry> lootEntries) {
        ArrayList<LootEntry> processed = new ArrayList<>(lootEntries.stream().filter(LootEntryTable.class::isInstance).collect(Collectors.toList()));
        Stream<LootEntryItem> stream = lootEntries.stream().filter(entry -> !(processed.contains(entry) && entry instanceof LootEntryEmpty)).map(entry -> ((LootEntryItem) entry));
        List<Integer> weights = stream.mapToInt(stringLootEntryPair -> stringLootEntryPair.weight).distinct().sorted().boxed().collect(Collectors.toList());
        for (int weight : weights) {
            stream.filter(stringLootEntryPair -> stringLootEntryPair.weight == weight).forEach(pair -> processed.add(new LootEntryItem(
                    pair.item,
                    weights.get(weights.size() - 1 - weights.indexOf(weight)),
                    pair.quality,
                    pair.functions,
                    pair.conditions,
                    pair.getEntryName()
            )));
        }
        return processed.toArray(new LootEntry[0]);
    }

    public static LootTable getReversedTable(LootTable table) {
        return cachedReversedTables.computeIfAbsent(table, LootReverser::reversedLootTable);
    }
}
