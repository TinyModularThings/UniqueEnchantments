package uniquee.enchantments.simple;

import com.google.common.base.Predicate;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.MiscUtil;

public class EnderEyes extends UniqueEnchantment
{
	public EnderEyes()
	{
		super(new DefaultData("ender_eyes", Rarity.UNCOMMON, 1, false, 10, 2, 5), EnumEnchantmentType.ARMOR_HEAD, new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD});
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.TREASURERS_EYES);
	}
	
	@Override
	public void loadData(Configuration config)
	{
	}
	
	public static Predicate<EntityPlayer> getPlayerFilter(EntityLivingBase living)
	{
		return new Predicate<EntityPlayer>() {
			@Override
			public boolean apply(EntityPlayer input)
			{
				if(MiscUtil.getEnchantmentLevel(UniqueEnchantments.ENDER_EYES, input.getItemStackFromSlot(EntityEquipmentSlot.HEAD)) <= 0) return false;
		        Vec3d vec3d1 = new Vec3d(living.posX - input.posX, living.getEntityBoundingBox().minY + living.getEyeHeight() - (input.posY + input.getEyeHeight()), living.posZ - input.posZ);
		        double d0 = vec3d1.lengthVector();
		        double d1 = input.getLook(1.0F).normalize().dotProduct(vec3d1.normalize());
		        return d1 > 1.0D - 0.025D / d0 ? input.canEntityBeSeen(living) : false;
			}
		};
	}
}
