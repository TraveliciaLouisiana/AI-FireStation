package experiment; // class ini berada di package experiment

import java.io.*;
import java.util.*;

// Membuat 30 seed dari NPM Ketua (6182401012)
// Hasil akan disimpan di seeds.txt

public class SeedGenerator{

    //Method 1: Membuat sejumlah seed berdasarkan NPM Ketua
    public static int[] buat(long npmKetua, int jumlah){
        Random random = new Random(npmKetua);    //buat objek random
        int batas = 65536;                      //batas angka seed 2^16   
        int[] seed = new int[jumlah];           //array untuk menyimpan hasil seed    
        
        //loop untuk menghasilkan seed sebanyak "jumlah"
        for(int i = 0; i < jumlah; i++){        
            seed[i] = random.nextInt(batas+1);
        }

        return seed;                            //kembalikan array yang berisi seluruh seed
    }

    //Method 2: Menyimpan array seed ke file
    public static void simpan(int[] seed, String path) throws IOException{
        //Membuka file untuk ditulis, jika file belum ada maka akan dibuat, jika sudah ada isinya ditimpa
        try(PrintWriter out = new PrintWriter(new FileWriter(path))){
            
            //Looping untuk ambil setiap nilai seed dari array
            for(int s : seed){

                //menuliskan satu seed ke dalam file
                out.println(s); //satu seed per satu baris
            }
        }
    }
}
