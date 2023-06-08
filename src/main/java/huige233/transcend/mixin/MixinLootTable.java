package huige233.transcend.mixin;

import huige233.transcend.init.ModItems;
import huige233.transcend.mixinitf.IMixinLootTableFieldGetter;
import huige233.transcend.util.handlers.BaublesHelper;
import huige233.transcend.util.other.LootReverser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Mixin(LootTable.class)
public class MixinLootTable implements IMixinLootTableFieldGetter {

    @Shadow
    @Final
    private List<LootPool> pools;

    @Inject(method = "generateLootForPools", at = @At("HEAD"),cancellable = true)
    public void generateLootForPools(Random p_generateLootForPools_1_, LootContext context, CallbackInfoReturnable<List<ItemStack>> cir) {
        boolean flag = false;
        List<ItemStack> baubles = BaublesHelper.getBaubles((EntityPlayer) context.getKillerPlayer());
        for (ItemStack stack : baubles) {
            if (stack.getItem() == ModItems.LN) {
                flag = true;
                break;
            }
        }
        if (context.getKillerPlayer() != null && flag) {
                cir.setReturnValue(LootReverser.getReversedTableAndGenerate(new LootTable(pools.toArray(new LootPool[0])),p_generateLootForPools_1_,context));
        }
    }

//    @Overwrite
//    public void fillInventory(IInventory inventory, Random rand, LootContext context){
//        WorldServer world = context.getWorld();
//        Entity lootedEntity = context.getLootedEntity();
//        List<ItemStack> list = this.generateLootForPools(rand, context);
//        for (ItemStack itemstack : list) {
//            inventory.addItem(itemstack);
//        }
//    }

    @Inject(method = "fillInventory", at = @At("HEAD"), cancellable = true)
    public void FilllNVeNtory(IInventory inventory, Random rand, LootContext context, CallbackInfo ci) {
        if (context.getKillerPlayer() != null) {
            context.getKillerPlayer().sendMessage(new TextComponentString("hello?"));
        }
    }

    @Override
    public List<LootPool> getLootPool() {
        return pools;
    }
}
