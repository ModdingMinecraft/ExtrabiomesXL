package extrabiomes.module.fabrica.block;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import extrabiomes.Extrabiomes;

public class ItemPaste extends Item
{
    
    public ItemPaste(int par1)
    {
        super(par1);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister)
    {
        itemIcon = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "cactuspaste");
    }

    @Override
    public void addInformation(ItemStack itemForTooltip, EntityPlayer playerViewingToolTip, List listOfLines, boolean sneaking) {
    	listOfLines.add("This green paste made from");
    	listOfLines.add("4 �oTiny Cacti�r can be smelted");
    	listOfLines.add("into �2Cactus Green�r.");
    }
}
