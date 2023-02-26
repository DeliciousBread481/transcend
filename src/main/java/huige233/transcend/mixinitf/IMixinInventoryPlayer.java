package huige233.transcend.mixinitf;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

public interface IMixinInventoryPlayer {
    void dropAllItems2();

    int clearMatchingItems2(Item itemIn, int metadataIn, int removeCount, NBTTagCompound itemNBT);
}
