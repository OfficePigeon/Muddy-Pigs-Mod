package fun.wich;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.CollisionEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Map;

public class MudCauldronBlock extends AbstractCauldronBlock {
	public static final MapCodec<MudCauldronBlock> CODEC = createCodec(MudCauldronBlock::new);
	private static final VoxelShape MUD_SHAPE = Block.createColumnShape(12.0, 4.0, 15.0);
	private static final VoxelShape INSIDE_COLLISION_SHAPE = VoxelShapes.union(AbstractCauldronBlock.OUTLINE_SHAPE, MUD_SHAPE);
	private static final CauldronBehavior.CauldronBehaviorMap MUD_CAULDRON_BEHAVIOR = CauldronBehavior.createMap("mud");
	static {
		Map<Item, CauldronBehavior> map = MUD_CAULDRON_BEHAVIOR.map();
		map.put(Items.BUCKET,
				(state, world, pos, player, hand, stack)
						-> CauldronBehavior.emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(MuddyPigsMod.MUD_BUCKET),
						statex -> true, SoundEvents.ITEM_BUCKET_FILL)
		);
		CauldronBehavior.registerBucketBehavior(map);
	}
	public static ActionResult tryFillWithMud(BlockState ignoredState, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
		return CauldronBehavior.fillCauldron(world, pos, player, hand, stack, MuddyPigsMod.MUD_CAULDRON.getDefaultState(), SoundEvents.ITEM_BUCKET_EMPTY);
	}
	@Override
	public MapCodec<MudCauldronBlock> getCodec() { return CODEC; }
	public MudCauldronBlock(Settings settings) { super(settings, MUD_CAULDRON_BEHAVIOR); }
	@Override
	protected double getFluidHeight(BlockState state) { return 0.9375; }
	@Override
	public boolean isFull(BlockState state) { return true; }
	@Override
	protected VoxelShape getInsideCollisionShape(BlockState state, BlockView world, BlockPos pos, Entity entity) { return INSIDE_COLLISION_SHAPE; }
	@Override
	protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
		handler.addEvent(CollisionEvent.EXTINGUISH);
		handler.addPostCallback(CollisionEvent.EXTINGUISH, MuddyPig::Mudden);
	}
}
