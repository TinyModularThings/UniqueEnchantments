package uniqueapex.handler.structure;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.server.ServerLifecycleHooks;
import uniqueapex.handler.FusionHandler;
import uniqueapex.handler.recipe.FusionContext;
import uniqueapex.handler.recipe.fusion.FusionRecipe;
import uniqueapex.utils.AnimationUtils;

public abstract class BaseTrackedRecipe
{
	static final Map<Class<?>, ResourceLocation> SAVERS = new Object2ObjectOpenHashMap<>();
	static final Map<ResourceLocation, Function<CompoundTag, BaseTrackedRecipe>> LOADERS = Util.make(new Object2ObjectOpenHashMap<>(), T -> {
		T.put(new ResourceLocation("ue_apex", "fusion"), TrackedRecipe::loadRecipe);
		SAVERS.put(TrackedRecipe.class, new ResourceLocation("ue_apex", "fusion"));
		T.put(new ResourceLocation("ue_apex", "upgrade"), TrackedUpgradeRecipe::loadRecipe);
		SAVERS.put(TrackedUpgradeRecipe.class, new ResourceLocation("ue_apex", "upgrade"));
	});
	
	Level world;
	BlockPos pos;
	FusionContext context;
	int beaconSize;
	int tick;
	List<EndCrystal> endCrystals;
	List<Vec3> basePositions;
	float rotation;
	
	protected BaseTrackedRecipe() {
		
	}
	
	public BaseTrackedRecipe(Level world, BlockPos pos, FusionContext context, int beaconSize, List<EndCrystal> endCrystals)
	{
		this.world = world;
		this.pos = pos;
		this.context = context;
		this.beaconSize = beaconSize;
		this.endCrystals = endCrystals;
		basePositions = new ObjectArrayList<>();
		for(int i = 1;i<5;i++)
		{
			basePositions.add(endCrystals.get(i).position());
		}
	}
	
	protected void set(Level world, BlockPos pos, FusionContext context, int beaconSize, int currentTick, List<EndCrystal> endCrystals, List<Vec3> basePositions)
	{
		this.world = world;
		this.pos = pos;
		this.context = context;
		this.beaconSize = beaconSize;
		this.tick = currentTick;
		this.endCrystals = endCrystals;
		for(int i = 0,m=tick-40;i<m;i++) {
			rotation += (float)Math.toRadians(AnimationUtils.getRotation(i+40));			
		}
	}
	
	public static BaseTrackedRecipe loadRecipe(CompoundTag data)
	{
		Function<CompoundTag, BaseTrackedRecipe> loader = LOADERS.get(ResourceLocation.tryParse(data.getString("id")));
		return loader == null ? null : loader.apply(data);
	}
	
	public void tick()
	{
		tick++;
		if(tick < 40) return;
		rotation += (float)Math.toRadians(AnimationUtils.getRotation(tick));
		float closer = AnimationUtils.getCloser(tick);
		float yOffset = AnimationUtils.getYOffset(tick);
		float masterY = AnimationUtils.getMasterY(tick);
		for(int i = 0;i<4;i++)
		{
			Direction dir = Direction.from2DDataValue(i).getOpposite();
			EndCrystal entity = endCrystals.get(i+1);
			Vec3 base = basePositions.get(i).add(dir.getStepX() * closer, yOffset, dir.getStepZ() * closer).subtract(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			Vec3 nextPos = base.yRot(rotation).add(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			entity.absMoveTo(nextPos.x(), nextPos.y(), nextPos.z());
		}
		EndCrystal entity = endCrystals.get(0);
		entity.absMoveTo(entity.getX(), pos.getY() + 3.5D + masterY, entity.getZ());
	}
	
	public LevelChunk getChunk()
	{
		return world.getChunkAt(getPos());
	}
	
	public int getTick()
	{
		return tick;
	}
	
	public BlockPos getPos()
	{
		return pos;
	}
	
	public List<Vec3> getBasePosition()
	{
		return basePositions;
	}
	
	public boolean isDone()
	{
		return tick >= 255;
	}
	
	public void finishRecipe()
	{
		if(context == null) {
			context = FusionHandler.INSTANCE.context(world, pos);
			if(context == null) return;
		}
		finishRecipe(context, world);
	}
		
	protected abstract void finishRecipe(FusionContext context, Level world);
	
	public void remove()
	{
		for(EndCrystal entity : endCrystals)
		{
			entity.remove(RemovalReason.DISCARDED);
		}
	}
	
	public CompoundTag save(CompoundTag data)
	{
		data.putString("world", world.dimension().location().toString());
		data.putLong("pos", pos.asLong());
		data.putInt("tick", tick);
		data.putInt("size", beaconSize);
		ListTag list = new ListTag();
		for(EndCrystal crystal : endCrystals)
		{
			list.add(NbtUtils.createUUID(crystal.getUUID()));
		}
		data.put("crystals", list);
		list = new ListTag();
		for(Vec3 pos : basePositions)
		{
			CompoundTag nbt = new CompoundTag();
			nbt.putDouble("x", pos.x());
			nbt.putDouble("y", pos.y());
			nbt.putDouble("z", pos.z());
			list.add(nbt);
		}
		data.put("base", list);
		data.putString("id", SAVERS.get(getClass()).toString());
		return data;
	}
	
	public BaseTrackedRecipe load(CompoundTag data)
	{
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		ServerLevel world = server.getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, ResourceLocation.tryParse(data.getString("world"))));
		BlockPos pos = BlockPos.of(data.getLong("long"));
		int tick = data.getInt("tick");
		int size = data.getInt("size");
		FusionRecipe recipe = (FusionRecipe)server.getRecipeManager().byKey(ResourceLocation.tryParse(data.getString("recipe"))).orElse(null);
		List<EndCrystal> endCrystal = new ObjectArrayList<>();
		for(Tag nbt : data.getList("crystals", 11))
		{
			Entity entity = world.getEntity(NbtUtils.loadUUID(nbt));
			if(entity instanceof EndCrystal)
			{
				endCrystal.add((EndCrystal)entity);
			}
		}
		List<Vec3> basePositions = new ObjectArrayList<>();
		for(Tag entry : data.getList("base", 10))
		{
			CompoundTag nbt = (CompoundTag)entry;
			basePositions.add(new Vec3(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z")));
		}
		if(endCrystal.size() != 5 || recipe == null)
		{
			for(EndCrystal crystal : endCrystal)
			{
				crystal.remove(RemovalReason.DISCARDED);
			}
			return null;
		}
		set(world, pos, null, size, tick, endCrystal, basePositions);
		return this;
	}
	
	public int[] getEntities()
	{
		int[] entities = new int[5];
		for(int i = 0;i<5;i++) {
			entities[i] = endCrystals.get(i).getId();
		}
		return entities;
	}
	
	public List<EndCrystal> getEntitites(Level world)
	{
		List<EndCrystal> newList = new ObjectArrayList<>();
		for(EndCrystal entity : endCrystals)
		{
			newList.add((EndCrystal)world.getEntity(entity.getId()));
		}
		return newList;
	}
}
