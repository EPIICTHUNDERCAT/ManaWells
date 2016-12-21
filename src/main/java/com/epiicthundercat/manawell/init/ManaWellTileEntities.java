package com.darkliz.manawell.init;


import com.darkliz.manawell.tileentity.TileEntityManaWell;

import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ManaWellTileEntities {


	public static void init() {
		
		GameRegistry.registerTileEntity(TileEntityManaWell.class, "mana_well_tile_entity");
		
	}
}
