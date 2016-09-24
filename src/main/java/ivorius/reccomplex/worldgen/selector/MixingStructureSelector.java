/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://ivorius.net
 */

package ivorius.reccomplex.worldgen.selector;

import ivorius.ivtoolkit.random.WeightedSelector;
import ivorius.reccomplex.structures.StructureInfo;
import ivorius.reccomplex.structures.generic.gentypes.StructureGenerationInfo;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by lukas on 24.09.16.
 */
public class MixingStructureSelector<T extends StructureGenerationInfo & EnvironmentalSelection<C>, C extends MixingStructureSelector.Category> extends StructureSelector<T, C>
{
    public MixingStructureSelector(Collection<StructureInfo> structures, WorldProvider provider, Biome biome, Class<T> typeClass)
    {
        super(structures, provider, biome, typeClass);
    }

    public float generationChance(C category, WorldProvider worldProvider, Biome biome)
    {
        if (category != null)
            return category.structureSpawnChance(biome, worldProvider, weightedStructureInfos.get(category).size());

        return 0.0f;
    }

    public List<Pair<StructureInfo, T>> generatedStructures(Random random, Biome biome, WorldProvider provider)
    {
        return weightedStructureInfos.keySet().stream()
                .filter(category -> random.nextFloat() < generationChance(category, provider, biome))
                .map(category -> WeightedSelector.select(random, weightedStructureInfos.get(category)))
                .collect(Collectors.toList());
    }

    @Nullable
    @Override
    public Pair<StructureInfo, T> selectOne(Random random, WorldProvider provider, Biome biome, @Nullable C c)
    {
        if (c != null)
            return super.selectOne(random, provider, biome, c);

        List<WeightedSelector.SimpleItem<C>> list = weightedStructureInfos.keySet().stream()
                .map(category -> new WeightedSelector.SimpleItem<>(generationChance(category, provider, biome), category))
                .collect(Collectors.toList());

        if (WeightedSelector.canSelect(list))
            return WeightedSelector.select(random, weightedStructureInfos.get(WeightedSelector.select(random, list)));
        else
            return null;
    }

    interface Category
    {
        float structureSpawnChance(Biome biome, WorldProvider worldProvider, int registeredStructures);
    }
}
