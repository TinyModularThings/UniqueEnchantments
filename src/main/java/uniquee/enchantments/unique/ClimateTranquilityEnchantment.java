package uniquee.enchantments.unique;

import java.util.Set;
import java.util.UUID;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;
import uniquee.utils.IntStat;
import uniquee.utils.MiscUtil;

public class ClimateTranquilityEnchantment extends UniqueEnchantment
{
	public static UUID SPEED_UUID = UUID.fromString("7b8a3791-8f94-4127-82b2-26418679d551");
	public static DoubleStat SPEED_SCALE = new DoubleStat(0.1D, "speed_scale");
	public static IntStat SLOW_TIME = new IntStat(30, "slow_time");
	
	public static UUID ATTACK_UUID = UUID.fromString("b47bd399-5ad0-4f1d-a0eb-0a9146ff1734");
	public static DoubleStat ATTACK_SCALE = new DoubleStat(0.1D, "attack_scale");
	public static IntStat BURN_TIME = new IntStat(1, "burn_time");
	
	public ClimateTranquilityEnchantment()
	{
		super(new DefaultData("climate_tranquility", Rarity.RARE, 3, true, 20, 2, 12), EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || EnchantmentType.BOW.canEnchantItem(stack.getItem());
	}
		
	@Override
	public void loadData(Builder config)
	{
		SPEED_SCALE.handleConfig(config);
		SLOW_TIME.handleConfig(config);
		
		ATTACK_SCALE.handleConfig(config);
		BURN_TIME.handleConfig(config);
	}
	
	
	public static void onClimate(PlayerEntity player)
	{
		Object2IntMap.Entry<EquipmentSlotType> slot = MiscUtil.getEnchantedItem(UniqueEnchantments.CLIMATE_TRANQUILITY, player);
		ModifiableAttributeInstance speed = player.getAttribute(Attributes.ATTACK_SPEED);
		ModifiableAttributeInstance damage = player.getAttribute(Attributes.ATTACK_DAMAGE);
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
			AttributeModifier speedMod = new AttributeModifier(SPEED_UUID, "Climate Boost", (SPEED_SCALE.get() * level), Operation.MULTIPLY_TOTAL);
			if(!speed.hasModifier(speedMod))
			{
				speed.applyPersistentModifier(speedMod);
			}
		}
		else
		{
			speed.removeModifier(SPEED_UUID);
		}
		if(hasCold && !hasHot)
		{
			AttributeModifier damageMod = new AttributeModifier(ATTACK_UUID, "Climate Boost", (ATTACK_SCALE.get() * level), Operation.MULTIPLY_TOTAL);
			if(!damage.hasModifier(damageMod))
			{
				damage.applyPersistentModifier(damageMod);
			}
		}
		else
		{
			damage.removeModifier(ATTACK_UUID);
		}
	}
}
