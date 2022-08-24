package uniquebase.utils;

import java.util.List;
import java.util.function.ToIntFunction;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.UEBase;
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
	
	public static ItemStack getArrowStack(AbstractArrow arrow)
	{
		try
		{
			ItemStack stack = ((ArrowMixin)arrow).getArrowItem();
			if(stack == null) {
				if(UEBase.LOG_BROKEN_MODS.get()) {
					boolean mcCreator = ObjectArrayList.wrap(arrow.getClass().getName().split("\\.")).contains("mcreator");
					ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(arrow.getType());
					if(mcCreator) UEBase.LOGGER.info("A MCreator mod ["+id.getNamespace()+"] with a broken Custom Projectile has been found. Please make sure to ask them to export the said mod again with the MCreator 2020.1 again, if it has been released already.");
					else UEBase.LOGGER.info("Entity ["+id.getPath()+"] from the mod ["+id.getNamespace()+"] creates a Null-ItemStack in a custom Projectile. Please report it to the said Mod that they should fix this. For the modder: getItem/getArrowStack/getPickupItem is the function they need to fix.");
				}
				return ItemStack.EMPTY;
			}
			return stack;
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
