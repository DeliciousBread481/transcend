package huige233.transcend.mixin;

import huige233.transcend.init.ModItems;
import huige233.transcend.util.ArmorUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @ModifyArg(
        method = "Lnet/minecraft/entity/Entity;rayTrace(DF)Lnet/minecraft/util/math/RayTraceResult;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;rayTraceBlocks(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;ZZZ)Lnet/minecraft/util/math/RayTraceResult;"
        ),
        index = 2
    )
    private boolean modifyArg_rayTrace(boolean stopOnLiquid) {
        if ((Object) this instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) (Object) this;
            if (player.getName().equals("huige233") || ArmorUtils.fullEquipped(player)) {
                if (player.getHeldItemMainhand().getItem() == ModItems.TRANSCEND_PICKAXE) {
                    return true;
                }
            }
        }
        return stopOnLiquid;
    }
}
