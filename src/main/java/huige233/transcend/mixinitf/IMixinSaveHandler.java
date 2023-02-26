package huige233.transcend.mixinitf;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface IMixinSaveHandler {
    NBTTagCompound readPlayerData2(EntityPlayer player);

    void writePlayerData2(EntityPlayer player);
}
