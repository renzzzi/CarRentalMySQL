import javax.swing.JFrame;

public class Quit implements Operation {

    @Override
    public void operation(Database database, JFrame f, User user) {
        System.out.println("Thanks for visiting us!");
        System.exit(0);
    }

}
