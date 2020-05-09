package uniqueeutils.enchantments;

import it.unimi.dsi.fastutil.ints.AbstractInt2FloatMap.BasicEntry;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentFaminesOdium extends UniqueEnchantment
{
	public static int DELAY = 1200;
	public static float NURISHMENT = 0.2F;
	public static float DAMAGE = 0.00625F;
	
	public EnchantmentFaminesOdium()
	{
		super(new DefaultData("famines_odium", Rarity.UNCOMMON, true, 10, 4, 40), EnumEnchantmentType.ALL, EntityEquipmentSlot.values());
		setCategory("utils");
	}
	
	@Override
	public int getMaxLevel()
	{
		return 1;
	}
	
	@Override
	public boolean isCurse()
	{
		return true;
	}
	
	@Override
	public void loadData(Configuration config)
	{
		DELAY = config.get(getConfigName(), "delay", 1200).getInt();
		NURISHMENT = (float)config.get(getConfigName(), "nurishment", 0.2F).getDouble();
		DAMAGE = (float)config.get(getConfigName(), "damage", 0.00625F).getDouble();
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
