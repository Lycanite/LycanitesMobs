package com.lycanitesmobs.api;

public interface IFusable {

	/** Gets the current fusion target that this fusable is attempting to get to and fuse with. **/
	public IFusable getFusionTarget();

	/** Sets the current fusion target that this fusable is attempting to get to and fuse with. **/
	public void setFusionTarget(IFusable fusionTarget);

	/** Returns the class to become when fusing with the provided fusable. Returns null if no fusion is possible. **/
	public Class getFusionClass(IFusable fusable);
}
