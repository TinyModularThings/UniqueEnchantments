package uniquee.enchantments.unique;

import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class EnchantmentEndestReap extends UniqueEnchantment
{
	public static final String REAP_STORAGE = "reap_storage";
	public static final DoubleStat BONUS_DAMAGE_LEVEL = new DoubleStat(0.7D, "bonus_damage_level");
	public static final DoubleStat REAP_MULTIPLIER = new DoubleStat(0.005D, "reap_multiplier");
	
	static final Set<ResourceLocation> VALID_MOBS = new ObjectOpenHashSet<>();
	
	public EnchantmentEndestReap()
	{
		super(new DefaultData("endest_reap", Rarity.VERY_RARE, 4, true, 30, 15, 20), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemHoe;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncomats(Enchantments.SHARPNESS, Enchantments.MENDING, UniqueEnchantments.ADV_SHARPNESS, UniqueEnchantments.ENDER_MENDING, UniqueEnchantments.SPARTAN_WEAPON, UniqueEnchantments.SAGES_BLESSING);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		BONUS_DAMAGE_LEVEL.handleConfig(config, getConfigName());
		REAP_MULTIPLIER.handleConfig(config, getConfigName());
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
