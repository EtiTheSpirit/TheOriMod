package etithespirit.orimod.datagen.advancements;

import etithespirit.orimod.registry.advancements.AdvancementRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public class GenerateAdvancements extends AdvancementProvider {
	//private ExistingFileHelper existingFileHelper;
	
	public GenerateAdvancements(DataGenerator generatorIn, ExistingFileHelper fileHelperIn) {
		super(generatorIn, fileHelperIn);
	}
	
	private void registerSingleAction(Consumer<Advancement> consumer, SimpleCriterionTrigger<?> trigger) {
		Advancement.Builder.advancement().addCriterion("main_action", AdvancementRegistry.BECOME_SPIRIT.createInstance()).save(consumer, trigger.getId(), this.fileHelper);
	}
	
	@Override
	protected void registerAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper) {
		registerSingleAction(consumer, AdvancementRegistry.BECOME_SPIRIT);
	}
}
