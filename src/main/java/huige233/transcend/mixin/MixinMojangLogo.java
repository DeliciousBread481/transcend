package huige233.transcend.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class,remap = false)
public abstract class MixinMojangLogo {
    @Shadow
    private static ResourceLocation LOCATION_MOJANG_PNG;

    @Inject(method = "drawSplashScreen", at =@At("HEAD"), remap = false)
    private void inject_drawSplashScreen(CallbackInfo ci) {
        LOCATION_MOJANG_PNG = new ResourceLocation("transcend", "textures/gui/mojang.png");
    }
}
