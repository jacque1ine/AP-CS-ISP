import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;


/*
 *Class Game - the main class of the "Zork" game.
 *
 *Author: Michael Kolling Version: 1.1 Date: March 2000
 *
 *This class is the main class of the "Zork" application. Zork is a very
 *simple, text based adventure game. Users can walk around some scenery. That's
 *all. It should really be extended to make it more interesting!
 *
 *To play this game, create an instance of this class and call the "play"
 *routine.
 *
 *This main class creates and initialises all the others: it creates all rooms,
 *creates the parser and starts the game. It also evaluates the commands that
 *the parser returns.
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

	/**
	 * initItems: creates items from the items.dat file, puts the items into their inventories,
	 * and puts items into hashmap
	 */
	private void initItems(String fileName) throws Exception{
		Scanner itemScanner;
		masterItemMap = new HashMap<String, Item>();
		
		try {
			itemScanner = new Scanner(new File(fileName));
			while (itemScanner.hasNext()) {

				//Create new item
				Item item = new Item();

				//Read and set name
				String itemName = getNextLine(itemScanner).split(":")[1].trim();
				item.setName(itemName);

				//Read and set description
				String itemDesc = getNextLine(itemScanner).split(":")[1].trim();
				item.setDescription(itemDesc.replaceAll("<br>", "\n").trim());

				//Read and set whether or not this object has its own inventory or is "openable"
				Boolean openable = Boolean.valueOf(getNextLine(itemScanner).split(":")[1].trim());
				item.setOpenable(openable);

				//Read and set weight
				int weight = Integer.parseInt(itemScanner.nextLine().split(":")[1].trim()); 
        		item.setWeight(weight); 
				
				//Puts into hashmap
				masterItemMap.put(itemName.toUpperCase().replaceAll(" ", "_"), item);
				
				//Adding items into the inventories of their respective rooms or within other items 
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

	/**
	 * initRooms: creates room objects from the room.dat file, and ensures that each room has exits
	 */
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
				boolean isKiller = Boolean.parseBoolean(getNextLine(roomScanner).split(": ")[1].replaceAll("<br>", "\n").trim());
				room.setKill(isKiller);

				// An array of strings in the format E-RoomName
				String[] rooms = roomExits.split(":")[1].split(",");
				HashMap<String, String> temp = new HashMap<String, String>();
				for (String s : rooms) {
					temp.put(s.split("-")[0].trim(), s.split("-")[1]);
				}

				//replaces spaces with underscore 
				exits.put(roomName.substring(10).trim().toUpperCase().replaceAll(" ", "_"), temp);

				// This puts the room we created (Without the exits in the masterMap)
				masterRoomMap.put(roomName.toUpperCase().substring(10).trim().replaceAll(" ", "_"), room);
			}
			
			//Set the exits.
			for (String key : masterRoomMap.keySet()) {
				Room roomTemp = masterRoomMap.get(key);
				HashMap<String, String> tempExits = exits.get(key);
				for (String s : tempExits.keySet()) {// s = direction
					String roomName2 = tempExits.get(s.trim());
					Room exitRoom = masterRoomMap.get(roomName2.toUpperCase().replaceAll(" ", "_"));
                    roomTemp.setExit(s.trim().charAt(0), exitRoom); 
				}
			}
		roomScanner.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 *getNextLine: ignores empty lines. Allows for blank lines in .dat files. 
	*/
	private String getNextLine(Scanner roomScanner){
    	String nextLine = roomScanner.nextLine(); 
    	while(nextLine != null && nextLine.trim().equals("")){
      		nextLine = roomScanner.nextLine();
    	}
    	return nextLine;
  	}

	/**
	 * Game Constructor: creates the game and initialise its internal map.
	*/
	public Game() {
		try {
			initRooms("data/Rooms.dat");	// creates the map from the rooms.dat file
			// initRooms is responsible for building/ initializing the masterRoomMap (private instance variable)
			currentRoom = masterRoomMap.get("TOWN_SQUARE");	// the key for the masterRoomMap is the name of the room all in Upper Case (spaces replaced with _)
			inventory = new Inventory();

			//Set the kets to each room. setKey() indicates the item needed
			masterRoomMap.get("GARAGE_1").setKey("REMOTE");
			masterRoomMap.get("GARAGE_SECRET_ROOM").setKey("WATCH");
			masterRoomMap.get("SECRET_STUDY").setKey("KEYCARD");
			masterRoomMap.get("ATTIC_CONTINUED").setKey("LANTERN");
			
      		initItems("data/items.dat");
		} catch (Exception e) {
			e.printStackTrace();
		}
		parser = new Parser();
	}

	
	/**
	 * play: main play routine. loops until game is over 
	 */
	public void play() {
		printWelcome();
		String message = "";
		boolean finished = false;
	
		//Repeatedly read commands and execute them until the game is over.
		while (!finished) {
			Command command = parser.getCommand();
			if(processCommand(command) || hasWon() || currentRoom.isKiller()){
				finished = true;
			}	
		}
		if (currentRoom.isKiller()){
			message=("You have died. Try again.");
		}else if(hasWon()){
			message=("CONGRATS!!! You have arrived safely back home.\nHopefully you had fun in the past even though it might have been a little scary.bye");
		}
		System.out.println(message + "\nThank you for playing.  Good bye.");
	}

	/**
	 * printWelcome: print out the opening message for the player.
	 */
	private void printWelcome() {
		System.out.println();
		System.out.println("Welcome to Timequest");
		System.out.println("Timequest is a fun text-based adventure game which draws from elements of Back to the Future.\n" + 
							"You will see that in this game some areas are locked, while some are not.\n" +
							"Once you have a certain item in your inventory the areas will unlock");
		System.out.println("Type 'help' if you need help.");
		System.out.println();
		System.out.println(currentRoom.shortDescription());
	}

	/*
	 *Given a command, process (that is: execute) the command. If this command ends
	 *the game, true is returned, otherwise false is returned.
	 */
	private boolean processCommand(Command command) {

		//will print this message if inputed word is not a command word 
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
		
		}else if (commandWord.equalsIgnoreCase("eat")) {
			if (!command.hasSecondWord())
				System.out.println("eat what?");
			eat(command.getSecondWord());
		} else if (commandWord.equalsIgnoreCase("talk")) {
			return talk();
		} else if (commandWord.equalsIgnoreCase("parts")) {
			partCount();
		}else if (commandWord.equalsIgnoreCase("look")) {
			System.out.println(currentRoom.lookAround());
		} else if (commandWord.equalsIgnoreCase("jump")) {
			return jump();
		} else if (commandWord.equalsIgnoreCase("play") ||commandWord.equalsIgnoreCase("shoot")) {
			if (!command.hasSecondWord())
				System.out.println("play what?");
			else if(command.hasSecondWord() && command.hasThirdWord())
				System.out.println("please choose one game to play");
			else{
				playGame(command.getSecondWord());
			}
		} else if (commandWord.equalsIgnoreCase("sit")) {
			sit();
		}else if (commandWord.equalsIgnoreCase("assemble")) {
			assemble();
		} else if ("udeswn".indexOf(commandWord) > -1) {
			goRoom(command);
		} else if (commandWord.equalsIgnoreCase("take")) {
			if (!command.hasSecondWord())
				System.out.println("Take what?");
			else if(command.hasSecondWord() && command.hasThirdWord())
				takeItem(command.getSecondWord() + " " + command.getThirdWord());
			else
				takeItem(command.getSecondWord());
		} else if (commandWord.equalsIgnoreCase("drop")) {
			if (!command.hasSecondWord())
				System.out.println("Drop what?");
			else if(command.hasSecondWord() && command.hasThirdWord())
				dropItem(command.getSecondWord() + " " + command.getThirdWord());
			else
				dropItem(command.getSecondWord());
		} else if (commandWord.equalsIgnoreCase("inspect") || commandWord.equalsIgnoreCase("read")){
			if (!command.hasSecondWord())
				System.out.println("c'mon you have to tell me what to specfically look at");
			else if(command.hasSecondWord() && command.hasThirdWord())
				inspectItem(command.getSecondWord() + " " + command.getThirdWord());
			else
				inspectItem(command.getSecondWord());
		}else if (commandWord.equalsIgnoreCase("wear")) {
				if (!command.hasSecondWord())
					System.out.println("Wear what?");
				else
					wearItem(command.getSecondWord());
		} else if (commandWord.equalsIgnoreCase("i")) {
			System.out.println("You are carrying the following:\n" + inventory + "\nYou have have " + inventory.getInvWeight() + "/15 slots filled in your inventory.");
		} else if (commandWord.equalsIgnoreCase("open")) {
			if (!command.hasSecondWord())
				System.out.println("Open what?");
			else if(command.hasSecondWord() && command.hasThirdWord())
				openItem(command.getSecondWord() + " " + command.getThirdWord());
			else
				openItem(command.getSecondWord());
		} else if (commandWord.equalsIgnoreCase("throw")){
			System.out.println("There are better ways to put things down"); 
		} else if (commandWord.equalsIgnoreCase("kill")){
			System.out.println("Great Scott!! Why are you so violent");
		} else if(commandWord.equalsIgnoreCase("sleep")){
			System.out.println("sleeping is for losers... don't you want to go back home?");
		} 
		return false;
	}

//Implementations of user commands:
	/**
	 * inspectItem: prints description of item in player or room inventory
	 */
	private void inspectItem(String itemName) {
		String description = "I don't see " + itemName;
		Item playerItem = inventory.contains(itemName);
		Item roomItem = currentRoom.getInventory().contains(itemName);
			
			if(playerItem != null) {
				description = playerItem.getDescription();
			}else if(roomItem != null){
				description = roomItem.getDescription();
			}else{
				for (int i=0; i<currentRoom.getInventory().getNumItems(); i++){
				   Inventory itemInventory = currentRoom.getInventory().getInventory().get(i).getContents(); 
				   if (itemInventory != null && itemInventory.hasItem(itemName)){ 
					  description = itemInventory.contains(itemName).getDescription();
				   }
				}
			}
			System.out.println(description);
	}

	/*
	*Open Item: opens the item requested if there is an item in the inventory.
	*/
	private void openItem(String itemName) {
		Item playerItem = inventory.contains(itemName);
		Item roomItem = currentRoom.getInventory().contains(itemName);
			
		if(playerItem != null) {
			System.out.println(playerItem.displayContents());
		}else if(roomItem != null ){
			System.out.println(roomItem.displayContents());
		}else{
			System.out.println("you can't open this item.");
		}
	}

	/*
	*Take Item: removes item from room inventory and puts it in the player's inventory.
	*/
	private void takeItem(String itemName) {
		Inventory roomInventory = currentRoom.getInventory();
		Item item = roomInventory.removeItem(itemName); 

    	//if item is in room inventory
		if (item != null){
			/**
			 * take item if the object can be picked up, when the inventory weight is 
			 */
			if(item.canPickUp()){
				if((inventory.getInvWeight() + item.getWeight())<=15){
					if(inventory.addItem(item)){
						System.out.println("You have taken the " + itemName);
					}
				}else{
					System.out.println(itemName + " is there but you do not have space. Drop some items and try again"+
					"\nYou have have " + inventory.getInvWeight() + "/15 slots filled in your inventory."); 
					currentRoom.getInventory().justAddItem(item); 
				}
			}else{
				System.out.println(itemName + " is too heavy to pickup");
				currentRoom.getInventory().justAddItem(item);
			}	
		}else{
			 //iterate through the player's inventory to see if target item is within any items that can contain other items
			for (int i=0; i<roomInventory.getNumItems(); i++){

				 //accessing the items arrayList of room inventory and getting one of the indexes, and getting the contents of that item. 
        		Inventory itemInventory = roomInventory.getInventory().get(i).getContents(); 
	
				/**
				 * if the current item has an invetory which contains the target item, 
				 * remove target item from cirrent item and store in removedItem. 
				 */
				if (itemInventory != null && itemInventory.hasItem(itemName)){ 
					Item removedItem = itemInventory.removeItem(itemName);

					/**
					 * add item to player inventory if removedItem weight is small enouhh
					 * to be picked up and the inventory has space.
					*/
					if(removedItem.canPickUp()){
						if((inventory.getInvWeight() + removedItem.getWeight())<=15){
							if(inventory.addItem(removedItem)){
								System.out.println("You have taken the " + itemName);
							}
						}else{
							System.out.println(itemName + " is there but you do not have space. Drop some items and try again"+
							"\nYou have have " + inventory.getInvWeight() + "/15 slots filled in your inventory."); 
							itemInventory.getInventory().get(i).addItem(removedItem); 
						}
					}else{
						System.out.println(itemName + " is too heavy to pickup");
						itemInventory.getInventory().get(i).addItem(removedItem); 
					}
				}
				else{
					System.out.println("You were unable to take the " + itemName + " here"); 
				}
					
			}
		}
		if(itemName.equalsIgnoreCase("helmet") && currentRoom.getRoomName().equalsIgnoreCase("Garage Secret Room")){
			System.out.println("You are now protected"); 
		}else if(itemName.equalsIgnoreCase("watch") && currentRoom.getRoomName().equalsIgnoreCase("Garage 1")){
			System.out.println("You hear a click, and the locked door to your west clicks open");
		}else if(itemName.equalsIgnoreCase("keycard") && currentRoom.getRoomName().equalsIgnoreCase("Science Section")){
			System.out.println("You have unlocked something, but what is it?");
		}else if(itemName.equalsIgnoreCase("remote") && currentRoom.getRoomName().equalsIgnoreCase("Garage")){
			System.out.println("You know that flimsy wall you were suspicious about? Well it seems like by taking the remote that wall slide open and revealed a new part of the garage");
		}
	}

	/*
	*Drop Item: removes item from player's inventory and adds it to the room they are in.
	*/
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

		if(currentRoom.getRoomName().equalsIgnoreCase("Parts Shop")){
			if(itemName.equals("trophy")){
				inventory.addItem(currentRoom.getInventory().removeItem("frame"));
				System.out.println("You have been given the frame");
			}
		}
	}

	/**
	 * assemble: will create the portal object if 4 parts of the time machine are in the player's inventory
	 */
	private void assemble() {
		if (inventory.hasItem("flux capacitor") && inventory.hasItem("wires")){
			if(inventory.hasItem("control panel") && inventory.hasItem("blinker")){
				if(inventory.hasItem("frame")){
					Item portal = new Item("portal","this will get you back home" );
					System.out.println("You have succesfully built the portal.");
					inventory.addItem(portal);
				}
			}
		}
		else{
			System.out.println("You don't have everything you need");
		}
	}

	private void partCount(){
		int score = 0; 
		if(inventory.hasItem("flux capacitor"))
			score++;
		if(inventory.hasItem("wires"))
			score++;
		if(inventory.hasItem("frame"))
			score++;
		if(inventory.hasItem("blinker"))
			score++;
		if(inventory.hasItem("control panel"))
			score++;
		System.out.println("You have " +score + "/5 parts to build the time machine");
	}

	/**
	 * HasWon: once a player as the portal object, the method will return true.  
	 */
	private boolean hasWon(){
		if(inventory.hasItem("Portal")){
			return true;
		}
		return false;
	}

	/**
	 *Print Help: Print out some help information. Here we print some stupid, cryptic message
	 *and a list of the command words.
	 */
	private void printHelp() {
		System.out.println(" Hello? Hello? Anybody home? Huh? Think, McFly! Think!");
		System.out.println();
		System.out.println("Your command words are:");
		parser.showCommands();
	}

	/**
	 *  Has Key: gets the key from a room, and checks if the player's inventory has the corresponding key
	 */
	private boolean hasKey(Room nextRoom) {
		String key = nextRoom.getKey();
		return key != null && inventory.contains(key) != null && inventory.contains(key).getName().equalsIgnoreCase(key);
	}
	/**
	 * talk: if you talk to anyone in the game, it automatically results in death.
	 */

	private boolean talk() {
		if(currentRoom.getRoomName().equalsIgnoreCase("Bakery")){
			System.out.println("Uh oh...remember what the letter's said: 'BE CAREFUL WITH WHEN YOU INTERACT WITH THOSE FROM THE PAST'."+ 
		"\nThe game actually hasn't happened yet, or the semi-finals, but you revealed the results, and provided lots of detail."+
		"\nThese two nobodies beleived you, and through betting became super rich.\nYOU CHANGED HISTORY, AND THEREFORE YOU NO LONGER EXISTS");
		}
		else{
			System.out.println("By talking to them, you have messsed up the timeline...YOU NO LONGER EXIST");
		}
	return true;

}
	/**
	 * Go Room: Try to go to one direction. If there is an exit, enter the new room,
	 *otherwise print an error message. ]
	 */
	private void goRoom(Command command) {
		if (!command.hasSecondWord() && ("udeswn".indexOf(command.getCommandWord()) < 0)) {
			// if there is no second word, we don't know where to go...
			System.out.println("Go where?");
			return;
		}
		//Allows the player to enter short forms for the directions
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

		if (nextRoom == null) //no room in requested direction
			System.out.println("There is something obstructing your path. You cannot go this way!");
		else if (!hasKey(nextRoom) && nextRoom.getRoomName().equalsIgnoreCase("Attic Continued")){
			System.out.println("You need something bright. It is too dark here");
		}
		else if (nextRoom.isLocked() && !hasKey(nextRoom)) { //when next room is locked and player does not have key
			System.out.println("The area is locked. You do not have the neccessary items to pass.");
		} else if(currentRoom.getRoomName().equalsIgnoreCase("Garage Secret Room") && nextRoom.getRoomName().equalsIgnoreCase("oldtown square")){
			if(inventory.hasItem("helmet")){ //if player has helmet they won't die
				nextRoom.setKill(false);
				currentRoom.setLocked(true);
				System.out.println("\nYOU FEEL A SERIES OF JOLTS, COLOURS EVERYWEHRE!\nYOU ARE VERY DIZZY, WHAT IS HAPPENING?!!\nAfter a series of zaps...you open your eyes and find yourself in...");
				currentRoom = nextRoom;
				System.out.println(currentRoom.shortDescription());
			}else{
				System.out.println("You were not protected from the harmful radiation of the portal");
				currentRoom=nextRoom;
			}
		} else if(nextRoom.isKiller()){ //die in next room
			currentRoom = nextRoom;
			if(currentRoom.getRoomName().equalsIgnoreCase("Alleyway")){
				System.out.println("you walked straight into some thugs, and you have nothing to protect yourself. You were killed because you saw something you shouldn't have");
			}else if(currentRoom.getRoomName().equalsIgnoreCase("Pound")){
				System.out.println("The dogs notified their owner that there was an intruder.");
			}else if(currentRoom.getRoomName().equalsIgnoreCase("Kitchen")){
				System.out.println("You came face-to-face with the Time Hunters. They stole all your items and killed you.");
			}else{
				System.out.println("you walked straight into danger and made a bad decision");
			}
		}else{ 
			if (nextRoom.isLocked() && hasKey(nextRoom)){
				System.out.println("You have unlocked this area");
			}
			currentRoom = nextRoom; //move on to next room
			System.out.println(currentRoom.shortDescription());
		}
	}

	//Methods for commands that just return fun Strings: 
	/**
	 * eat: prints fun string based on different second command words 
	 */
	private void eat(String secondWord) {
		if (secondWord.equalsIgnoreCase("steak"))
			System.out.println("YUMMY");
		else if (secondWord.equalsIgnoreCase("bread"))
			System.out.println("I don't eat carbs...");
		else 
			System.out.println("You are the " + secondWord);
	}

	/**
	 * sit: prints fun String 
	 */
	private void sit() {
		System.out.println("You are now sitting. You lazy excuse for a person.");	
	}

	/**
	 * jump: prints string and returns true
	 * (when used in process comand, true = death)
	 */

	private boolean jump() {
		System.out.println("You jumped. Ouch you fell. You fell hard. Really hard." 
		+"You are getting sleepy. Very sleepy! You are dead!");
		return true;
	}

	/**
	 * playGame: mini game that randomly returns a result
	 */
	private void playGame(String secondWord) {
		int num = (int)(Math.random()*2) +1;
		if(num==1){
			if(secondWord.equals("basketball")){
				System.out.println("he shoots and he scores");
			}else if(secondWord.equals("claw")){
				System.out.println("you got the toy!");
			}else{
				System.out.println("You won and got a highscore");
			}
			Item trophy = new Item("trophy", "this is a shiny trophy", true, 1);
			System.out.println("a shiny trophy has just been added to your inventory");
			if (inventory.getInvWeight()<15){
				inventory.addItem(trophy);
			}else{
				System.out.println("you inventory is too full. drop items and play again.");
			}
		
		}else{
			if(secondWord.equals("basketball")){
				System.out.println("he shoots and...AIRBALL");
			}else if(secondWord.equals("claw")){
				System.out.println("you missed the toy...completely");
			}else{
				System.out.println("You lost");
			}
		}
	}

	/**
	 * wearItem: prints out special string if called in "Garage Secret Room"
	 * if not than prints out fun Stirng 
	 */
	private void wearItem(String itemName) {
		if(currentRoom.getRoomName().equalsIgnoreCase("Garage Secret Room") && itemName.equalsIgnoreCase("helmet")){
			System.out.println("Yay! You now are protected"); 
		}
		else{
			System.out.println(itemName + " looks good on you"); 
		}
	}
}

