package uniquee.enchantments.simple;

import com.google.common.base.Predicate;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IdStat;
import uniquebase.utils.MiscUtil;
import uniquee.UE;

public class EnderEyes extends UniqueEnchantment
{
	public static final DoubleStat TRANSCENDED_CHANCE = new DoubleStat(0.6, "transcended_chance");
	public static final IdStat<EntityType<?>> AFFECTED_ENTITIES = new IdStat<>("affected_entities", ForgeRegistries.ENTITY_TYPES, EntityType.ENDERMAN);
	
	public EnderEyes()
	{
		super(new DefaultData("ender_eyes", Rarity.UNCOMMON, 1, false, false, 10, 2, 5).setTrancendenceLevel(200), EnchantmentCategory.ARMOR_HEAD, EquipmentSlot.HEAD);
		addStats(TRANSCENDED_CHANCE, AFFECTED_ENTITIES);
	}
	
	@Override
	public void loadData(Builder config)
	{
		addIncompats(UE.TREASURERS_EYES);
	}
	
	public static TargetingConditions createTarget(LivingEntity man)
	{
		return TargetingConditions.forNonCombat().selector(getPlayerFilter(man)).range(MiscUtil.getAttribute(man, Attributes.FOLLOW_RANGE, 16D));
	}
	
	public static Predicate<LivingEntity> getPlayerFilter(Entity living)
	{
		return new Predicate<LivingEntity>() {
			@Override
			public boolean apply(LivingEntity input)
			{
				if(MiscUtil.getEnchantmentLevel(UE.ENDER_EYES, input.getItemBySlot(EquipmentSlot.HEAD)) <= 0) return false;
				Vec3 vec3d1 = new Vec3(living.getX() - input.getX(), living.getBoundingBox().minY + living.getEyeHeight() - (input.getY() + input.getEyeHeight()), living.getZ() - input.getZ());
		        double d0 = vec3d1.length();
		        double d1 = input.getViewVector(1.0F).normalize().dot(vec3d1.normalize());
		        return d1 > 1.0D - 0.025D / d0 ? input.hasLineOfSight(living) : false;
			}
		};
	}
	
	public static boolean isValidEntity(LivingEntity living)
	{
		return !living.isSpectator() && living.isPickable();
	}
}
