package org.LiangMi.soulstone.block.entity;


import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.LiangMi.soulstone.block.SoulBlocks;

public class SoulBlockEntityType {

    public static <T extends BlockEntityType<?>> T register(String path, T blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("tutorial", path), blockEntityType);
    }

    public static final BlockEntityType<SoulTableBlockEntity> SOUL_TABLE = register(
            "soul_table",
            FabricBlockEntityTypeBuilder.create(SoulTableBlockEntity::new, SoulBlocks.SoulTable).build()
    );

    public static void initialize() {
    }
}
