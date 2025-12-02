package fun.wich.mixin;

import fun.wich.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PigEntity.class)
public abstract class MuddyPigs_PigEntityMixin extends AnimalEntity implements MuddyPig, Shearable {
	@Unique	@SuppressWarnings("WrongEntityDataParameterClass")
	private static final TrackedData<Integer> MUD = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.INTEGER);
	@Unique	@SuppressWarnings("WrongEntityDataParameterClass")
	private static final TrackedData<Integer> FLOWER = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected MuddyPigs_PigEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) { super(entityType, world); }
	@Unique	private static final int WET_MUD_TIME_DELAY = 600;
	@Unique	private static final int FLOWER_DELAY = 200;
	@Unique	private static final int FLOWER_ODDS = 100;
	@Unique	public int wetMudTime = -1;
	@Override
	public MuddyPigMud MuddyPig_GetMud() { return MuddyPigMud.get(dataTracker.get(MUD)); }
	@Override
	public void MuddyPig_SetMud(MuddyPigMud mud) {
		dataTracker.set(MUD, mud.ordinal());
		if (this.wetMudTime < 0 && mud == MuddyPigMud.MUDDY) this.wetMudTime = random.nextInt(WET_MUD_TIME_DELAY) + WET_MUD_TIME_DELAY;
	}
	@Override
	public MuddyPigFlower MuddyPig_GetFlower() { return MuddyPigFlower.get(dataTracker.get(FLOWER)); }
	@Override
	public void MuddyPig_SetFlower(MuddyPigFlower flower) { this.dataTracker.set(FLOWER, flower == null ? -1 : flower.ordinal()); }
	@Inject(method="initDataTracker", at=@At("TAIL"))
	protected void MuddyPig_InitDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
		builder.add(MUD, 0);
		builder.add(FLOWER, -1);
	}
	@Inject(method="writeCustomData", at=@At("TAIL"))
	protected void MuddyPig_WriteCustomData(WriteView view, CallbackInfo ci) {
		super.writeCustomData(view);
		view.putInt("MuddyPigMud", dataTracker.get(MUD));
		view.putInt("MuddyPigFlower", dataTracker.get(FLOWER));
	}
	@Inject(method="readCustomData", at=@At("TAIL"))
	protected void MuddyPig_ReadCustomData(ReadView view, CallbackInfo ci) {
		super.readCustomData(view);
		MuddyPig_SetMud(MuddyPigMud.get(view.getInt("MuddyPigMud", 0)));
		MuddyPig_SetFlower(MuddyPigFlower.get(view.getInt("MuddyPigFlower", -1)));
	}
	@Inject(method="interactMob", at=@At("HEAD"), cancellable=true)
	public void MuddyPig_InteractMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isOf(Items.SHEARS) && this.isShearable()) {
			if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
				this.sheared(serverWorld, SoundCategory.PLAYERS, itemStack);
				this.emitGameEvent(GameEvent.SHEAR, player);
				itemStack.damage(1, player, hand.getEquipmentSlot());
			}
			cir.setReturnValue(ActionResult.SUCCESS);
		}
	}
	@Override
	public void sheared(ServerWorld world, SoundCategory shearedSoundCategory, ItemStack shears) {
		world.playSoundFromEntity(null, this, MuddyPigsMod.ENTITY_PIG_MUDDY_SHEAR, shearedSoundCategory, 1, 1);
		MuddyPigFlower flower = MuddyPig_GetFlower();
		if (flower != null) this.dropStack(world, new ItemStack(flower.getItem(), 1), this.getHeight());
		this.MuddyPig_SetFlower(null);
	}
	@Override
	public boolean isShearable() { return this.isAlive() && !this.isBaby() && MuddyPig_GetFlower() != null; }
	@Inject(method="getAmbientSound", at=@At("HEAD"), cancellable=true)
	protected void GetWallowingAsAmbientSound(CallbackInfoReturnable<SoundEvent> cir) {
		if (this instanceof EntityTouchingMud touchingMud && touchingMud.EntityInMud_IsTouchingMud()) {
			cir.setReturnValue(MuddyPigsMod.ENTITY_PIG_MUDDY_WALLOW);
		}
	}
	@Override
	public void tickMovement() {
		if (this.isAlive()) {
			MuddyPigMud mud = MuddyPig_GetMud();
			if (this instanceof EntityTouchingMud touchingMud && touchingMud.EntityInMud_IsTouchingMud()) MuddyPig_SetMud(MuddyPigMud.MUDDY);
			else if (this.isTouchingWaterOrRain()) MuddyPig_SetMud(MuddyPigMud.CLEAN);
			else if (mud == MuddyPigMud.MUDDY) {
				if (--wetMudTime <= 0) MuddyPig_SetMud(MuddyPigMud.DRIED);
			}
			if (mud == MuddyPigMud.MUDDY) {
				if (this.age % FLOWER_DELAY == 0 && MuddyPig_GetFlower() == null && random.nextInt(FLOWER_ODDS) == 0) {
					MuddyPigFlower flower = MuddyPigFlower.getRandom((PigEntity)(Object)this);
					if (flower != null) playSound(MuddyPigsMod.ENTITY_PIG_MUDDY_BLOOM);
					MuddyPig_SetFlower(flower);
				}
			}
		}
		super.tickMovement();
	}
	@Inject(method="initGoals", at=@At("TAIL"))
	private void AddMoveIntoMudGoal(CallbackInfo ci) { this.goalSelector.add(5, new MoveIntoMudGoal(this)); }
	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView world) {
		if (MuddyPig.IsDried(this) && (world.getFluidState(pos.down()).isIn(MuddyPigsMod.TAG_MUD) || world.getFluidState(pos).isIn(MuddyPigsMod.TAG_MUD))) return 30F;
		return super.getPathfindingFavor(pos, world);
	}
}
