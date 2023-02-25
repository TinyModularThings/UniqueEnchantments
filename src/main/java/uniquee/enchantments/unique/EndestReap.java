package uniquee.enchantments.unique;

import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UE;

public class EndestReap extends UniqueEnchantment
{
	public static final String REAP_STORAGE = "reap_storage";
	public static final DoubleStat BONUS_DAMAGE_LEVEL = new DoubleStat(0.5D, "bonus_damage_level");
	public static final DoubleStat REAP_MULTIPLIER = new DoubleStat(0.01D, "reap_multiplier");
	public static final DoubleStat TRANSCENDED_CHACNE = new DoubleStat(0.25, "transcended_chance");
	
	static final Object2IntMap<ResourceLocation> VALID_MOBS = new Object2IntLinkedOpenHashMap<>();
	static ConfigValue<List<? extends String>> VALID_CONFIG;
	
	public EndestReap()
	{
		super(new DefaultData("endest_reap", Rarity.VERY_RARE, 4, true, false, 30, 10, 30).setTrancendenceLevel(1000), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		addStats(BONUS_DAMAGE_LEVEL, REAP_MULTIPLIER, TRANSCENDED_CHACNE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof HoeItem || stack.getItem() instanceof TridentItem;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(Enchantments.SHARPNESS, Enchantments.MENDING, UE.ADV_SHARPNESS, UE.ENDER_MENDING, UE.SPARTAN_WEAPON, UE.SAGES_BLESSING);
	}
	
	@Override
	public void loadData(Builder config)
	{
		VALID_CONFIG = config.defineList("validMobs", ObjectArrayList.wrap(new String[]{"minecraft:ender_dragon;8", "minecraft:wither;3", "minecraft:shulker;1", "minecraft:elder_guardian;5", "minecraft:ravager;2", "minecraft:evoker;1"}), T -> true);
	}
	
	public static int isValid(Entity entity)
	{
		return VALID_MOBS.getInt(ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()));
	}
	
	public static boolean isValid(EntityType<?> type)
	{
		return VALID_MOBS.getInt(ForgeRegistries.ENTITY_TYPES.getKey(type)) > 0;
	}
	
	@Override
	public void onConfigChanged()
	{
		super.onConfigChanged();
		List<? extends String> list = VALID_CONFIG.get();
		VALID_MOBS.clear();
		for(int i = 0,m=list.size();i<m;i++)
		{
			try
			{
				String[] s = list.get(i).split(";");
				if(s.length != 2) continue;
				VALID_MOBS.put(new ResourceLocation(s[0]), Integer.parseInt(s[1]));
			}
			catch(Exception e) { e.printStackTrace(); }
		}
	}
}