package uniquee.enchantments.complex;

import java.util.List;
import java.util.Map.Entry;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.EnchantmentContainer;
import uniquebase.utils.IdStat;
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
	public static final IdStat VALID_ENCHANTMENTS = new IdStat("repaireable_enchantments", ForgeRegistries.ENCHANTMENTS);
	
	public EnderMending()
	{
		super(new DefaultData("ender_mending", Rarity.VERY_RARE, 3, true, 20, 10, 5), EnchantmentType.BREAKABLE, EquipmentSlotType.values());
		addStats(THRESHOLD, LIMIT, LIMIT_MULTIPLIER, VALID_ENCHANTMENTS);
	}
	
	public void loadIncompats()
	{
		addIncompats(Enchantments.MENDING, UniqueEnchantments.ENDERMARKSMEN, UniqueEnchantments.WARRIORS_GRACE, UniqueEnchantments.ECOLOGICAL);
		VALID_ENCHANTMENTS.addDefault(Enchantments.MENDING, UniqueEnchantments.ENDER_MENDING, UniqueEnchantments.GRIMOIRE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof CrossbowItem;
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return EnchantmentType.ARMOR.canEnchantItem(stack.getItem());
	}
	
	public static void shareXP(PlayerEntity player, EnchantmentContainer container)
	{
		EquipmentSlotType hasSlot = null;
		int stored = 0;
		for(Entry<EquipmentSlotType, Object2IntMap<Enchantment>> entry : container.getAll())
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
			if(stack.isEmpty() || !stack.isDamaged()) continue;
			Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
			for(Enchantment ench : enchantments.keySet())
			{
				if(!VALID_ENCHANTMENTS.contains(ench.getRegistryName())) continue;
				found.add(stack);
				break;
			}
		}
		int last = stored;
		stored -= StackUtils.evenDistribute(stored, player.world.rand, found, (stack, i) -> {
			int used = Math.min(i, stack.getDamage());
            stack.setDamage(stack.getDamage() - used);
            return used;
		});
		if(last != stored) StackUtils.setInt(player.getItemStackFromSlot(hasSlot), EnderMending.ENDER_TAG, stored);
	}
}