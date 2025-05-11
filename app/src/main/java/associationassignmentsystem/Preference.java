package associationassignmentsystem;

public class Preference {

    private Service _service;
    private int _rank; // 1 for first choice, 2 for second, etc.

    public Preference(Service service, int rank) {
        
        if (rank <= 0) {
            throw new IllegalArgumentException("Preference rank must be a positive integer.");
        }
        this._service = service;
        this._rank = rank;
    }

    // Getters
    public Service getService() {
        return _service;
    }

    public int getRank() {
        return _rank;
    }

    // You might want to add other methods later, for example,
    // methods to compare preferences or check if two preferences are for the same service.

    // Example: A method to get the cost factor for this preference if assigned (for the optimization algorithm)
    public int getCostFactor() {
        // (i-1)² if assigned to i-th preferred service
        return (this._rank - 1) * (this._rank - 1);
    }
}
