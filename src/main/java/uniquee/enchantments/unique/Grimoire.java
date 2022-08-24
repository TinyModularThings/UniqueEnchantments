package uniquee.enchantments.unique;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.BaseUEMod;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IdStat;
import uniquebase.utils.IntStat;
import uniquee.UE;

public class Grimoire extends UniqueEnchantment
{
	public static final String GRIMOIRE_LEVEL = "grimoire_level";
	public static final String GRIMOIRE_STORAGE = "grimoire_storage";
	public static final String GRIMOIRE_OWNER = "grimoire_owner";
	public static final DoubleStat LEVEL_SCALING = new DoubleStat(0.9D, "level_scaling");
	public static final DoubleStat STEP_SKIP = new DoubleStat(5D, "step_skip");
	public static final IntStat START_LEVEL = new IntStat(50, "start_level");
	public static final IdStat<Enchantment> INCOMPATS = new IdStat<>("ignored_enchantments", ForgeRegistries.ENCHANTMENTS);
	
	public Grimoire()
	{
		super(new DefaultData("grimoire", Rarity.VERY_RARE, 5, true, false, 70, 36, 20), BaseUEMod.ALL_TYPES, EquipmentSlot.values());
		addStats(LEVEL_SCALING, STEP_SKIP, INCOMPATS, START_LEVEL);
		setCurse();
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(Enchantments.MENDING, UE.ENDER_MENDING);
		INCOMPATS.addDefault(Enchantments.BLOCK_FORTUNE, Enchantments.BLOCK_EFFICIENCY, Enchantments.MOB_LOOTING, UE.MIDAS_BLESSING, UE.ENDEST_REAP, Enchantments.MENDING, Enchantments.SILK_TOUCH);
	}
		
	public static boolean applyGrimore(ItemStack stack, int level, Player player)
	{
		CompoundTag compound = stack.getTag();
		if(compound == null) compound = new CompoundTag();
		int nextLevel = Math.max(0, Mth.floor(Math.log((Math.max(1, START_LEVEL.get())+player.experienceLevel)*level)*LEVEL_SCALING.get()-STEP_SKIP.get()));
		int grimoreCount = compound.contains(GRIMOIRE_STORAGE) ? compound.getList(GRIMOIRE_STORAGE, 10).size() : 0;
		int enchCount = compound.contains("Enchantments") ? compound.getList("Enchantments", 10).size() : 0;
		boolean result = false;
		if(compound.getInt(GRIMOIRE_LEVEL) != nextLevel || grimoreCount != enchCount)
		{
			result = true;
			compound.putInt(GRIMOIRE_LEVEL, nextLevel);
			if(compound.contains(GRIMOIRE_STORAGE))
			{
				compound.put("Enchantments", compound.get(GRIMOIRE_STORAGE));
				compound.remove(GRIMOIRE_STORAGE);
			}
			ListTag ench = compound.getList("Enchantments", 10);
			compound.put(GRIMOIRE_STORAGE, ench);
			compound.remove("Enchantments");
			ListTag list = new ListTag();
			String exclusion = ForgeRegistries.ENCHANTMENTS.getKey(UE.GRIMOIRE).toString();
			for(int i = 0,m=ench.size();i<m;i++)
			{
				CompoundTag enchantment = ench.getCompound(i);
				String id = enchantment.getString("id");
				CompoundTag copy = new CompoundTag();
				copy.putString("id", id);
				copy.putShort("lvl", (short)(enchantment.getShort("lvl") + (exclusion.equals(id) || INCOMPATS.contains(ResourceLocation.tryParse(id)) ? 0 : nextLevel)));
				list.add(copy);
			}
			compound.put("Enchantments", list);
		}
		if(compound.contains(GRIMOIRE_OWNER))
		{
			if(!compound.getUUID(GRIMOIRE_OWNER).equals(player.getUUID()))
			{
				player.hurt(DamageSource.OUT_OF_WORLD, 1F);
			}
		}
		else
		{
			compound.putUUID(GRIMOIRE_OWNER, player.getUUID());
		}
		stack.setTag(compound);
		return result;
	}
}