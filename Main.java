import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

	//declare and initialize the frame
    static JFrame f = new JFrame("Herd Game");
    public static int DELAY = 17; // Frame time in milliseconds

    public static void main(String[] args) {

		//make it so program exits on close button click
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		//the size of the game will be 480x640, the size of the JFrame needs to be slightly larger
        f.setSize(810,810);

        // Create game object
        HerdGame game = new HerdGame();

        // Add game object to frame
        f.add(game);

		//show the window
        f.setVisible(true);

        //add a frame timer object
        Timer timer = new Timer(DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //game logic 
                game.think(DELAY);

                //repaint the screen
                game.repaint();

            }
        });

        // Start frame timer once it is initialized
        timer.start();

	}
}