package uniquebase.api;

import net.minecraft.nbt.ByteTag;
import net.minecraft.world.item.ItemStack;

public interface IApexEnchantment
{
	static final String APEX_ID = "apex_valid";
	
	public default boolean isApexValid(ItemStack stack)
	{
		return stack.hasTag() && stack.getTag().getBoolean(APEX_ID);
	}
	
	public static void setApex(ItemStack stack)
	{
		stack.addTagElement(APEX_ID, ByteTag.ONE);
	}
}