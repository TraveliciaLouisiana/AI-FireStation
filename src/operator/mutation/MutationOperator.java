package operator.mutation;

import chromosome.Chromosome;
import map.Grid;
import java.util.Random;

// aturan main buat semua mutasi: pasti mutasi (peluang pm-nya urusan engine) dan balikin kromosom baru.
// kromosom aslinya nggak diutak-atik, jadi fitness yang udah kesimpan nggak bakal basi.
public interface MutationOperator {
    Chromosome mutate(Chromosome c, Grid grid, Random random);
}
