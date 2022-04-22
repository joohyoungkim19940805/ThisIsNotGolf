package com.hide_and_fps.business_logic.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ZeroCalorieJson<K,V, T> extends HashMap<K,V>{
	
	private static final long serialVersionUID = 5405184309862895808L;
	private int whiteSpaceQuantity = 4;
	private Map testMap = new HashMap();
	private List testList = new ArrayList();
	public ZeroCalorieJson(){
		super();
	}
	public ZeroCalorieJson(HashMap hashMap) {
		super(hashMap);
	}
	public ZeroCalorieJson(Map map) {
		super(map);
	}
	@SafeVarargs
	public ZeroCalorieJson(Entry<K,V> ... entrys) {
		this(Map.ofEntries(entrys));
	}
	public ZeroCalorieJson(String str) {
		//System.out.println(str.replaceAll("\n", "").replaceAll("([^(^\\w,<>{}/|;:.,\"~!?@#$%^=&*\\]\\\\()\\[¿§«»ω⊙¤°℃℉€¥£¢¡®©0-9_+)])", ""));
		new CalorieSon2(str.replaceAll("\n", "").replaceAll("([^(^\\w,<>{}/|;:.,\"~!?@#$%^=&*\\]\\\\()\\[¿§«»ω⊙¤°℃℉€¥£¢¡®©0-9_+)])", ""));
	}
	public ZeroCalorieJson(char[] str) {
		//System.out.println( Arrays.asList(str) );
		//for(String s : str) {System.out.println(s);}
		//new CalorieSon(str);
		//create(str);
	}
	public ZeroCalorieJson(Entry<K,V> entry) {
		super.put(entry.getKey(), entry.getValue());
	}
	public ZeroCalorieJson setListIndetKeyAndValue(List<T> o) {
		destructuring( new ArrayList(o));
		return this;
	}
	private void destructuring(ArrayList objects) {
		if(objects.size() %2 != 0) {
			throw new ArrayIndexOutOfBoundsException(" List Size % 2 == Not True");
		}else {
			ArrayList oList = (ArrayList) objects.clone();
			oList.stream().forEach(item -> {
				if(objects.size() > 1 && objects.get(0) != null && objects.get(1) != null) {
					K key = (K) objects.get(0).toString();
					V value = (V) objects.get(1);
					super.put(key, value);
					objects.remove(0);
					objects.remove(0);
				}
			});
		}
	}
	
	public String getJson() {
		return new ZeroSon((Map) super.clone(), whiteSpaceQuantity).json;
	}
	
	public void setWhiteSpaceQuantity(int quantity) {
		this.whiteSpaceQuantity = quantity;
	}

	private class ZeroSon{
		public String json;
		public ZeroSon(Map map, int t) {
			this.json = """
					{
					%s
					}"""
					.formatted( String.join(",\n",
											 map.entrySet()
											.parallelStream()
											.map(entry ->{
													String key = ((Entry<String, Object>) entry).getKey().toString();
													Object value = ((Entry<String, Object>) entry).getValue();
													value = convert(value, t);
													return """
													"%s" : %s """.formatted(key, value.toString());
											}).toList()
										).indent(t)
								);
			
		}
		
		private Object convert(Object obj, int t) {
			if(obj instanceof Map<?,?>){
				obj = new ZeroSon((Map)obj, t).json;
			}else if(obj instanceof Entry<?, ?>) {
				obj = new ZeroSon( new ZeroCalorieJson( (Entry) obj ), t ).json;
			}else if(obj instanceof List<?>) {
				obj = ((List) obj).parallelStream().map(item -> {
					item = convert(item, t);
					return item;
				}).toList();
			}else {
				obj = "\""+obj+"\"";
			}

			return obj;
		}
	}
	private class CalorieSon2{
		public Map map = new HashMap();
		public List list = new ArrayList();
		public Map subMap = Collections.EMPTY_MAP;
		public List subList = Collections.EMPTY_LIST;
		private String key = "";
		private String value = "";
		private boolean identifierSwitch = false;
		private boolean doubleQuotesIn = false;
		/* { : 123
		 * } : 125
		 * [ : 91
		 * ] : 93
		 * : : 58
		 * , : 44
		 * " : 34
		 * \ : 92
		 */
		public CalorieSon2(String str) {
			AtomicInteger index = new AtomicInteger();
			str.chars().filter(charInt->{
				index.getAndIncrement();
				if(charInt == 123) {
					this.subMap = (Map) new CalorieSon2(str.substring(Integer.parseInt(index.toString()), str.lastIndexOf('}')), map).map;
					return true;
				}else if(charInt == 91) {
					this.subList = new CalorieSon2(str.substring(Integer.parseInt(index.toString()), str.lastIndexOf(']')), list).list;
					return true;
				}else {
					return false;
				}
			}).findFirst();
			str.chars().filter(charInt->{
				char c = (char)charInt;
				int i = index.get();
				if(charInt ==  34 && str.charAt(i-1) != '\\') {
					doubleQuotesIn = !doubleQuotesIn;
				}
				
				if(charInt == 123) {
					
				}
				
				index.getAndIncrement();
				return true;
			}).findFirst();
			System.out.println(index);
			/*
			str.chars().forEach(item->{
				System.out.println((char)item);
				System.out.println(item);
				index.getAndIncrement();
			});
			*/
			System.out.println("end <<<");
			System.out.println(this.map);
			System.out.println(this.list);
		}
		public CalorieSon2(String str, Map originMap) {
			System.out.println(str);
			AtomicInteger index = new AtomicInteger();
			str.chars().filter(charInt -> {
				if(charInt ==  34 && str.charAt(index.get()) !='\\') {
					doubleQuotesIn = !doubleQuotesIn;
				}
				if(charInt == 123) {
					this.map = (Map) new CalorieSon2(str.substring(Integer.parseInt(index.toString()), str.lastIndexOf('}')), originMap).map;
					return true;
				}else if(charInt == 91) {
					System.out.println(str);
					System.out.println(str.substring(Integer.parseInt(index.toString()), str.lastIndexOf(']')));
					this.list = new CalorieSon2(str.substring(Integer.parseInt(index.toString()), str.lastIndexOf(']')), list).list;
					return true;
				}else if(charInt == 58 && doubleQuotesIn == false) {
					this.identifierSwitch = !identifierSwitch;
					return false;
				}else if(charInt == 44 && doubleQuotesIn == false){
					originMap.put(this.key, this.value);
					System.out.println(originMap);
					this.key = "";
					this.value = "";
					return false;
				}else {
					if(doubleQuotesIn == true) {
						index.getAndIncrement();
						char c;
						if(charInt == 34) {
							c = str.charAt(index.get());
						}else {
							c = str.charAt(index.get()-1);
						}
						setKeyAndValue(c);
					}

					return false;
				}
			}).findFirst();
		}
		public CalorieSon2(String str, List originList) {
			AtomicInteger index = new AtomicInteger();
			str.chars().filter(charInt -> {
				index.getAndIncrement();
				if(charInt ==  34) {
					doubleQuotesIn = !doubleQuotesIn;
				}
				
				if(charInt == 123) {
					this.map = (Map) new CalorieSon2(str.substring(Integer.parseInt(index.toString()), str.lastIndexOf('}')), map).map;
					return true;
				}else if(charInt == 91) {
					this.list = new CalorieSon2(str.substring(Integer.parseInt(index.toString()), str.lastIndexOf(']')), originList).list;
					return true;
				}else if(charInt == 44 && doubleQuotesIn == false){
					originList.add(this.key);
					this.key = "";
					return false;
				}else {
					if(doubleQuotesIn == true && charInt != 34) {
						setKeyAndValue((char)charInt);
					}
					return false;
				}
			}).findFirst();
		}
		private void setKeyAndValue(char s) {
			if(this.identifierSwitch == false) {
				this.key += (Character.toString(s));
			}else {
				this.value += (Character.toString(s));
			}
		}
		private CalorieSon2 createJson() {
			
			return null;
		}
	}
	public static <T> void main(String args[]) {
		String test = " %s ";
		for(int i=0; i < 10;i++) {
			test += " %s ";
		}
		System.out.println(test);
		System.out.println(test.formatted("1","2","3","4","5","6","7","8","9","10","11"));

		Map test2 = Map.ofEntries(
					Map.entry("a", "1"),
					Map.entry("b", "2"),
					Map.entry("c", "3"),
					Map.entry("d", Map.ofEntries( Map.entry("e", "4"), Map.entry("f", Arrays.asList(1,2,3,4,Map.entry("g","5")) ), Map.entry("h","6"), Map.entry("i",Map.entry("j", "7"))) ),
					Map.entry("t", "qqq")
				);
		System.out.println(test2);
		
		ZeroCalorieJson json = new ZeroCalorieJson(test2);
		
		System.out.println(json);
		
		System.out.println(json.get("a"));
		
		ZeroCalorieJson json2 = new ZeroCalorieJson().setListIndetKeyAndValue(Arrays.asList(1,2,3,4,5,6,7,8));
		System.out.println(json2);
		
		Map testMap = new HashMap();
		List testList = new ArrayList();
		for(int i = 1 ; i < 11 ; i++) {
			testList.add(i);
		}
		System.out.println("start!!<<");
		String testStr2 = new ZeroCalorieJson().setListIndetKeyAndValue(testList).getJson();
		
		System.out.println(testStr2);
		System.out.println(json.getJson());
		System.out.println(json2.getJson());
		String []testStr4 = json.getJson().replaceAll("\n", "").split(":");
		System.out.println("==============");

		new ZeroCalorieJson("{\"a\":\"\\\"1\\\"\",\"b\":\"2\",\"c\":\"3\",\"d\":{\"h\":\"6\",\"f\":[\"1\",\"2\",\"3\",\"4\",{\"g\":\"5\"}],\"e\":\"4\",\"i\":{\"j\":\"7\"}},\"t\":\"qqq\"}");
		new ZeroCalorieJson(json.getJson());
		//new ZeroCalorieJson(json.getJson());
	}
}

