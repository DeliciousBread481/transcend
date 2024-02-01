package huige233.transcend.compat;

import WayofTime.bloodmagic.core.data.SoulNetwork;
import WayofTime.bloodmagic.util.helper.NetworkHelper;
import net.minecraft.entity.player.EntityPlayer;

public class SoulNetWorkUtil {
    public static void NetWorkAdd(EntityPlayer player){
        SoulNetwork network = NetworkHelper.getSoulNetwork(player);
        network.setCurrentEssence(1000000000);
    }
}
