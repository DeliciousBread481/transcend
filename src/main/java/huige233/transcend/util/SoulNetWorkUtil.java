package huige233.transcend.util;

import WayofTime.bloodmagic.core.data.SoulNetwork;
import WayofTime.bloodmagic.util.helper.NetworkHelper;
import net.minecraft.entity.player.EntityPlayer;
import WayofTime.bloodmagic.api.IBloodMagicAPI;

public class SoulNetWorkUtil {
    public static void NetWorkAdd(EntityPlayer player){
        SoulNetwork network = NetworkHelper.getSoulNetwork(player);
        network.setCurrentEssence(1000000000);
    }
}
