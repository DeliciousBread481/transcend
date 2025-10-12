package huige233.transcend.init;

import huige233.transcend.effect.BaldrEffect;
import huige233.transcend.effect.TimeStopEffect;
import huige233.transcend.util.Reference;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModPotions {
    public static final Potion TIME_STOP = new TimeStopEffect().setBeneficial();
    public static final Potion Baldr = new BaldrEffect().setBeneficial();


    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> evt){
        evt.getRegistry().register(TIME_STOP);
        evt.getRegistry().register(Baldr);
    }
}
