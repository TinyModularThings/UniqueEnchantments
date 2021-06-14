package uniqueebattle.enchantments;

import java.util.UUID;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleLevelStats;

public class EnchantmentCelestialBlessing extends UniqueEnchantment
{
	public static final UUID SPEED_MOD = UUID.fromString("ce9b483d-1091-4f34-b09b-b05cc867c8db");
	public static final DoubleLevelStats SPEED_BONUS = new DoubleLevelStats("speed_bonus", 0.02D, 0.06D);
	
	public EnchantmentCelestialBlessing()
	{
		super(new DefaultData("celestial_blessing", Rarity.UNCOMMON, 3, false, 14, 5, 15), EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
		setCategory("battle");
	}
	
	@Override
	public void loadIncompats()
	{
		addIncomats(new ResourceLocation("uniuqee", "climate_tranquility"));
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof BowItem || stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem;
	}
	
	@Override
	public void loadData(ForgeConfigSpec.Builder config)
	{
		SPEED_BONUS.handleConfig(config, getConfigName());
	}
	
}
