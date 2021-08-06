package uniquee.enchantments.unique;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UniqueEnchantments;

public class Grimoire extends UniqueEnchantment
{
	public static final String GRIMOIRE_LEVEL = "grimoire_level";
	public static final String GRIMOIRE_STORAGE = "grimoire_storage";
	public static final String GRIMOIRE_OWNER = "grimoire_owner";
	public static final DoubleStat LEVEL_SCALING = new DoubleStat(1D, "level_scaling");
	public static final DoubleStat STEP_SKIP = new DoubleStat(4D, "step_skip");
	
	public Grimoire()
	{
		super(new DefaultData("grimoire", Rarity.VERY_RARE, 5, true, 70, 36, 20), EnumEnchantmentType.ALL, EntityEquipmentSlot.values());
		addStats(LEVEL_SCALING, STEP_SKIP);
		setCurse();
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(Enchantments.MENDING, UniqueEnchantments.ENDER_MENDING);
	}
	
	public static void applyGrimore(ItemStack stack, int level, EntityPlayer player)
	{
		NBTTagCompound compound = stack.getTagCompound();
		if(compound == null) compound = new NBTTagCompound();
		int nextLevel = Math.max(0, MathHelper.floor(Math.log(player.experienceLevel*level)*LEVEL_SCALING.get()-STEP_SKIP.get()));
		int grimoreCount = compound.hasKey(GRIMOIRE_STORAGE) ? compound.getTagList("GRIMOIRE_STORAGE", 10).tagCount() : 0;
		int enchCount = compound.hasKey("ench") ? compound.getTagList("ench", 10).tagCount() : 0;
		if(compound.getInteger(GRIMOIRE_LEVEL) != nextLevel || grimoreCount != enchCount)
		{
			compound.setInteger(GRIMOIRE_LEVEL, nextLevel);
			if(compound.hasKey(GRIMOIRE_STORAGE))
			{
				compound.setTag("ench", compound.getTag(GRIMOIRE_STORAGE));
				compound.removeTag(GRIMOIRE_STORAGE);
			}
			NBTTagList ench = compound.getTagList("ench", 10);
			compound.setTag(GRIMOIRE_STORAGE, ench);
			compound.removeTag("ench");
			NBTTagList list = new NBTTagList();
			short exclusion = (short)Enchantment.getEnchantmentID(UniqueEnchantments.GRIMOIRE);
			for(int i = 0,m=ench.tagCount();i<m;i++)
			{
				NBTTagCompound enchantment = ench.getCompoundTagAt(i);
				short id = enchantment.getShort("id");
				NBTTagCompound copy = new NBTTagCompound();
				copy.setShort("id", id);
				copy.setShort("lvl", (short)(enchantment.getShort("lvl") + (id == exclusion ? 0 : nextLevel)));
				list.appendTag(copy);
			}
			compound.setTag("ench", list);
		}
		if(compound.hasKey(GRIMOIRE_OWNER))
		{
			if(!compound.getUniqueId(GRIMOIRE_OWNER).equals(player.getUniqueID()))
			{
				player.attackEntityFrom(DamageSource.OUT_OF_WORLD, 1F);
			}
		}
		else
		{
			compound.setUniqueId(GRIMOIRE_OWNER, player.getUniqueID());
		}
		stack.setTagCompound(compound);
	}
}