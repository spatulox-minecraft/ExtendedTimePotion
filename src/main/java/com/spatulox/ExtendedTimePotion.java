package com.spatulox;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtendedTimePotion implements ModInitializer {
    public static final String MOD_ID = "extended-time-potion";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Aliases, so the hundred registrations below read unchanged. The arithmetic
    // lives in PotionDuration because this class cannot be loaded outside the game:
    // reading a constant here runs the static initialisers, which hit the registries.
    public static final int ELEVEN_MINUTES = PotionDuration.ELEVEN_MINUTES;
    public static final int FIFTEEN_MINUTES = PotionDuration.FIFTEEN_MINUTES;

    // NIGHT_VISION
    public static Holder<Potion> LONG_LONG_NIGHT_VISION = registerPotion("long_long_night_vision",
            new Potion("night_vision", new MobEffectInstance(MobEffects.NIGHT_VISION, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_NIGHT_VISION = registerPotion("ultra_long_night_vision",
            new Potion("night_vision", new MobEffectInstance(MobEffects.NIGHT_VISION, FIFTEEN_MINUTES)));

    // INVISIBILITY
    public static Holder<Potion> LONG_LONG_INVISIBILITY = registerPotion("long_long_invisibility",
            new Potion("invisibility", new MobEffectInstance(MobEffects.INVISIBILITY, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_INVISIBILITY = registerPotion("ultra_long_invisibility",
            new Potion("invisibility", new MobEffectInstance(MobEffects.INVISIBILITY, FIFTEEN_MINUTES)));

    // LEAPING
    public static Holder<Potion> LONG_LONG_LEAPING = registerPotion("long_long_leaping",
            new Potion("leaping", new MobEffectInstance(MobEffects.JUMP_BOOST, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_LEAPING = registerPotion("ultra_long_leaping",
            new Potion("leaping", new MobEffectInstance(MobEffects.JUMP_BOOST, FIFTEEN_MINUTES)));

    // STRONG_LEAPING
    public static Holder<Potion> LONG_LONG_STRONG_LEAPING = registerPotion("long_long_strong_leaping",
            new Potion("leaping", new MobEffectInstance(MobEffects.JUMP_BOOST, ELEVEN_MINUTES, 1)));
    public static Holder<Potion> ULTRA_LONG_STRONG_LEAPING = registerPotion("ultra_long_strong_leaping",
            new Potion("leaping", new MobEffectInstance(MobEffects.JUMP_BOOST, FIFTEEN_MINUTES, 1)));

    // FIRE RESISTANCE
    public static Holder<Potion> LONG_LONG_FIRE_RESISTANCE = registerPotion("long_long_fire_resistance",
            new Potion("fire_resistance", new MobEffectInstance(MobEffects.FIRE_RESISTANCE, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_FIRE_RESISTANCE = registerPotion("ultra_long_fire_resistance",
            new Potion("fire_resistance", new MobEffectInstance(MobEffects.FIRE_RESISTANCE, FIFTEEN_MINUTES)));

    // SWIFTNESS
    public static Holder<Potion> LONG_LONG_SWIFTNESS = registerPotion("long_long_swiftness",
            new Potion("swiftness", new MobEffectInstance(MobEffects.SPEED, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_SWIFTNESS = registerPotion("ultra_long_swiftness",
            new Potion("swiftness", new MobEffectInstance(MobEffects.SPEED, FIFTEEN_MINUTES)));

    // STRONG SWIFTNESS
    public static Holder<Potion> LONG_LONG_STRONG_SWIFTNESS = registerPotion("long_long_strong_swiftness",
            new Potion("swiftness", new MobEffectInstance(MobEffects.SPEED, ELEVEN_MINUTES, 1)));
    public static Holder<Potion> ULTRA_LONG_STRONG_SWIFTNESS = registerPotion("ultra_long_strong_swiftness",
            new Potion("swiftness", new MobEffectInstance(MobEffects.SPEED, FIFTEEN_MINUTES, 1)));

    // SLOWNESS
    public static Holder<Potion> LONG_LONG_SLOWNESS = registerPotion("long_long_slowness",
            new Potion("slowness", new MobEffectInstance(MobEffects.SLOWNESS, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_SLOWNESS = registerPotion("ultra_long_slowness",
            new Potion("slowness", new MobEffectInstance(MobEffects.SLOWNESS, FIFTEEN_MINUTES)));

    // STRONG SLOWNESS
    public static Holder<Potion> LONG_LONG_STRONG_SLOWNESS = registerPotion("long_long_strong_slowness",
            new Potion("slowness", new MobEffectInstance(MobEffects.SLOWNESS, ELEVEN_MINUTES, 3)));
    public static Holder<Potion> ULTRA_LONG_STRONG_SLOWNESS = registerPotion("ultra_long_strong_slowness",
            new Potion("slowness", new MobEffectInstance(MobEffects.SLOWNESS, FIFTEEN_MINUTES, 3)));

    // TURTLE MASTER
    public static Holder<Potion> LONG_LONG_TURTLE_MASTER = registerPotion("long_long_turtle_master",
            new Potion("turtle_master", new MobEffectInstance[]{
                    new MobEffectInstance(MobEffects.SLOWNESS, ELEVEN_MINUTES, 3),
                    new MobEffectInstance(MobEffects.RESISTANCE, ELEVEN_MINUTES, 2)
            }));
    public static Holder<Potion> ULTRA_LONG_TURTLE_MASTER = registerPotion("ultra_long_turtle_master",
            new Potion("turtle_master", new MobEffectInstance[]{
                    new MobEffectInstance(MobEffects.SLOWNESS, FIFTEEN_MINUTES, 3),
                    new MobEffectInstance(MobEffects.RESISTANCE, FIFTEEN_MINUTES, 2)
            }));

    // STRONG TURTLE MASTER
    public static Holder<Potion> LONG_LONG_STRONG_TURTLE_MASTER = registerPotion("long_long_strong_turtle_master",
            new Potion("turtle_master", new MobEffectInstance[]{
                    new MobEffectInstance(MobEffects.SLOWNESS, ELEVEN_MINUTES, 5),
                    new MobEffectInstance(MobEffects.RESISTANCE, ELEVEN_MINUTES, 3)
            }));
    public static Holder<Potion> ULTRA_LONG_STRONG_TURTLE_MASTER = registerPotion("ultra_long_strong_turtle_master",
            new Potion("turtle_master", new MobEffectInstance[]{
                    new MobEffectInstance(MobEffects.SLOWNESS, FIFTEEN_MINUTES, 5),
                    new MobEffectInstance(MobEffects.RESISTANCE, FIFTEEN_MINUTES, 3)
            }));

    // WATER BREATHING
    public static Holder<Potion> LONG_LONG_WATER_BREATHING = registerPotion("long_long_water_breathing",
            new Potion("water_breathing", new MobEffectInstance(MobEffects.WATER_BREATHING, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_WATER_BREATHING = registerPotion("ultra_long_water_breathing",
            new Potion("water_breathing", new MobEffectInstance(MobEffects.WATER_BREATHING, FIFTEEN_MINUTES)));

    // POISON
    public static Holder<Potion> LONG_LONG_POISON = registerPotion("long_long_poison",
            new Potion("poison", new MobEffectInstance(MobEffects.POISON, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_POISON = registerPotion("ultra_long_poison",
            new Potion("poison", new MobEffectInstance(MobEffects.POISON, FIFTEEN_MINUTES)));

    // STRONG POISON
    public static Holder<Potion> LONG_LONG_STRONG_POISON = registerPotion("long_long_strong_poison",
            new Potion("poison", new MobEffectInstance(MobEffects.POISON, ELEVEN_MINUTES, 1)));
    public static Holder<Potion> ULTRA_LONG_STRONG_POISON = registerPotion("ultra_long_strong_poison",
            new Potion("poison", new MobEffectInstance(MobEffects.POISON, FIFTEEN_MINUTES, 1)));

    // REGENERATION
    public static Holder<Potion> LONG_LONG_REGENERATION = registerPotion("long_long_regeneration",
            new Potion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_REGENERATION = registerPotion("ultra_long_regeneration",
            new Potion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, FIFTEEN_MINUTES)));

    // STRONG REGENERATION
    public static Holder<Potion> LONG_LONG_STRONG_REGENERATION = registerPotion("long_long_strong_regeneration",
            new Potion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, ELEVEN_MINUTES, 1)));
    public static Holder<Potion> ULTRA_LONG_STRONG_REGENERATION = registerPotion("ultra_long_strong_regeneration",
            new Potion("regeneration", new MobEffectInstance(MobEffects.REGENERATION, FIFTEEN_MINUTES, 1)));

    // STRENGTH
    public static Holder<Potion> LONG_LONG_STRENGTH = registerPotion("long_long_strength",
            new Potion("strength", new MobEffectInstance(MobEffects.STRENGTH, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_STRENGTH = registerPotion("ultra_long_strength",
            new Potion("strength", new MobEffectInstance(MobEffects.STRENGTH, FIFTEEN_MINUTES)));

    // STRONG STRENGTH
    public static Holder<Potion> LONG_LONG_STRONG_STRENGTH = registerPotion("long_long_strong_strength",
            new Potion("strength", new MobEffectInstance(MobEffects.STRENGTH, ELEVEN_MINUTES, 1)));
    public static Holder<Potion> ULTRA_LONG_STRONG_STRENGTH = registerPotion("ultra_long_strong_strength",
            new Potion("strength", new MobEffectInstance(MobEffects.STRENGTH, FIFTEEN_MINUTES, 1)));

    // WEAKNESS
    public static Holder<Potion> LONG_LONG_WEAKNESS = registerPotion("long_long_weakness",
            new Potion("weakness", new MobEffectInstance(MobEffects.WEAKNESS, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_WEAKNESS = registerPotion("ultra_long_weakness",
            new Potion("weakness", new MobEffectInstance(MobEffects.WEAKNESS, FIFTEEN_MINUTES)));

    // LUCK
    public static Holder<Potion> LONG_LONG_LUCK = registerPotion("long_long_luck",
            new Potion("luck", new MobEffectInstance(MobEffects.LUCK, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_LUCK = registerPotion("ultra_long_luck",
            new Potion("luck", new MobEffectInstance(MobEffects.LUCK, FIFTEEN_MINUTES)));

    // SLOW FALLING
    public static Holder<Potion> LONG_LONG_SLOW_FALLING = registerPotion("long_long_slow_falling",
            new Potion("slow_falling", new MobEffectInstance(MobEffects.SLOW_FALLING, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_SLOW_FALLING = registerPotion("ultra_long_slow_falling",
            new Potion("slow_falling", new MobEffectInstance(MobEffects.SLOW_FALLING, FIFTEEN_MINUTES)));

    // WIND CHARGED
    public static Holder<Potion> LONG_LONG_WIND_CHARGED = registerPotion("long_long_wind_charged",
            new Potion("wind_charged", new MobEffectInstance(MobEffects.WIND_CHARGED, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_WIND_CHARGED = registerPotion("ultra_long_wind_charged",
            new Potion("wind_charged", new MobEffectInstance(MobEffects.WIND_CHARGED, FIFTEEN_MINUTES)));

    // WEAVING
    public static Holder<Potion> LONG_LONG_WEAVING = registerPotion("long_long_weaving",
            new Potion("weaving", new MobEffectInstance(MobEffects.WEAVING, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_WEAVING = registerPotion("ultra_long_weaving",
            new Potion("weaving", new MobEffectInstance(MobEffects.WEAVING, FIFTEEN_MINUTES)));

    // OOZING
    public static Holder<Potion> LONG_LONG_OOZING = registerPotion("long_long_oozing",
            new Potion("oozing", new MobEffectInstance(MobEffects.OOZING, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_OOZING = registerPotion("ultra_long_oozing",
            new Potion("oozing", new MobEffectInstance(MobEffects.OOZING, FIFTEEN_MINUTES)));

    // INFESTED
    public static Holder<Potion> LONG_LONG_INFESTED = registerPotion("long_long_infested",
            new Potion("infested", new MobEffectInstance(MobEffects.INFESTED, ELEVEN_MINUTES)));
    public static Holder<Potion> ULTRA_LONG_INFESTED = registerPotion("ultra_long_infested",
            new Potion("infested", new MobEffectInstance(MobEffects.INFESTED, FIFTEEN_MINUTES)));

    @Override
    public void onInitialize() {

        // Brewing recipes are data driven since 26.3: the mixes are generated by
        // src/datagen into src/main/generated/data/extended-time-potion/recipe/brewing.
        // Counting them once the server is up proves the JSON actually loaded.
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            long mixes = server.getRecipeManager().getRecipes().stream()
                    .filter(holder -> holder.value().getType() == RecipeType.BREWING)
                    .filter(holder -> MOD_ID.equals(holder.id().identifier().getNamespace()))
                    .count();
            if (mixes == 0) {
                LOGGER.error("No brewing recipe loaded, the generated data is missing from the jar");
                return;
            }
            // The CI checks this line to validate compatibility with a new version
            // (see .github/mc-bump.yml, tests.server.expect).
            LOGGER.info("Brewing mixes registered: {} recipes", mixes);
        });

        long registered = BuiltInRegistries.POTION.keySet().stream()
                .filter(id -> MOD_ID.equals(id.getNamespace()))
                .count();
        LOGGER.info("Registered {} potions", registered);
    }

    private static Holder<Potion> registerPotion(String name, Potion potion) {
        return Registry.registerForHolder(BuiltInRegistries.POTION, ExtendedTimePotion.id(name), potion);
    }

    public static Identifier id(String path){
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
