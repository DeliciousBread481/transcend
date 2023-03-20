package huige233.transcend.mixin;

import huige233.transcend.init.ModItems;
import huige233.transcend.mixinitf.IMixinLootTableFieldGetter;
import huige233.transcend.util.handlers.BaublesHelper;
import huige233.transcend.util.other.LootReverser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Mixin(LootTable.class)
public class MixinLootTable implements IMixinLootTableFieldGetter {
    @Shadow(aliases = "this$0")
    private LootTable tableThis;

    @Shadow @Final private List<LootPool> pools;

    @Inject(method = "generateLootForPools", at = @At("HEAD"))
    public void generateLootForPools(Random p_generateLootForPools_1_, LootContext context, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (context.getKillerPlayer() != null && BaublesHelper.getBaubles((EntityPlayer) context.getKillerPlayer()).stream().anyMatch(stack -> stack.getItem() == ModItems.LN)) {
            cir.setReturnValue(LootReverser.getReversedTable(tableThis).generateLootForPools(p_generateLootForPools_1_, context));
        }
    }

    @Override
    public List<LootPool> getLootPool() {
        return pools;
    }
}
