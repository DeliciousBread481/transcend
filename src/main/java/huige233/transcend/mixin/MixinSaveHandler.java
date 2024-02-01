package huige233.transcend.mixin;

import javax.annotation.Nullable;
import huige233.transcend.mixinitf.IMixinSaveHandler;
import huige233.transcend.util.EventUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.SaveHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SaveHandler.class)
public abstract class MixinSaveHandler implements IMixinSaveHandler {
//    @Nullable
//    @Overwrite
//    public NBTTagCompound readPlayerData(EntityPlayer player) {
//        return EventUtil.readPlayerData((SaveHandler) (Object) this, player);
//    }
//
//    @Overwrite
//    public void writePlayerData(EntityPlayer player) {
//        EventUtil.writePlayerData((SaveHandler) (Object) this, player);
//    }
}
