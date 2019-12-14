package uniquee.enchantments.unique;

import java.util.Set;
import java.util.function.ToIntFunction;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLootBonus;
import net.minecraft.enchantment.EnchantmentUntouching;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IBlessingEnchantment;

public class EnchantmentMidasBlessing extends UniqueEnchantment implements IBlessingEnchantment
{
	static Set<Item> VALID_ITEMS = new ObjectOpenHashSet<Item>();
	public static ToIntFunction<ItemStack> VALIDATOR = new ToIntFunction<ItemStack>(){
		@Override
		public int applyAsInt(ItemStack value)
		{
			//Could implement ore-dictionary but honestly. Really needed? Doubt it!
			return value.getItem() == Items.GOLD_INGOT || VALID_ITEMS.contains(value.getItem()) ? 1 : 0;
		}
	};
	public static String GOLD_COUNTER = "gold_storage";
	public static int LEVEL_SCALAR = 6;
	public static int BASE_COST = 2;
	
	public EnchantmentMidasBlessing()
	{
		super(new DefaultData("midas_blessing", Rarity.VERY_RARE, true, 22, 2, 75), EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 3;
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return !(stack.getItem() instanceof ItemPickaxe);
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof EnchantmentLootBonus || ench instanceof EnchantmentUntouching || ench instanceof EnchantmentIfritsGrace ? false : super.canApplyTogether(ench);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		LEVEL_SCALAR = config.get(getConfigName(), "level_scalar", 6).getInt();
		BASE_COST = config.get(getConfigName(), "gold_cost", 2).getInt();
		VALID_ITEMS.clear();
		String[] items = config.get(getConfigName(), "optional_gold_items", new String[0], "Optional Items that can be used as gold replacement. No meta-nbt support").getStringList();
		for(int i = 0,m=items.length;i<m;i++)
		{
			Item item = Item.getByNameOrId(items[i]);
			if(item == Items.AIR || item == null)
			{
				continue;
			}
			VALID_ITEMS.add(item);
		}
	}
	
}
