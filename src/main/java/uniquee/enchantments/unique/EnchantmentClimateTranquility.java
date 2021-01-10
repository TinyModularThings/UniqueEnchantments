package uniquee.enchantments.unique;

import java.util.Set;
import java.util.UUID;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.MiscUtil;

public class EnchantmentClimateTranquility extends UniqueEnchantment
{
	public static UUID SPEED_UUID = UUID.fromString("7b8a3791-8f94-4127-82b2-26418679d551");
	public static double SPEED_SCALE = 0.1D;
	public static int SLOW_TIME = 30;
	
	public static UUID ATTACK_UUID = UUID.fromString("b47bd399-5ad0-4f1d-a0eb-0a9146ff1734");
	public static double ATTACK_SCALE = 0.1D;
	public static int BURN_TIME = 1;
	
	public EnchantmentClimateTranquility()
	{
		super(new DefaultData("climate_tranquility", Rarity.RARE, 3, true, 20, 2, 12), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe || EnumEnchantmentType.BOW.canEnchantItem(stack.getItem());
	}
	
	@Override
	public void loadData(Configuration config)
	{
		SPEED_SCALE = config.get(getConfigName(), "speed_scale", 0.1D).getDouble();
		SLOW_TIME = config.get(getConfigName(), "slow_duration", 30).getInt();
		
		ATTACK_SCALE = config.get(getConfigName(), "attack_scale", 0.1D).getDouble();
		BURN_TIME = config.get(getConfigName(), "burn_time", 1).getInt();
	}
	
	
	public static void onClimate(EntityPlayer player)
	{
		Object2IntMap.Entry<EntityEquipmentSlot> slot = MiscUtil.getEnchantedItem(UniqueEnchantments.CLIMATE_TRANQUILITY, player);
		AbstractAttributeMap map = player.getAttributeMap();
		IAttributeInstance speed = map.getAttributeInstance(SharedMonsterAttributes.ATTACK_SPEED);
		IAttributeInstance damage = map.getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
		if(slot.getIntValue() <= 0)
		{
			speed.removeModifier(SPEED_UUID);
			damage.removeModifier(ATTACK_UUID);
			return;
		}
		int level = slot.getIntValue();
		Set<BiomeDictionary.Type> effects = BiomeDictionary.getTypes(player.world.getBiome(player.getPosition()));
		boolean hasHot = effects.contains(BiomeDictionary.Type.HOT) || effects.contains(BiomeDictionary.Type.NETHER);
		boolean hasCold = effects.contains(BiomeDictionary.Type.COLD);
		if(hasHot && !hasCold)
		{
			AttributeModifier speedMod = new AttributeModifier(SPEED_UUID, "Climate Boost", (SPEED_SCALE * level), 2);
			if(!speed.hasModifier(speedMod))
			{
				speed.applyModifier(speedMod);
			}
		}
		else
		{
			speed.removeModifier(SPEED_UUID);
		}
		if(hasCold && !hasHot)
		{
			AttributeModifier damageMod = new AttributeModifier(ATTACK_UUID, "Climate Boost", (ATTACK_SCALE * level), 2);
			if(!damage.hasModifier(damageMod))
			{
				damage.applyModifier(damageMod);
			}
		}
		else
		{
			damage.removeModifier(ATTACK_UUID);
		}
	}
}
