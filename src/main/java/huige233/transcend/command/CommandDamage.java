package huige233.transcend.command;

import huige233.transcend.util.Reference;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class CommandDamage extends CommandBase {
    public String getName() {
        return "damage";
    }

    @Override
    public String getUsage(ICommandSender sender){
        return "/damage <entity> <DamageSource>";
    }

    public void execute(MinecraftServer server,ICommandSender sender,String[] args) throws CommandException{
        if(args.length == 0) {
            EntityPlayer player = getCommandSenderAsPlayer(sender);
            DamageSource damageSource = player == null ? new DamageSource("OUT_OF_WORLD") : new EntityDamageSource("OUT_OF_WORLD",player);
            player.getCombatTracker().trackDamage(damageSource,10,10);
        } else if(args.length == 1){
            Entity entity = getEntity(server,sender,args[0]);
            EntityPlayer player = getCommandSenderAsPlayer(sender);
            DamageSource damageSource = player == null ? new DamageSource("OUT_OF_WORLD") : new EntityDamageSource("OUT_OF_WORLD",player);
            EntityLivingBase e = (EntityLivingBase) entity;
            e.getCombatTracker().trackDamage(damageSource,10,10);
        } else if(args.length == 3){
            Entity entity = getEntity(server,sender,args[0]);
            EntityPlayer player = getCommandSenderAsPlayer(sender);
            EntityLivingBase e = (EntityLivingBase) entity;
            DamageSource damageSource = player == null ? new DamageSource(args[3]) : new EntityDamageSource(args[3],player);
            e.getCombatTracker().trackDamage(damageSource,10,10);
        } else if(args.length == 4){
            Entity entity = getEntity(server,sender,args[0]);
            EntityPlayer player = getCommandSenderAsPlayer(sender);
            EntityLivingBase e = (EntityLivingBase) entity;
            DamageSource damageSource = player == null ? new DamageSource(args[3]) : new EntityDamageSource(args[3],player);
            float i = (float) parseDouble(args[4]);
            e.getCombatTracker().trackDamage(damageSource,i,i);
        }
    }
}
