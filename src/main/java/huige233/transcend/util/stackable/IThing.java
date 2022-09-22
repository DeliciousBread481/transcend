package huige233.transcend.util.stackable;

import huige233.transcend.util.NNList;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

interface IThing {
    @Nonnull
    NNList<IThing> bake();

    boolean is(@Nullable Item item);

    boolean is(@Nullable ItemStack itemStack);

    boolean is(@Nullable Block block);

    @Nonnull
    NNList<Item> getItems();

    @Nonnull
    NNList<ItemStack> getItemStacks();

    @Nonnull
    NNList<Block> getBlocks();

    public static interface Zwieback extends IThing {

        @Nullable
        IThing rebake();

    }
}
