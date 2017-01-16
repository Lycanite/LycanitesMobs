package lycanite.lycanitesmobs.core.modelloader.obj;

import java.util.HashMap;

public class HashMapWithDefault<K, V> extends HashMap<K, V>
{

	private static final long serialVersionUID = 5995791692010816132L;
	private V				 defaultValue;

	public void setDefault(V value)
	{
		defaultValue = value;
	}

	public V getDefault()
	{
		return defaultValue;
	}

	public V get(Object key)
	{
		if(this.containsKey(key))
			return super.get(key);
		else
			return defaultValue;
	}
}
