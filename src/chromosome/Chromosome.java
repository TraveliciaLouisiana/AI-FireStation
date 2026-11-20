package chromosome; // class ini berada di package chromosome


/**
 * Class Chromosome: untuk merepresentasikan semua kromosom pada Genetic Algorithm.
 * Setiap kromosom harus dapat diubah menjadi indeks sel kosong, divalidasi, dan disalin.
 */
public interface Chromosome {
    // menghasilkan indeks sel kosong yang dipilih sebagai lokasi Fire Station
    int[] decode();

    // memeriksa apakah kromosom memiliki tepat p indeks yang valid dan berbeda
    boolean isValid();

    // membuat salinan kromosom agar parent tidak ikut berubah saat reproduksi
    Chromosome copy();

    // crossover dan mutasi ada di package operator (operator.crossover dan operator.mutation)
}
