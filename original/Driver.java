import java.util.ArrayList;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.lang.NumberFormatException;

public class Driver {
	// ArrayLists for dog and monkey
	private static ArrayList<Dog> dogList = new ArrayList<Dog>();
    	private static ArrayList<Monkey> monkeyList = new ArrayList<Monkey>();
  
    	public static void main(String[] args) {
    		// Instance variables
    		Scanner scnr = new Scanner(System.in);
    		String input = "";
    	
    	
    		// Initialize Lists
        	initializeDogList();
        	initializeMonkeyList();

        	// Menu
        	displayMenu();   
        	// User input
        	input = getUserInput(scnr);
        	// Run specified program
        	runProgram(input,scnr);   
        
        	return;  
        
    	}// End of main
	
        
        // This method gets the user input
        public static String getUserInput(Scanner scnr) {
        	
        	// Instance variables
        	String userInput = "";	
        	boolean validInput = false;
        	
        	// Do while input is invalid
        	do {
				try{
        	
        			validInput = true;
        			userInput = scnr.nextLine();
        	
        	
        			// If user chooses quit
        			if(userInput.equalsIgnoreCase("q")) {
        				System.out.println("Program closed.");
        				System.exit(0);
        			}
            			// If user enters number less than 1
        			if (Integer.parseInt(userInput) < 1){
            				throw new Exception("Number can not be less than 1. Try again.");
        			}
            
            			// If user enters number greater than 6
        			if (Integer.parseInt(userInput) > 6){
            				throw new Exception("Number can not be greater than 6. Try again.");
        			}
				}
            
            		// If input is not a number
        		catch(NumberFormatException e){
            			System.out.println("Invalid input. Try again");
            			validInput = false;
        		}
            
        		catch(Exception e){
            			System.out.println(e.getMessage());
            			validInput = false;
        		}
        	
      		}while(!validInput); 
      		
      		return userInput;
      	}
       
      
	// This method runs the program
        public static void runProgram(String input, Scanner scnr) {
		boolean programIsRunning = true;
	
        	// MENU
        	// case 1: dog intake
        	// case 2: monkey intake
        	// case 3: reserve a animal
        	// case 4: Print all dogs
        	// case 5: print all monkeys
        	// case 6: print available animals
        	while(programIsRunning) { 
    	
         		switch (input) {
         		
         		case "1":
        			intakeNewDog(scnr);
        			break;
         		case "2":
        			intakeNewMonkey(scnr);
        			break;
         		case "3":
        			reserveAnimal(scnr);
        			break;
         		case "4":
        			printAnimals(input);
        			break;
         		case "5":
        			printAnimals(input);
        			break;
         		case "6":
        			printAnimals(input);
        			break;
       
        		}
        
        		// Menu
        		displayMenu();   
        		// Get user input
        		input = getUserInput(scnr); 	
                
		}
       
       }
    
    	// This method prints the menu options
    	public static void displayMenu() {
    	
        	System.out.println("\n\n");
        	System.out.println("\t\t\t\tRescue Animal System Menu");
        	System.out.println("[1] Intake a new dog");
        	System.out.println("[2] Intake a new monkey");
        	System.out.println("[3] Reserve an animal");
        	System.out.println("[4] Print a list of all dogs");
        	System.out.println("[5] Print a list of all monkeys");
        	System.out.println("[6] Print a list of all animals that are not reserved");
        	System.out.println("[q] Quit application");
        	System.out.println();
        	System.out.println("Enter a menu selection");
    	}

    	// Adds dogs to a list for testing
    	public static void initializeDogList() {
    	
        	Dog dog1 = new Dog("Spot", "German Shepherd", "male", "1", "25.6", "05-12-2019", "United States", "in service", false, "United States");
        	Dog dog2 = new Dog("Rex", "Great Dane", "male", "3", "35.2", "02-03-2020", "United States", "Phase I", false, "United States");
        	Dog dog3 = new Dog("Bella", "Chihuahua", "female", "4", "25.6", "12-12-2019", "Canada", "in service", false, "Canada");

        	dogList.add(dog1);
        	dogList.add(dog2);
        	dogList.add(dog3);
    	}
    
    	// Adds monkeys to a list for testing
    	public static void initializeMonkeyList() {
        	Monkey monkey1 = new Monkey("Kylo", "male", "1", "25.6", "6.5", "20.1", "25.4", "Marmoset", "05-12-2019", "United States", "Phase I", false, "United States");
        	Monkey monkey2 = new Monkey("Ren", "male", "3", "35.2", "8.5", "15.7", "23.4", "Guenon", "02-03-2020", "United States", "in service", false, "United States");
        	Monkey monkey3 = new Monkey("Chase", "female", "4", "25.6", "4.0", "13.5", "17.5", "Squirell monkey", "12-12-2019", "Canada", "in service", false, "Canada");

        	monkeyList.add(monkey1);
        	monkeyList.add(monkey2);
        	monkeyList.add(monkey3);
    	}

    	// This method checks in a new dog
    	public static void intakeNewDog(Scanner scanner) {
    	
        	System.out.println("What is the dog's name?");
        	String name = scanner.nextLine();
        	
        	for(Dog dog: dogList) {
				if(dog.getName().equalsIgnoreCase(name)) {
                	System.out.println("\n\nThis dog is already in our system\n\n");
               		return; //returns to menu
				}
        	}

        	// Creates new dog object if the dog is new
        	Dog dog = new Dog();
        	dog.setName(name);

        	// get data from user for all other attributes
        	System.out.println("What is " + name + "'s breed?");
        	dog.setBreed(scanner.nextLine());
        	System.out.println("What is " + name + "'s gender?");
        	dog.setGender(scanner.nextLine());
        	System.out.println("What is " + name + "'s age?");
        	dog.setAge(scanner.nextLine());
        	System.out.println("What is " + name + "'s weight?");
        	dog.setWeight(scanner.nextLine());
        	System.out.println("On what date was " + name + " aquired?");
        	dog.setAcquisitionDate(scanner.nextLine());
        	System.out.println("In what country was " + name + " aquired?");
        	dog.setAcquisitionLocation(scanner.nextLine());
        	System.out.println("What is " + name + "'s training status? ");
        	dog.setTrainingStatus(scanner.nextLine());
        	
        	try {
        	System.out.println("Is " + name + " reserved? Enter true or false: ");
        	dog.setReserved(scanner.nextBoolean());
    		}
        	catch (InputMismatchException e) {
        		System.out.println("Invalid input. Set to false by default");
        		dog.setReserved(false);
        	}
        			
        	scanner.nextLine();
        	System.out.println("What country is " + name + " in service?");
        	dog.setInServiceCountry(scanner.nextLine());
        
        	// Add dog object to dog list
        	dogList.add(dog);
        	System.out.println("Dog added! Returning to the menu.");

    	}

    	// This method checks in a new monkey 
        public static void intakeNewMonkey(Scanner scanner) {

        	System.out.println("What is the monkey's name?");
			String name = scanner.nextLine();
            	for(Monkey monkey: monkeyList) {
                	if(monkey.getName().equalsIgnoreCase(name)) {
                    	System.out.println("\n\nThis monkey is already in our system\n\n");
                    	return; //returns to menu
                	}
            	}
            
            	// Creates new monkey object if the dog is new
            	Monkey monkey = new Monkey();
            	monkey.setName(name);

            	// get data from user for all other attributes
            	System.out.println("What is " + name + "'s gender?");
            	monkey.setGender(scanner.nextLine());
            	System.out.println("What is " + name + "'s age?");
            	monkey.setAge(scanner.nextLine());
            	System.out.println("What is " + name + "'s weight?");
            	monkey.setWeight(scanner.nextLine());
            	System.out.println("What is " + name + "'s tail length?");
            	monkey.setTailLength(scanner.nextLine());
            	System.out.println("What is " + name + "'s height?");
            	monkey.setHeight(scanner.nextLine());
            	System.out.println("What is " + name + "'s body length?");
            	monkey.setBodyLength(scanner.nextLine());
            	System.out.println("What is " + name + "'s species?");
            	monkey.setSpecies(scanner.nextLine());
            	System.out.println("On what date was " + name + " aquired?");
            	monkey.setAcquisitionDate(scanner.nextLine());
            	System.out.println("In what country was " + name + " aquired?");
            	monkey.setAcquisitionLocation(scanner.nextLine());
            	System.out.println("What is " + name + "'s training status?");
            	monkey.setTrainingStatus(scanner.nextLine());
            	
            	try {
                	System.out.println("Is " + name + " reserved? Enter true or false: ");
                	monkey.setReserved(scanner.nextBoolean());
            		}
                	catch (InputMismatchException e) {
                		System.out.println("Invalid input. Set to false by default");
                		monkey.setReserved(false);
                	}
            	
            	scanner.nextLine();
            	System.out.println("What country is " + name + " in service?");
            	monkey.setInServiceCountry(scanner.nextLine());
            
            	// Add monkey object to monkey list
            	monkeyList.add(monkey);
            	System.out.println("Monkey added! Returning to the menu.");
            
            	return;
        }

        // This method updates the reserved status of the animal
        public static void reserveAnimal(Scanner scanner) {
        	// Instance variables
        	boolean foundDog = false;
        	boolean foundMonkey = false;
        	
        	System.out.println("Is the animal a dog or a monkey??");
        	String animal = scanner.nextLine();
        	// If a dog
        	if (animal.equalsIgnoreCase("dog")) {
        		System.out.println("What is the dog's name?");
        		String name = scanner.nextLine();
        		for(Dog dog: dogList) {
        			if(dog.getName().equalsIgnoreCase(name)) {
        				foundDog = true;
        				System.out.println("Current reserve status: " + dog.getReserved());
        				System.out.println("\nUpdate reserve status: ");
        				
        				try {
        					dog.setReserved(scanner.nextBoolean());
        				}
        				catch (InputMismatchException e) {
        					System.out.println("Invalid input. Reserve status update failed. Returning to menu.");
        					scanner.nextLine();
        					return;
        				}
        				System.out.println("\nReserve status updated to " + dog.getReserved());
        				scanner.nextLine();
        				}        			
        			}
        		if(!foundDog) {
        			System.out.println("Dog not found. Returning to menu.");
        			
        		}
            }
        	// If a monkey 
        	else if (animal.equalsIgnoreCase("monkey")) {
        		System.out.println("What is the monkey's name?");
        		String name = scanner.nextLine();
        		for(Monkey monkey: monkeyList) {
        			if(monkey.getName().equalsIgnoreCase(name)) {
        				foundMonkey = true;
        				System.out.println("Current reserve status: " + monkey.getReserved());
        				System.out.println("\nUpdate reserve status: ");
        				
        				try {
        					monkey.setReserved(scanner.nextBoolean());
        				}
        				catch(InputMismatchException e) {
        					System.out.println("Invalid input. Reserve status update failed. Returning to menu.");
        					scanner.nextLine();
        					return;
        				}
        				System.out.println("\nReserve status updated to " + monkey.getReserved());
        				scanner.nextLine();
        			}
        			
        		}
        		if(!foundMonkey) {
        			System.out.println("Monkey not found. Returning to menu.");
        			
        		}	
            }
        	else { // If input is neither monkey or dog
        		System.out.println("Invalid input. Returning to menu.");
        	}
        	
	}
       
        // This method prints all animals based on user input 
        // case 4 - prints the list of dogs
        // case 5 - prints the list of monkeys
        // case 6 - print all animals with completed training and are not reserved
        public static void printAnimals(String input) {
        	switch (input) {
            	case "4":
            		Dog.getAllDogs(dogList);
            		break;
            	case "5":
            		Monkey.getAllMonkeys(monkeyList);
            		break;
            	case "6":
            		animalsAvailable(dogList,monkeyList);
            		break;            	
            	}
        }
        
        // animalsAvailable - prints a combined list of all animals that are
        // fully trained ("in service") but not reserved 
        public static void animalsAvailable(ArrayList<Dog> dogList, ArrayList<Monkey> monkeyList) {
        	
        	// For loop iterates through dogList to find a match
        	System.out.println("\nDogs available: ");
        	for (Dog dog: dogList) {
        		if(dog.getTrainingStatus().equalsIgnoreCase("in service") && dog.getReserved() == false) {
        				System.out.println(dog.getName() + "  ");
        		}        		
        	}
        	System.out.println();
        	
        	// For loop iterates through monkeyList to find a match
        	System.out.println("\nMonkeys available: ");
        	for (Monkey monkey: monkeyList) {
        		if(monkey.getTrainingStatus().equalsIgnoreCase("in service") && monkey.getReserved() == false) {
        				System.out.println(monkey.getName() + "  ");
        		}        		
        	}
        	System.out.println();
        }
}// End of class

