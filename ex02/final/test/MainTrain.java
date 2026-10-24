package test;

public class MainTrain {

	public static void main(String[] args) {
		
		if(!new MyStringBuilder('a','b','c').toString().equals("abc")) {
			System.out.println("wrong implementation of toString (-25)");
			System.out.println("please fix. this affects the other tests (-75)");
		}else {
		
			if(!new MyStringBuilder().append("abc").toString().equals("abc"))
				System.out.println("wrong implementation of append (-25)");
	
			if(!new MyStringBuilder('a','b','c').reverse().toString().equals("cba"))
				System.out.println("wrong implementation of reverse (-25)");
			
			if(!new MyStringBuilder('a','b','c').apply(c->(char)(c-('a'-'A'))).toString().equals("ABC"))
				System.out.println("wrong implementation of apply (-25)");
		}
		// all together
		MyStringBuilder sb=new MyStringBuilder();
		String s = sb.append("abc").append("def").reverse()
				.apply(c->(char)(c-('a'-'A'))).toString();
		
		System.out.println(s); // FEDCBA
		
		System.out.println("done");
	}

}
