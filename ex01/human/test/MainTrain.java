package test;

public class MainTrain {
	
	public static boolean test(PlayList pl, String exp) {
		StringBuilder sb=new StringBuilder();
		pl.forEach(name->sb.append(name));
		return exp.equals(sb.toString());		
	}

	public static void main(String[] args) {
		PlayList p2=new PlayList("P2", new Item[]{new Song("s21"),new Song("s23")});
		PlayList p1=new PlayList("P1", new Item[]{new Song("s11"),p2,new Song("s13")});
		PlayList p0=new PlayList("P0", new Item[]{new Song("s01"),new Song("s02"),p1,new Song("s03")});
		
		if(!test(p2,"s21s23"))
			System.out.println("wrong result for a flat play list (-33)");
		if(!test(p1,"s11s21s23s13"))
			System.out.println("wrong result for a 1 level deep play list (-33)");
		if(!test(p0,"s01s02s11s21s23s13s03"))
			System.out.println("wrong result for a 2 level deep play list (-34)");		
		
		System.out.println("done");
	}	

}
