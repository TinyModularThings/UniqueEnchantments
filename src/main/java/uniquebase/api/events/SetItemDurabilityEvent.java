package uniquebase.api.events;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class SetItemDurabilityEvent extends Event {
	
	final ItemStack item;
	int damage;
	
	public SetItemDurabilityEvent(ItemStack item, int damage)
	{
		this.item = item;
		this.damage = damage;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public int getDurability() {
		return damage;
	}
	
	public void setDurability(int dur) {
		this.damage = dur;
	}
}
