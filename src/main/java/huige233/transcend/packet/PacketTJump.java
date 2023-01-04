package huige233.transcend.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketTJump implements IMessageHandler<PacketTJump.PacketJump,IMessage> {
    @Override
    public IMessage onMessage(PacketJump message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        player.getServerWorld().addScheduledTask(() -> {
            player.fallDistance = 0;
            player.jump();
        });
        return null;
    }
    public static class PacketJump implements IMessage {
        public PacketJump(){}
        @Override
        public void fromBytes(ByteBuf buf){}
        @Override
        public void toBytes(ByteBuf buf){}
    }
}
