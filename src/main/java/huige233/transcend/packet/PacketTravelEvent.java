package huige233.transcend.packet;

import huige233.transcend.event.TeleportEntityEvent;
import huige233.transcend.util.NullHelper;
import huige233.transcend.util.Vector.Vector3d;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;

public class PacketTravelEvent implements IMessage {


    long pos;
    boolean conserveMotion;
    int source;
    int hand;

    public PacketTravelEvent() {
    }

    public PacketTravelEvent(BlockPos pos,boolean conserveMotion, EnumHand hand) {
        this.pos = pos.toLong();
        this.conserveMotion = conserveMotion;
        this.hand = (hand == null ? EnumHand.MAIN_HAND : hand).ordinal();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos);
        buf.writeBoolean(conserveMotion);
        buf.writeInt(source);
        buf.writeInt(hand);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = buf.readLong();
        conserveMotion = buf.readBoolean();
        source = buf.readInt();
        hand = buf.readInt();
    }

    public static class Handler implements IMessageHandler<PacketTravelEvent, IMessage> {

        @Override
        public IMessage onMessage(PacketTravelEvent message, MessageContext ctx) {
            Entity toTp = ctx.getServerHandler().player;

            EnumHand hand = NullHelper.notnullJ(EnumHand.values()[message.hand], "Enum.values()");

            doServerTeleport(toTp, BlockPos.fromLong(message.pos),message.conserveMotion,hand);

            return null;
        }

        private boolean doServerTeleport(@Nonnull Entity toTp, @Nonnull BlockPos pos,boolean conserveMotion, @Nonnull EnumHand hand) {
            EntityPlayer player = toTp instanceof EntityPlayer ? (EntityPlayer) toTp : null;

            TeleportEntityEvent evt = new TeleportEntityEvent(toTp, pos, toTp.dimension);
            if (MinecraftForge.EVENT_BUS.post(evt)) {
                return false;
            }
            pos = evt.getTarget();


            if (player != null) {
                player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5);
            } else {
                toTp.setPosition(pos.getX(), pos.getY(), pos.getZ());
            }


            toTp.fallDistance = 0;

            if (player != null) {
                if (conserveMotion) {
                    Vector3d velocityVex = getLookVecEio(player);
                    SPacketEntityVelocity p = new SPacketEntityVelocity(toTp.getEntityId(), velocityVex.x, velocityVex.y, velocityVex.z);
                    ((EntityPlayerMP) player).connection.sendPacket(p);
                }

            }
            return true;
        }

    }

    public static @Nonnull Vector3d getLookVecEio(@Nonnull EntityPlayer player) {
        Vec3d lv = player.getLookVec();
        return new Vector3d(lv.x, lv.y, lv.z);
    }
}
