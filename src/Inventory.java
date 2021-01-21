import java.util.ArrayList;

public class Inventory{
	private ArrayList<Item> items; 

	/**
	* Constructor which creates an ArrayList of item objects
	*/
	public Inventory(){
		  items = new ArrayList<Item>(); 
  	}

	/**
   * addItem: adds item and will return true 
   * if item has been added 
   */
	public boolean addItem(Item item){
    	return items.add(item); 
	}
	
	/**
   * justAddItem: adds item and does not return anything 
   */
	public void justAddItem(Item item){
    	items.add(item); 
	}

	/**removeItem: will remove item from inventory, 
	* based on the name and will return the object.
  	* if the item is not in inventory, return null 
   	*/
	public Item removeItem(String name){
    	for(int i=0; i<items.size(); i++){
      		if (name.equalsIgnoreCase(items.get(i).getName())){
        		return items.remove(i);
      		}
		}
    	return null; 
	  }
	  
	/**
	* toString: will return a list of the names 
	*of all items in inventory. If inventory is empty, 
	* then it will return a string indicating so
	*/
	public String toString(){
    	if(items.size() ==0){
     		 return "nothing\n";
    	}
   		String msg = ""; 
    	for(Item i: items){
      		msg+=i.getName() + "\n";
    	}
    	return msg;
	}
	
	/**
	* contains: returns the item from inventory based on item name 
	* If it is not found, it returns null
	*/
	public Item contains(String name){
    	for (int i=0; i<items.size(); i++){
      		if (name.equalsIgnoreCase(items.get(i).getName())){
        		return items.get(i);
      		}
    	}
		return null;
	  }
	  
	/**
	* hasItem: return true if inventory has the given item based on name 
	* return false if the inventory does not have the item
	*/
	public boolean hasItem(String itemName) {
		for(Item i : items){
            if(i.getName().equalsIgnoreCase(itemName)){
                return true;
            }
        }
        return false;
	}

	/**
	* getInventory: returns ArrayList of items
	*/
	public ArrayList<Item> getInventory(){
        return items;
	}
	
	/**
	* getNumItems: returns number of items in inventory
	*/
	public int getNumItems(){
		return items.size();
	}

	/**
	* getInvItems: returns the total weight of inventory
	*/
	public int getInvWeight(){
        int sum = 0;
        for (int i=0; i<items.size(); i++){
           sum+= items.get(i).getWeight();
        }
        return sum;
	}
	
	/**
	* canHold: returns true if there is space in inventory 
	* to hold a new item based on weight. 
	* returns false if there is no more space.
	*/
	public boolean canHold(int num){
		return getInvWeight() + num <=15; //15 is max weight of inventory 
    }
}