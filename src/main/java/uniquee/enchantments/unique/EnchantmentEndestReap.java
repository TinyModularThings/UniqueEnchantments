package uniquee.enchantments.unique;

import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentEndestReap extends UniqueEnchantment
{
	public static final String REAP_STORAGE = "reap_storage";
	public static double BONUS_DAMAGE_LEVEL = 0.7D;
	public static double REAP_MULTIPLIER = 0.005D;
	static final Set<ResourceLocation> VALID_MOBS = new ObjectOpenHashSet<>();
	
	public EnchantmentEndestReap()
	{
		super(new DefaultData("endest_reap", Rarity.RARE, 4, true, 30, 6, 20), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
	}
	
	@Override
	public void loadIncompats()
	{
		addIncomats(Enchantments.SHARPNESS, Enchantments.MENDING, UniqueEnchantments.ADV_SHARPNESS, UniqueEnchantments.ENDER_MENDING, UniqueEnchantments.SPARTAN_WEAPON, UniqueEnchantments.SAGES_BLESSING);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		BONUS_DAMAGE_LEVEL = config.get(getConfigName(), "bonus_damage_level", 0.7D).getDouble();
		REAP_MULTIPLIER = config.get(getConfigName(), "reap_multiplier", 0.005D).getDouble();
		String[] result = config.get(getConfigName(), "validMobs", new String[]{"minecraft:ender_dragon", "minecraft:wither", "minecraft:shulker", "minecraft:elder_guardian", "minecraft:ravager", "minecraft:evoker"}).getStringList();
		VALID_MOBS.clear();
		for(int i = 0;i<result.length;i++)
		{
			try
			{
				VALID_MOBS.add(new ResourceLocation(result[i]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static boolean isValid(Entity entity)
	{
		return VALID_MOBS.contains(EntityList.getKey(entity.getClass()));
	}
}
