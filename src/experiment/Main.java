
package experiment; // class ini berada di package experiment

import java.io.File; // untuk mengakses file dan folder
import java.util.*; // untuk menggunakan List, ArrayList, Map, HashMap, dan Arrays
import map.MapReader; // untuk membaca file peta
import map.Grid; // untuk menyimpan data peta yang sudah dibaca

//class untuk menjalankan Tahap 1, Tahap 2A, Tahap 2B, dan Pengujian
public class Main {

    //inisiasi npm ketua sebagai seed 
    static final long NPM_KETUA = 6182401012L;

    public static void main(String[] args) throws Exception {

        //membuat Map untuk menyimpan argumen yang diberikan saat program dijalankan
        Map<String, String> argumen = new HashMap<>();

        //looping untuk membaca setiap argumen yang dimasukkan
        for (String s : args) {
            int posisi = s.indexOf('=');

            //memisahkan nama argumen dan nilainya berdasarkan tanda =
            if (posisi > 0) {
                String nama = s.substring(0, posisi);
                String nilai = s.substring(posisi + 1);
                argumen.put(nama, nilai);

            }
        }

        //ambil tahap eksperimen, jika tidak diisi maka nilainya kosong
        String tahap = "";
        if (argumen.containsKey("tahap")) {
            tahap = argumen.get("tahap");
        }

        //ambil nama folder peta, jika tidak diisi menggunakan TestCases
        String folder = "TestCases";
        if (argumen.containsKey("peta")) {
            folder = argumen.get("peta");
        }

        //menentukan nama file CSV untuk menyimpan hasil eksperimen
        String out = "hasil_" + tahap + ".csv";
        if (argumen.containsKey("out")) {
            out = argumen.get("out");
        }

        int jumlahSeed = 30; //jumlah seed yang akan digunakan

        if (tahap.equals("2a")) {
            jumlahSeed = 10;
        } else if (tahap.equals("uji")) {
            jumlahSeed = 10;
        }

        // jumlah seed ditentukan melalui argumen
        if (argumen.containsKey("seeds")) {
            jumlahSeed = Integer.parseInt(argumen.get("seeds"));
        }

        //membuat 30 seed berdasarkan NPM ketua
        int[] semuaSeed = SeedGenerator.buat(NPM_KETUA, 30);

        //membuat array untuk simpan seed yang akan digunakan
        int[] seed = new int[jumlahSeed];

        //looping untuk ambil seed
        for (int i = 0; i < jumlahSeed && i < semuaSeed.length; i++) {
            seed[i] = semuaSeed[i];
        }

        //simpan seluruh 30 seed ke file seeds.txt 
        SeedGenerator.simpan(semuaSeed, "seeds.txt");

        //list untuk simpan daftar peta sesuai dengan tahap eksperimen
        List<InfoPeta> peta = pilihPeta(folder, tahap);

        //membuat seluruh 16 kombinasi model GA
        List<KonfigurasiModel> semuaModel = ModelFactory.buatSemua();

        //membuat daftar model yang nantinya akan dijalankan
        List<KonfigurasiModel> model = new ArrayList<>();

        //memeriksa apakah pengguna memilih model tertentu
        if (argumen.containsKey("model")) {

            //ambil daftar ID model yang dipisahkan dengan tanda koma
            String[] daftarId = argumen.get("model").split(",");

            //looping untuk membuat setiap ID menjadi objek KonfigurasiModel
            for (String id : daftarId) {
                model.add(KonfigurasiModel.dariId(id.trim()));
            }

        } else {
            //menggunakan seluruh 16 model jika tidak ada model yang dipilih
            model.addAll(semuaModel);
        }

        //membuat parameter GA (nilai default)
        int populasi = 100;
        double pc = 0.8;
        double pm = 0.05;
        double elitisme = 0.2;

        // Mengambil nilai parameter dari argumen jika pengguna mengubahnya
        if (argumen.containsKey("pop")) {
            populasi = Integer.parseInt(argumen.get("pop"));
        }

        if (argumen.containsKey("pc")) {
            pc = Double.parseDouble(argumen.get("pc"));
        }

        if (argumen.containsKey("pm")) {
            pm = Double.parseDouble(argumen.get("pm"));
        }

        if (argumen.containsKey("elit")) {
            elitisme = Double.parseDouble(argumen.get("elit"));
        }

        // Membuat objek parameter GA berdasarkan nilai yang sudah ditentukan
        ParameterGA dasar = new ParameterGA(populasi, pc, pm, elitisme);

        // Membuat daftar untuk menyimpan kombinasi parameter yang akan diuji
        List<ParameterGA> daftarParam = new ArrayList<>();

        //membuat variabel label untuk membedakan nama setiap tahap di CSV
        String label;

        //memastikan eksperimen yang dijalankan adalah Tahap 2A
        if (tahap.equals("2a")) {

            //ambil nama parameter yang akan diuji
            String nama = argumen.get("param");

            //ambil lima nilai parameter yang dipisahkan dengan tanda koma
            String[] daftarNilai = argumen.get("nilai").split(",");

            //looping untuk buat kombinasi parameter untuk setiap nilai yang diuji
            for (String nilai : daftarNilai) {
                double angka = Double.parseDouble(nilai);
                daftarParam.add(dasar.dengan(nama, angka));
            }

            // Memberikan nama tahap berdasarkan parameter yang diuji
            label = "TAHAP2A_" + nama;

        } else if (tahap.equals("2b")) {

            // Mengambil daftar nilai populasi yang akan diuji
            String[] daftarPopulasi = argumen.get("poplist").split(",");

            // Mengambil daftar nilai crossover yang akan diuji
            String[] daftarCrossover = argumen.get("pclist").split(",");

            // Menggabungkan setiap nilai populasi dengan setiap nilai crossover
            for (String p : daftarPopulasi) {
                for (String c : daftarCrossover) {

                    // Membuat parameter GA dengan kombinasi populasi dan crossover
                    // Nilai mutasi dan elitisme tetap menggunakan nilai dasar
                    int nilaiPopulasi = Integer.parseInt(p);
                    double nilaiCrossover = Double.parseDouble(c);

                    ParameterGA parameter = new ParameterGA(nilaiPopulasi, nilaiCrossover, dasar.pm, dasar.elitisme);
                    daftarParam.add(parameter);
                }
            }

            // nama untuk Tahap 2B
            label = "TAHAP2B";

        } else {

            // Tahap 1 dan Pengujian hanya menggunakan satu kombinasi parameter
            daftarParam.add(dasar);

            // Menentukan nama tahap yang akan ditulis ke CSV
            if (tahap.equals("uji")) {
                label = "PENGUJIAN";
            } else {
                label = "TAHAP1";
            }
        }

        // Membuat daftar untuk menampung seluruh percobaan yang akan dijalankan
        List<Experiment> daftar = new ArrayList<>();

        // Menggabungkan model, peta, parameter, dan seed menjadi daftar eksperimen
        for (KonfigurasiModel m : model) {
            List<Experiment> eksperimen = PenyusunEksperimen.susun(label, peta, m, daftarParam, seed);
            daftar.addAll(eksperimen);
        }

        // Menampilkan jumlah seluruh percobaan sebelum program dijalankan
        System.out.println(label + ": " + daftar.size() + " percobaan");
        System.out.println("Jumlah model     : " + model.size());
        System.out.println("Jumlah peta      : " + peta.size());
        System.out.println("Jumlah parameter : " + daftarParam.size());
        System.out.println("Jumlah seed      : " + seed.length);

        // Membuat objek ExperimentRunner untuk menjalankan seluruh percobaan
        // AdapterGAEngine menjalankan algoritma GA, sedangkan PenulisCsv menyimpan hasilnya
        ExperimentRunner runner = new ExperimentRunner(new AdapterGAEngine(), new PenulisCsv(out));

        // Menjalankan seluruh eksperimen dan menyimpan hasilnya ke file CSV
        // Proses resume tetap ditangani oleh ExperimentRunner
        runner.jalankan(daftar);
    }

    // Method untuk memilih file peta berdasarkan tahap eksperimen
    static List<InfoPeta> pilihPeta(String folder, String tahap) throws Exception {

        // Membuat daftar untuk menyimpan informasi peta yang akan digunakan
        List<InfoPeta> hasil = new ArrayList<>();

        // Daftar peta yang digunakan pada Tahap 1
        String[] nomor1 = {"acak_01", "acak_02", "klaster_01", "labirin_01"};

        // Daftar peta yang digunakan pada Tahap 2A dan Tahap 2B
        // Peta berbeda dari Tahap 1
        String[] nomor2 = {"acak_03", "acak_04", "klaster_02", "labirin_02"};

        // Daftar enam peta yang digunakan untuk pengujian
        String[] nomorUji = {"acak_01", "acak_02", "klaster_01", "klaster_02", "labirin_01", "labirin_02"};

        // Memeriksa apakah tahap yang dijalankan adalah pengujian
        if (tahap.equals("uji")) {

            // Pengujian hanya menggunakan peta berukuran 80 x 80
            for (String nomor : nomorUji) {
                hasil.add(infoDari(folder, 80, nomor, "PENGUJIAN"));
            }

        } else {

            // Memilih daftar peta berdasarkan tahap eksperimen
            String[] nomor;

            if (tahap.equals("1")) {
                nomor = nomor1;
            } else {
                nomor = nomor2;
            }

            // Menggunakan tiga ukuran peta: 20, 40, dan 60
            int[] ukuranPeta = {20, 40, 60};

            // Membaca seluruh kombinasi ukuran dan jenis peta
            for (int sisi : ukuranPeta) {
                for (String nama : nomor) {

                    // Menentukan nama tahap untuk informasi peta
                    String namaTahap;

                    if (tahap.equals("1")) {
                        namaTahap = "TAHAP1";
                    } else {
                        namaTahap = "TAHAP2";
                    }

                    // Mengambil informasi peta dan memasukkannya ke daftar
                    InfoPeta info = infoDari(folder, sisi, nama, namaTahap);
                    hasil.add(info);
                }
            }
        }

        // Mengembalikan seluruh informasi peta yang sudah dipilih
        return hasil;
    }

    // Method untuk membaca satu file peta dan mengambil informasinya
    static InfoPeta infoDari(String folder, int sisi, String nomor, String tahap) throws Exception {

        // Membuat alamat file berdasarkan folder, ukuran, dan nama peta
        // Contoh: TestCases/n20_acak_01.txt
        String path = folder + File.separator + "n" + sisi + "_" + nomor + ".txt";

        // Membaca file peta dan menyimpan isinya ke objek Grid
        Grid g = MapReader.read(path);

        // Membuat objek InfoPeta untuk menyimpan informasi peta
        InfoPeta info = new InfoPeta();

        // Mengisi identitas dasar peta
        info.tahap = tahap;
        info.file = path;
        info.baris = g.m;
        info.kolom = g.n;
        info.p = g.p;

        // Menentukan kategori ukuran peta berdasarkan panjang sisinya
        if (sisi == 20) {
            info.ukuran = "tiny";
        } else if (sisi == 40) {
            info.ukuran = "small";
        } else if (sisi == 60) {
            info.ukuran = "normal";
        } else {
            info.ukuran = "large";
        }

        // Mengambil jenis peta dari nama file
        // Contoh: acak_01 menghasilkan jenis acak
        String jenis = nomor.substring(0, nomor.indexOf('_'));

        // Mengubah nama jenis peta menjadi format yang digunakan di CSV
        if (jenis.equals("acak")) {
            info.tipe = "ACAK";
        } else if (jenis.equals("klaster")) {
            info.tipe = "CLUSTER";
        } else {
            info.tipe = "LABIRIN";
        }

        // Mengambil jumlah rumah dari data peta
        info.rumah = g.numHouses;

        // Mengambil jumlah sel kosong dari data peta
        info.kosong = g.numEmpty;

        // Menghitung jumlah pohon dari total sel dikurangi rumah dan sel kosong
        info.pohon = (g.m * g.n) - g.numHouses - g.numEmpty;

        // Mengembalikan informasi peta yang sudah lengkap
        return info;
    }
}
