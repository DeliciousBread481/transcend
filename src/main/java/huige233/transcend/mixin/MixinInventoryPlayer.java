package huige233.transcend.mixin;

import javax.annotation.Nullable;

import huige233.transcend.mixinitf.IMixinInventoryPlayer;
import huige233.transcend.util.EventUtil;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(InventoryPlayer.class)
public abstract class MixinInventoryPlayer implements IMixinInventoryPlayer {
    @Overwrite
    public void dropAllItems() {
        EventUtil.dropAllItems((InventoryPlayer) (Object) this);
    }

    @Overwrite
    public int clearMatchingItems(@Nullable Item itemIn, int metadataIn, int removeCount, @Nullable NBTTagCompound itemNBT) {
        return EventUtil.clearMatchingItems((InventoryPlayer) (Object) this, itemIn, metadataIn, removeCount, itemNBT);
    }
}
