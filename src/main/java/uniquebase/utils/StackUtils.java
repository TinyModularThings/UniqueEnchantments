package uniquebase.utils;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.oredict.OreDictionary;

@SuppressWarnings("deprecation")
public class StackUtils
{
	public static int getInt(ItemStack stack, String tagName, int defaultValue)
	{
		NBTTagCompound nbt = stack.getTagCompound();
		return nbt == null || !nbt.hasKey(tagName) ? defaultValue : nbt.getInteger(tagName);
	}
	
	public static void setInt(ItemStack stack, String tagName, int value)
	{
		stack.setTagInfo(tagName, new NBTTagInt(value));
	}
	
	public static long getLong(ItemStack stack, String tagName, long defaultValue)
	{
		NBTTagCompound nbt = stack.getTagCompound();
		return nbt == null || !nbt.hasKey(tagName) ? defaultValue : nbt.getLong(tagName);
	}
	
	public static void setLong(ItemStack stack, String tagName, long value)
	{
		stack.setTagInfo(tagName, new NBTTagLong(value));
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
	
	public static ItemStack getArrowStack(EntityArrow arrow)
	{
		try
		{
			//For Every ASM user. No. Anti ASM Guy writing this. Aka no ASM coming here. Live with it.
			return (ItemStack)ReflectionHelper.findMethod(EntityArrow.class, "getArrowStack", "func_184550_j").invoke(arrow, new Object[0]);
		}
		catch(Exception e)
		{
		}
		if(arrow instanceof EntitySpectralArrow)
		{
			return new ItemStack(Items.SPECTRAL_ARROW);
		}
		else if(arrow instanceof EntityTippedArrow)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			arrow.writeEntityToNBT(nbt);
			if(nbt.hasKey("CustomPotionEffects"))
			{
				ItemStack stack = new ItemStack(Items.TIPPED_ARROW);
				stack.setTagInfo("CustomPotionEffects", nbt.getTag("CustomPotionEffects"));
				return stack;
			}
			return new ItemStack(Items.ARROW);
		}
		return ItemStack.EMPTY;
	}
	
	public static boolean isOre(IBlockState state)
	{
		Item item = Item.getItemFromBlock(state.getBlock());
		if(item == Items.AIR)
		{
			return false;
		}
		//Correct way to extract meta-data out of a BlockState. This is accurate from 1.12 backwards to most versions I know. (1.4.7 I think)
		for(int id : OreDictionary.getOreIDs(new ItemStack(item, 1, item.getMetadata(state.getBlock().getMetaFromState(state)))))
		{
			if(OreDictionary.getOreName(id).startsWith("ore"))
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean isGem(IBlockState state)
	{
		Item item = Item.getItemFromBlock(state.getBlock());
		if(item == Items.AIR)
		{
			return false;
		}
		//Correct way to extract meta-data out of a BlockState. This is accurate from 1.12 backwards to most versions I know. (1.4.7 I think)
		for(int id : OreDictionary.getOreIDs(new ItemStack(item, 1, item.getMetadata(state.getBlock().getMetaFromState(state)))))
		{
			String gem = OreDictionary.getOreName(id);
			if(gem.startsWith("ore") && OreDictionary.doesOreNameExist("gem"+gem.substring(3)))
			{
				return true;
			}
		}
		return false;
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
	
	public static int consumeItems(EntityPlayer player, ToIntFunction<ItemStack> validator, int limit)
	{
		int found = 0;
		NonNullList<ItemStack> inv = player.inventory.mainInventory;
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
	
	public static boolean hasBlockCount(World world, BlockPos pos, int limit, Predicate<IBlockState> validator)
	{
		MutableBlockPos newPos = new MutableBlockPos();
		int found = 0;
		for(int y = 0;y<=1;y++)
		{
			for(int x = -4;x<=4;x++)
			{
				for(int z = -4;z<=4;z++)
				{
					newPos.setPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
					if(validator.test(world.getBlockState(newPos)))
					{
						found++;
						if(found >= limit)
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
