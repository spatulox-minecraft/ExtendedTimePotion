package com.spatulox.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.crafting.Recipe;

import java.util.concurrent.CompletableFuture;

/**
 * Run with ./gradlew runDatagen, then commit src/main/generated: the jar ships those
 * files, nothing is generated at build time.
 */
public class ExtendedTimePotionDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(BrewingRecipes::new);
    }

    private static class BrewingRecipes extends FabricRecipeProvider {
        BrewingRecipes(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries,
                BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
            return new RecipeProvider(recipes, advancements) {
                @Override
                public void buildRecipes() {
                    new ExtendedTimePotionBrewingProvider(output).buildRecipes();
                }
            };
        }

        @Override
        public String getName() {
            return "Extended Time Potion brewing recipes";
        }
    }
}
