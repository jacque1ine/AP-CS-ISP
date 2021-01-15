import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;


/**
 * Class Game - the main class of the "Zork" game.
 *
 * Author: Michael Kolling Version: 1.1 Date: March 2000
 * 
 * This class is the main class of the "Zork" application. Zork is a very
 * simple, text based adventure game. Users can walk around some scenery. That's
 * all. It should really be extended to make it more interesting!
 * 
 * To play this game, create an instance of this class and call the "play"
 * routine.
 * 
 * This main class creates and initialises all the others: it creates all rooms,
 * creates the parser and starts the game. It also evaluates the commands that
 * the parser returns.
 */
class Game {
	private Parser parser;
	private Room currentRoom;
	private Inventory inventory;
	// This is a MASTER object that contains all of the rooms and is easily
	// accessible.
	// The key will be the name of the room -> no spaces (Use all caps and
	// underscore -> Great Room would have a key of GREAT_ROOM
	// In a hashmap keys are case sensitive.
	// masterRoomMap.get("GREAT_ROOM") will return the Room Object that is the Great
	// Room (assuming you have one).
	private HashMap<String, Room> masterRoomMap;
	private HashMap<String, Item> masterItemMap;
	private boolean finished;
	private String message;


	private void initItems(String fileName) throws Exception{
		Scanner itemScanner;
		masterItemMap = new HashMap<String, Item>();

		try {
			
			itemScanner = new Scanner(new File(fileName));
			while (itemScanner.hasNext()) {
				Item item = new Item();
				String itemName = getNextLine(itemScanner).split(":")[1].trim();
				item.setName(itemName);
				String itemDesc = getNextLine(itemScanner).split(":")[1].trim();
				item.setDescription(itemDesc);	
				Boolean openable = Boolean.valueOf(getNextLine(itemScanner).split(":")[1].trim());
				item.setOpenable(openable);
				
				masterItemMap.put(itemName.toUpperCase().replaceAll(" ", "_"), item);
				
				String temp = getNextLine(itemScanner);
				String itemType = temp.split(":")[0].trim();
				String name = temp.split(":")[1].trim();
				if (itemType.equals("Room"))
					masterRoomMap.get(name).getInventory().addItem(item);
				else
					masterItemMap.get(name).addItem(item);
			}
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void initRooms(String fileName) throws Exception {
		masterRoomMap = new HashMap<String, Room>();
		Scanner roomScanner;
		try {
			HashMap<String, HashMap<String, String>> exits = new HashMap<String, HashMap<String, String>>();
			roomScanner = new Scanner(new File(fileName));
			while (roomScanner.hasNext()) {
				Room room = new Room();
				
				// Read the Name
				String roomName = getNextLine(roomScanner);
				room.setRoomName(roomName.split(":")[1].trim());
				
				// Read the Description
				String roomDescription = getNextLine(roomScanner);
				room.setDescription(roomDescription.split(":")[1].replaceAll("<br>", "\n").trim());
				
				// Read the Exits
                String roomExits = getNextLine(roomScanner);
				
				//Read if room is locked 
				boolean locked = Boolean.parseBoolean(getNextLine(roomScanner).split(": ")[1].replaceAll("<br>", "\n").trim());
				room.setLocked(locked);

				//read if room kills you
				boolean kills = Boolean.parseBoolean(getNextLine(roomScanner).split(": ")[1].replaceAll("<br>", "\n").trim());
				room.setKill(kills);

				// An array of strings in the format E-RoomName
				String[] rooms = roomExits.split(":")[1].split(",");
				HashMap<String, String> temp = new HashMap<String, String>();
				for (String s : rooms) {
					temp.put(s.split("-")[0].trim(), s.split("-")[1]);
				}

				exits.put(roomName.substring(10).trim().toUpperCase().replaceAll(" ", "_"), temp);

				// This puts the room we created (Without the exits in the masterMap)
				masterRoomMap.put(roomName.toUpperCase().substring(10).trim().replaceAll(" ", "_"), room);

				// Now we better set the exits.
			}

			for (String key : masterRoomMap.keySet()) {
				Room roomTemp = masterRoomMap.get(key);
				HashMap<String, String> tempExits = exits.get(key);
				for (String s : tempExits.keySet()) {
					// s = direction
					// value is the room.

					String roomName2 = tempExits.get(s.trim());
					Room exitRoom = masterRoomMap.get(roomName2.toUpperCase().replaceAll(" ", "_"));
                    
                    // roomTemp.setExit(s.trim(), exitRoom); //String 
                    roomTemp.setExit(s.trim().charAt(0), exitRoom); //char

				}

			}

			roomScanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

//allows spaces in the room.dat file 
  private String getNextLine(Scanner roomScanner){
    String nextLine = roomScanner.nextLine(); 
    while(nextLine != null && nextLine.trim().equals("")){
      nextLine = roomScanner.nextLine();
    }
    return nextLine;
  }

	/**
	 * Create the game and initialise its internal map.
	 */
	public Game() {
		try {
			initRooms("data/Rooms.dat");	// creates the map from the rooms.dat file
			// initRooms is responsible for building/ initializing the masterRoomMap (private instance variable)
			currentRoom = masterRoomMap.get("TOWN_SQUARE");	// the key for the masterRoomMap is the name of the room all in Upper Case (spaces replaced with _)
			inventory = new Inventory();
			masterRoomMap.get("GARAGE").setKey("BAG");
			
      	initItems("data/items.dat");
      
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		parser = new Parser();
	}

	

	/**
	 * Main play routine. Loops until end of play.
	 */
	public void play() {
		printWelcome();
		// Enter the main command loop.  Here we repeatedly read commands and
		// execute them until the game is over.
		message = ("Thank you for playing.  Good bye.");
		finished = false;
		
		while (!finished) {
			Command command = parser.getCommand();
			// if(!processCommand(command) || hasWon()){
			// 	finished = true;
			// }
			// if (currentRoom.getKill()){
			// 	message = ("you have wandered to the wrong place...you have died");
			// 	finished = true;
			// }
			// if(!finished){
				finished = processCommand(command);
			// }
			
		}
		System.out.println(message);
	}

	/**
	 * Print out the opening message for the player.
	 */
	private void printWelcome() {
		System.out.println();
		System.out.println("Welcome to Zork!");
		System.out.println("Zork is a new, incredibly boring adventure game.");
		System.out.println("Type 'help' if you need help.");
		System.out.println();
		System.out.println(currentRoom.longDescription());
	}

	/**
	 * Given a command, process (that is: execute) the command. If this command ends
	 * the game, true is returned, otherwise false is returned.
	 */
	private boolean processCommand(Command command) {
		if (command.isUnknown()) {
			System.out.println("I don't know what you mean...");
			return false;
		}
		String commandWord = command.getCommandWord();
		if (commandWord.equalsIgnoreCase("help"))
			printHelp();
		else if (commandWord.equalsIgnoreCase("go"))
			goRoom(command);
		else if (commandWord.equalsIgnoreCase("quit")) {
			if (command.hasSecondWord())
				System.out.println("Quit what?");
			else
				return true; // signal that we want to quit
		// } else if (currentRoom.getRoomName().equalsIgnoreCase("GARAGE")){
		// 	if(command.equalsIgnoreCase("w") || command.equalsIgnoreCase("go west")){
		// 		currentRoom.
		// 	}
		}else if (commandWord.equalsIgnoreCase("eat")) {
			eat(command.getSecondWord());
		} else if (commandWord.equalsIgnoreCase("jump")) {
			return jump();
		} else if (commandWord.equalsIgnoreCase("sit")) {
			sit();
		} else if ("udeswn".indexOf(commandWord) > -1) {
			goRoom(command);
		} else if (commandWord.equalsIgnoreCase("take")) {
			if (!command.hasSecondWord())
				System.out.println("Take what?");
			else
				takeItem(command.getSecondWord());
		} else if (commandWord.equalsIgnoreCase("drop")) {
			if (!command.hasSecondWord())
				System.out.println("Drop what?");
			else
				dropItem(command.getSecondWord());
		} else if (commandWord.equalsIgnoreCase("inventory")) {
			System.out.println("You are carrying the following:" + inventory);
		} else if (commandWord.equalsIgnoreCase("open")) {
			if (!command.hasSecondWord())
				System.out.println("Open what?");
			else
				openItem(command.getSecondWord());
		} else if (commandWord.equalsIgnoreCase("throw")){
			System.out.println("There are better ways to put things down"); 
		} else if (commandWord.equalsIgnoreCase("kill")){
			System.out.println("Great Scott!! Why are you so violent");
		} else if(commandWord.equalsIgnoreCase("sleep")){
			System.out.println("sleeping is for losers... don't you want to go back home?");
			
		}else if(commandWord.equalsIgnoreCase("pull")){
			//add of it has second word integrate with pull method
			if (!command.hasSecondWord()){
				System.out.println("pull what?");
			}
			else
				System.out.println("theres nothing to pull");
		}
		
		return false;
	}

	private void openItem(String itemName) {
		Item item = inventory.contains(itemName);
		
		if(item != null) {
			System.out.println(item.displayContents());
		}else {
			System.out.println("What is it that you think you have but do not.");
        }
        
	}

	private void takeItem(String itemName) {
		Inventory temp = currentRoom.getInventory();
		
		Item item = temp.removeItem(itemName);
    
    //if null it is not in the room inventory
		if (item != null) {
			if (inventory.addItem(item)) {
				System.out.println("You have taken the " + itemName);
				
			// 	if (currentRoom.getRoomName().equalsIgnoreCase("Hallway") &&  itemName.equalsIgnoreCase("ball")) {
			// 		currentRoom = masterRoomMap.get("ATTIC");
			// 		System.out.println("You seem to be lying on the floor all confused. It seems you have been here for a while.\n");
			// 		System.out.println(currentRoom.longDescription());
			// 	}
			// }else {
			// 	System.out.println("You were unable to take the " + itemName);
			// }
		}else {
			System.out.println("you are unable to take " + itemName + " here.");
    }
  
  }else{
    System.out.println("There is no " + itemName + " here.");
  }
    

	}
	
	private void dropItem(String itemName) {
		Item item = inventory.removeItem(itemName);
		
		if (item != null) {
			if (currentRoom.getInventory().addItem(item)) {
				System.out.println("You have dropped the " + itemName);
			}else {
				System.out.println("You were unable to drop the " + itemName);
			}
		}else {
			System.out.println("You are not carrying a " + itemName + ".");
		}
	}

	private void eat(String secondWord) {
		if (secondWord.equalsIgnoreCase("steak"))
			System.out.println("YUMMY");
		else if (secondWord.equalsIgnoreCase("bread"))
			System.out.println("I don't eat carbs...");
		else 
			System.out.println("You are the " + secondWord);
	}

	private void sit() {
		System.out.println("You are now sitting. You lazy excuse for a person.");	
	}

	private boolean jump() {
		System.out.println("You jumped. Ouch you fell. You fell hard. Really hard." 
		+"You are getting sleepy. Very sleepy! Yuo are dead!");
		return true;
	}

	// private boolean hasWon(){
	// 	if (item.getName().equalsIgnoreCase())
		
	// 	if(inventory.contains(generator)){
	// 		return true;
	// 	}
	// 	return false;
	// }

// implementations of user commands:
	/**
	 * Print out some help information. Here we print some stupid, cryptic message
	 * and a list of the command words.
	 */
	private void printHelp() {
		System.out.println(" Hello? Hello? Anybody home? Huh? Think, McFly! Think!");
		System.out.println();
		System.out.println("Your command words are:");
		parser.showCommands();
	}

	/**
	 * Try to go to one direction. If there is an exit, enter the new room,
	 * otherwise print an error message.
	 */
	private void goRoom(Command command) {
		if (!command.hasSecondWord() && ("udeswn".indexOf(command.getCommandWord()) < 0)) {
			// if there is no second word, we don't know where to go...
			System.out.println("Go where?");
			return;
		}
		
		String direction = command.getSecondWord();
		if ("udeswn".indexOf(command.getCommandWord()) > -1) {
			direction = command.getCommandWord();
			if (direction.equalsIgnoreCase("u"))
				direction = "up";
			else if (direction.equalsIgnoreCase("d"))
				direction = "down";
			else if (direction.equalsIgnoreCase("e"))
				direction = "east";
			else if (direction.equalsIgnoreCase("w"))
				direction = "west";
			else if (direction.equalsIgnoreCase("n"))
				direction = "north";
			else if (direction.equalsIgnoreCase("s"))
				direction = "south";
		}
		
// Try to leave current room.
		Room nextRoom = currentRoom.nextRoom(direction);
		if (nextRoom == null)
			System.out.println("There is something obstructing your path. You cannot go this way!");
		else if (nextRoom.isLocked() && !hasKey(nextRoom)) {
			System.out.println("The door is locked. You need a key to open it.");
	
		
		// } else if(nextRoom.getKill() == true){
			
		// 	System.out.println(nextRoom.getDescription());
			
		}else {
			currentRoom = nextRoom;
		
			System.out.println(currentRoom.longDescription());
		}
	}
	/* Has Key: gets the key from a room, and checks if the player's inventory has the corresponding key */
	private boolean hasKey(Room nextRoom) {
		String key = nextRoom.getKey();
		return key != null && inventory.contains(key) != null && inventory.contains(key).getName().equalsIgnoreCase(key);
	}
}
	
