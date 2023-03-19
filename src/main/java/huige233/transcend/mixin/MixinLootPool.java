package huige233.transcend.mixin;

import com.google.common.collect.Lists;
import huige233.transcend.init.ModItems;
import huige233.transcend.mixinitf.ILootPoolFieldGetter;
import huige233.transcend.util.handlers.BaublesHelper;
import huige233.transcend.util.other.LootReverser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Mixin(value = LootPool.class)
public class MixinLootPool implements ILootPoolFieldGetter {
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

