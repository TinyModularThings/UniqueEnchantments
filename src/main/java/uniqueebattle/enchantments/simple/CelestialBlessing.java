package uniqueebattle.enchantments.simple;

import java.util.UUID;

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

public class CelestialBlessing extends UniqueEnchantment
{
	public static final String CELESTIAL_DAY = "celestial_day";
	public static final UUID SPEED_MOD = UUID.fromString("ce9b483d-1091-4f34-b09b-b05cc867c8db");
	public static final DoubleLevelStats SPEED_BONUS = new DoubleLevelStats("speed_bonus", 0.08D, 0.04D);
	
	public CelestialBlessing()
	{
		super(new DefaultData("hecates_blessing", Rarity.UNCOMMON, 3, false, false, 20, 5, 15).setTrancendenceLevel(200), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
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