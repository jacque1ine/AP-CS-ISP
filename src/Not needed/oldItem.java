// public class Item {
//     private String description;
//     private String name; 
//     private int weight; 


//     public Item(String name, String description, int weight){
//         this.name = name; 
//         this.description = description;
//         this.weight = weight;
//     }

//     public String getDescription(){
//         return description;
//     }

//     public int getWeight(){
//         return weight;
//     }

//     public Boolean moveable(){
//         if(weight==1){
//             return true;
//         }
//         else{
//             return false; 
//         }
//     }


// //has name
// //description
// //weight -> you can only hold so much 

    
// }


/* from DLO 
public class oldItem {
	private String name;
	private String description;
	private Inventory items;
	private boolean isOpenable;
	
	public oldItem() {
	}
	
	public boolean isOpenable() {
		return isOpenable;
	}

	public void setOpenable(boolean isOpenable) {
		this.isOpenable = isOpenable;
		if(isOpenable)
			this.items = new Inventory(); 
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public oldItem(String name, String description, boolean isOpenable) {
		super();
		this.name = name;
		this.description = description;
		this.isOpenable = isOpenable;
		if(isOpenable)
			this.items = new Inventory(); 
	}
	
	public oldItem(String name, String description) {
		super();
		this.name = name;
		this.description = description;
		this.isOpenable = false;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public Inventory getContents() {
		if (!isOpenable) return null;
		return items;
	}
	
	public boolean addItem(Item item) {
		if (!isOpenable){
            return false;
        }
		return items.addItem(item);
	}
	
	public Item removeItem(String item) {
		if (!isOpenable){
            return null;
        }
		return items.removeItem(item);
	}
	
	public String displayContents() {
		if (!isOpenable) return null;

		return "The " + name + " contains:\n" + items;
	}
}

*/

