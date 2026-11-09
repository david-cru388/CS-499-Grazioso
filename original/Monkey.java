import java.util.ArrayList;

public class Monkey extends RescueAnimal {

        // Instance Fields
        private String tailLength;
        private String height;
        private String bodyLength;
        private String species;

        // Constructor
        public Monkey(String name, String gender, String age,
        String weight, String tailLength, String height, String bodyLength, 
        String species, String acquisitionDate, String acquisitionCountry,
    	String trainingStatus, boolean reserved, String inServiceCountry) {
        
        setName(name);
        setGender(gender);
        setAge(age);
        setWeight(weight);
        setTailLength(tailLength);
        setHeight(height);
        setBodyLength(bodyLength);
        setSpecies(species);
        setAcquisitionDate(acquisitionDate);
        setAcquisitionLocation(acquisitionCountry);
        setTrainingStatus(trainingStatus);
        setReserved(reserved);
        setInServiceCountry(inServiceCountry);
        }
     // Default constructor 
        public Monkey () { this("unknown", "unknown", "unknown", "unknown", "unknown", "unknown", "unknown", "unknown", "unknown", "unknown", "unknown", false, "unknown"); }
        
     // Prints the monkey name, status, acquisition country and if the monkey is reserved.
        public static void getAllMonkeys(ArrayList<Monkey> monkeyList) { 
        	for(Monkey monkey: monkeyList) {
        		System.out.println("\nName: " + monkey.getName() + "\n" + "Training status: " + monkey.getTrainingStatus() + "\n" +
        						   "Acquisition country: " + monkey.getAcquisitionLocation() + "\n" + "Reserved: " + monkey.getReserved() + "\n");
        		}
        	}
        
        //Accessors and Mutators
        public void setTailLength(String tailLength) { this.tailLength = tailLength; }
        public String getTailLength() { return tailLength; }

        public void setHeight(String height) { this.height = height; }
        public String getHeight() { return height; }

        public void setBodyLength(String bodyLength) { this.bodyLength = bodyLength; }
        public String getBodyLength() { return bodyLength; }

        public void setSpecies(String species){ this.species = species; }
        public String getSpecies() { return species; }
        

        
    }