package fun.wich.client;

import fun.wich.MuddyPigsMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class ModBlockLeakParticle extends BillboardParticle {
	private final Fluid fluid;
	protected ModBlockLeakParticle(ClientWorld world, double x, double y, double z, Fluid fluid, Sprite sprite) {
		super(world, x, y, z, sprite);
		this.setBoundingBoxSpacing(0.01F, 0.01F);
		this.gravityStrength = 0.06F;
		this.fluid = fluid;
	}
	public BillboardParticle.RenderType getRenderType() { return RenderType.PARTICLE_ATLAS_OPAQUE; }
	public void tick() {
		this.lastX = this.x;
		this.lastY = this.y;
		this.lastZ = this.z;
		this.updateAge();
		if (!this.dead) {
			this.velocityY -= this.gravityStrength;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.updateVelocity();
			if (!this.dead) {
				this.velocityX *= 0.98;
				this.velocityY *= 0.98;
				this.velocityZ *= 0.98;
				if (this.fluid != Fluids.EMPTY) {
					BlockPos blockPos = BlockPos.ofFloored(this.x, this.y, this.z);
					FluidState fluidState = this.world.getFluidState(blockPos);
					if (fluidState.getFluid() == this.fluid && this.y < blockPos.getY() + fluidState.getHeight(this.world, blockPos)) this.markDead();
				}
			}
		}
	}
	protected void updateAge() { if (this.maxAge-- <= 0) this.markDead(); }
	protected void updateVelocity() { }
	@Environment(EnvType.CLIENT)
	protected static class Dripping extends ModBlockLeakParticle {
		private final ParticleEffect nextParticle;
		public Dripping(ClientWorld world, double x, double y, double z, Fluid fluid, ParticleEffect nextParticle, Sprite sprite) {
			super(world, x, y, z, fluid, sprite);
			this.nextParticle = nextParticle;
			this.gravityStrength *= 0.02F;
			this.maxAge = 40;
		}
		protected void updateAge() {
			if (this.maxAge-- <= 0) {
				this.markDead();
				this.world.addParticleClient(this.nextParticle, this.x, this.y, this.z, this.velocityX, this.velocityY, this.velocityZ);
			}
		}
		protected void updateVelocity() {
			this.velocityX *= 0.02;
			this.velocityY *= 0.02;
			this.velocityZ *= 0.02;
		}
	}
	@Environment(EnvType.CLIENT)
	protected static class ContinuousFalling extends Falling {
		protected final ParticleEffect nextParticle;
		public ContinuousFalling(ClientWorld world, double x, double y, double z, Fluid fluid, ParticleEffect nextParticle, Sprite sprite) {
			super(world, x, y, z, fluid, sprite);
			this.nextParticle = nextParticle;
		}
		protected void updateVelocity() {
			if (this.onGround) {
				this.markDead();
				this.world.addParticleClient(this.nextParticle, this.x, this.y, this.z, 0, 0, 0);
			}
		}
	}
	@Environment(EnvType.CLIENT)
	protected static class Falling extends ModBlockLeakParticle {
		public Falling(ClientWorld clientWorld, double d, double e, double f, Fluid fluid, Sprite sprite) {
			this(clientWorld, d, e, f, fluid, (int)((double)64.0F / (Math.random() * 0.8 + 0.2)), sprite);
		}
		public Falling(ClientWorld world, double x, double y, double z, Fluid fluid, int maxAge, Sprite sprite) {
			super(world, x, y, z, fluid, sprite);
			this.maxAge = maxAge;
		}
		protected void updateVelocity() { if (this.onGround) this.markDead(); }
	}
	@Environment(EnvType.CLIENT)
	public record DrippingDripstoneMudFactory(SpriteProvider spriteProvider) implements ParticleFactory<SimpleParticleType> {
		public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Random random) {
			ModBlockLeakParticle blockLeakParticle = new Dripping(clientWorld, x, y, z, Fluids.WATER, ParticleTypes.FALLING_DRIPSTONE_WATER, this.spriteProvider.getSprite(random));
			blockLeakParticle.setColor(0.25F, 0.125F, 0);
			return blockLeakParticle;
		}
	}
	@Environment(EnvType.CLIENT)
	public record FallingDripstoneMudFactory(SpriteProvider spriteProvider) implements ParticleFactory<SimpleParticleType> {
		public Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Random random) {
			ModBlockLeakParticle blockLeakParticle = new Dripping(world, x, y, z, MuddyPigsMod.STILL_MUD_FLUID, MuddyPigsMod.FALLING_DRIPSTONE_MUD, this.spriteProvider.getSprite(random));
			blockLeakParticle.setColor(0.25F, 0.125F, 0);
			return blockLeakParticle;
		}
	}
	@Environment(EnvType.CLIENT)
	public record FallingMudFactory(SpriteProvider spriteProvider) implements ParticleFactory<SimpleParticleType> {
		public Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Random random) {
				ModBlockLeakParticle blockLeakParticle = new ContinuousFalling(world, x, y, z, MuddyPigsMod.STILL_MUD_FLUID, MuddyPigsMod.MUD_SPLASH, this.spriteProvider.getSprite(random));
				blockLeakParticle.setColor(0.25F, 0.125F, 0F);
				return blockLeakParticle;
			}
	}
	@Environment(EnvType.CLIENT)
	public record DrippingMudFactory(SpriteProvider spriteProvider) implements ParticleFactory<SimpleParticleType> {
		public Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Random random) {
				ModBlockLeakParticle blockLeakParticle = new Dripping(world, x, y, z, MuddyPigsMod.STILL_MUD_FLUID, MuddyPigsMod.FALLING_MUD, this.spriteProvider.getSprite(random));
				blockLeakParticle.setColor(0.25F, 0.125F, 0F);
				return blockLeakParticle;
			}
	}
}