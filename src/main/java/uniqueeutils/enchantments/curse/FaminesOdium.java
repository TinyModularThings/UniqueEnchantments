package uniqueeutils.enchantments.curse;

import it.unimi.dsi.fastutil.ints.AbstractInt2FloatMap.BasicEntry;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.BaseUEMod;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IdStat;
import uniquebase.utils.IntStat;

public class FaminesOdium extends UniqueEnchantment
{
	public static final IntStat DELAY = new IntStat(1200, "delay");
	public static final DoubleStat NURISHMENT = new DoubleStat(0.2F, "nourishment");
	public static final DoubleStat DAMAGE = new DoubleStat(0.00625F, "damage");
	public static final IdStat FOOD_BLACK_LIST = new IdStat("food_black_list", ForgeRegistries.ITEMS);
	
	public FaminesOdium()
	{
		super(new DefaultData("famines_odium", Rarity.VERY_RARE, 2, true, true, 10, 4, 40), BaseUEMod.ALL_TYPES, EquipmentSlotType.values());
		setCategory("utils");
		addStats(DELAY, NURISHMENT, DAMAGE, FOOD_BLACK_LIST);
		setCurse();	
	}
	
	public static Int2FloatMap.Entry consumeRandomItem(IInventory inventory, float effect)
	{
		for(int i = 0,m=inventory.getContainerSize();i<m;i++)
		{
			ItemStack stack = inventory.getItem(i);
			if(!FOOD_BLACK_LIST.contains(stack.getItem().getRegistryName()) && stack.getItem().isEdible())
			{
				Food food = stack.getItem().getFoodProperties();
				BasicEntry entry = new BasicEntry(food.getNutrition() / 4, food.getSaturationModifier() * effect);
				if(stack.hasContainerItem())
				{
					ItemStack container = stack.getContainerItem();
					stack.shrink(1);
					inventory.setItem(i, container);
				}
				else
				{
					stack.shrink(1);
					inventory.setItem(i, stack);
				}
				return entry;
			}
		}
		return null;
	}
	
}
