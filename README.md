## LOGIC / DESIGN 

This small game is designed to simulate sheep herding, through implementing "Boids"-style AI agents.
After leaving the main menu screen by left-clicking within the window, 
The player character appears as a dog, 
which can be moved to any point in the play area by placing their mouse cursor at a position.
The dog will then attempt to reach the position that is specified. 

The objective of the game is to herd a flock of sheep into a small green pen located in the top-left
corner of the screen, before the time runs out. When a player moves the dog close to the sheep, 
the sheep will begin to run away from the dog, while also staying within the bounds of the play-area.

The sheep tend to flock together, attempting to maintain a certain distance from eachother, while also
mirroring eachother's velocities. This algorithm is similar to what is used in "boids".
The sheep may also split into smaller flocks if they are sufficiently seperated. If a sheep enters the
pen, then they are considered "captured" and will no longer behave with flock logic. Instead, they 
will float around within the pen aimlessly.

The player may also click the screen at any point during gameplay to cause the dog to bark. 
This will temporarily increase the radius for which the sheep run away from the dog.

The player's score is incremented for each sheep that is succesfully herded into the pen, and after
capturing all sheep, a portion of the remaining round time is added to the player's score.
The next level will then begin after the player left clicks, which closes the "Next level" display.

The next level will then start, which also decreases the amount of time the player has to capture all 
of the shepe. The start of each new level will reset the timer, and randomly distribute the freed sheep
near the center of the screen.

If the timer runs out, then the game is ended, and the player's score + final round they achieved are 
displayed. Further hitting left click will restart the game from round 0.




## HOW TO COMPILE / RUN


To run game, simply execute the included .jar file.

To compile game from source into .jar file, ensure you have java jdk installed, 
then open up your command-line interface.
navigate to the "source" folder included in this zip file, and run the following 2 commands:


javac *.javac

jar cvfm game.jar MANIFEST.MF *.class movement/behaviours/*.class movement/util/*.class resources/*.png

the .jar file may then be executed from the command line by: java -jar game.jar

