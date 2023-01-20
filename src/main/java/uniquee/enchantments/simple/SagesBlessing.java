package uniquee.enchantments.simple;

import java.util.function.ToIntFunction;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UE;

public class SagesBlessing extends UniqueEnchantment
{
	public static final ToIntFunction<ItemStack> VALIDATOR = new ToIntFunction<ItemStack>(){
		@Override
		public int applyAsInt(ItemStack value)
		{
			return value.is(Items.EXPERIENCE_BOTTLE) || value.is(SAGES) ? 1 : 0;
		}
	};
	public static final TagKey<Item> SAGES = ItemTags.create(new ResourceLocation("uniquee", "sages_blessing"));
	public static final String SAGES_XP = "sages_xp";
	public static final DoubleStat XP_BOOST = new DoubleStat(0.1D, "xp_boost");
	public static final DoubleStat TRANSCENDED_BOOST = new DoubleStat(2.0, "transcended_exponent");
	
	public SagesBlessing()
	{
		super(new DefaultData("sages_blessing", Rarity.UNCOMMON, 5, false, false, 15, 5, 20).setTrancendenceLevel(500), EnchantmentCategory.BREAKABLE, EquipmentSlot.values());
		addStats(XP_BOOST, TRANSCENDED_BOOST);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UE.FAST_FOOD, Enchantments.SILK_TOUCH);
	}	
}