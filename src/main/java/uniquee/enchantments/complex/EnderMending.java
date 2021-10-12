package uniquee.enchantments.complex;

import java.util.List;
import java.util.Map.Entry;

import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Configuration;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.EnchantmentContainer;
import uniquebase.utils.IntStat;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;
import uniquee.UniqueEnchantments;

public class EnderMending extends UniqueEnchantment
{
	public static final String ENDER_TAG = "ender_mending";
	public static final DoubleStat THRESHOLD = new DoubleStat(1D, "threshold");
	public static final IntStat LIMIT = new IntStat(250, "storage_limit");
	public static final DoubleStat LIMIT_MULTIPLIER = new DoubleStat(1.72, "storage_limit_multiplier");
	public static final IntSet VALID_ENCHANTMENTS = new IntLinkedOpenHashSet();
	
	public EnderMending()
	{
		super(new DefaultData("ender_mending", Rarity.VERY_RARE, 3, true, 20, 10, 5), EnumEnchantmentType.BREAKABLE, EntityEquipmentSlot.values());
		addStats(THRESHOLD, LIMIT, LIMIT_MULTIPLIER);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(Enchantments.MENDING, UniqueEnchantments.ENDERMARKSMEN, UniqueEnchantments.WARRIORS_GRACE, UniqueEnchantments.ECOLOGICAL);
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return EnumEnchantmentType.ARMOR.canEnchantItem(stack.getItem());
	}
	
	@Override
	public void loadData(Configuration config)
	{
		String[] repairs = config.getStringList("repaireable_enchantments", getConfigName(), new String[]{Enchantments.MENDING.getRegistryName().toString(), UniqueEnchantments.ENDER_MENDING.getRegistryName().toString(), UniqueEnchantments.GRIMOIRE.getRegistryName().toString()}, "Enchantments should be allowed to repaired by Ender mending");
		VALID_ENCHANTMENTS.clear();
		for(int i = 0,m=repairs.length;i<m;i++)
		{
			try
			{
				VALID_ENCHANTMENTS.add(Enchantment.getEnchantmentID(Enchantment.getEnchantmentByLocation(repairs[i])));
			}
			catch(Exception e) {}
		}
	}
	
	public static void shareXP(EntityPlayer player, EnchantmentContainer container)
	{
		EntityEquipmentSlot hasSlot = null;
		int stored = 0;
		for(Entry<EntityEquipmentSlot, Object2IntMap<Enchantment>> entry : container.getAll())
		{
			int level = entry.getValue().getInt(UniqueEnchantments.ENDER_MENDING);
			if(level > 0)
			{
				double xpProvided = 1D/(1D+(THRESHOLD.get(level)/100D));
				int required = (int)(MathHelper.ceil(EnderMending.LIMIT.get() * Math.pow(EnderMending.LIMIT_MULTIPLIER.get(), Math.sqrt(level))) * xpProvided);
				int found = StackUtils.getInt(player.getItemStackFromSlot(entry.getKey()), EnderMending.ENDER_TAG, 0);
				if(found >= required)
				{
					hasSlot = entry.getKey();
					stored = found;
					break;
				}
			}
		}
		if(hasSlot == null) return;
		List<ItemStack> found = new ObjectArrayList<>();
		for(int i = 0,m=player.inventory.getSizeInventory();i<m;i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);
			if(stack.isEmpty() || !stack.isItemDamaged()) continue;
			Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
			for(Enchantment ench : enchantments.keySet())
			{
				if(!VALID_ENCHANTMENTS.contains(Enchantment.getEnchantmentID(ench))) continue;
				found.add(stack);
				break;
			}
		}
		int last = stored;
		stored -= StackUtils.evenDistribute(stored, player.world.rand, found, (stack, i) -> {
			int used = Math.min(i, stack.getItemDamage());
            stack.setItemDamage(stack.getItemDamage() - used);
            return used;
		});
		if(last != stored) StackUtils.setInt(player.getItemStackFromSlot(hasSlot), EnderMending.ENDER_TAG, stored);
	}
}