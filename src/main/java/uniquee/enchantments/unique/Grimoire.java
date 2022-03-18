package uniquee.enchantments.unique;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
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
	public static final IdStat INCOMPATS = new IdStat("ignored_enchantments", ForgeRegistries.ENCHANTMENTS);
	
	public Grimoire()
	{
		super(new DefaultData("grimoire", Rarity.VERY_RARE, 5, true, false, 70, 36, 20), BaseUEMod.ALL_TYPES, EquipmentSlotType.values());
		addStats(LEVEL_SCALING, STEP_SKIP, INCOMPATS, START_LEVEL);
		setCurse();
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(Enchantments.MENDING, UE.ENDER_MENDING);
		INCOMPATS.addDefault(Enchantments.BLOCK_FORTUNE, Enchantments.BLOCK_EFFICIENCY, Enchantments.MOB_LOOTING, UE.MIDAS_BLESSING, UE.ENDEST_REAP, Enchantments.MENDING, Enchantments.SILK_TOUCH);
	}
		
	public static void applyGrimore(ItemStack stack, int level, PlayerEntity player)
	{
		CompoundNBT compound = stack.getTag();
		if(compound == null) compound = new CompoundNBT();
		int nextLevel = Math.max(0, MathHelper.floor(Math.log((Math.max(1, START_LEVEL.get())+player.experienceLevel)*level)*LEVEL_SCALING.get()-STEP_SKIP.get()));
		int grimoreCount = compound.contains(GRIMOIRE_STORAGE) ? compound.getList(GRIMOIRE_STORAGE, 10).size() : 0;
		int enchCount = compound.contains("Enchantments") ? compound.getList("Enchantments", 10).size() : 0;
		System.out.println(nextLevel);
		compound.remove(GRIMOIRE_LEVEL);
		if(compound.getInt(GRIMOIRE_LEVEL) != nextLevel || grimoreCount != enchCount)
		{
			compound.putInt(GRIMOIRE_LEVEL, nextLevel);
			if(compound.contains(GRIMOIRE_STORAGE))
			{
				compound.put("Enchantments", compound.get(GRIMOIRE_STORAGE));
				compound.remove(GRIMOIRE_STORAGE);
			}
			ListNBT ench = compound.getList("Enchantments", 10);
			compound.put(GRIMOIRE_STORAGE, ench);
			compound.remove("Enchantments");
			ListNBT list = new ListNBT();
			String exclusion = UE.GRIMOIRE.getRegistryName().toString();
			for(int i = 0,m=ench.size();i<m;i++)
			{
				CompoundNBT enchantment = ench.getCompound(i);
				String id = enchantment.getString("id");
				CompoundNBT copy = new CompoundNBT();
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
	}
}