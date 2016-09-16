/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.reccomplex.structures.generic.transformers;

import com.google.gson.*;
import ivorius.ivtoolkit.tools.IvWorldData;
import ivorius.ivtoolkit.tools.MCRegistry;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.blocks.RCBlocks;
import ivorius.reccomplex.gui.editstructure.transformers.TableDataSourceBTNegativeSpace;
import ivorius.reccomplex.gui.table.TableDataSource;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.structures.Environment;
import ivorius.reccomplex.structures.StructureLoadContext;
import ivorius.reccomplex.structures.StructurePrepareContext;
import ivorius.reccomplex.structures.StructureSpawnContext;
import ivorius.reccomplex.structures.generic.matchers.BlockMatcher;
import ivorius.reccomplex.structures.generic.matchers.PositionedBlockMatcher;
import net.minecraft.block.state.IBlockState;
import ivorius.reccomplex.utils.NBTNone;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

/**
 * Created by lukas on 25.05.14.
 */
public class TransformerNegativeSpace extends Transformer<NBTNone>
{
    public BlockMatcher sourceMatcher;
    public PositionedBlockMatcher destMatcher;

    public TransformerNegativeSpace()
    {
        this(null, BlockMatcher.of(RecurrentComplex.specialRegistry, RCBlocks.genericSpace, 0), "");
    }

    public TransformerNegativeSpace(@Nullable String id, String sourceExpression, String destExpression)
    {
        super(id != null ? id : randomID(TransformerNegativeSpace.class));
        this.sourceMatcher = new BlockMatcher(RecurrentComplex.specialRegistry, sourceExpression);
        this.destMatcher = new PositionedBlockMatcher(RecurrentComplex.specialRegistry, destExpression);
    }

    @Override
    public boolean mayGenerate(NBTNone instanceData, StructureSpawnContext context, IvWorldData worldData, TransformerMulti transformer, TransformerMulti.InstanceData transformerID)
    {
        return true;
    }

    @Override
    public boolean skipGeneration(NBTNone instanceData, Environment environment, BlockPos pos, IBlockState state)
    {
        return sourceMatcher.test(state) && (destMatcher.expressionIsEmpty() || destMatcher.test(PositionedBlockMatcher.Argument.at(environment.world, pos)));
    }

    @Override
    public void transform(NBTNone instanceData, Phase phase, StructureSpawnContext context, IvWorldData worldData, TransformerMulti transformer, TransformerMulti.InstanceData transformerID)
    {

    }

    @Override
    public String getDisplayString()
    {
        return "Space: " + sourceMatcher.getDisplayString(null);
    }

    @Override
    public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate)
    {
        return new TableDataSourceBTNegativeSpace(this, navigator, delegate);
    }

    @Override
    public NBTNone prepareInstanceData(StructurePrepareContext context, IvWorldData worldData)
    {
        return new NBTNone();
    }

    @Override
    public NBTNone loadInstanceData(StructureLoadContext context, NBTBase nbt)
    {
        return new NBTNone();
    }

    public static class Serializer implements JsonDeserializer<TransformerNegativeSpace>, JsonSerializer<TransformerNegativeSpace>
    {
        private MCRegistry registry;

        public Serializer(MCRegistry registry)
        {
            this.registry = registry;
        }

        @Override
        public TransformerNegativeSpace deserialize(JsonElement jsonElement, Type par2Type, JsonDeserializationContext context)
        {
            JsonObject jsonObject = JsonUtils.asJsonObject(jsonElement, "transformerNegativeSpace");

            String id = JsonUtils.getString(jsonObject, "id", null);

            String expression = TransformerReplace.Serializer.readLegacyMatcher(jsonObject, "source", "sourceMetadata"); // Legacy
            if (expression == null)
                expression = JsonUtils.getString(jsonObject, "sourceExpression", "");

            String destExpression = JsonUtils.getString(jsonObject, "destExpression", "");

            return new TransformerNegativeSpace(id, expression, destExpression);
        }

        @Override
        public JsonElement serialize(TransformerNegativeSpace transformer, Type par2Type, JsonSerializationContext context)
        {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("id", transformer.id());
            jsonObject.addProperty("sourceExpression", transformer.sourceMatcher.getExpression());
            jsonObject.addProperty("destExpression", transformer.destMatcher.getExpression());

            return jsonObject;
        }
    }
}
