package uniqueeutils.enchantments.curse;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.BaseUEMod;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IdStat;
import uniquebase.utils.IntStat;

public class FaminesOdium extends UniqueEnchantment
{
	public static final IntStat DELAY = new IntStat(1200, "delay");
//	public static final DoubleStat NURISHMENT = new DoubleStat(0.2F, "nourishment");
	public static final DoubleStat DAMAGE = new DoubleStat(0.00625F, "damage");
	public static final IdStat<Item> FOOD_BLACK_LIST = new IdStat<>("food_black_list", ForgeRegistries.ITEMS);

	public FaminesOdium()
	{
		super(new DefaultData("famines_odium", Rarity.VERY_RARE, 2, true, true, 10, 4, 40), BaseUEMod.ALL_TYPES, EquipmentSlot.values());
		setCategory("utils");
		addStats(DELAY, DAMAGE, FOOD_BLACK_LIST);
		setCurse();	
	}
	
	public static ItemStack getRandomFood(Container inventory, RandomSource rand)
	{
		List<ItemStack> list = new ArrayList<>();
		for(int i = 0,m=inventory.getContainerSize();i<m;i++)
		{
			ItemStack stack = inventory.getItem(i);
			if(!FOOD_BLACK_LIST.contains(stack.getItem()) && stack.getItem().isEdible())
			{
				list.add(stack);
			}
		}
		return list.size() > 0 ? list.get(rand.nextInt(list.size())) : ItemStack.EMPTY;
	}
	
}
