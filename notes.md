- two word commands (addmore)
- parser: resposnible to allow us to type things in - parses up input into command 
- room class - rooms have name, descirption, and hasmap of exits
- some items found in rooms will have letters -> enter them at the end? 



- tem have weight 
each item has same weight exceptpt for items that stay in he room 
each item - weight = 1 unless it is stationalty

TO DO: 
- NEED TO ADD LOCKED ROOM FUNCTION: 
- room inventory
- locked door garage 
- better descirptions 
- add newspaper in oldtown square 
- at end, change game LONGDESCRIPTION() TO SHORT DESCRIPTION OR EVEN JUST DESCIRPTION



// } else if(commandWord.equalsIgnoreCase("i") && command.getSecondWord().equals("am") ){
		// 	if(command.getThirdWord().equals("confused")){
		// 		System.out.println(" Hello? Hello? Anybody home? Huh? Think, McFly! Think!");
		// 	}
		// 	else if(command.getThirdWord().equals("stuck")){
		// 		System.out.println("If you put your mind to it you can accomplish anything.! - Doc Brown");
		// 	}
		// 	else if(command.getThirdWord().equals("stressed")){
		// 		System.out.println("“Wait a minute... are you telling me you are stressed from playing a video game");
		// 	}
		// 	else{
		// 		System.out.println("you are what? im listening");
		// 	}



	private void takeItem(String itemName) {
      Inventory temp = currentRoom.getInventory(); //room inventory
      Item item = temp.removeItem(itemName);//this is the item the player picks up from the room. Item can have a value of null (if it is null then that means that the item they command to take is not in the room inventory)
      boolean isInInventory = false;
      boolean noSpace = false;

      if (item != null){ //if the player picks up an item that is in the room inventory
        if (item.pickUpable()){ 
          if (inventory.getTotalWeight() + item.getWeight() > 10){
            System.out.println("You don't have enough space in your inventory! Remove some items to be able to add this one.");
            temp.addItem(item);
            noSpace = true;
          }else{
            if (inventory.addItem(item)) //adding item that was in room inventory to player's inventory
              System.out.println("You have taken the " + itemName);
            else //some items cannot be picked up.
              System.out.println("You were unable to take the " + itemName); 
          }
        }else
          System.out.println("You cannot pick up this item.");
      }
      else{ //if the player commands to pick up an item that is not in the room inventory 
        for (int i=0; i<inventory.getInventory().size(); i++){ //go through the player's inventory to see if requested item is within any items that can contain other items
          temp = inventory.getInventory().get(i).getContents(); //the contents of item that can hold other items
          if (temp != null){ //if the item in the inventory we are currently focusing on can hold other items
            item = temp.removeItem(itemName);
            if (item != null){
              if (inventory.getTotalWeight() + item.getWeight() > 10){
                System.out.println("You don't have enough space in your inventory! Remove some items to be able to add this one.");
                temp.addItem(item);
                noSpace = true;
              }else{
                if (inventory.addItem(item)){ //adding item that was in the item that can hold other item's inventory to player's inventory
                  System.out.println("You have taken the " + itemName);
                  isInInventory = true;
                }else //some items cannot be picked up.
                  System.out.println("You were unable to take the " + itemName); 
              }
            }
          
          }
        }

        if (!isInInventory && !noSpace) //if requested item is not in room or in any of the items that can store other items
          System.out.println("The item " + itemName + " isn't here."); 
      }
  }