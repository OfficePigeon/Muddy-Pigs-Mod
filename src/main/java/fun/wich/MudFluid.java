package fun.wich;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.CollisionEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class MudFluid extends FlowableFluid {
	@Override
	public boolean matchesType(Fluid fluid) { return fluid == getStill() || fluid == getFlowing(); }
	@Override
	public Fluid getFlowing() { return MuddyPigsMod.FLOWING_MUD_FLUID; }
	@Override
	public Fluid getStill() { return MuddyPigsMod.STILL_MUD_FLUID; }
	@Override
	public Item getBucketItem() { return MuddyPigsMod.MUD_BUCKET; }
	@Override
	protected boolean isInfinite(ServerWorld world) { return false; }
	@Override
	protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
		Block.dropStacks(state, world, pos, blockEntity);
	}
	@Override
	protected void onEntityCollision(World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
		handler.addEvent(CollisionEvent.EXTINGUISH);
		handler.addPostCallback(CollisionEvent.EXTINGUISH, MuddyPig::Mudden);
	}
	@Override
	protected int getMaxFlowDistance(WorldView worldIn) { return 4; }
	@Override
	protected int getLevelDecreasePerBlock(WorldView worldIn) { return 3; }
	@Override
	public boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
		//noinspection deprecation
		return fluid.isIn(FluidTags.WATER) || direction == Direction.DOWN && !fluid.isIn(MuddyPigsMod.TAG_MUD);
	}
	@Override
	public int getTickRate(WorldView world) { return 5; }
	@Override
	protected float getBlastResistance() { return 100; }
	@Override
	protected BlockState toBlockState(FluidState state) {
		return MuddyPigsMod.MUD_FLUID_BLOCK.getDefaultState().with(FluidBlock.LEVEL, getBlockStateLevel(state));
	}
	@Override
	public Optional<SoundEvent> getBucketFillSound() { return Optional.of(SoundEvents.ITEM_BUCKET_FILL); }
	@Nullable
	public ParticleEffect getParticle() { return MuddyPigsMod.DRIPPING_MUD; }
	public static class Flowing extends MudFluid {
		@Override
		protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) { super.appendProperties(builder); builder.add(LEVEL); }
		public int getLevel(FluidState state) { return state.get(LEVEL); }
		public boolean isStill(FluidState state) { return false; }
	}
	public static class Still extends MudFluid {
		public int getLevel(FluidState state) { return 8; }
		public boolean isStill(FluidState state) { return true; }
	}
}
