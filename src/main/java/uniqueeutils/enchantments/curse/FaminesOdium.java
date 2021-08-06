package uniqueeutils.enchantments.curse;

import it.unimi.dsi.fastutil.ints.AbstractInt2FloatMap.BasicEntry;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;

public class FaminesOdium extends UniqueEnchantment
{
	public static final IntStat DELAY = new IntStat(1200, "delay");
	public static final DoubleStat NURISHMENT = new DoubleStat(0.2D, "nurishment");
	public static final DoubleStat DAMAGE = new DoubleStat(0.00625D, "damage");
	
	public FaminesOdium()
	{
		super(new DefaultData("famines_odium", Rarity.UNCOMMON, 2, true, 10, 4, 40), EnumEnchantmentType.ALL, EntityEquipmentSlot.values());
		setCategory("utils");
		addStats(DELAY, NURISHMENT, DAMAGE);
	}
	
	@Override
	public boolean isCurse()
	{
		return true;
	}
	
	public static Int2FloatMap.Entry consumeRandomItem(IInventory inventory, float effect)
	{
		for(int i = 0,m=inventory.getSizeInventory();i<m;i++)
		{
			ItemStack stack = inventory.getStackInSlot(i);
			if(stack.getItem() instanceof ItemFood)
			{
				ItemFood food = (ItemFood)stack.getItem();
				BasicEntry entry = new BasicEntry(food.getHealAmount(stack) / 4, food.getSaturationModifier(stack) * effect);
				if(stack.getItem().hasContainerItem(stack))
				{
					ItemStack container = stack.getItem().getContainerItem(stack);
					stack.shrink(1);
					inventory.setInventorySlotContents(i, container);
				}
				else
				{
					stack.shrink(1);
					inventory.setInventorySlotContents(i, stack);
				}
				return entry;
			}
		}
		return null;
	}
	
}