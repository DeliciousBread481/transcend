package huige233.transcend.items;

import huige233.transcend.Main;
import huige233.transcend.init.ModItems;
import huige233.transcend.util.IHasModel;
import huige233.transcend.util.Reference;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;

public class InfinityFood extends ItemFood implements IHasModel {
    public InfinityFood(){
        super(20,10.f,false);
        this.setMaxStackSize(1);
        this.setTranslationKey("infinity_food");
        this.setAlwaysEdible();
        setRegistryName("infinity_food");
        this.setCreativeTab(Main.TranscendTab);
        addPropertyOverride(new ResourceLocation(Reference.MOD_ID,"easter"),(stack, worldIn, entityIn) -> InfinityFood.isEaster(stack)?1F:0F);
        ModItems.ITEMS.add(this);
    }

    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(this, 0, "inventory");
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        if (entity instanceof EntityPlayer){
            stack.grow(1);
        }
        if(isEaster(stack)){
            entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 1));
        }
        return super.onItemUseFinish(stack,world,entity);
    }

    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 8;
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect( ItemStack stack)
    {
        return true;
    }

    public static boolean isEaster(ItemStack stack){
        String name = stack.getDisplayName().toLowerCase(Locale.ROOT).trim();
        return name.equals("azazel sakura");
    }
}
