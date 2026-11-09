import java.util.ArrayList;

public class Dog extends RescueAnimal {

    // Instance variable
    private String breed;

    // Constructor
    public Dog(String name, String breed, String gender, String age,
    String weight, String acquisitionDate, String acquisitionCountry,
	String trainingStatus, boolean reserved, String inServiceCountry) {
        setName(name);
        setBreed(breed);
        setGender(gender);
        setAge(age);
        setWeight(weight);
        setAcquisitionDate(acquisitionDate);
        setAcquisitionLocation(acquisitionCountry);
        setTrainingStatus(trainingStatus);
        setReserved(reserved);
        setInServiceCountry(inServiceCountry);

    }
    // Default constructor 
    public Dog () { this("unknown", "unknown", "unknown", "unknown", "unknown", "unknown", "unknown", "unknown", false, "unknown"); }

    // Prints the dog name, status, acquisition country and if the dog is reserved.
    public static void getAllDogs(ArrayList<Dog> dogList) { 
    	for(Dog dog: dogList) {
    		System.out.println("\nName: " + dog.getName() + "\n" + "Training status: " + dog.getTrainingStatus() + "\n" +
    						   "Acquisition country: " + dog.getAcquisitionLocation() + "\n" + "Reserved: " + dog.getReserved() + "\n");
    		}    	
    	} 
    	
    // Accessor Method
    public String getBreed() {
        return breed;
    }

    // Mutator Method
    public void setBreed(String dogBreed) {
        breed = dogBreed;
    }

}
