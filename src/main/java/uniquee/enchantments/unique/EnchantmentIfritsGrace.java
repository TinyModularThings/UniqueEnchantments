package uniquee.enchantments.unique;

import java.util.function.ToIntFunction;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.LootBonusEnchantment;
import net.minecraft.enchantment.SilkTouchEnchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IGraceEnchantment;
import uniquee.utils.IntStat;

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
	public static IntStat SCALAR = new IntStat(10, "scalar");
	
	public EnchantmentIfritsGrace()
	{
		super(new DefaultData("ifrits_grace", Rarity.RARE, true, 14, 4, 40), EnchantmentType.DIGGER, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 3;
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof LootBonusEnchantment || ench instanceof SilkTouchEnchantment || ench instanceof EnchantmentMidasBlessing ? false : super.canApplyTogether(ench);
	}

	@Override
	public void loadData(Builder config)
	{
		SCALAR.handleConfig(config);
		LAVA_ITEMS.clear();
		LAVA_ITEMS.put(Items.LAVA_BUCKET, 250);
		LAVA_ITEMS.put(Items.MAGMA_CREAM, 20);
	}
}
