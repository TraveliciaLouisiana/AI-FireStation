
package experiment; // class ini berada di package experiment

import ga.GAConfig; // konfigurasi algoritma GA
import ga.GeneticAlgorithmEngine; // mesin GA dari Orang 2
import ga.Individual; // menyimpan satu individu atau solusi GA
import map.DistanceMatrix; // menyimpan matriks jarak hasil BFS
import map.Grid; // menyimpan data peta
import map.MapReader; // membaca file peta
import java.util.HashMap; // tempat menyimpan data cache
import java.util.Map; // tipe data Map
import java.util.Random; // membuat angka acak berdasarkan seed

/*
 * AdapterGAEngine bertugas menghubungkan ExperimentRunner dengan mesin GA.
 * Class ini menjalankan satu percobaan dan mengambil hasilnya untuk CSV.
 */
public class AdapterGAEngine implements AdapterGA {

    //cache Grid agar file peta tidak perlu dibaca berulang kali.
    private final Map<String, Grid> cacheGrid = new HashMap<>();

    //cache DistanceMatrix agar perhitungan BFS tidak perlu diulang.
    private final Map<String, DistanceMatrix> cacheDist = new HashMap<>();

    @Override
    public HasilRun jalankanSatu(Experiment e) throws Exception {

        //mengambil data peta dari cache berdasarkan nama file.
        Grid g = cacheGrid.get(e.peta.file);

        // Jika peta belum pernah dibaca, baca dan simpan ke cache.
        if (g == null) {
            g = MapReader.read(e.peta.file);
            cacheGrid.put(e.peta.file, g);

            // Hitung jarak antar posisi menggunakan BFS.
            DistanceMatrix dm = new DistanceMatrix(g);
            cacheDist.put(e.peta.file, dm);
        }

        //mengambil matriks jarak yang sudah pernah disimpan
        DistanceMatrix dm = cacheDist.get(e.peta.file);

        // Buat konfigurasi GA berdasarkan data percobaan.
        GAConfig cfg = new GAConfig(
                e.param.populasi, e.budget, e.param.pc, e.param.pm, e.param.elitisme,
                100, 60000L, 3,
                GAConfig.RepresentationType.valueOf(e.model.representasi),
                GAConfig.SelectionType.valueOf(e.model.seleksi),
                GAConfig.CrossoverType.valueOf(e.model.crossover),
                GAConfig.MutationType.valueOf(e.model.mutasi)
        );

        //menajalankan GA menggunakan seed dari percobaan.
        Random random = new Random(e.seed);
        GeneticAlgorithmEngine mesin = new GeneticAlgorithmEngine(random, g, dm.dist, cfg);

        //menjalankan algoritma GA dan ambil individu terbaik.
        Individual terbaik = mesin.run();

        //variabel untuk menyimpan hasil percobaan.
        HasilRun h = new HasilRun();

        //mengambil informasi hasil dari mesin GA.
        h.cost = terbaik.getCost();
        h.evaluasiTerpakai = mesin.getEvaluationCount();
        h.generasi = mesin.getGenerationCount();
        h.alasanBerhenti = mesin.getStopReason();

        
        //String untuk simpan seluruh posisi Fire Station.
        String posisiFS = "";

        //ambil semua posisi Fire Station dari kromosom terbaik.
        int[] posisi = terbaik.getChromosome().decode();

        for (int f : posisi) {

            // Jika bukan posisi pertama, tambahkan pemisah titik koma.
            if (!posisiFS.equals("")) {
                posisiFS += ";";
            }

            //menambahkan koordinat x dan y ke dalam String.
            posisiFS += (g.emptyCol[f] + 1) + " " + (g.emptyRow[f] + 1);
        }

        //memasukan seluruh posisi Fire Station ke hasil percobaan.
        h.posisiFS = posisiFS;


        return h;
    }
}
