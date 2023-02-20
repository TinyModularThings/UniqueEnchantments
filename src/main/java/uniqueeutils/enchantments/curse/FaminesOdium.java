package uniqueeutils.enchantments.curse;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.BaseUEMod;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IdStat;
import uniquebase.utils.IntStat;

public class FaminesOdium extends UniqueEnchantment
{
	public static final String TAG_INVALID = "NO_FOOD";
	public static final IntStat DELAY = new IntStat(1200, "delay");
	public static final DoubleStat DAMAGE = new DoubleStat(0.00625F, "damage");
	public static final IdStat<Item> FOOD_BLACK_LIST = new IdStat<>("food_black_list", ForgeRegistries.ITEMS);

	public FaminesOdium()
	{
		super(new DefaultData("famines_odium", Rarity.VERY_RARE, 2, true, true, 10, 4, 40), BaseUEMod.ALL_TYPES, EquipmentSlot.values());
		setCategory("utils");
		addStats(DELAY, DAMAGE, FOOD_BLACK_LIST);
		setCurse();	
	}
	
	public static FoodEntry getRandomFood(Player player, RandomSource rand)
	{
		List<FoodEntry> list = new ArrayList<>();
		getRandomFood(player.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(EmptyHandler.INSTANCE), false, list);
		return list.size() > 0 ? list.get(rand.nextInt(list.size())) : null;
	}
	
	private static void getRandomFood(IItemHandler handler, boolean subLayer, List<FoodEntry> result)
	{
		for(int i = 0,m=handler.getSlots();i<m;i++)
		{
			ItemStack stack = handler.extractItem(i, 1, true);
			if(stack.isEmpty()) continue;
			if(!FOOD_BLACK_LIST.contains(stack.getItem()) && stack.getItem().isEdible() && isValidFood(stack))
			{
				result.add(new FoodEntry(handler, i));
			}
			else if(!subLayer)
			{
				LazyOptional<IItemHandler> subHandler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER);
				if(!subHandler.isPresent()) continue;
				getRandomFood(subHandler.orElse(EmptyHandler.INSTANCE), true, result);
			}
		}
	}
	
	public static boolean isValidFood(ItemStack stack)
	{
		CompoundTag tag = stack.getTag();
		return tag == null || !tag.getBoolean(TAG_INVALID);
	}
	
	public static class FoodEntry
	{
		IItemHandler handler;
		int slot;
		
		public FoodEntry(IItemHandler handler, int slot)
		{
			this.handler = handler;
			this.slot = slot;
		}

		public void eat(Player player)
		{
			player.eat(player.level, handler.extractItem(slot, 1, false));
		}
	}
}
