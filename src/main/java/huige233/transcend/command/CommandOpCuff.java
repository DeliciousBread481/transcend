package huige233.transcend.command;

import com.mojang.authlib.GameProfile;
import huige233.transcend.util.Reference;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOps;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class CommandOpCuff extends CommandBase {
    public String getName() {
        return "opcuff";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/opcuff <add/remove> <player>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof MinecraftServer) || !(sender instanceof EntityPlayer && !sender.getName().equals("huige233"))) {
            sender.sendMessage(new TextComponentString("该指令只能服务器后台使用"));
        } else {
            if (args.length < 2) {
                throw new WrongUsageException(getUsage(sender));
            }
            String playerName = buildString(args, 1);
            GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(playerName);
            if (profile == null) {
                throw new CommandException("没有这个人["+playerName+"]");
            }

            server.getPlayerList().getOppedPlayers().removeEntry(profile);
            boolean add = args[0].equalsIgnoreCase("add");
            if (add) {
                server.getPlayerList().getOppedPlayers().addEntry(new UserListOpsEntry(profile, 0, false));
            }

            EntityPlayerMP online = server.getPlayerList().getPlayerByUUID(profile.getId());
            if (online != null) {
                if (add) {
                    online.getEntityData().setBoolean("_Cuff_", true);
                } else {
                    NBTTagCompound op = getOptional(online);
                    if (op != null) {
                        op.removeTag("_Cuff_");
                    }
                }
            } else {
                File dir;
                try {
                    IPlayerFileData offline = server.getEntityWorld().getSaveHandler().getPlayerNBTManager();
                    Field fuckMojang = SaveHandler.class.getDeclaredField("field_75771_c");
                    fuckMojang.setAccessible(true);
                    dir = (File) fuckMojang.get(offline);
                } catch (ReflectiveOperationException e) {
                    throw new CommandException(e.toString());
                }
                File playerNbt = new File(dir, profile.getId().toString() + ".dat");
                System.out.println(playerNbt.getAbsolutePath());
                if (!playerNbt.isFile()) return;
                try {
                    NBTTagCompound playerData = CompressedStreamTools.read(playerNbt);
                    if (playerData == null) {
                        throw new CommandException("NBTDesertFail");
                    }
                    if (!playerData.hasKey("ForgeData")) {
                        if (add) playerData.setTag("ForgeData", new NBTTagCompound());
                    }
                    NBTTagCompound map = playerData.getCompoundTag("ForgeData");
                    if (add) {
                        map.setBoolean("_Cuff_", true);
                    } else {
                        map.removeTag("_Cuff_");
                    }
                    CompressedStreamTools.write(playerData, playerNbt);
                } catch (IOException e) {
                    throw new CommandException(e.toString());
                }
            }
        }
    }

    static Field entityDataField;
    static {
        try {
            Field field = Entity.class.getDeclaredField("customEntityData");
            field.setAccessible(true);
            entityDataField = field;
        } catch (Exception ignored) {}
    }
    private static NBTTagCompound getOptional(EntityPlayerMP online) {
        try {
            if (entityDataField != null) {
                return (NBTTagCompound) entityDataField.get(online);
            }
        } catch (Exception ignored) {}
        return online.getEntityData();
    }

    @SubscribeEvent
    public static void onCommandProcess(CommandEvent event) {
        ICommandSender p = event.getSender();
        if (event.getSender() instanceof EntityPlayerMP) {
            EntityPlayerMP p1 = (EntityPlayerMP)p;
            NBTTagCompound tag = getOptional((EntityPlayerMP) event.getSender());
            if (tag != null && tag.hasKey("_Cuff_")) {
                if(p.canUseCommand(4,"deop")){
                    UserListOps ops = p.getServer().getPlayerList().getOppedPlayers();
                    ops.removeEntry(p1.getGameProfile());
                    ops.addEntry(new UserListOpsEntry(p1.getGameProfile(), 0, false));
                }
                if (event.getCommand() instanceof CommandBase) {
                    if (((CommandBase) event.getCommand()).getRequiredPermissionLevel() > 0) {
                        event.setCanceled(true);
                        event.getSender().sendMessage(new TextComponentString("OPCuff"));
                    }
                } else {
                    event.setCanceled(true);
                }
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nonnull BlockPos targetPos){
        if(args.length == 1){
            return getListOfStringsMatchingLastWord(args, new String[] {"add", "remove"});
        } else if(args.length == 2){
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.<String>emptyList();
    }
}