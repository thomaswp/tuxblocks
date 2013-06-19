package tuxkids.tuxblocks.core;

import tuxkids.tuxblocks.core.utils.HashCode;
import tuxkids.tuxblocks.core.utils.HashCode.Hashable;

public class HashTest implements Hashable {

	int foo;
	String bar;
	
	@Override
	public void addFields(HashCode hashCode) {
		hashCode.addField(foo);
		hashCode.addField(bar);
	}
	
	public static void test() {
		HashTest h1 = new HashTest();
		h1.foo = 1; h1.bar = new String("!");
		HashTest h2 = new HashTest();
		h2.foo = 1; h2.bar = new String("!");
		
		HashCode hc1 = new HashCode(h1);
		HashCode hc2 = new HashCode(h2);
		
		System.out.println(String.format("%d %d", hc1.hashCode(), hc2.hashCode()));
		System.out.println(hc1.equals(hc2));
		
	}
}
