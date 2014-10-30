package fundamental;

import com.google.common.collect.HashBiMap;
public class CompressedHashBiMapper {
	private HashBiMap <CompressedString, Integer> map;
	public CompressedHashBiMapper(){
		map=HashBiMap.create();
	}
	public int size(){
		return map.size();
	}	
	public void clear(){
		map.clear();
	}
	public int getValue(String key){
		int id;
		Integer value=map.get(new CompressedString(key));
		if (value==null){
			id = -1;
		}else{
			id=value.intValue();
		}
		return id;
	}
	public Integer[] getValues(String[]keys){
		Integer [] result=new Integer[keys.length];
		for (int i=0; i<keys.length; i++){
			result[i]=this.getValue(keys[i]);
		}
		return result;
	}
	public boolean containsKey(String key){
		if (map.get(new CompressedString(key))!=null){
			return true;
		}else{
			return false;
		}
	}
	public boolean containsValue(int value){
		if (map.inverse().get(value)!=null){
			return true;
		}else{
			return false;
		}
	}
	public int put(String key){
		CompressedString bytes=new CompressedString(key);
		Integer value=map.get(bytes);
		if (value==null){
			value=new Integer(map.size()); 
			map.put(bytes, value);
		}
		return value.intValue();
	}
	//caution: remove introduce discontinuous values range!
	public void removeByKey(String key){
		this.map.remove(new CompressedString(key));
	}
	//caution: remove introduce discontinuous values range!
	public void removeByValue(int value){
		this.map.remove(this.getKey(value));
	}
	public String getKey(int value){
		String result;
		CompressedString bytes =map.inverse().get(new Integer(value));
		if (bytes!=null){
			result = bytes.getString();
		}else{
			result=null;
		}
		return result;
	}
	public String[] getKeys(Integer[] values){
		String[] result=new String[values.length];
		for (int i=0; i<values.length; i++){
			result[i]=this.getKey(values[i]);
		}
		return result;
	}
}
