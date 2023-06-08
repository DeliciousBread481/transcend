package huige233.transcend.tileEntity;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.energy.tile.IMultiEnergySource;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Redstone;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.gui.dynamic.DynamicContainer;
import ic2.core.gui.dynamic.GuiParser;
import ic2.core.init.Localization;
import ic2.core.network.GuiSynced;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class TileCreativeEUSource extends TileEntityInventory implements ITickable, IMultiEnergySource, IHasGui, INetworkClientTileEntityEventListener {
    public static CreativeEUSourceConfig settings;
    @GuiSynced
    public int production;
    @GuiSynced
    public int tier;

    protected Redstone redstone;
    private boolean addedToEnet;

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {

    }

    @Override
    public int getInventoryStackLimit() {
        return 0;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {

    }

    @Override
    public String getName() {
        return "creative_energy_source";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public void update() {

    }

    public static final class CreativeEUSourceConfig {
        int production;
        int tier;

        public CreativeEUSourceConfig(int production, int tier) {
            this.production = production;
            this.tier = tier;
        }
    }

    public TileCreativeEUSource() {
        this.production = settings.production;
        this.tier = settings.tier;
        this.redstone = (Redstone) addComponent((TileEntityComponent) new Redstone((TileEntityBlock) this));
        this.redstone.subscribe(new Redstone.IRedstoneChangeHandler() {
            public void onRedstoneChange(int newLevel) {
                TileCreativeEUSource.this.setActive(newLevel <= 0);
            }
        });
    }

    protected void onLoaded() {
        super.onLoaded();
        if (!this.world.isRemote)
            this.addedToEnet = !MinecraftForge.EVENT_BUS.post((Event) new EnergyTileLoadEvent((IEnergyTile) this));
    }

    protected void onUnloaded() {
        super.onUnloaded();
        if (this.addedToEnet)
            this.addedToEnet = MinecraftForge.EVENT_BUS.post((Event) new EnergyTileUnloadEvent((IEnergyTile) this));
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.production = nbt.getInteger("production");
        this.tier = nbt.getInteger("tier");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("production", this.production);
        nbt.setInteger("tier", this.tier);
        return nbt;
    }

    public void onPlaced(ItemStack stack, EntityLivingBase placer, EnumFacing facing) {
        super.onPlaced(stack, placer, facing);
        if (this.world.isRemote) setActive(true);
    }

    public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side) {
        return true;
    }

    public int getSourceTier() {
        return this.tier;
    }

    public double getOfferedEnergy() {
        return getActive() ? (sendMultipleEnergyPackets() ? (this.production / getMultipleEnergyPacketAmount()) : this.production) : 0.0D;
    }

    public void drawEnergy(double amount) {
    }

    public boolean sendMultipleEnergyPackets() {
        return (this.production - EnergyNet.instance.getPowerFromTier(this.tier) > 0.0D);
    }

    public int getMultipleEnergyPacketAmount() {
        return (int) Math.ceil(this.production / EnergyNet.instance.getPowerFromTier(this.tier));
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, tooltip, advanced);
        tooltip.add(Localization.translate("ic2.item.tooltip.PowerTier", new Object[]{"Variable"}));
    }

    public ContainerBase<TileCreativeEUSource> getGuiContainer(EntityPlayer player) {
        ContainerBase containerBase = (DynamicContainer.create((IInventory) this, player, GuiParser.parse(this.teBlock)));
        return (ContainerBase<TileCreativeEUSource>) containerBase;
    }

    @SideOnly(Side.CLIENT)
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return (GuiScreen) BackgroundlessDynamicGUI.create((IInventory) this, player, GuiParser.parse(this.teBlock));
    }

    public void onGuiClosed(EntityPlayer player) {
    }

    public void onNetworkEvent(EntityPlayer player, int event) {
        switch (event / 10) {
            case 0:
                switch (event % 10) {
                    case 0:
                        changeProduction(-100);
                        break;
                    case 1:
                        changeProduction(-10);
                        break;
                    case 2:
                        changeProduction(-1);
                        break;
                    case 3:
                        changeProduction(1);
                        break;
                    case 4:
                        changeProduction(10);
                        break;
                    case 5:
                        changeProduction(100);
                        break;
                }
                break;
            case 1:
                switch (event % 10) {
                    case 0:
                        changeProduction(-500);
                        break;
                    case 1:
                        changeProduction(-50);
                        break;
                    case 2:
                        changeProduction(-5);
                        break;
                    case 3:
                        changeProduction(5);
                        break;
                    case 4:
                        changeProduction(50);
                        break;
                    case 5:
                        changeProduction(500);
                        break;
                }
                break;
            case 2:
                this.tier = event % 10 + 1;
                break;
        }
    }

    protected void changeProduction(int value) {
        this.production += value;
        if (this.production < 0)
            this.production = 0;
    }

    public String getTier() {
        return (this.tier > 5) ? Localization.translate("creative.eu.gui.max") : Integer.toString(this.tier);
    }
}
