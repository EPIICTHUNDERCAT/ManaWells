package com.epiicthundercat.manawell.init;


import com.epiicthundercat.manawell.tileentity.TileEntityManaWell;

import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ManaWellTileEntities {


	public static void init() {
		
		GameRegistry.registerTileEntity(TileEntityManaWell.class, "mana_well");
		
	}
}
