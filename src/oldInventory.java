
import java.util.ArrayList;

public class oldInventory {
    //have a bunch of items 
    //ArrayList or Hashmap of Item 


  private ArrayList<Item> bag;
  // private Item currentItem; 

  public oldInventory(){
    this.bag = bag;
  }

  public void addItem(Item item){
    bag.add(item);
  }

  public void removeItem(Item item){
    bag.remove(item);
  }

 
  public void allInventory() {
    if (bag.size() == 0){
      System.out.println("There is nothing in your inventory! :)"); 
    }
    
    else if(bag.size()>0){
      for (int i = 0; i < bag.size(); i++){ 
        System.out.println(bag.get(i));
      }
    }
  }


  public int numItems(){
    return bag.size();
  }

  public ArrayList getInventory(){
    return bag;
  }

//maybe add a isinIvnetory to check if a specific item is in inventory 




    
}
