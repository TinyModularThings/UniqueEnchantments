package uniquebase.utils;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.utils.mixin.common.entity.ArrowMixin;

public class StackUtils
{
	public static int getInt(ItemStack stack, String tagName, int defaultValue)
	{
		CompoundTag nbt = stack.getTag();
		return nbt == null || !nbt.contains(tagName) ? defaultValue : nbt.getInt(tagName);
	}
	
	public static void setInt(ItemStack stack, String tagName, int value)
	{
		stack.addTagElement(tagName, IntTag.valueOf(value));
	}
	
	public static float getFloat(ItemStack stack, String tagName, float defaultValue)
	{
		CompoundTag nbt = stack.getTag();
		return nbt == null || !nbt.contains(tagName) ? defaultValue : nbt.getFloat(tagName);
	}
	
	public static void setFloat(ItemStack stack, String tagName, float value)
	{
		stack.addTagElement(tagName, FloatTag.valueOf(value));
	}
	
	public static long getLong(ItemStack stack, String tagName, long defaultValue)
	{
		CompoundTag nbt = stack.getTag();
		return nbt == null || !nbt.contains(tagName) ? defaultValue : nbt.getLong(tagName);
	}
	
	public static void setLong(ItemStack stack, String tagName, long value)
	{
		stack.addTagElement(tagName, LongTag.valueOf(value));
	}
	
	public static void growStack(ItemStack source, int size, List<ItemStack> output)
	{
		while(size > 0)
		{
			ItemStack stack = source.copy();
			stack.setCount(Math.min(size, stack.getMaxStackSize()));
			output.add(stack);
			size -= stack.getCount();
		}
	}
	
	public static void addTooltip(ItemStack stack, Component s)
	{
		CompoundTag nbt = stack.getOrCreateTagElement("display");
		ListTag list = nbt.getList("Lore", 8);
		list.add(StringTag.valueOf(Component.Serializer.toJson(s)));
		nbt.put("Lore", list);
	}
	
	public static void addTooltips(ItemStack stack, List<Component> add)
	{
		CompoundTag nbt = stack.getOrCreateTagElement("display");
		ListTag list = nbt.getList("Lore", 8);
		for(Component entry : add)
		{
			list.add(StringTag.valueOf(Component.Serializer.toJson(entry)));
		}
		nbt.put("Lore", list);
	}
	
	public static ItemStack getArrowStack(AbstractArrow arrow)
	{
		try
		{
			return ((ArrowMixin)arrow).getArrowItem();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(arrow instanceof SpectralArrow)
		{
			return new ItemStack(Items.SPECTRAL_ARROW);
		}
		else if(arrow instanceof Arrow)
		{
			CompoundTag nbt = new CompoundTag();
			arrow.saveWithoutId(nbt);
			if(nbt.contains("CustomPotionEffects"))
			{
				ItemStack stack = new ItemStack(Items.TIPPED_ARROW);
				stack.addTagElement("CustomPotionEffects", nbt.get("CustomPotionEffects"));
				return stack;
			}
			return new ItemStack(Items.ARROW);
		}
		return ItemStack.EMPTY;
	}
	
	public static boolean isOre(BlockState state)
	{
		return ForgeRegistries.ITEMS.tags().getTag(Tags.Items.ORES).contains(state.getBlock().asItem());
	}
	
	public static void addOrDrop(Player player, List<ItemStack> stacks) 
	{
		for(ItemStack stack : stacks) 
		{
			addOrDrop(player, stack);
		}
	}
	
	public static void addOrDrop(Player player, ItemStack stack)
	{
		if(stack.isEmpty()) return;
		if(!player.addItem(stack)) player.drop(stack, false);
		else player.containerMenu.broadcastChanges();
	}
	
	public static int evenDistribute(int xpPoints, RandomSource rand, List<ItemStack> items, ObjIntFunction<ItemStack> consumer)
	{
		int left = xpPoints;
		while(left >= items.size() && items.size() > 0)
		{
			int xpPerItem = left / items.size();
			for(int i = 0;i<items.size();i++)
			{
				int consumed = consumer.apply(items.get(i), xpPerItem);
				if(consumed <= xpPerItem) items.remove(i--);
				left-=consumed;
			}
		}
		if(!items.isEmpty())
		{
			int startIndex = rand.nextInt(items.size());
			while(left > 0 && items.size() > 0)
			{
				int consumed = consumer.apply(items.get(startIndex), 1);
				if(consumed <= 0) items.remove(startIndex--);
				left-=consumed;
				if(items.isEmpty()) break;
				startIndex = ++startIndex % items.size();
			}
		}
		return xpPoints - left;
	}
	
	public static void scanInventory(IItemHandler handler, boolean recursion, Predicate<ItemStack> filter, Consumer<ItemStack> action) 
	{
		for(int i = 0,m=handler.getSlots();i<m;i++)
		{
			ItemStack stack = handler.extractItem(i, 1, true);
			if(stack.isEmpty()) continue;
			if(filter.test(stack))
			{
				action.accept(stack);
			}
			else if(recursion)
			{
				LazyOptional<IItemHandler> subHandler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER);
				if(!subHandler.isPresent()) continue;
				scanInventory(subHandler.orElse(EmptyHandler.INSTANCE), false, filter, action);
			}
		}
	}
	
	public static int consumeItems(Player player, ToIntFunction<ItemStack> validator, int limit)
	{
		int found = 0;
		NonNullList<ItemStack> inv = player.getInventory().items;
		for(int i = 0,m=inv.size();i<m;i++)
		{
			ItemStack stack = inv.get(i);
			int value = validator.applyAsInt(stack);
			if(value <= 0)
			{
				continue;
			}
			int left = limit - found;
			if(left >= stack.getCount() * value)
			{
				found+=stack.getCount() * value;
				if(stack.hasCraftingRemainingItem())
				{
					inv.set(i, stack.getCraftingRemainingItem());
					continue;
				}
				inv.set(i, ItemStack.EMPTY);
			}
			else if(left / value > 0)
			{
				stack.shrink(left / value);
				found += left;
			}
			if(found >= limit)
			{
				break;
			}
		}
		return found;
	}
	
	public static int hasBlockCount(Level world, BlockPos pos, int limit, ToIntFunction<BlockState> validator)
	{
		MutableBlockPos newPos = new MutableBlockPos();
		int found = 0;
		for(int y = 0;y<=1;y++)
		{
			for(int x = -4;x<=4;x++)
			{
				for(int z = -4;z<=4;z++)
				{
					found += validator.applyAsInt(world.getBlockState(newPos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z)));
					if(found >= limit)
					{
						return limit;
					}
				}
			}
		}
		return found;
	}
}
