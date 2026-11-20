package chromosome; // class ini berada di package chromosome

import map.Grid; // pakai class Grid dari package map
import java.util.Random; 

/**
 * Class ini digunakan untuk membuat kromosom dalam bentuk bit string
 * bit string adalah kumpulan angka 0 dan 1 yang mewakili penempatan Fire Station
 * angka 1 berarti sel dipilih untuk Fire Station, sedangkan 0 berarti tidak dipilih
 */
public class BitStringChromosome implements Chromosome {

    private final Grid grid; // menyimpan data peta, seperti jumlah sel kosong dan jumlah Fire Station yang dibutuhkan
    private final int[] genes; // menyimpan kromosom dalam bentuk array yang berisi angka 0 dan 1

    // constructor ini digunakan untuk membuat kromosom awal secara acak
    public BitStringChromosome(Grid grid, Random random) {

        // memastikan objek Grid dan Random sudah diberikan agar tidak terjadi error saat digunakan
        if (grid == null || random == null) {
            throw new IllegalArgumentException("Grid dan Random tidak boleh null.");
        }

        // memastikan jumlah Fire Station tidak kurang dari 0 dan tidak melebihi jumlah sel kosong
        if (grid.p < 0 || grid.p > grid.numEmpty) {
            throw new IllegalArgumentException("Jumlah Fire Station harus antara 0 dan jumlah sel kosong.");
        }

        this.grid = grid; // Menyimpan data peta agar bisa digunakan oleh method lain di class ini.

        // membuat array untuk menyimpan semua indeks sel kosong
        int[] positions = new int[grid.numEmpty];

        // engisi array dengan indeks sel kosong mulai dari 0 sampai jumlah sel kosong dikurangi 1
        for (int i = 0; i < positions.length; i++) {
            positions[i] = i;
        }

        // mengacak urutan indeks supaya posisi Fire Station tidak selalu sama
        for (int i = positions.length - 1; i > 0; i--) {

            // mengambil indeks secara acak dari 0 sampai i
            int j = random.nextInt(i + 1);

            // menyimpan sementara nilai pada posisi i
            int temp = positions[i];

            // memindahkan nilai pada posisi j ke posisi i
            positions[i] = positions[j];

            // memindahkan nilai sebelumnya dari posisi i ke posisi j
            positions[j] = temp;
        }

        // membuat array kromosom dengan panjang sebanyak jumlah sel kosong
        // secara otomatis semua elemennya memiliki nilai 0
        this.genes = new int[grid.numEmpty];

        // mengubah sebanyak p posisi pertama menjadi 1
        // dengan begitu, jumlah Fire Station yang ditempatkan sesuai dengan kebutuhan
        for (int i = 0; i < grid.p; i++) {
            genes[positions[i]] = 1;
        }
    }

    // Constructor ini digunakan untuk membuat kromosom dari array yang sudah tersedia
    // biasanya digunakan setelah proses crossover atau saat membuat salinan kromosom
    private BitStringChromosome(Grid grid, int[] genes) {

        this.grid = grid; // menyimpan data peta yang digunakan oleh kromosom

        // menyalin isi array agar perubahan pada array dari luar tidak langsung mengubah kromosom ini
        this.genes = genes.clone();
    }

    // method ini digunakan untuk membuat kromosom dari array bit yang sudah disiapkan
    // method ini bisa dipanggil oleh class lain, misalnya Repair
    public static BitStringChromosome fromGenes(Grid grid, int[] genes) {

        // membuat objek BitStringChromosome baru berdasarkan data grid dan array genes
        return new BitStringChromosome(grid, genes);
    }

    // method ini digunakan untuk mengambil isi kromosom dalam bentuk array
    public int[] getGenes() {

        // mengembalikan salinan array supaya array asli tidak bisa diubah langsung dari luar class
        return genes.clone();
    }

    // method decode digunakan untuk mengubah kromosom menjadi daftar indeks sel yang dipilih
    // hasilnya adalah indeks sel yang memiliki nilai 1
    @Override
    public int[] decode() {

        // membuat array untuk menyimpan indeks sel yang dipilih sebagai lokasi Fire Station
        // panjangnya disesuaikan dengan jumlah Fire Station yang dibutuhkan
        int[] selected = new int[grid.p];

        // digunakan untuk menghitung berapa banyak indeks yang sudah dimasukkan ke array selected
        int position = 0;

        // memeriksa setiap bit pada kromosom dari awal sampai akhir
        for (int i = 0; i < genes.length; i++) {

            // jika nilai bit adalah 1, berarti sel tersebut dipilih untuk Fire Station
            if (genes[i] == 1) {

                // memastikan posisi array selected masih tersedia sebelum memasukkan indeks
                if (position < selected.length) {

                    // menyimpan indeks sel yang memiliki nilai 1
                    selected[position] = i;
                }

                // menambah jumlah bit 1 yang sudah ditemukan
                position++;
            }
        }

        // memeriksa apakah jumlah bit 1 sudah sesuai dengan jumlah Fire Station yang dibutuhkan
        if (position != grid.p) {

            // menghentikan proses jika jumlah Fire Station pada kromosom tidak sesuai
            throw new IllegalStateException("Bit string tidak valid; jalankan repair sebelum decode.");
        }

        // mengembalikan daftar indeks sel yang dipilih untuk Fire Station
        return selected;
    }

    // method ini digunakan untuk memeriksa apakah kromosom sudah memenuhi aturan 
    @Override
    public boolean isValid() {

        // panjang kromosom harus sama dengan jumlah sel kosong pada peta
        if (genes.length != grid.numEmpty) {
            return false;
        }

        // variabel untuk menghitung jumlah bit yang bernilai 1
        int count = 0;

        // memeriksa setiap nilai bit pada kromosom
        for (int bit : genes) {

            // setiap bit hanya boleh memiliki nilai 0 atau 1
            if (bit != 0 && bit != 1) {
                return false;
            }

            // jika bit bernilai 1, berarti ada satu Fire Station yang ditempatkan
            if (bit == 1) {
                count++;
            }
        }

        // kromosom valid jika jumlah bit 1 sama dengan jumlah Fire Station yang dibutuhkan
        return count == grid.p;
    }

    // method copy digunakan untuk membuat salinan kromosom
    @Override
    public Chromosome copy() {

        // membuat objek baru dengan isi genes yang sama seperti kromosom asli
        return new BitStringChromosome(grid, genes);
    }

    // method ini digunakan untuk menampilkan isi kromosom dalam bentuk teks
    @Override
    public String toString() {

        // mengubah array genes menjadi bentuk String agar mudah dilihat saat program dijalankan
        return java.util.Arrays.toString(genes);
    }
}
