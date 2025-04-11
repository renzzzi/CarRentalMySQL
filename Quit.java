import java.util.Scanner;

public class Quit implements Operation {

	@Override
	public void operation(Database database, Scanner s, User user) {

		System.out.println("Thanks for visiting us!");
		s.close();
		
	}

}
