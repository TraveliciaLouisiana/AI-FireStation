package operator.selection;

import ga.Individual;
import java.util.List;
import java.util.Random;

// aturan main buat semua seleksi. pilih satu parent dari populasi (udah urut, fitness paling gede di indeks 0)
public interface SelectionOperator {
    Individual select(List<Individual> population, Random random);
}
