/**
 * This work is licensed under the Creative Commons
 * Attribution-ShareAlike 3.0 Unported License. To view a copy of this
 * license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package extrabiomes.items;

import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import extrabiomes.blocks.BlockCustomSapling;
import extrabiomes.blocks.BlockMiniLog;
import extrabiomes.utility.MultiItemBlock;

public class ItemKneeLog extends ItemBlock
{    
    public ItemKneeLog(final Block block)
    {
        super(block);
    }

    @Override
    public void addInformation(ItemStack itemForTooltip, EntityPlayer playerViewingToolTip, List listOfLines, boolean sneaking) {
    	listOfLines.add("Can be crafted to/from normal");
    	listOfLines.add("logs and back via a 2x2 \"L\"");
    	listOfLines.add("in a crafting table.");
    }
    
}
