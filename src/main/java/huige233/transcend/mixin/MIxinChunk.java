package huige233.transcend.mixin;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Chunk.class)
public abstract class MIxinChunk {

    @Shadow
    @Final
    private World world;


    @Shadow
    public abstract ChunkPos getPos();

    @Inject(method = "needsSaving", at = @At("HEAD"), cancellable = true)
    private void needSave(boolean p_76601_1_, CallbackInfoReturnable<Boolean> cir) {
        if (world.playerEntities.stream().anyMatch(player -> getPos().getDistanceSq(player) <= 64)) {
            cir.setReturnValue(true);
        }
    }
}
