package experiment; // class ini berada di package experiment

//class untuk menyimpan informasi setiap peta 

public class InfoPeta {
    //menyimpan informasi dasar dari sebuah peta.
    public String tahap;                //tahap peta: TAHAP1, TAHAP2, atau PENGUJIAN
    public String file;                 //lokasi file peta
    public String ukuran;               //ukuran peta: tiny, small, normal, atau large
    public String tipe;                 //jenis peta: ACAK, CLUSTER, atau LABIRIN
    public int baris, kolom, p;         //jumlah baris, kolom, dan fire station (FS)
    public int rumah, pohon, kosong;    //jumlah rumah, pohon, dan sel kosong
}
