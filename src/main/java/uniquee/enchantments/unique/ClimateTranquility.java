package uniquee.enchantments.unique;

import java.util.Set;
import java.util.UUID;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.BiomeDictionary;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;
import uniquebase.utils.EnchantmentContainer;
import uniquebase.utils.IntStat;
import uniquee.UniqueEnchantments;

public class ClimateTranquility extends UniqueEnchantment
{
	public static final UUID SPEED_UUID = UUID.fromString("7b8a3791-8f94-4127-82b2-26418679d551");
	public static final DoubleLevelStats SPEED_BONUS = new DoubleLevelStats("speed_bonus", 0.1D, 0.03D);
	public static final IntStat SLOW_TIME = new IntStat(30, "slow_duration");
	
	public static final UUID ATTACK_UUID = UUID.fromString("b47bd399-5ad0-4f1d-a0eb-0a9146ff1734");
	public static final DoubleLevelStats ATTACK_BONUS = new DoubleLevelStats("attack_bonus", 0.1D, 0.03D);
	public static final IntStat BURN_TIME = new IntStat(1, "burn_time");
	
	public ClimateTranquility()
	{
		super(new DefaultData("climate_tranquility", Rarity.RARE, 3, true, true, 20, 7, 30), EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND);
		addStats(SPEED_BONUS, SLOW_TIME, ATTACK_BONUS, BURN_TIME);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || EnchantmentType.BOW.canEnchantItem(stack.getItem());
	}
	
	public static void onClimate(PlayerEntity player, EnchantmentContainer container)
	{
		Object2IntMap.Entry<EquipmentSlotType> slot = container.getEnchantedItem(UniqueEnchantments.CLIMATE_TRANQUILITY);
		AttributeModifierManager map = player.getAttributeManager();
		ModifiableAttributeInstance speed = map.createInstanceIfAbsent(Attributes.ATTACK_SPEED);
		ModifiableAttributeInstance damage = map.createInstanceIfAbsent(Attributes.ATTACK_DAMAGE);
		if(slot.getIntValue() <= 0)
		{
			speed.removeModifier(SPEED_UUID);
			damage.removeModifier(ATTACK_UUID);
			return;
		}
		int level = slot.getIntValue();
		Set<BiomeDictionary.Type> effects = BiomeDictionary.getTypes(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, player.world.getBiome(player.getPosition()).getRegistryName()));
		boolean hasHot = effects.contains(BiomeDictionary.Type.HOT) || effects.contains(BiomeDictionary.Type.NETHER);
		boolean hasCold = effects.contains(BiomeDictionary.Type.COLD);
		if(hasHot && !hasCold)
		{
			AttributeModifier speedMod = new AttributeModifier(SPEED_UUID, "Climate Boost", SPEED_BONUS.getAsDouble(level), Operation.MULTIPLY_TOTAL);
			if(!speed.hasModifier(speedMod))
			{
				speed.applyNonPersistentModifier(speedMod);
			}
		}
		else
		{
			speed.removeModifier(SPEED_UUID);
		}
		if(hasCold && !hasHot)
		{
			AttributeModifier damageMod = new AttributeModifier(ATTACK_UUID, "Climate Boost", ATTACK_BONUS.getAsDouble(level), Operation.MULTIPLY_TOTAL);
			if(!damage.hasModifier(damageMod))
			{
				damage.applyNonPersistentModifier(damageMod);
			}
		}
		else
		{
			damage.removeModifier(ATTACK_UUID);
		}
	}
}
