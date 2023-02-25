package uniqueebattle.enchantments.simple;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;

public class SagesGrace extends UniqueEnchantment {

	public SagesGrace()
	{
		super(new DefaultData("sages_grace", Rarity.COMMON, 5, false, true, 10, 10, 50), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		setCategory("battle");
	}
	
	@Override
	public void loadIncompats()
	{
//		addIncompats(UEBattle.ARES_FRAGMENT);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem && stack.getItem() instanceof BowItem && stack.getItem() instanceof HoeItem || stack.getItem() instanceof TridentItem;
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack) {
		return !stack.isDamageableItem();
	}
}
