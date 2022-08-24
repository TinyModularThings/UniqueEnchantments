package uniquee.enchantments.complex;

import java.util.List;
import java.util.Map.Entry;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.EnchantmentContainer;
import uniquebase.utils.IdStat;
import uniquebase.utils.IntStat;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;
import uniquee.UE;

public class EnderMending extends UniqueEnchantment
{
	public static final String ENDER_TAG = "ender_mending";
	public static final DoubleStat THRESHOLD = new DoubleStat(1D, "threshold");
	public static final IntStat LIMIT = new IntStat(250, "storage_limit");
	public static final DoubleStat LIMIT_MULTIPLIER = new DoubleStat(1.72, "storage_limit_multiplier");
	public static final IdStat<Enchantment> VALID_ENCHANTMENTS = new IdStat<>("repaireable_enchantments", ForgeRegistries.ENCHANTMENTS);
	
	public EnderMending()
	{
		super(new DefaultData("ender_mending", Rarity.VERY_RARE, 3, true, false, 20, 10, 5), EnchantmentCategory.BREAKABLE, EquipmentSlot.values());
		addStats(THRESHOLD, LIMIT, LIMIT_MULTIPLIER, VALID_ENCHANTMENTS);
	}
	
	public void loadIncompats()
	{
		addIncompats(Enchantments.MENDING, UE.ENDERMARKSMEN, UE.WARRIORS_GRACE, UE.ECOLOGICAL);
		VALID_ENCHANTMENTS.addDefault(Enchantments.MENDING, UE.ENDER_MENDING, UE.GRIMOIRE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof CrossbowItem;
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return EnchantmentCategory.ARMOR.canEnchant(stack.getItem());
	}
	
	public static void shareXP(Player player, EnchantmentContainer container)
	{
		EquipmentSlot hasSlot = null;
		int stored = 0;
		for(Entry<EquipmentSlot, Object2IntMap<Enchantment>> entry : container.getAll())
		{
			int level = entry.getValue().getInt(UE.ENDER_MENDING);
			if(level > 0)
			{
				double xpProvided = 1D/(1D+(THRESHOLD.get(level)/100D));
				int required = (int)(Mth.ceil(EnderMending.LIMIT.get() * Math.pow(EnderMending.LIMIT_MULTIPLIER.get(), Math.sqrt(level))) * xpProvided);
				int found = StackUtils.getInt(player.getItemBySlot(entry.getKey()), EnderMending.ENDER_TAG, 0);
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
		for(int i = 0,m=player.getInventory().getContainerSize();i<m;i++)
		{
			ItemStack stack = player.getInventory().getItem(i);
			if(stack.isEmpty() || !stack.isDamaged()) continue;
			Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
			for(Enchantment ench : enchantments.keySet())
			{
				if(!VALID_ENCHANTMENTS.contains(ench)) continue;
				found.add(stack);
				break;
			}
		}
		int last = stored;
		stored -= StackUtils.evenDistribute(stored, player.level.random, found, (stack, i) -> {
			int used = Math.min(i, stack.getDamageValue());
            stack.setDamageValue(stack.getDamageValue() - used);
            return used;
		});
		if(last != stored) StackUtils.setInt(player.getItemBySlot(hasSlot), EnderMending.ENDER_TAG, stored);
	}
}