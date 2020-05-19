package query;

import model.Patient;

import java.util.Comparator;

class SortByLastName implements Comparator<Patient>
{

    @Override
    public int compare(Patient p1, Patient p2) {
        return p1.getLastName().compareTo(p2.getLastName());
    }
}
