package uniquebase.utils.events;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class FishingLuckEvent extends Event
{
	final ItemStack stack;
	int level;
	
	public FishingLuckEvent(ItemStack stack, int level)
	{
		this.stack = stack;
		this.level = level;
	}
	
	public ItemStack getStack()
	{
		return stack;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public void setLevel(int level)
	{
		this.level = level;
	}
}
