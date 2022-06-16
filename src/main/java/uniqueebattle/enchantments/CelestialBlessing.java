package uniqueebattle.enchantments;

import java.util.UUID;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;

public class CelestialBlessing extends UniqueEnchantment
{
	public static final String CELESTIAL_DAY = "celestial_day";
	public static final UUID SPEED_MOD = UUID.fromString("ce9b483d-1091-4f34-b09b-b05cc867c8db");
	public static final DoubleLevelStats SPEED_BONUS = new DoubleLevelStats("speed_bonus", 0.08D, 0.04D);
	
	public CelestialBlessing()
	{
		super(new DefaultData("celestial_blessing", Rarity.UNCOMMON, 3, false, false, 20, 5, 15).setTrancendenceLevel(200), EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
		addStats(SPEED_BONUS);
		setCategory("battle");
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(new ResourceLocation("uniquee", "climate_tranquility"));
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof BowItem || stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem || stack.getItem() instanceof CrossbowItem;
	}
}