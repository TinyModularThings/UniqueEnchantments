package uniquee.enchantments.simple;

import com.google.common.base.Predicate;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.vector.Vector3d;
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
	public static final IdStat AFFECTED_ENTITIES = new IdStat("affected_entities", ForgeRegistries.ENTITIES, EntityType.PLAYER);
	
	public EnderEyes()
	{
		super(new DefaultData("ender_eyes", Rarity.UNCOMMON, 1, false, false, 10, 2, 5).setTrancendenceLevel(200), EnchantmentType.ARMOR_HEAD, EquipmentSlotType.HEAD);
		addStats(TRANSCENDED_CHANCE, AFFECTED_ENTITIES);
	}
	
	@Override
	public void loadData(Builder config)
	{
		addIncompats(UE.TREASURERS_EYES);
	}
	
	public static Predicate<LivingEntity> getPlayerFilter(LivingEntity living)
	{
		return new Predicate<LivingEntity>() {
			@Override
			public boolean apply(LivingEntity input)
			{
				if(MiscUtil.getEnchantmentLevel(UE.ENDER_EYES, input.getItemBySlot(EquipmentSlotType.HEAD)) <= 0) return false;
				Vector3d vec3d1 = new Vector3d(living.getX() - input.getX(), living.getBoundingBox().minY + living.getEyeHeight() - (input.getY() + input.getEyeHeight()), living.getZ() - input.getZ());
		        double d0 = vec3d1.length();
		        double d1 = input.getViewVector(1.0F).normalize().dot(vec3d1.normalize());
		        return d1 > 1.0D - 0.025D / d0 ? input.canSee(living) : false;
			}
		};
	}
}
