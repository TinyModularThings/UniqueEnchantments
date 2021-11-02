package uniqueebattle.handler;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import uniquebase.handler.IMathCache;
import uniquebase.utils.MiscUtil;
import uniqueebattle.UniqueEnchantmentsBattle;
import uniqueebattle.enchantments.AresFragment;
import uniqueebattle.enchantments.CelestialBlessing;
import uniqueebattle.enchantments.GolemSoul;
import uniqueebattle.enchantments.IfritsBlessing;
import uniqueebattle.enchantments.IfritsJudgement;
import uniqueebattle.enchantments.LunaticDespair;

public class BattleHandler
{
	public static final BattleHandler INSTANCE = new BattleHandler();
	
	@SubscribeEvent
	public void onBlockClick(RightClickBlock event)
	{
		if(event.getWorld() instanceof WorldServer)
		{
			int count = event.getEntityPlayer().getEntityData().getInteger(IfritsJudgement.FLAG_JUDGEMENT_LOOT);
			if(count > 0)
			{
				TileEntity tile = event.getWorld().getTileEntity(event.getPos());
				if(tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, event.getFace()))
				{
					IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, event.getFace());
					if(handler.getSlots() < 9) return;
					LootTable table = ((WorldServer)event.getWorld()).getLootTableManager().getLootTableFromLocation(IfritsJudgement.JUDGEMENT_LOOT);
					for(ItemStack stack : table.generateLootForPools(event.getWorld().rand, new LootContext.Builder((WorldServer)event.getWorld()).withPlayer(event.getEntityPlayer()).withLuck(event.getEntityPlayer().getLuck()).build()))
					{
						ItemHandlerHelper.insertItem(handler, stack, false);
					}
					event.getEntityPlayer().getEntityData().setInteger(IfritsJudgement.FLAG_JUDGEMENT_LOOT, count-1);
					if(event.getEntityPlayer().isSneaking()) event.setCanceled(true);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onArmorDamage(LivingHurtEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof EntityLivingBase)
		{
			EntityLivingBase source = (EntityLivingBase)entity;
			Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(source.getHeldItemMainhand());
			int level = ench.getInt(UniqueEnchantmentsBattle.ARES_FRAGMENT);
			if(level > 0 && source instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)source;
				int maxRolls = MathHelper.floor(Math.sqrt(level*player.experienceLevel)*AresFragment.BASE_ROLL_MULTIPLIER.get()) + AresFragment.BASE_ROLL.get();
				int posRolls = 1+source.world.rand.nextInt(maxRolls);
				int negRolls = maxRolls - posRolls;
				EntityLivingBase enemy = event.getEntityLiving();
				double toughness = enemy.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();
				double speed = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue();
				float damageFactor = (float)(Math.log(1+IMathCache.SQRT.get(player.experienceLevel*level*level)*(1+(enemy.getTotalArmorValue()+toughness*2.5)*AresFragment.ARMOR_PERCENTAGE.get())) / (100F*speed));
				event.setAmount(event.getAmount() * ((1F+(damageFactor*posRolls)) / (1F+(damageFactor*negRolls))));
				source.getHeldItemMainhand().damageItem(MathHelper.ceil((Math.sqrt(Math.abs(posRolls-negRolls) / Math.pow(AresFragment.DURABILITY_REDUCTION_SCALING.get(), 1+(level/100D))) * (((posRolls * Math.pow(AresFragment.DURABILITY_DISTRIBUTION.get(), -1D))-(negRolls * AresFragment.DURABILITY_DISTRIBUTION.get())) / AresFragment.DURABILITY_ANTI_SCALING.get()))/speed), source);
			}
			if(event.getEntityLiving().isBurning())
			{
				level = event.getSource().isProjectile() ? MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.IFRITS_BLESSING, source).getIntValue() : ench.getInt(UniqueEnchantmentsBattle.IFRITS_BLESSING);
				if(level > 0)
				{
					event.setAmount(event.getAmount() * ((1 + IfritsBlessing.BONUS_DAMAGE.getLogValue(2.8D, level)) * (source.isBurning() ? 2 : 1)));
				}
			}
			EntityEquipmentSlot slot = null;
			if(event.getSource().isProjectile())
			{
				Object2IntMap.Entry<EntityEquipmentSlot> found = MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.IFRITS_BLESSING, source);
				slot = found.getKey();
				level = found.getIntValue();
			}
			else
			{
				level = ench.getInt(UniqueEnchantmentsBattle.IFRITS_JUDGEMENT);
				slot = level > 0 ? EntityEquipmentSlot.MAINHAND : null;
			}
			if(level > 0)
			{
				NBTTagCompound entityNBT = event.getEntityLiving().getEntityData();
				NBTTagList list = entityNBT.getTagList(IfritsJudgement.FLAG_JUDGEMENT_ID, 10);
				boolean found = false;
				String id = source.getItemStackFromSlot(slot).getItem().getRegistryName().toString();
				for(int i = 0,m=list.tagCount();i<m;i++)
				{
					NBTTagCompound data = list.getCompoundTagAt(i);
					if(data.getString(IfritsJudgement.FLAG_JUDGEMENT_ID).equalsIgnoreCase(id))
					{
						found = true;
						data.setInteger(IfritsJudgement.FLAG_JUDGEMENT_COUNT, data.getInteger(IfritsJudgement.FLAG_JUDGEMENT_COUNT)+1);
						break;
					}
				}
				if(!found)
				{
					NBTTagCompound data = new NBTTagCompound();
					data.setString(IfritsJudgement.FLAG_JUDGEMENT_ID, id);
					data.setInteger(IfritsJudgement.FLAG_JUDGEMENT_COUNT, 1);
					list.appendTag(data);
					entityNBT.setTag(IfritsJudgement.FLAG_JUDGEMENT_ID, list);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof EntityLivingBase)
		{
			EntityLivingBase source = (EntityLivingBase)entity;
			Object2IntMap.Entry<EntityEquipmentSlot> found = MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.IFRITS_JUDGEMENT, source);
			if(found.getIntValue() > 0)
			{
				NBTTagList list = event.getEntityLiving().getEntityData().getTagList(IfritsJudgement.FLAG_JUDGEMENT_ID, 10);
				int max = 0;
				for(int i = 0,m=list.tagCount();i<m;i++)
				{
					max = Math.max(max, list.getCompoundTagAt(i).getInteger(IfritsJudgement.FLAG_JUDGEMENT_COUNT));
				}
				if(max > IfritsJudgement.LAVA_HITS.get())
				{
					int combined = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsBattle.IFRITS_JUDGEMENT, source);
					source.attackEntityFrom(DamageSource.LAVA, IfritsJudgement.LAVA_DAMAGE.getAsFloat(found.getIntValue() * IMathCache.LOG_MUL_MAX.getFloat(combined)));
					entity.setFire(Math.max(1, IfritsJudgement.DURATION.get(found.getIntValue()) / 20));
				}
				else if(max > IfritsJudgement.FIRE_HITS.get())
				{
					int combined = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsBattle.IFRITS_JUDGEMENT, source);
					source.attackEntityFrom(DamageSource.IN_FIRE, IfritsJudgement.FIRE_DAMAGE.getAsFloat(found.getIntValue() * IMathCache.LOG_MUL_MAX.getFloat(combined)));
					entity.setFire(Math.max(1, IfritsJudgement.DURATION.get(found.getIntValue()) / 20));					
				}
				else if(max > 0)
				{
					NBTTagCompound compound = source.getEntityData();
					compound.setInteger(IfritsJudgement.FLAG_JUDGEMENT_LOOT, compound.getInteger(IfritsJudgement.FLAG_JUDGEMENT_LOOT)+1);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDamage(LivingDamageEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof EntityLivingBase)
		{
			EntityLivingBase source = (EntityLivingBase)entity;
			int level = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsBattle.LUNATIC_DESPAIR, source);
			if(level > 0)
			{
				float value = IMathCache.LOG_ADD.getFloat(level);
				event.setAmount(event.getAmount() * (1F + LunaticDespair.BONUS_DAMAGE.getFloat(value)));
				source.attackEntityFrom(DamageSource.GENERIC, LunaticDespair.SELF_DAMAGE.getFloat(value));
				source.hurtResistantTime = 0;
				source.attackEntityFrom(DamageSource.MAGIC, LunaticDespair.SELF_MAGIC_DAMAGE.getFloat(value));
			}
		}
	}
	
	@SubscribeEvent
	public void onEquippementSwapped(LivingEquipmentChangeEvent event)
	{
		AbstractAttributeMap attribute = event.getEntityLiving().getAttributeMap();
		Multimap<String, AttributeModifier> mods = createModifiersFromStack(event.getFrom(), event.getSlot(), event.getEntityLiving().getEntityWorld());
		if(!mods.isEmpty())
		{
			attribute.removeAttributeModifiers(mods);
		}
		mods = createModifiersFromStack(event.getTo(), event.getSlot(), event.getEntityLiving().getEntityWorld());
		if(!mods.isEmpty())
		{
			attribute.applyAttributeModifiers(mods);
		}
	}
	
	private Multimap<String, AttributeModifier> createModifiersFromStack(ItemStack stack, EntityEquipmentSlot slot, World world)
	{
		Multimap<String, AttributeModifier> mods = HashMultimap.create();
		Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
		int level = enchantments.getInt(UniqueEnchantmentsBattle.CELESTIAL_BLESSING);
		if(level > 0)
		{
			mods.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(CelestialBlessing.SPEED_MOD, "speed_boost", world.isDaytime() ? 0F : CelestialBlessing.SPEED_BONUS.getAsDouble(level), 2));
		}
		level = enchantments.getInt(UniqueEnchantmentsBattle.GOLEM_SOUL);
		if(level > 0)
		{
			mods.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(GolemSoul.SPEED_MOD, "speed_loss", (Math.pow(1-GolemSoul.SPEED.get(), level)-1), 2));
			mods.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(GolemSoul.KNOCKBACK_MOD, "knockback_boost", GolemSoul.KNOCKBACK.get(level), 0));
		}
		return mods;
	}
}
