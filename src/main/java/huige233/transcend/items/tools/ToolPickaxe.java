package huige233.transcend.items.tools;

import com.google.common.collect.Multimap;
import huige233.transcend.Transcend;
import huige233.transcend.init.ModBlock;
import huige233.transcend.init.ModItems;
import huige233.transcend.items.EntityFireImmune;
import huige233.transcend.util.ArmorUtils;
import huige233.transcend.util.IHasModel;
import huige233.transcend.util.ItemNBTHelper;
import huige233.transcend.util.Reference;
import huige233.transcend.util.other.MathHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ToolPickaxe extends ItemPickaxe implements IHasModel {
    public ToolPickaxe(String name, CreativeTabs tab, ToolMaterial material) {
        super(material);
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(tab);
        ModItems.ITEMS.add(this);
    }

    @Override
    public void registerModels() {
        Transcend.proxy.registerItemRenderer(this, 0, "inventory");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.isSneaking()) {
            if (player.getHeldItem(hand).getItem() == ModItems.TRANSCEND_PICKAXE) {
                if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack) != 10) {
                    stack.addEnchantment(Enchantments.FORTUNE, 10);
                    player.swingArm(hand);
                }
            }
        } else if(player.isSneaking()){
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
        return new ActionResult(EnumActionResult.SUCCESS, stack);
    }

    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        if (stack.getTagCompound() != null ){
            Iterator var3 = this.getToolClasses(stack).iterator();
            String type;
            do {
                if (!var3.hasNext()) {
                    return Math.max(super.getDestroySpeed(stack, state), 6.0F);
                }
                type = (String)var3.next();
            } while(!state.getBlock().isToolEffective(type, state));
        }
        return this.efficiency;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        World world = player.world;
        IBlockState state = world.getBlockState(pos);
        BlockPos blockpos1 = pos.add(1,-1,0);
        BlockPos blockpos2 = pos.add(0,-1,1);
        BlockPos blockpos3 = pos.add(-1,-1,0);
        BlockPos blockpos4 = pos.add(0,-1,-1);
        BlockPos blockPos = pos.add(0, -1, 0);
        int yi = 0;
        if (!world.isRemote) {
            if (state.getBlock() == ModBlock.NETHER_STAR_BLOCK && player.getHeldItem(EnumHand.MAIN_HAND).getItem() == ModItems.BREAK_BEDROCK_TOOL) {
                if (world.getBlockState(blockpos1).getBlock() == Blocks.DRAGON_EGG) {
                    world.setBlockToAir(blockpos1);
                    yi++;
                }
                if (world.getBlockState(blockpos2).getBlock() == Blocks.PURPUR_PILLAR) {
                    world.setBlockToAir(blockpos2);
                    yi++;
                }
                if (world.getBlockState(blockpos3).getBlock() == Blocks.REDSTONE_BLOCK) {
                    world.setBlockToAir(blockpos3);
                    yi++;
                }
                if (world.getBlockState(blockpos4).getBlock() == Blocks.EMERALD_BLOCK) {
                    world.setBlockToAir(blockpos4);
                    yi++;
                }
                stack.setCount(stack.getCount() - 1);
            }

            if(yi>=2) {
                Random ran = world.rand;
                double wa = yi*0.25f;
                if(wa >= ran.nextDouble()) {
                    world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Item.getItemFromBlock(ModBlock.BEDROCK_ORE))));
                    world.setBlockToAir(blockPos);
                } else {
                    player.sendMessage(new TextComponentString(I18n.translateToLocal("message.bedrock_ore_failed")));
                }
            }
        }
        return false;
    }

    public boolean hitEntity(ItemStack stack, EntityLivingBase victim, EntityLivingBase player) {
        int i = 30;
        if(!(victim instanceof EntityPlayer) || !ArmorUtils.fullEquipped((EntityPlayer) victim)) {
            victim.addVelocity(-MathHelper.sin((double) (player.rotationYaw * 3.1415927F / 180.0F)) * (double) i * 0.5, 2.0, MathHelper.cos((double) (player.rotationYaw * 3.1415927F / 180.0F)) * (double) i * 0.5);
        }
        return true;
    }

    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    public void setDamage(ItemStack stack, int damage) {
        if(stack.getItem() == ModItems.TRANSCEND_PICKAXE) {
            super.setDamage(stack, 0);
        }
    }

    public Entity createEntity(World world,Entity location, ItemStack itemstack) {
        return new EntityFireImmune(world,location.posX,location.posY,location.posZ,itemstack);
    }

    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> attrib = super.getAttributeModifiers(slot, stack);
        UUID uuid = new UUID((slot.toString()).hashCode(), 0);
        if(slot == EntityEquipmentSlot.MAINHAND && stack.getItem() == ModItems.TRANSCEND_PICKAXE) {
            attrib.put(EntityPlayer.REACH_DISTANCE.getName(),new AttributeModifier(uuid,"Pickaxe modifier",256,0));
        }
        return attrib;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onHarvestDrops(HarvestDropsEvent event) {
        ListIterator<ItemStack> iter = event.getDrops().listIterator();
        if(event.getHarvester() !=null){
            ItemStack stack = event.getHarvester().getHeldItemMainhand();
            boolean smelt = ItemNBTHelper.getBoolean(stack,"AutoSmelt",false);
            if(smelt) {
                while (iter.hasNext()) {
                    ItemStack drop = (ItemStack) iter.next();
                    ItemStack smelted = FurnaceRecipes.instance().getSmeltingResult(drop);
                    if (!smelted.isEmpty()) {
                        smelted = smelted.copy();
                        smelted.setCount(drop.getCount());
                        int fortuene = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
                        if (fortuene > 0) {
                            Random random = event.getWorld().rand;
                            smelted.setCount(smelted.getCount() * random.nextInt(fortuene + 1) + 1);
                        }
                        iter.set(smelted);
                        float xp = FurnaceRecipes.instance().getSmeltingExperience(smelted) * 10;
                        if (xp < 1.0f && Math.random() < (double) xp) {
                            ++xp;
                        }
                        if (xp >= 1.0f) {
                            event.getState().getBlock().dropXpOnBlockBreak(event.getWorld(), event.getPos(), (int) xp);
                        }
                    }
                }
            }
        }
    }
    public EnumRarity getRarity(ItemStack stack )
    {
        return(ModItems.COSMIC_RARITY);
    }
}
