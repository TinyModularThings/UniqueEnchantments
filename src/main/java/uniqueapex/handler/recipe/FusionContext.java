package uniqueapex.handler.recipe;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import uniquebase.UEBase;
import uniquebase.utils.MiscUtil;

public final class FusionContext extends Inventory
{
	IItemHandler[] originals;
	IInventory[] copies;
	IItemHandler mainChest;
	
	public FusionContext(IItemHandler mainChest, IItemHandler[] originals)
	{
		super(0);
		this.mainChest = mainChest;
		this.originals = originals;
		copies = copy(originals);
	}
	
	private IInventory[] copy(IItemHandler[] input)
	{
		IInventory[] result = new IInventory[input.length];
		for(int i = 0;i<input.length;i++)
		{
			IInventory inventory = new Inventory(input[i].getSlots()) {
				@Override
				public int getMaxStackSize() { return Integer.MAX_VALUE; }
			};
			for(int j = 0,m=input[i].getSlots();j<m;j++)
			{
				inventory.setItem(j, input[i].extractItem(j, Integer.MAX_VALUE, true));
			}
			result[i] = inventory;
		}
		return result;
	}
	
	public IItemHandler[] createWrapper(IInventory[] input)
	{
		IItemHandler[] result = new IItemHandler[input.length];
		for(int i = 0;i<input.length;i++)
		{
			IInventory inventory = new Inventory(input[i].getContainerSize()) {
				@Override
				public int getMaxStackSize() { return Integer.MAX_VALUE; }
			};
			for(int j = 0,m=input[i].getContainerSize();j<m;j++)
			{
				inventory.setItem(j, input[i].getItem(j).copy());
			}
			result[i] = new InvWrapper(inventory);
		}
		return result;		
	}
	
	public void applyEnchantment(Enchantment ench, int level, boolean lock, int max)
	{
		ItemStack stack = mainChest.getStackInSlot(0);
		ListNBT list = stack.getEnchantmentTags();
		while(list.size() > max && !list.isEmpty()) {
			list.remove(list.size()-1);
		}
		Map<Enchantment, Integer> enchs = EnchantmentHelper.getEnchantments(stack);
		enchs.put(ench, level+(enchs.getOrDefault(ench, 0) == level ? 1 : 0));
		boolean replace = false;
		if(stack.getItem() == Items.ENCHANTED_BOOK) stack.removeTagKey("StoredEnchantments");
		else if(stack.getItem() == Items.BOOK)
		{
			stack = new ItemStack(Items.ENCHANTED_BOOK);
			replace = true;
		}
		EnchantmentHelper.setEnchantments(enchs, stack);
		if(lock) {
			stack.addTagElement("fusioned", ByteNBT.ONE);
			stack.addTagElement("fusion_backup", stack.getEnchantmentTags().copy());
		}
		if(replace)
		{
			mainChest.extractItem(0, Integer.MAX_VALUE, false);
			mainChest.insertItem(0, stack, false);
		}
		for(int i = 1,m=mainChest.getSlots();i<m;i++)
		{
			mainChest.extractItem(i, Integer.MAX_VALUE, false);
		}
	}
	
	public boolean isValid(Object2IntMap<Ingredient> items, Enchantment ench, List<Object2IntMap.Entry<Enchantment>> enchantments)
	{
		if(!isValidTool(ench)) return false;
		if(!containsItem(items, true)) return false;
		Object2IntMap<Enchantment> map = getEnchantmentInputs(new ObjectOpenHashSet<>(Lists.transform(enchantments, Entry::getKey)));
		for(int i = 0,m=enchantments.size();i<m;i++)
		{
			Object2IntMap.Entry<Enchantment> entry = enchantments.get(i);
			if(map.getInt(entry.getKey()) < entry.getIntValue()) {
				UEBase.LOGGER.info("Recipe Fail 3: "+entry.getKey().getRegistryName()+", "+map.getInt(entry.getKey())+", "+entry.getIntValue());
				return false;
			}
		}
		return true;
	}
	
	public boolean isValidTool(Enchantment ench)
	{
		ItemStack stack = mainChest.getStackInSlot(0);
		return (!stack.hasTag() || !stack.getTag().getBoolean("fusioned"));
	}
	
	public Object2IntMap<Enchantment> getEnchantmentInputs(Set<Enchantment> enchantments)
	{
		Object2IntMap<Enchantment> entries = new Object2IntLinkedOpenHashMap<>();
		for(int i = 1,m=mainChest.getSlots();i<m;i++)
		{
			ItemStack stack = mainChest.getStackInSlot(i);
			if(stack.isEmpty()) continue;
			Object2IntMap.Entry<Enchantment> entry = MiscUtil.getFirstEnchantment(stack);
			if(entry != null) entries.putIfAbsent(entry.getKey(), entry.getIntValue());
		}
		entries.keySet().retainAll(enchantments);
		return entries;
	}
	
	public boolean containsItem(Object2IntMap<Ingredient> items, boolean simulate)
	{
		IItemHandler[] toProcess = simulate ? createWrapper(copies) : originals;
		for(int i = 0;i<4;i++)
		{
			IItemHandler handler = toProcess[i];
			for(Entry<Ingredient> entry : Object2IntMaps.fastIterable(items))
			{
				Ingredient target = entry.getKey();
				int toConsume = entry.getIntValue();
				for(int j = 0,m=handler.getSlots();j<m && toConsume > 0;j++)
				{
					ItemStack stack = handler.getStackInSlot(j);
					if(stack.isEmpty() || !target.test(stack)) continue;
					toConsume -= handler.extractItem(j, toConsume, false).getCount();
				}
				if(toConsume > 0) {
					UEBase.LOGGER.info("Missing: "+toConsume+", "+target.getItems()[0]);
					return false;
				}
			}
		}
		return true;
	}
}
