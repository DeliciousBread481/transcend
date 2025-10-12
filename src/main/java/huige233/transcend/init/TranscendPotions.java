package huige233.transcend.init;

import huige233.transcend.effect.BaldrEffect;
import huige233.transcend.effect.TimeStopEffect;
import huige233.transcend.util.Reference;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

//@ObjectHolder(Reference.MOD_ID)
//@Mod.EventBusSubscriber
//public class TranscendPotions {
//    private TranscendPotions(){}
//
//    @Nonnull
//    @SuppressWarnings("ConstantConditions")
//    private static <T> T placeholder(){return null;}
//
//    public static final Potion time_stop = placeholder();
//
//    public static void registerPotion(IForgeRegistry<Potion> registry,String name,Potion potion) {
//        potion.setRegistryName(Reference.MOD_ID,name);
//        potion.setPotionName("potion."+potion.getRegistryName().toString());
//        registry.register(potion);
//    }
//
//    @SubscribeEvent
//    public static void register(RegistryEvent.Register<Potion> event){
//        IForgeRegistry<Potion> registry = event.getRegistry();
////        registerPotion(registry,"time_stop",new TimeStopEffect(false,0x3bebb).setBeneficial());
////        registerPotion(registry,"baldr",new BaldrEffect(false,0x3bebb).setBeneficial());
//    }
//}
