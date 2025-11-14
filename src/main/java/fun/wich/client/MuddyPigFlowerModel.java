package fun.wich.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.PigEntityRenderState;

@Environment(EnvType.CLIENT)
public class MuddyPigFlowerModel extends EntityModel<PigEntityRenderState> {
	private final ModelPart head;
	public MuddyPigFlowerModel(ModelPart root) {
		super(root);
		this.head = root.getChild("head");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 11).cuboid(-1.0F, -5.0F, -7.0F, 4.0F, 1.0F, 4.0F)
				.uv(0, 0).cuboid(-3.0F, -16.0F, -5.0F, 9.0F, 11.0F, 0.0F)
				.uv(0, 16).cuboid(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F), ModelTransform.origin(0.0F, 12.0F, -6.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}
	@Override
	public void setAngles(PigEntityRenderState state) {
		super.setAngles(state);
		this.head.pitch = state.pitch * (float) (Math.PI / 180.0);
		this.head.yaw = state.relativeHeadYaw * (float) (Math.PI / 180.0);
	}
}
