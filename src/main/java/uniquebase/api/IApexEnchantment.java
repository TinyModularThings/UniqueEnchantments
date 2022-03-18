package uniquebase.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ByteNBT;

public interface IApexEnchantment
{
	static final String APEX_ID = "apex_valid";
	
	public default boolean isApexValid(ItemStack stack)
	{
		return stack.hasTag() && stack.getTag().getBoolean(APEX_ID);
	}
	
	public static void setApex(ItemStack stack)
	{
		stack.addTagElement(APEX_ID, ByteNBT.ONE);
	}
}