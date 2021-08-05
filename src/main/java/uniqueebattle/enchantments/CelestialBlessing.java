package uniqueebattle.enchantments;

import java.util.UUID;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleLevelStats;

public class CelestialBlessing extends UniqueEnchantment
{
	public static final UUID SPEED_MOD = UUID.fromString("ce9b483d-1091-4f34-b09b-b05cc867c8db");
	public static final DoubleLevelStats SPEED_BONUS = new DoubleLevelStats("speed_bonus", 0.08D, 0.04D);
	
	public CelestialBlessing()
	{
		super(new DefaultData("celestial_blessing", Rarity.UNCOMMON, 3, false, 14, 5, 15), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
		setCategory("battle");
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.CLIMATE_TRANQUILITY);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemBow || stack.getItem() instanceof ItemAxe || stack.getItem() instanceof ItemHoe;
	}
	
	@Override
	public void loadData(Configuration config)
	{
		SPEED_BONUS.handleConfig(config, getConfigName());
	}
	
}
