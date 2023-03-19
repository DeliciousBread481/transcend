package huige233.transcend.mixin;

import com.google.common.collect.Lists;
import huige233.transcend.init.ModItems;
import huige233.transcend.items.compat.LootUnder;
import huige233.transcend.mixinitf.ILootPoolFieldGetter;
import huige233.transcend.util.handlers.BaublesHelper;
import huige233.transcend.util.handlers.LootEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.storage.loot.*;
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

    @Inject(method = "generateLoot", at = @At("HEAD"), cancellable = true)
    public void inject_generateLoot(Collection<ItemStack> stacks, Random rand, LootContext context, CallbackInfo ci) {
        if (context.getKillerPlayer() != null && BaublesHelper.getBaubles((EntityPlayer) context.getKillerPlayer()).stream().anyMatch(stack -> stack.getItem() == ModItems.LN)) {
//            ((LootPool) LootEvent.get(name)).generateLoot(stacks, rand, context);
            LootPool lootPool = (LootPool) LootEvent.get(name);
            StringBuilder s = new StringBuilder();
            s.append(LootConditionManager.testAllConditions(this.poolConditions, rand, context)).append("\n");
            int q = this.rolls.generateInt(rand) + MathHelper.floor(this.bonusRolls.generateFloat(rand) * context.getLuck());
            s.append(q).append("\n");
            for (int w = 0; w < q; ++w) {
                List<LootEntry> list = Lists.newArrayList();
                int i = 0;
                Iterator var6 = this.lootEntries.iterator();

                while (var6.hasNext()) {
                    LootEntry lootentry = (LootEntry) var6.next();
                    if (LootConditionManager.testAllConditions(lootentry.conditions, rand, context)) {
                        int j = lootentry.getEffectiveWeight(context.getLuck());
                        s.append("effective weight").append(j).append("\n");
                        s.append("lootenrty").append(lootentry).append("\n");
                        if (j > 0) {
                            list.add(lootentry);
                            i += j;
                        }
                    }
                }

                if (i != 0 && !list.isEmpty()) {
                    int k = rand.nextInt(i);
                    Iterator var10 = list.iterator();

                    while (var10.hasNext()) {
                        LootEntry lootentry1 = (LootEntry) var10.next();
                        k -= lootentry1.getEffectiveWeight(context.getLuck());
                        s.append(k).append("\n");
                        if (k < 0) {
                            lootentry1.addLoot(stacks, rand, context);
                            return;
                        }
                    }
                }
            }

//            ci.cancel();
            stacks.forEach(st->s.append(st.toString()));
            context.getKillerPlayer().sendMessage(new TextComponentString(s.toString()));
        }
    }

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

