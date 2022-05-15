package uniquebase.utils;

import java.util.List;
import java.util.Random;
import java.util.function.ToIntFunction;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import uniquebase.UEBase;
import uniquebase.utils.mixin.common.entity.ArrowMixin;

public class StackUtils
{
	public static int getInt(ItemStack stack, String tagName, int defaultValue)
	{
		CompoundNBT nbt = stack.getTag();
		return nbt == null || !nbt.contains(tagName) ? defaultValue : nbt.getInt(tagName);
	}
	
	public static void setInt(ItemStack stack, String tagName, int value)
	{
		stack.addTagElement(tagName, IntNBT.valueOf(value));
	}
	
	public static long getLong(ItemStack stack, String tagName, long defaultValue)
	{
		CompoundNBT nbt = stack.getTag();
		return nbt == null || !nbt.contains(tagName) ? defaultValue : nbt.getLong(tagName);
	}
	
	public static void setLong(ItemStack stack, String tagName, long value)
	{
		stack.addTagElement(tagName, LongNBT.valueOf(value));
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
	
	public static ItemStack getArrowStack(AbstractArrowEntity arrow)
	{
		try
		{
			ItemStack stack = ((ArrowMixin)arrow).getArrowItem();
			if(stack == null) {
				if(UEBase.LOG_BROKEN_MODS.get()) {
					boolean mcCreator = ObjectArrayList.wrap(arrow.getClass().getName().split("\\.")).contains("mcreator");
					ResourceLocation id = arrow.getType().getRegistryName();
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
		if(arrow instanceof SpectralArrowEntity)
		{
			return new ItemStack(Items.SPECTRAL_ARROW);
		}
		else if(arrow instanceof ArrowEntity)
		{
			CompoundNBT nbt = new CompoundNBT();
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
		return Tags.Items.ORES.contains(state.getBlock().asItem());
	}
	
	public static int evenDistribute(int xpPoints, Random rand, List<ItemStack> items, ObjIntFunction<ItemStack> consumer)
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
	
	public static int consumeItems(PlayerEntity player, ToIntFunction<ItemStack> validator, int limit)
	{
		int found = 0;
		NonNullList<ItemStack> inv = player.inventory.items;
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
				if(stack.getItem().hasContainerItem(stack))
				{
					inv.set(i, stack.getItem().getContainerItem(stack));
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
	
	public static int hasBlockCount(World world, BlockPos pos, int limit, ToIntFunction<BlockState> validator)
	{
		Mutable newPos = new Mutable();
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
