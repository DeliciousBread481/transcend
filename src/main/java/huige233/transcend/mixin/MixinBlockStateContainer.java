package huige233.transcend.mixin;

import net.minecraft.block.*;
import net.minecraft.block.state.BlockStateContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockStateContainer.StateImplementation.class)
public class MixinBlockStateContainer {
    @Shadow
    @Final
    private Block block;

    // close redstone light
    @Inject(method = "getLightValue*", at = @At("HEAD"), cancellable = true)
    private void getLightValue(CallbackInfoReturnable<Integer> cir) {
        if (block instanceof BlockRedstoneTorch || block instanceof BlockRedstoneWire ||
                block instanceof BlockRedstoneRepeater || block instanceof BlockRedstoneComparator ||
                block instanceof  BlockCompressedPowered) {
            cir.setReturnValue(0);
        }
    }
}
