package uniquee.enchantments.unique;

import java.util.Set;
import java.util.function.ToIntFunction;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IBlessingEnchantment;
import uniquee.utils.IntStat;

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
	public static final IntStat LEVEL_SCALAR = new IntStat(6, "level_scalar");
	public static final IntStat BASE_COST = new IntStat(2, "gold_cost");
	
	public EnchantmentMidasBlessing()
	{
		super(new DefaultData("midas_blessing", Rarity.VERY_RARE, 3, true, 22, 2, 75), EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return !(stack.getItem() instanceof ItemPickaxe);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncomats(Enchantments.FORTUNE, Enchantments.SILK_TOUCH, UniqueEnchantments.IFRIDS_GRACE);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		LEVEL_SCALAR.handleConfig(config, getConfigName());
		BASE_COST.handleConfig(config, getConfigName());
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
