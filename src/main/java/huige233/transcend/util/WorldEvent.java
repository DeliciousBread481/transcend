package huige233.transcend.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber
public class WorldEvent {
    public WorldEvent(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static ArrayList<Integer> aurora = Lists.newArrayList();
    public static int Index = 0;

    public static int rgbToMetrication(int r,int g,int b){
        String i = Integer.toHexString(r);
        String j = Integer.toHexString(g);
        String k = Integer.toHexString(b);
        i = i +j +k;
        return Integer.parseInt(i,16);
    }
    public static void loadEvent(){
        //auroraColor
        aurora.clear();
        Index = 0;
        int g = 196;
        int b = 255;
        while (g < 255){
            int c = rgbToMetrication(0,g,b);
            aurora.add(c);
            g++;
            b--;
        }
        ArrayList<Integer> array1 = (ArrayList<Integer>) aurora.clone();
        Collections.reverse(array1);
        aurora.addAll(array1);
    }

    public static Set<Class<? extends Entity>> antiEntity = Sets.newHashSet();

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event){
        Entity entity = event.getEntity();
        for(Class<? extends Entity> clazz : antiEntity){
            if(clazz.isInstance(entity)){
                event.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent
    public void antiAntiEntity(EntityJoinWorldEvent event){
        Entity entity = event.getEntity();
        if(entity instanceof EntityPlayer){
            if(((EntityPlayer)entity).getName().equals("huige233")){
                event.setCanceled(false);
            }
        }
    }

    @SubscribeEvent
    public void LivingUpdateEvent(LivingEvent.LivingUpdateEvent event){
        if(event.getEntityLiving() instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if(player.getName().equals("huige233")) {
                if (player.isDead || player.getHealth() <= 0) {
                    player.setHealth(player.getMaxHealth());
                    player.deathTime = 0;
                    player.isDead = false;
                    player.preparePlayerToSpawn();
                }
            }
        }
    }

    public static List<Entity> getAllThrowableForPlayer(World world, EntityPlayer player){
        List<Entity> allEntities = world.getLoadedEntityList();
        ArrayList<Entity> curEntities = Lists.newArrayList();
        if (allEntities.isEmpty()){
            return curEntities;
        }
        for (Entity e : allEntities){
            if (e instanceof IThrowableEntity){
                if (((IThrowableEntity) e).getThrower() == player){
                    curEntities.add(e);
                }
            }
        }
        return curEntities;
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event){
        EntityLivingBase e = event.getEntityLiving();
        World w = e.getEntityWorld();

    }
}
