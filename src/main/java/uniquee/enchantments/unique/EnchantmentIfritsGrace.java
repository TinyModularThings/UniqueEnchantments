package uniquee.enchantments.unique;

import java.util.function.ToIntFunction;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLootBonus;
import net.minecraft.enchantment.EnchantmentUntouching;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IGraceEnchantment;

public class EnchantmentIfritsGrace extends UniqueEnchantment implements IGraceEnchantment
{
	public static Object2IntMap<Item> LAVA_ITEMS = new Object2IntOpenHashMap<Item>();
	public static ToIntFunction<ItemStack> VALIDATOR = new ToIntFunction<ItemStack>(){
		@Override
		public int applyAsInt(ItemStack value)
		{
			return LAVA_ITEMS.getInt(value.getItem());
		}
	};
	public static String LAVA_COUNT = "lava_storage";
	public static int SCALAR = 10;
	
	public EnchantmentIfritsGrace()
	{
		super(new DefaultData("ifrits_grace", Rarity.RARE, 3, true, 14, 4, 40), EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof EnchantmentLootBonus || ench instanceof EnchantmentUntouching || ench instanceof EnchantmentMidasBlessing ? false : super.canApplyTogether(ench);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		SCALAR = config.get(getConfigName(), "scalar", 10).getInt();
		LAVA_ITEMS.clear();
		LAVA_ITEMS.put(Items.LAVA_BUCKET, 250);
		LAVA_ITEMS.put(Items.MAGMA_CREAM, 20);
		String[] items = config.get(getConfigName(), "lava_items", new String[]{Items.LAVA_BUCKET.getRegistryName().toString()+";"+250}).getStringList();
		for(int i = 0,m=items.length;i<m;i++)
		{
			String[] values = items[i].split(";");
			Item item = Item.getByNameOrId(values[0]);
			if(item == null || item == Items.AIR)
			{
				continue;
			}
			try
			{
				LAVA_ITEMS.putIfAbsent(item, Integer.parseInt(values[1]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
}
