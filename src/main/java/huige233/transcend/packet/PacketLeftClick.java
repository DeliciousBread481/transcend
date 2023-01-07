package huige233.transcend.packet;

import huige233.transcend.init.ModItems;
import huige233.transcend.items.tools.ToolWarp;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketLeftClick implements IMessage {
    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<PacketLeftClick, IMessage> {

        @Override
        public IMessage onMessage(PacketLeftClick message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.server.addScheduledTask(() -> ((ToolWarp) ModItems.WARP_SWORD).trySpawnBurst(player));
            return null;
        }
    }

}
