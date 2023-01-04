package huige233.transcend.network.message;

import huige233.transcend.network.NetworkMessage;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageTr extends NetworkMessage<MessageTr> {
    public int slot;
    public ItemStack stack = ItemStack.EMPTY;
    public MessageTr() {}
    public MessageTr(int slot){
     this.slot=slot;
    }
    public MessageTr(int slot, ItemStack stack){
        this.slot=slot;
        this.stack=stack;
    }

    @Override
    public IMessage handleMessage(MessageContext context){
        return null;
    }
}

