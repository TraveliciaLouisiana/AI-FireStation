package chromosome; // class ini berada di package chromosome

import map.Grid; // pakai class Grid dari package map
import java.util.Random; 

/* class IndexListChromosome: representasi kromosom yang menyimpan p indeks sel kosong tempat fire station berada */
public class IndexListChromosome implements Chromosome { // mendeklarasikan class indexlistchromosome yang mengimplement interface chromosome

    private final Grid grid; // menyimpan referensi objek grid
    private final int[] genes; // menyimpan array gen berupa indeks sel kosong

    // membuat kromosom acak yang berisi tepat p indeks sel kosong yang berbeda
    public IndexListChromosome(Grid grid, Random random) { // constructor utama
        if (grid == null || random == null) { // validasi apakah grid atau random bernilai null
            throw new IllegalArgumentException("grid dan random tidak boleh null"); // lempar error jika null
        } 
        if (grid.p < 0 || grid.p > grid.numEmpty) { // validasi jumlah fire station terhadap sel kosong
            throw new IllegalArgumentException("jumlah fire station harus antara 0 dan jumlah sel kosong"); // lempar error jika tidak valid
        } 
        this.grid = grid; // mengisi variabel grid
        int[] shuffled = new int[grid.numEmpty]; // membuat array penampung seluruh sel kosong
        for (int i = 0; i < shuffled.length; i++) { // perulangan mengisi array dengan nomor indeks
            shuffled[i] = i; // masukkan indeks i ke array shuffled
        } 

        // fisher-yates shuffle menghasilkan urutan kandidat yang acak
        for (int i = shuffled.length - 1; i > 0; i--) { // perulangan untuk mengacak array
            int j = random.nextInt(i + 1); // pilih indeks acak j dari 0 sampai i
            int temp = shuffled[i]; // simpan nilai sementara dari shuffled[i]
            shuffled[i] = shuffled[j]; // tukar posisi nilai i dengan j
            shuffled[j] = temp; // kembalikan nilai sementara ke posisi j
        } 
        this.genes = new int[grid.p]; // inisialisasi array genes dengan ukuran p fire station
        System.arraycopy(shuffled, 0, this.genes, 0, grid.p); // ambil p elemen pertama dari array yang sudah diacak
        java.util.Arrays.sort(this.genes); // urutkan gen agar rapi
    } 

    // constructor internal untuk membuat kromosom dari gen hasil operasi genetik
    private IndexListChromosome(Grid grid, int[] genes) { // constructor sekunder
        this.grid = grid; // mengisi variabel grid
        this.genes = genes.clone(); // menyalin array gen agar aman
    } 

    // membuat kromosom dari indeks yang sudah diperbaiki oleh repair
    public static IndexListChromosome fromGenes(Grid grid, int[] genes) { // method pembuat objek dari gen luar
        return new IndexListChromosome(grid, genes); // kembalikan objek baru
    } 

    @Override
    public int[] decode() { // method decode untuk melihat isi gen
        return genes.clone(); // kembalikan salinan array gen
    } 

    @Override
    public boolean isValid() { // method mengecek apakah kromosom valid
        if (genes.length != grid.p) { // cek apakah panjang gen tidak sesuai dengan p
            return false; // kembalikan false jika tidak sesuai
        } 
        boolean[] used = new boolean[grid.numEmpty]; // buat penampung status penggunaan sel
        for (int gene : genes) { // perulangan mengecek setiap gen
            if (gene < 0 || gene >= grid.numEmpty || used[gene]) { // cek apakah gen di luar batas atau duplikat
                return false; // kembalikan false jika ada yang tidak valid
            } 
            used[gene] = true; // tandai sel sudah digunakan
        } 
        return true; // kembalikan true jika semuanya valid
    } 

    @Override
    public Chromosome copy() { // method untuk menyalin kromosom
        return new IndexListChromosome(grid, genes); // kembalikan objek kromosom baru dengan gen yang sama
    } 
    @Override
    public String toString() { // method mengubah objek ke bentuk string
        return java.util.Arrays.toString(genes); // kembalikan representasi string dari array gen
    } 
} 
