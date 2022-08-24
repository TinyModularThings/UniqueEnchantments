package uniqueebattle.enchantments.complex;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;

public class IfritsBlessing extends UniqueEnchantment
{
	public static final DoubleLevelStats BONUS_DAMAGE = new DoubleLevelStats("bonus_damage", 0.2D, 0.6D);
	
	public IfritsBlessing()
	{
		super(new DefaultData("ifrits_blessing", Rarity.COMMON, 5, false, true, 12, 2, 60), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		setCategory("battle");
		addStats(BONUS_DAMAGE);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(new ResourceLocation("uniquee", "berserk"));
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof BowItem || stack.getItem() instanceof AxeItem || stack.getItem() instanceof CrossbowItem || stack.getItem() instanceof HoeItem;
	}
}
