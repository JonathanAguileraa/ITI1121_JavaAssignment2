import java.io.File;
import java.util.Scanner;

/**
 * @author Mehrdad Sabetzadeh, University of Ottawa
 */
public class ParkingLot {
	/**
	 * The delimiter that separates values
	 */
	private static final String SEPARATOR = ",";

	/**
	 * Instance variable for storing the number of rows in a parking lot
	 */
	private int numRows;

	/**
	 * Instance variable for storing the number of spaces per row in a parking lot
	 */
	private int numSpotsPerRow;

	/**
	 * Instance variable (two-dimensional array) for storing the lot design
	 */
	private CarType[][] lotDesign;

	/**
	 * Instance variable (two-dimensional array) for storing occupancy information
	 * for the spots in the lot
	 */
	private Spot[][] occupancy;

	/**
	 * Constructs a parking lot by loading a file
	 * 
	 * @param strFilename is the name of the file
	 */
	public ParkingLot(String strFilename) throws Exception {
		if (strFilename == null) {
			System.out.println("File name cannot be null.");
			return;
		}

		calculateLotDimensions(strFilename);

		this.lotDesign = new CarType[this.numRows][this.numSpotsPerRow];
		this.occupancy = new Spot[this.numRows][this.numSpotsPerRow];

		populateDesignFromFile(strFilename);
	}

	public int getNumRows() {
		return numRows;
	}

	public int getNumSpotsPerRow() {
		return numSpotsPerRow;
	}

	/**
	 * Parks a car (c) at a give location (i, j) within the parking lot.
	 * 
	 * @param i         is the parking row index
	 * @param j         is the index of the spot within row i
	 * @param c         is the car to be parked
	 * @param timestamp is the (simulated) time when the car gets parked in the lot
	 */
	public void park(int i, int j, Car c, int timestamp) {

		occupancy[i][j] = new Spot(c, timestamp);

	}

	/**
	 * Removes the car parked at a given location (i, j) in the parking lot
	 * 
	 * @param i is the parking row index
	 * @param j is the index of the spot within row i
	 * @return the spot removed; the method returns null when either i or j are out
	 *         of range, or when there is no car parked at (i, j)
	 */
	public Spot remove(int i, int j) {

		if (occupancy[i][j] != null) {
			Spot returnCar = occupancy[i][j];
			occupancy[i][j] = null;
			return returnCar;
		}
		return null;

	}

	/**
	 * Returns the spot instance at a given position (i, j)
	 * 
	 * @param i is the parking row index
	 * @param j is the index of the spot within row i
	 * @return the spot instance at position (i, j)
	 */
	public Spot getSpotAt(int i, int j) {

		return occupancy[i][j];
	
	}

	/**
	 * Checks whether a car (which has a certain type) is allowed to park at
	 * location (i, j)
	 *
	 * NOTE: This method is complete; you do not need to change it.
	 * 
	 * @param i is the parking row index
	 * @param j is the index of the spot within row i
	 * @return true if car c can park at (i, j) and false otherwise
	 */
	public boolean canParkAt(int i, int j, Car c) {
		boolean returnVal = false;
		if (i >= this.numRows || j >= this.numSpotsPerRow){
			returnVal = false;
		} else if (Util.getLabelByCarType(lotDesign[i][j]) == "N" || occupancy[i][j] != null) {
			returnVal = false;
		} else if (Util.getLabelByCarType(c.getType()) == "E") {
			returnVal = true;
		} else if (Util.getLabelByCarType(c.getType()) == "S" && (Util.getLabelByCarType(lotDesign[i][j]) == "S" || Util.getLabelByCarType(lotDesign[i][j]) == "R" || Util.getLabelByCarType(lotDesign[i][j]) == "L")) {
			returnVal = true;
		} else if (Util.getLabelByCarType(c.getType()) == "R" && (Util.getLabelByCarType(lotDesign[i][j]) == "R" || Util.getLabelByCarType(lotDesign[i][j]) == "L")) {
			returnVal = true;
		} else if (Util.getLabelByCarType(c.getType()) == "L" && (Util.getLabelByCarType(lotDesign[i][j]) == "L")) {
			returnVal = true;
		}
		return returnVal;
	}

	/**
	 * Attempts to park a car in the lot. Parking is successful if a suitable parking spot
	 * is available in the lot. If some suitable spot is found (anywhere in the lot), the car
	 * is parked at that spot with the indicated timestamp and the method returns "true".
	 * If no suitable spot is found, no parking action is taken and the method simply returns
	 * "false"
	 * 
	 * @param c is the car to be parked
	 * @param timestamp is the simulation time at which parking is attempted for car c 
	 * @return true if c is successfully parked somwhere in the lot, and false otherwise
	 */
	public boolean attemptParking(Car c, int timestamp) {

		for (int i = 0; i < lotDesign.length; i++) {
			for (int j = 0; j < lotDesign[i].length; j++) {
				if (canParkAt(i, j, c)) {
					park(i, j, c, timestamp);
					return true;
				}
			}
		}
		
		return false;

	}

	/**
	 * @return the total capacity of the parking lot excluding spots that cannot be
	 *         used for parking (i.e., excluding spots that point to CarType.NA)
	 */
	public int getTotalCapacity() {
		int returnVal = 0;
		for (int i = 0; i < lotDesign.length; i++) {
			for (int j = 0; j < lotDesign[i].length; j++) {
				if (Util.getLabelByCarType(lotDesign[i][j]) != "N") {
					returnVal++;
				}
			}
		}
		return returnVal;
	}

	/**
	 * @return the total occupancy of the parking lot
	 */
	public int getTotalOccupancy() {
		int returnVal = 0;
		for (int i = 0; i < occupancy.length; i++) {
			for (int j = 0; j < occupancy[i].length; j++) {
				if (occupancy[i][j] != null) {
					returnVal++;
				}
			}
		}
		return returnVal;
	}

	private void calculateLotDimensions(String strFilename) throws Exception {
		Scanner scanner = new Scanner(new File(strFilename));

		this.numRows = 0;
		while (scanner.hasNext()) {
			String str = scanner.nextLine();
			str = str.replace(" ", "");
			str = str.replace(SEPARATOR, "");
			if (str.length() > 0) {
				this.numSpotsPerRow = str.length();
				this.numRows++;
			}
		}

		scanner.close();
	}

	private void populateDesignFromFile(String strFilename) throws Exception {
		Scanner scanner = new Scanner(new File(strFilename));

		int i = 0;
		while (scanner.hasNext()) {
			String str = scanner.nextLine();
			str = str.replace(" ", "");
			str = str.replace(SEPARATOR, "");
			if (str.length() > 0) {
				for (int j = 0; j < str.length() - 1; j++) {
					this.lotDesign[i][j] = Util.getCarTypeByLabel(Character.toString(str.charAt(j)));						}
					i++;
				
			}
		}

		scanner.close();
	}

	/**
	 * NOTE: This method is complete; you do not need to change it.
	 * @return String containing the parking lot information
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("==== Lot Design ====").append(System.lineSeparator());

		for (int i = 0; i < lotDesign.length; i++) {
			for (int j = 0; j < lotDesign[0].length; j++) {
				buffer.append((lotDesign[i][j] != null) ? Util.getLabelByCarType(lotDesign[i][j])
						: Util.getLabelByCarType(CarType.NA));
				if (j < numSpotsPerRow - 1) {
					buffer.append(", ");
				}
			}
			buffer.append(System.lineSeparator());
		}

		buffer.append(System.lineSeparator()).append("==== Parking Occupancy ====").append(System.lineSeparator());

		for (int i = 0; i < occupancy.length; i++) {
			for (int j = 0; j < occupancy[0].length; j++) {
				buffer.append(
						"(" + i + ", " + j + "): " + ((occupancy[i][j] != null) ? occupancy[i][j] : "Unoccupied"));
				buffer.append(System.lineSeparator());
			}

		}
		return buffer.toString();
	}
}