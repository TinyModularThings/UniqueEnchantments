package uniquebase.blockenchantments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class BlockEnchantmentCapability implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
	BlockEnchantments enchantments;
	LazyOptional<BlockEnchantments> enchs;
	Capability<BlockEnchantments> myCapability;
	
	public BlockEnchantmentCapability(BlockEnchantments enchantments, Capability<BlockEnchantments> myCapability) {
		this.enchantments = enchantments;
		this.myCapability = myCapability;
		enchs = LazyOptional.of(() -> enchantments);
	}

	public BlockEnchantmentCapability(Capability<BlockEnchantments> myCapability) {
		this(new BlockEnchantments(), myCapability);
	}
	
	public void invalidate() {
		enchs.invalidate();
		enchs = null;
	}
	
	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return side == null ? myCapability.orEmpty(cap, enchs) : LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		return enchantments.serialize();
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		enchantments.deserialize(nbt);
	}
	
}
