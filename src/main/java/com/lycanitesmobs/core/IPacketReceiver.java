package com.lycanitesmobs.core;

import com.google.common.io.ByteArrayDataInput;

public interface IPacketReceiver {
	
	// Receive Packet Data:
	public void receivePacketData(ByteArrayDataInput data);
}
