package huige233.transcend.util.handlers;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Optional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BaublesHelper {
    @CapabilityInject(IBaublesItemHandler.class)
    public static Capability<IBaublesItemHandler> CAPABILITY_BAUBLES = null;

    public BaublesHelper() {
    }

    @Optional.Method(modid = "baubles")
    public static List<ItemStack> getBaubles(EntityPlayer entity) {
        if (CAPABILITY_BAUBLES == null) {
            return new ArrayList();
        } else {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(entity);
            if (handler == null) {
                return new ArrayList();
            } else {
                IntStream var10000 = IntStream.range(0, handler.getSlots());
                handler.getClass();
                return (List)var10000.mapToObj(handler::getStackInSlot).filter((stack) -> {
                    return !stack.isEmpty();
                }).collect(Collectors.toList());
            }
        }
    }
}