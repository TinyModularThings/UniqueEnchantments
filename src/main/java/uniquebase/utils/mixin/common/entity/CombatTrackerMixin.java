package uniquebase.utils.mixin.common.entity;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.damagesource.CombatTracker;

@Mixin(CombatTracker.class)
public interface CombatTrackerMixin
{
	@Accessor("entries")
	public List<CombatEntry> getCombatEntries();
}
