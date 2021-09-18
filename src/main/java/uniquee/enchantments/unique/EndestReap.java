package uniquee.enchantments.unique;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UniqueEnchantments;

public class EndestReap extends UniqueEnchantment
{
	public static final String REAP_STORAGE = "reap_storage";
	public static final DoubleStat BONUS_DAMAGE_LEVEL = new DoubleStat(0.5D, "bonus_damage_level");
	public static final DoubleStat REAP_MULTIPLIER = new DoubleStat(0.01D, "reap_multiplier");
	
	static final Object2IntMap<ResourceLocation> VALID_MOBS = new Object2IntLinkedOpenHashMap<>();
	
	public EndestReap()
	{
		super(new DefaultData("endest_reap", Rarity.VERY_RARE, 4, true, 30, 15, 20), EnumEnchantmentType.WEAPON, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND);
		addStats(BONUS_DAMAGE_LEVEL, REAP_MULTIPLIER);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemHoe;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(Enchantments.SHARPNESS, Enchantments.MENDING, UniqueEnchantments.ADV_SHARPNESS, UniqueEnchantments.ENDER_MENDING, UniqueEnchantments.SPARTAN_WEAPON, UniqueEnchantments.SAGES_BLESSING);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		String[] result = config.get(getConfigName(), "validMobs", new String[]{"minecraft:ender_dragon;8", "minecraft:wither;3", "minecraft:shulker;1", "minecraft:elder_guardian;5", "minecraft:ravager;2", "minecraft:evoker;1"}).getStringList();
		VALID_MOBS.clear();
		for(int i = 0;i<result.length;i++)
		{
			try
			{
				String[] s = result[i].split(";");
				if(s.length != 2) continue;
				VALID_MOBS.put(new ResourceLocation(s[0]), Integer.parseInt(s[1]));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static int isValid(Entity entity)
	{
		return VALID_MOBS.getInt(EntityList.getKey(entity.getClass()));
	}
}
