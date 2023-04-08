package uniquee.enchantments.unique;

import java.util.UUID;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.Tags;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.EnchantmentContainer;
import uniquebase.utils.IntStat;
import uniquee.UE;

public class ClimateTranquility extends UniqueEnchantment
{
	public static final UUID SPEED_UUID = UUID.fromString("7b8a3791-8f94-4127-82b2-26418679d551");
	public static final DoubleLevelStats SPEED_BONUS = new DoubleLevelStats("speed_bonus", 0.1D, 0.03D);
	public static final IntStat SLOW_TIME = new IntStat(30, "slow_duration");
	
	public static final UUID ATTACK_UUID = UUID.fromString("b47bd399-5ad0-4f1d-a0eb-0a9146ff1734");
	public static final DoubleLevelStats ATTACK_BONUS = new DoubleLevelStats("attack_bonus", 0.1D, 0.03D);
	public static final IntStat BURN_TIME = new IntStat(1, "burn_time");

	public static final DoubleStat TRANSCENDED_BURN_DAMAGE = new DoubleStat(0.5D, "transcended_burn_damage");
	
	public ClimateTranquility()
	{
		super(new DefaultData("climate_tranquility", Rarity.RARE, 5, false, true, 10, 5, 30).setTrancendenceLevel(500), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		addStats(SPEED_BONUS, SLOW_TIME, ATTACK_BONUS, BURN_TIME, TRANSCENDED_BURN_DAMAGE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || EnchantmentCategory.BOW.canEnchant(stack.getItem()) || stack.getItem() instanceof CrossbowItem  || stack.getItem() instanceof TridentItem;
	}
	
	public static void onClimate(Player player, EnchantmentContainer container)
	{
		Object2IntMap.Entry<EquipmentSlot> slot = container.getEnchantedItem(UE.CLIMATE_TRANQUILITY);
		AttributeMap map = player.getAttributes();
		AttributeInstance speed = map.getInstance(Attributes.ATTACK_SPEED);
		AttributeInstance damage = map.getInstance(Attributes.ATTACK_DAMAGE);
		if(slot.getIntValue() <= 0)
		{
			speed.removeModifier(SPEED_UUID);
			damage.removeModifier(ATTACK_UUID);
			return;
		}
		int level = slot.getIntValue();
		Holder<Biome> effects = player.level.getBiome(player.blockPosition());
		boolean hasHot = effects.containsTag(Tags.Biomes.IS_HOT) || effects.containsTag(BiomeTags.IS_NETHER);
		boolean hasCold = effects.containsTag(Tags.Biomes.IS_COLD);
		if(hasHot && !hasCold)
		{
			AttributeModifier speedMod = new AttributeModifier(SPEED_UUID, "Climate Boost", SPEED_BONUS.getAsDouble(level), Operation.MULTIPLY_TOTAL);
			if(!speed.hasModifier(speedMod))
			{
				speed.addTransientModifier(speedMod);
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
				damage.addTransientModifier(damageMod);
			}
		}
		else
		{
			damage.removeModifier(ATTACK_UUID);
		}
	}
}
