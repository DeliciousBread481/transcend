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
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class CommandDamage extends CommandBase {
    public String getName() {
        return "damage";
    }

    @Override
    public String getUsage(ICommandSender sender){
        return "/damage <entity> <DamageSource> <count>";
    }

    public void execute(MinecraftServer server,ICommandSender sender,String[] args) throws CommandException{
        EntityPlayer attacker = getCommandSenderAsPlayer(sender);
        EntityLivingBase victim = args.length > 0 ? getEntity(server,sender,args[0],EntityLivingBase.class) : attacker;
        DamageSource damageSource = new EntityDamageSource(args.length > 1 ? args[1] : "OUT_OF_WORLD", attacker);
        float damage = args.length > 2 ? (float) parseDouble(args[2]) : 10;
        victim.getCombatTracker().trackDamage(damageSource,damage,damage);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nonnull BlockPos targetPos){
        if(args.length == 1){
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.<String>emptyList();
    }
}
