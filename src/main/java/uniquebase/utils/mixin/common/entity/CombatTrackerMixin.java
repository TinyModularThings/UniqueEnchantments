package uniquebase.utils.mixin.common.entity;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.CombatEntry;
import net.minecraft.util.CombatTracker;

@Mixin(CombatTracker.class)
public interface CombatTrackerMixin
{
	@Accessor("entries")
	public List<CombatEntry> getCombatEntries();
}
