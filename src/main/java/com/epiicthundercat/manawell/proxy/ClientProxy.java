package com.darkliz.manawell.proxy;

import com.darkliz.manawell.init.ManaWellBlocks;

public class ClientProxy extends CommonProxy{

	@Override
	public void registerRenders()
	{
		ManaWellBlocks.registerRenders();
		
	}
	

}
