package fun.wich.client;

import fun.wich.MuddyPigFlower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.client.render.entity.state.PigEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MuddyPigFlowerFeatureRenderer extends FeatureRenderer<PigEntityRenderState, PigEntityModel> {
	private final MuddyPigFlowerModel model;
	public MuddyPigFlowerFeatureRenderer(FeatureRendererContext<PigEntityRenderState, PigEntityModel> context, LoadedEntityModels loader) {
		super(context);
		this.model = new MuddyPigFlowerModel(loader.getModelPart(MuddyPigClient.MUDDY_PIG_FLOWER));
	}
	@Override
	public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, PigEntityRenderState state, float limbAngle, float limbDistance) {
		if (!(state instanceof MuddyPigRenderState muddyPig)) return;
		MuddyPigFlower flower = muddyPig.MuddyPig_GetFlower();
		if (flower == null) return;
		Identifier TEXTURE = flower.getTexture();
		boolean bl = state.hasOutline() && state.invisible;
		if (!state.invisible || bl) {
			RenderLayer renderLayer = bl ? RenderLayer.getOutline(TEXTURE) : RenderLayer.getEntityCutout(TEXTURE);
			queue.getBatchingQueue(1).submitModel(this.model, state, matrices, renderLayer, light, LivingEntityRenderer.getOverlay(state, 0), -1, null, state.outlineColor, null);
		}
	}
}
