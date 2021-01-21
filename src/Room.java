	/*
	* Class Room - a room in an adventure game.
	*
	* Author:  Michael Kolling
	* Version: 1.1
	* Date:    August 2000
	* 
	* This class is part of Zork. Zork is a simple, text based adventure game.
	*
	* "Room" represents one location in the scenery of the game.  It is 
	* connected to at most four other rooms via exits.  The exits are labelled
	* north, east, south, west.  For each direction, the room stores a reference
	* to the neighbouring room, or null if there is no exit in that direction.
	*/
import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;

public class Room {
	private String roomName;
	private String description;
	private HashMap<String, Room> exits; //stores exits of this room. 
	// HashMap stores MULTIPLE OBJECTS in a variable. 
	private Inventory inventory; 
	private boolean locked;
	private String key; 
	private boolean isKiller;
	/**
	 * Create a room described "description". Initially, it has no exits.
	 * "description" is something like "a kitchen" or "an open court yard".
	 */
	public Room(String description) {
		this.description = description;
		exits = new HashMap<String, Room>();
		inventory = new Inventory();
		locked = false;
		isKiller = false;
	}

	public Room() {
		// default constructor.
		roomName = "DEFAULT ROOM";
		description = "DEFAULT DESCRIPTION";
		exits = new HashMap<String, Room>(); // <key(direction), value> 
		inventory = new Inventory();
		locked = false;
		isKiller = false;
	}


	public void setExit(char direction, Room r) throws Exception {
		String dir = "";
		switch (direction) {
		case 'E':
			dir = "east";
			break;
		case 'W':
			dir = "west";
			break;
		case 'S':
			dir = "south";
			break;
		case 'N':
			dir = "north";
			break;
		case 'U':
			dir = "up";
			break;
		case 'D':
			dir = "down";
			break;
		default:
			throw new Exception("Invalid Direction");
		}

		exits.put(dir, r);
	}


	/**
	 * Define the exits of this room. Every direction either leads to another room
	 * or is null (no exit there).
	 */
	public void setExits(Room north, Room east, Room south, Room west, Room up, Room down, Room southwest) {
		if (north != null)
		exits.put("north", north);
		if (east != null)
		exits.put("east", east);
		if (south != null)
		exits.put("south", south);
		if (west != null)
		exits.put("west", west);
		if (up != null)
		exits.put("up", up);
		if (up != null)
		exits.put("down", down);
		if (southwest != null)
		exits.put("southwest", southwest);
	}


	public String justDescription(){
		return "\n\n" + description;
	}

	/**
	 * Return the description of the room (the one that was defined in the
	 * constructor).
	 */


	public String shortDescription() {
		return "LOCATION: " + roomName + "\n\n" + description;
	}

	/**
	 * Return a long description of this room, on the form: You are in the kitchen.
	 * Exits: north west
	 */
	public String longDescription() {
		return "------------------------------------\nLOCATION: " + roomName + "\n" + description + "\n" + "\nITEMS HERE:\n" + inventory + "\n" + exitString();
	}


	public String longestDescription() {
		return "------------------------------------\nLOCATION: " + roomName + "\n" + description + "\n" + "\nITEMS HERE:\n" + inventory + "\n" + exitString() +  
		(numLockedRooms()>0?"\nIt appears that the " + getLockedRooms() + " "+(numLockedRooms()==1?"is": "are")+" locked.":"");
	}

	public String lookAround(){ 
		return "\nITEMS HERE:\n" + inventory + "\n" + exitString() +  
		(numLockedRooms()>0?"\nIt appears that the " + getLockedRooms() + " may be locked.":"");
	}

	
	/**
	 * Return a string describing the room's exits, for example "Exits: north west
	 * ".
	 */
	private String exitString() {
		String returnString = "EXITS:";
		Set<String> keys = exits.keySet();
		for (Iterator<String> iter = keys.iterator(); iter.hasNext();)
		returnString += " " + iter.next();
		return returnString;
	}

	private int numLockedRooms(){
		int num = 0; 
		for (String s: exits.keySet()){ //key - nsew
			if(exits.get(s).isLocked())
				num++; 
		}
		return num;
	}

	private String getLockedRooms(){
		String str = ""; 

		for (String s: exits.keySet()){ //key - nsew
			Room room = exits.get(s);
			if(room.isLocked()){
				str+=room.getRoomName() + ", ";
			}; 
		}
		return str.substring(0,str.length()-2);
	}
	/**
	 * Return the room that is reached if we go from this room in direction
	 * "direction". If there is no room in that direction, return null.
	 */
	public Room nextRoom(String direction) {
		return (Room) exits.get(direction);
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Inventory getInventory(){
		return inventory;
	}

	public void setLocked(boolean locked){
		this.locked = locked;
	}

	public boolean isLocked(){
		return locked;
	}
	
	public void setKey(String key){
		this.key = key; 
	}

	public String getKey(){
		return this.key;
	}

	public void setKill(boolean isKiller){
		this.isKiller = isKiller;
	}

	public boolean isKiller(){
		return isKiller;
	}
}

