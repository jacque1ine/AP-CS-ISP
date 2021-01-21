// import javax.management.openmbean.OpenDataException;

public class Item{
	private String name; 
	private String description; 
	private Inventory items; 
	private boolean isOpenable; 
	private int weight;

	/*
	Constructor for all private variables of the Item class
	*/
	public Item(String name, String description, Boolean isOpenable, int weight){
		super(); 
		this.name = name; 
		this.description = description;
		this.isOpenable = isOpenable;
		if(isOpenable){
			this.items = new Inventory();
		}
		this.weight = weight;
	}


	/*
	Constructor for a basic item with default settings.
	Object created with this constructor can be picked up (if inventory has space).
	This object does not have its own inventory. 
	*/
	public Item(String name, String description){
		super(); 
		this.name = name; 
		this.description = description;
		this.isOpenable = false;
		this.weight = 1; 
	}

	/*
	Default constructor  
	*/
	public Item(){
	}

	/*
	getName: returns the name of the item
	*/

	public String getName(){
		return name;
	}

	/*
	getDescription: returns the description of item 
	*/
	public String getDescription(){
		return description;
	}

	/*
	getContents: returns the items in the inventory of an item if it is openable.
	If it is not openable, it will return null.
	*/
	public Inventory getContents(){
		if(!isOpenable) return null;
		return items;
	}

	/*
	addItem: if an item is openable, add a new item to its inventory. 
	*/
	public boolean addItem(Item item){
		if(!isOpenable) return false;
		return items.addItem(item);
	}

	/*
	removeItem: if an item is openable, remove the given item from its inventory. 
	*/
	public Item removeItem(String item){
		if(!isOpenable) return null;
		return items.removeItem(item); 
	}

	/*
	displayContents: if an item is openable, it will return all the contents within the inventory of the item.
	*/
	public String displayContents(){
		if(!isOpenable) return null;
		return "The " + name + " contains: \n" + items;
	}

	/*
	setName: allows access to set a name to an item
	*/
	public void setName(String name){
		this.name = name;
	}

	/*
	setDescription: allows access to set a description to an item
	*/
	public void setDescription(String description){
		this.description = description;
	}

	/*
	setOpenable: allows access to set whether or not item has an inventory. 
	If it does, it will create a inventory
	*/
	public void setOpenable(Boolean isOpenable){
		this.isOpenable = isOpenable;
		if(isOpenable)
			this.items = new Inventory();
	}

	public boolean isOpenable(){
		return isOpenable;
	}

	public void setWeight(int weight){
		this.weight = weight; 
	}
	public int getWeight(){
		return weight;
	}

	public boolean canPickUp(){
		return weight<=3;
	}
}