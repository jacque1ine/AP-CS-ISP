public class Item {
    private String description;
    private String name; 
    private int weight; 


    public Item(String name, String description, int weight){
        this.name = name; 
        this.description = description;
        this.weight = weight;
    }

    public String getDescription(){
        return description;
    }

    public int getWeight(){
        return weight;
    }

    public Boolean moveable(){
        if(weight==1){
            return true;
        }
        else{
            return false; 
        }
    }


//has name
//description
//weight -> you can only hold so much 

    
}
