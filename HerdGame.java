import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import movement.behaviours.*;
import movement.util.*;
import java.util.ArrayList; // For arbitrary size lists 


public class HerdGame extends JPanel implements MouseListener, MouseMotionListener{
    
    static final int WINDOW_WIDTH = 800, WINDOW_HEIGHT = 800;

    // User's mouse location
    private int mouseX, mouseY;
    private boolean mouseActive = true;

    // Images of dog/sheep
    private BufferedImage sheepImage, dogImage;

    // Object representing the pen that the sheep are to be herded into
    private Pen pen;

    // Kinematic/Character object for the player character
    private Kinematic target;
    private Character playerChar;

    // Behaviour responsible for player's movement
    private Behaviour playerArrive;         

    // List of all sheep characters (flockChars), and a subset of that list representing those that are wandering free (freeChars)
    public ArrayList<Character> flockChars; 
    public ArrayList<Character> freeChars;

    // List of kinematics that the flock will run from
    private ArrayList<Kinematic> runFromKinematics = new ArrayList<Kinematic>();

    // Collision detectors for walls of screen, and walls of pen, respectively.
    private WallCollisionDetector CDetector, PenCDetector;
    
    // How many sheep to create.
    private int herdSize = 8;

    // Menu screen handling
    // 0 = main menu, 1 = new level screen, 2 = game over
    private boolean inMenuScreen = true;
    private int menuScreenIndex = 0;            
    private Font menuFont = new Font("CalibriBold", Font.PLAIN, 32);
    private Font barkFont = new Font("CalibriBold", Font.BOLD, 12);

    // Difficulty level (increments between levels)
    private int difficulty = 0;

    // The player's score, where each sheep in the pen adds 1 to their score, and the remaining round time is added to the score at the end of each round.
    private int score = 0;

    // add a round timer 
    private Timer timer; 
    private float roundTime = 0;

    // Timer for handling dog bark
    private Timer barkTimer;

    // Threshold for seperation from players
    private float seperationThreshold;

    // Keep track of separation behaviours to implement bark 
    private ArrayList<Separation> separations = new ArrayList<Separation>();

    public HerdGame(){

        // Get images
        try {                
            this.sheepImage = ImageIO.read(getClass().getResourceAsStream("resources/sheep.png"));
         } catch (IOException e) {
            e.printStackTrace();
         }

         try {                
            this.dogImage = ImageIO.read(getClass().getResourceAsStream("resources/dog.png"));
         } catch (IOException e) {
            e.printStackTrace();
         }

        // Create round timer, which calls updateRoundTimer() every ~100ms
        this.timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRoundTimer();
            }
        });

        // Create bark timer, which makes sheep run away faster for a small period of time
        this.barkTimer = new Timer(800, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                endBark();
            }
        });

        //Initialize user's mouse coordinates to (0, 0)
        mouseX = 0;
        mouseY = 0;

        //listen for mouse events (clicks and movements) on this object
        addMouseMotionListener(this);
        addMouseListener(this);

        // Create pen
        this.pen = new Pen(new Vector2(0,0), new Vector2(200, 200), Color.GREEN.darker());

        // Create collision detector for pen
        this.PenCDetector = new WallCollisionDetector(new Vector2(0, 200), new Vector2(0, 200));

        // Create collision detector for edges of screen
        this.CDetector = new WallCollisionDetector(new Vector2(0, HerdGame.WINDOW_WIDTH), new Vector2(0, HerdGame.WINDOW_HEIGHT));

        // Allocate arraylists for flock
        this.flockChars = new ArrayList<Character>();
        this.freeChars = new ArrayList<Character>();

        // Kinematic that will be updated to the mouse position every frame.
        // Used for the player's Arrive target 
        this.target = new Kinematic(new Vector2(mouseX, mouseY), 5);

        // The player character, starts at center of screen
        this.playerChar = new Character(new Vector2(WINDOW_WIDTH / 2f, WINDOW_HEIGHT / 2f), 55, this.dogImage);

        // The player's arrive behaviour, which targets the mouse cursor while it is on screen.
        this.playerArrive = new Arrive(this.playerChar, this.target, 100);

        // Allocate the arrive behaviour to the player character.
        this.playerChar.setBehaviour(this.playerArrive);

        // Add the player character, and bark to the list of kinematics the flock will run from
        this.runFromKinematics.add(this.playerChar);


        Vector2 newPos;
        
        for(int i = 0; i < this.herdSize; i++){
           
            // Create new Kinematic for single flock member
            newPos = new Vector2((WINDOW_WIDTH / 2f) + (i * 200/this.herdSize), (WINDOW_HEIGHT / 2f) + 100); 

            // Create Character object, allows for image and behaviour to be assigned
            Character newChar = new Character(newPos, 40, this.sheepImage);

            // Add to list of all members of the flock
            this.flockChars.add(newChar);

            // Create blendedSteering behaviour for this flock member, representing its active behaviour outside of a pen
            BlendedSteering activeBehaviour = new BlendedSteering(100, 100);
            

            // Cohesion 
            Behaviour cohesion = new Cohesion(newChar, this.freeChars, 40, 350);
            activeBehaviour.addBehaviour(cohesion, 0.4f);
            
            // Velocity Match
            Behaviour velMatch = new VelocityMatch(newChar, this.freeChars, 40);
            activeBehaviour.addBehaviour(velMatch, 0.4f);
            
            // Separation
            Behaviour separation = new Separation(newChar, this.freeChars, 40, 140.0f, 10000.0f);
            activeBehaviour.addBehaviour(separation, 0.4f);
            
            // Run from player
            this.seperationThreshold = 250.0f;
            Behaviour separation2 = new Separation(newChar, this.runFromKinematics, 40, this.seperationThreshold, 10000.0f);
            activeBehaviour.addBehaviour(separation2, 1.5f);
            // Add to separation behaviour list to implement bark behaviour
            this.separations.add((Separation)separation2);
            
            // Avoid walls
            Behaviour WallAvoidance = new WallAvoidance(newChar, this.CDetector, 50, 72);
            activeBehaviour.addBehaviour(WallAvoidance, 1.2f);
            

            // Setup new behaviour for when character is trapped in pen
            BlendedSteering trappedBehaviour = new BlendedSteering(100, 100);

            // Avoid walls of pen
            Behaviour penBehaviour = new WallAvoidance(newChar, this.PenCDetector, 15, 32);
            trappedBehaviour.addBehaviour(penBehaviour, 1.0f);

            // Limit character's speed while in pen
            Behaviour dampenSpeed = new DampenSpeed(newChar, 10, 5);
            trappedBehaviour.addBehaviour(dampenSpeed, 1.0f);

            // Setup parent behaviour, consisting of both trapped, and active behaviours.
            BlendedSteering bothBehaviours = new BlendedSteering(100, 100);
            
            // Start with the active behaviour having a weight of 1.0, allowing the sheep to run free,
            // And having the trappedBehaviour being deactivated, by having a weight of 0.
            bothBehaviours.addBehaviour(activeBehaviour, 1.0f);
            bothBehaviours.addBehaviour(trappedBehaviour, 0.0f);

            // Set this flock member to have parent behaviour
            newChar.setBehaviour(bothBehaviours);
        }
    }

    // Distribute a given flock around a certain point
    public void distributeFlock(ArrayList<Character> characters, Vector2 center){
        for(int i = 0; i < characters.size(); i++){
            Math.random();
            Vector2 newpos = center.add(new Vector2((float)(Math.random() - Math.random())*128, (float)(i*16 - (characters.size() / 2))));
            characters.get(i).position = newpos;
        }
    }
    
    // Handles logic to reset flock, and setting timer, for starting a new round
    public void startNewRound(){
        // Reset all sheep to have their "free" behaviour, rather than their "trapped" behaviour
        for(int i = 0; i < this.herdSize; i++){
            Character c = this.flockChars.get(i);
            BlendedSteering b = (BlendedSteering)c.getBehaviour();
            b.setBehaviourWeight(0, 1);
            b.setBehaviourWeight(1, 0);
            this.freeChars.add(c);
        }

        // Distribute sheep randomly around center of screen
        distributeFlock(this.flockChars, new Vector2(WINDOW_WIDTH/2, WINDOW_HEIGHT/2));
       
        // Set round time (decreases by 0.5s each round, to a minimum of 8 seconds)
        this.roundTime = Math.max(20f - (this.difficulty / 2f), 8f);

        this.timer.start();

    }

    public void endRound(){
        this.timer.stop();
        this.score += Math.floor(this.roundTime * 0.8);
        this.difficulty += 1;
        endBark();       
        this.inMenuScreen = true;
    }

    public void gameOver(){
        this.menuScreenIndex = 2;
        this.inMenuScreen = true;
    }

    public void restartGame(){
        this.score = 0;
        this.difficulty = 0;
        startNewRound();
    }

    public void updateRoundTimer(){
        if(this.roundTime < 0.1){
            gameOver();
            this.menuScreenIndex = 2;
        }
        this.roundTime -= 0.1;
    }

    public void drawCenteredString(Graphics g, Vector2 pos, String text, Font font){
        FontMetrics metrics = g.getFontMetrics(font);
        g.setFont(font);
        int width = metrics.stringWidth(text);
        int height = metrics.getHeight();
        g.drawString(text, Math.round(pos.x - (width / 2)), Math.round(pos.y + (height / 2)));
    }

    public void endBark(){
        this.barkTimer.stop();
        for(int i = 0; i < this.separations.size(); i++){
                this.separations.get(i).threshold = this.seperationThreshold;
        }
    }

    // Called each game frame, handles game logic.
    public void think(int delay){
        
        if(!inMenuScreen){
            
            // If there are no characters left, end the round.
            if(this.freeChars.size() == 0){
                endRound();
            }

            // Handle player steering as long as mouse is within window
            if(mouseActive){
                this.target.position = new Vector2(mouseX, mouseY); 
            }
            // If mouse leaves window, then set dog to coast to stop
            else{
                this.target.position = this.playerChar.velocity.scalar(0.85f).add(this.playerChar.position);
            }
            this.playerChar.updateCharacter(delay/100f);

            // Check if any free sheep have entered the pen. If so, set their behaviour to the "trapped" behaviour, and remove them from the "free" list
            int j = this.freeChars.size();
            for(int i = 0; i < j; i++){
                Character curChar = this.freeChars.get(i);
                if(this.pen.isInPen(curChar.position)){
                    Behaviour blend = curChar.getBehaviour();
                    ((BlendedSteering)blend).setBehaviourWeight(0, 0.0f);
                    ((BlendedSteering)blend).setBehaviourWeight(1, 1.0f);
                    this.freeChars.remove(i);
                    i -= 1;
                    j -= 1;
                    this.score += 1;
                }
            }

            // Set target for player character to mouse position
            this.target.position = new Vector2(mouseX, mouseY);

            // Handle flock steering
            for(int i = 0; i < this.herdSize; i++){
                this.flockChars.get(i).updateCharacter(delay/100f);
            }
        }
    }
    
    // Redraws the graphics on the game window
    public void paintComponent(Graphics g){
        // If we aren't in a menu screen (in-game)
        if(!inMenuScreen){
            // Color Background Grey
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

            g.setColor(Color.WHITE);
            drawCenteredString(g, new Vector2(WINDOW_WIDTH / 2, 32), String.format("%.1f%n", this.roundTime), this.menuFont);

            this.pen.drawPen(g);

            this.playerChar.drawCharacter(g);

            for(int i = 0; i < this.herdSize; i++){
                this.flockChars.get(i).drawCharacter(g);
            }
            g.setColor(Color.WHITE);
            drawRoundScore(g);

            if(barkTimer.isRunning()){
                drawCenteredString(g, this.playerChar.position.add(new Vector2(-22 * this.playerChar.facing, -35)), "bark!", this.barkFont);
            }

        }
        // If we are in a menu screen
        else{
            // Color Background Black
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
            g.setColor(Color.WHITE);
            switch(menuScreenIndex){
                case 0:
                    g.setColor(Color.DARK_GRAY);
                    drawCenteredString(g, new Vector2(WINDOW_WIDTH / 2, 48), "Instructions:", this.menuFont);
                    drawCenteredString(g, new Vector2(WINDOW_WIDTH / 2, 96), "Use your mouse to control the dog,", this.menuFont);
                    drawCenteredString(g, new Vector2(WINDOW_WIDTH / 2, 130), "Herd all the sheep into the green pen", this.menuFont);
                    drawCenteredString(g, new Vector2(WINDOW_WIDTH / 2, 164), "before the time runs out!", this.menuFont);
                    g.setColor(Color.WHITE);
                    drawCenteredString(g, new Vector2(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2), "Left click playing area to start game.", this.menuFont);
                    break;
                case 1:
                    drawCenteredString(g, new Vector2(WINDOW_WIDTH / 2, 32), "Level Complete!", this.menuFont);
                    drawCenteredString(g, new Vector2(WINDOW_WIDTH / 2, 66), "Left click to continue.", this.menuFont);
                    drawRoundScore(g);
                    break;
                case 2:
                    drawCenteredString(g, new Vector2(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2), "Game Over", this.menuFont);
                    g.setColor(Color.DARK_GRAY);
                    drawCenteredString(g, new Vector2(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 + 32), "Left click to restart", this.menuFont);
                    drawRoundScore(g);
                    break;
            }     
        }
        // Add warning to bottom of screen if mouse leaves the playing area
        if(!mouseActive){
            g.setColor(Color.RED);
            drawCenteredString(g, new Vector2(WINDOW_WIDTH / 2, WINDOW_HEIGHT-64), "Return mouse to playing area!", this.menuFont);
        }

    }

    public void drawRoundScore(Graphics g){
        g.setColor(Color.WHITE);
        g.drawString("Score: " + this.score, 8, 34);
        g.drawString("Round: " + (this.difficulty + 1), 7, 72);
    }

    // Capture mouse drag events
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override 
    public void mousePressed(MouseEvent e){

        if(this.inMenuScreen){

            this.inMenuScreen = false;
            switch(this.menuScreenIndex){
                case(0):
                    this.menuScreenIndex = 1;
                    startNewRound();
                    break;
                case(1):
                    startNewRound();
                    break;
                case(2):
                    this.menuScreenIndex = 1;
                    restartGame();
                    break;
            }

        }
        else{
            for(int i = 0; i < this.separations.size(); i++){
                this.separations.get(i).threshold = this.seperationThreshold + 130f;
            }

            this.barkTimer.restart();
        }
    }

    // Capture mouse move events
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseExited(MouseEvent e){
        this.mouseActive = false;
    }

    @Override
    public void mouseEntered(MouseEvent e){
        this.mouseActive = true;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

}