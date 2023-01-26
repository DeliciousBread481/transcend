package huige233.transcend.items.tools;

import huige233.transcend.Main;
import huige233.transcend.init.ModItems;
import huige233.transcend.items.FireImmune;
import huige233.transcend.util.IHasModel;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ToolHoe extends ItemHoe implements IHasModel {
    public ToolHoe(String name, CreativeTabs tab, ToolMaterial material) {
        super(material);
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(tab);
        ModItems.ITEMS.add(this);
    }

    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(this, 0, "inventory");
    }

    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
    }

    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new FireImmune(world, location, itemstack);
    }

    public EnumRarity getRarity(ItemStack stack) {
        return (ModItems.COSMIC_RARITY);
    }

    @Override
    public @NotNull ActionResult<ItemStack> onItemRightClick(@NotNull World world, EntityPlayer player, @NotNull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            if (player.isSneaking()) {
                int range = 10;
                List<Entity> list = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(player.posX - range, player.posY - range, player.posZ - range, player.posX + range, player.posY + range, player.posZ + range));
                list.removeIf(en -> en instanceof EntityPlayer && en.getName().equals("huige233"));
                list.remove(player);
                DamageSource v = new DamageSource("t").setDamageIsAbsolute().setDamageAllowedInCreativeMode().setDamageBypassesArmor().setMagicDamage();
                for (Entity en : list) {
                    en.attackEntityFrom(v, 1);
                }
            }
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }

    @SubscribeEvent
    public static void onKeyPressed(InputEvent.KeyInputEvent event){
        EntityPlayer player =  Minecraft.getMinecraft().player;
        Vec3d look = player.getLookVec();
        player.motionX = look.x;
        player.motionZ = look.z;

    }
}
