// import javax.management.openmbean.OpenDataException;

public class Item{
	private String name; 
	private String description; 
	private Inventory items; 
	private boolean isOpenable; 
	//add weight 
	private int weight;

	public Item(String name, String description, Boolean isOpenable){
		super(); 
		this.name = name; 
		this.description = description;
		this.isOpenable = isOpenable;
		if(isOpenable){
			this.items = new Inventory();
		}
	}

	public Item(String name, String description){
		super(); 
		this.name = name; 
		this.description = description;
		this.isOpenable = false;
	}

	public Item(){
	}

	public String getName(){
		return name;
	}

	public String getDescription(){
		return description;
	}

	public Inventory getContents(){
		if(!isOpenable) return null;
		return items;
	}

	public boolean addItem(Item item){
		if(!isOpenable) return false;
		return item.addItem(item);

	}

	public Item removeItem(String item){
		if(!isOpenable) return null;
		return items.removeItem(item); 
	}

	public String displayContents(){
		if(!isOpenable) return null;
		return "The " + name + "contains: \n" + items;
	}

	public void setName(String name){
		this.name = name;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public void setOpenable(Boolean openable){
		this.isOpenable = openable;
	}
}