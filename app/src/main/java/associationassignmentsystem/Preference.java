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
}
