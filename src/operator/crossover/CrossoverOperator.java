package operator.crossover;

import chromosome.Chromosome;
import map.Grid;
import java.util.Random;

// aturan main buat semua crossover: hasilnya satu anak yang udah valid (udah di-repair, pas p fire station).
// sengaja balikin Chromosome, bukan Individual, biar fitness-nya cuma dihitung sekali aja di engine.
public interface CrossoverOperator {
    Chromosome crossover(Chromosome a, Chromosome b, Grid grid, Random random);
}
