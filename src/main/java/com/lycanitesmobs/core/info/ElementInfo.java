package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.helpers.JSONHelper;

import java.util.ArrayList;
import java.util.List;

/** Elements affect what buffs and debuffs a creature has in addition to some strengths and weaknesses and fusion. **/
public class ElementInfo {

	/** The name of this element. **/
	public String name;

	/** The names of elements that make up this elements, used by compound elements. **/
	protected List<String> componentNames = new ArrayList<>();

	/** The elements that make up this elements, used by compound elements. This is populated from the component names list once all elements are loaded. **/
	public List<ElementInfo> components = new ArrayList<>();

	/** The type of this element, can be primal or compound. Default: Automatic (primal if no components, compound if there are components.) **/
	public String type;

	/** A list of beneficial potion effects that this element can grant. **/
	public List<String> buffs = new ArrayList<>();

	/** A list of detrimental potion effects that this element can inflict as well as grant immunity to. **/
	public List<String> debuffs = new ArrayList<>();


	/** Loads this element from a JSON object. **/
	public void loadFromJSON(JsonObject json) {
		if(json.has("name"))
			this.name = json.get("name").getAsString();

		if(json.has("components"))
			this.componentNames = JSONHelper.getJsonStrings(json.get("components").getAsJsonArray());

		if(json.has("type"))
			this.type = json.get("type").getAsString();
		else this.type = this.componentNames.isEmpty() ? "primal" : "compound";

		if(json.has("buffs"))
			this.buffs = JSONHelper.getJsonStrings(json.get("buffs").getAsJsonArray());

		if(json.has("debuffs"))
			this.debuffs = JSONHelper.getJsonStrings(json.get("debuffs").getAsJsonArray());
	}


	/** Initialises this Element, called once all Elements have been loaded. **/
	public void init() {
		// Compound components:
		if(this.type.equalsIgnoreCase("compound")) {
			for(String componentName : this.componentNames) {
				if(ElementManager.getInstance().elements.containsKey(componentName)) {
					this.components.add(ElementManager.getInstance().elements.get(componentName));
				}
			}
		}
	}
}
