package fun.wich;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class MoveIntoMudGoal extends Goal {
	private final MobEntity mob;
	public MoveIntoMudGoal(MobEntity mob) { this.mob = mob; }
	@Override
	public boolean canStart() {
		return this.mob.isOnGround() && MuddyPig.IsDried(mob)
				&& !this.mob.getEntityWorld().getFluidState(this.mob.getBlockPos()).isIn(MuddyPigsMod.TAG_MUD);
	}
	@Override
	public boolean shouldContinue() { return MuddyPig.IsDried(mob); }
	@Override
	public void start() {
		BlockPos blockPos = null;
		for (BlockPos blockPos2 : BlockPos.iterate(
				MathHelper.floor(this.mob.getX() - 2.0),
				MathHelper.floor(this.mob.getY() - 2.0),
				MathHelper.floor(this.mob.getZ() - 2.0),
				MathHelper.floor(this.mob.getX() + 2.0),
				this.mob.getBlockY(),
				MathHelper.floor(this.mob.getZ() + 2.0)
		)) {
			if (this.mob.getEntityWorld().getFluidState(blockPos2).isIn(MuddyPigsMod.TAG_MUD)) {
				blockPos = blockPos2;
				break;
			}
		}
		if (blockPos != null) this.mob.getMoveControl().moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0);
	}
}