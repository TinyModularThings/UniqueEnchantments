package uniquee.enchantments.simple;

import com.google.common.base.Predicate;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.MiscUtil;
import uniquee.UniqueEnchantments;

public class EnderEyes extends UniqueEnchantment
{
	public EnderEyes()
	{
		super(new DefaultData("ender_eyes", Rarity.UNCOMMON, 1, false, 10, 2, 5), EnchantmentType.ARMOR_HEAD, EquipmentSlotType.HEAD);
	}
	
	@Override
	public void loadData(Builder config)
	{
		addIncompats(UniqueEnchantments.TREASURERS_EYES);
	}
	
	public static Predicate<LivingEntity> getPlayerFilter(LivingEntity living)
	{
		return new Predicate<LivingEntity>() {
			@Override
			public boolean apply(LivingEntity input)
			{
				if(MiscUtil.getEnchantmentLevel(UniqueEnchantments.ENDER_EYES, input.getItemStackFromSlot(EquipmentSlotType.HEAD)) <= 0) return false;
		        Vec3d vec3d1 = new Vec3d(living.posX - input.posX, living.getBoundingBox().minY + living.getEyeHeight() - (input.posY + input.getEyeHeight()), living.posZ - input.posZ);
		        double d0 = vec3d1.length();
		        double d1 = input.getLook(1.0F).normalize().dotProduct(vec3d1.normalize());
		        return d1 > 1.0D - 0.025D / d0 ? input.canEntityBeSeen(living) : false;
			}
		};
	}
}
