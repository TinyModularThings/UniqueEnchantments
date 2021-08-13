package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
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
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IdStat;
import uniquee.UniqueEnchantments;

public class Grimoire extends UniqueEnchantment
{
	public static final String GRIMOIRE_LEVEL = "grimoire_level";
	public static final String GRIMOIRE_STORAGE = "grimoire_storage";
	public static final String GRIMOIRE_OWNER = "grimoire_owner";
	public static final DoubleStat LEVEL_SCALING = new DoubleStat(0.95D, "level_scaling");
	public static final DoubleStat STEP_SKIP = new DoubleStat(5D, "step_skip");
	public static final IdStat INCOMPATS = new IdStat("ignored_enchantments", ForgeRegistries.ENCHANTMENTS);
	
	public Grimoire()
	{
		super(new DefaultData("grimoire", Rarity.VERY_RARE, 5, true, 70, 36, 20), EnchantmentType.ALL, EquipmentSlotType.values());
		addStats(LEVEL_SCALING, STEP_SKIP, INCOMPATS);
		setCurse();
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(Enchantments.MENDING, UniqueEnchantments.ENDER_MENDING);
	}
	
	public static void applyGrimore(ItemStack stack, int level, PlayerEntity player)
	{
		CompoundNBT compound = stack.getTag();
		if(compound == null) compound = new CompoundNBT();
		int nextLevel = Math.max(0, MathHelper.floor(Math.log((player.experienceLevel+1)*level)*LEVEL_SCALING.get()-STEP_SKIP.get()));
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
			String exclusion = UniqueEnchantments.GRIMOIRE.getRegistryName().toString();
			for(int i = 0,m=ench.size();i<m;i++)
			{
				CompoundNBT enchantment = ench.getCompound(i);
				String id = enchantment.getString("id");
				CompoundNBT copy = new CompoundNBT();
				copy.putString("id", id);
				copy.putShort("lvl", (short)(enchantment.getShort("lvl") + (exclusion.equals(id) || INCOMPATS.contains(ResourceLocation.tryCreate(id)) ? 0 : nextLevel)));
				list.add(copy);
			}
			compound.put("Enchantments", list);
		}
		if(compound.contains(GRIMOIRE_OWNER))
		{
			if(!compound.getUniqueId(GRIMOIRE_OWNER).equals(player.getUniqueID()))
			{
				player.attackEntityFrom(DamageSource.OUT_OF_WORLD, 1F);
			}
		}
		else
		{
			compound.putUniqueId(GRIMOIRE_OWNER, player.getUniqueID());
		}
		stack.setTag(compound);
	}
}