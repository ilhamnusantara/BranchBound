/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package brancgbounds;
import java.util.*;
/**
 *
 * @author ilham-07010
 */
public class BranchBounds {
static int N = 4; 
    // final_path[] menyimpan solusi terakhir 
    // jalur salesma
    static int final_path[] = new int[N + 1]; 
  
    // visited[] melacak node yang sudah dikunjungi
    // di jalur tertentu 
    static boolean visited[] = new boolean[N]; 
  
    // Menyimpan bobot minimum terakhir dari tur terpendek
    static int final_res = Integer.MAX_VALUE; 
  
    // Berfungsi untuk menyalin solusi sementara ke solusi terakhir
    static void copyToFinal(int curr_path[]) 
    { 
        for (int i = 0; i < N; i++) 
            final_path[i] = curr_path[i]; 
        final_path[N] = curr_path[0]; 
    } 
  
    // Berfungsi untuk menemukan biaya tepi minimum / low cost 
    // memiliki akhir di simpul i
    static int firstMin(int adj[][], int i) 
    { 
        int min = Integer.MAX_VALUE; 
        for (int k = 0; k < N; k++) 
            if (adj[i][k] < min && i != k) 
                min = adj[i][k]; 
        return min; 
    } 
  
    // berfungsi untuk menemukan biaya tepi minimum / low cost kedua
    // memiliki akhir di simpul i
    static int secondMin(int adj[][], int i) 
    { 
        int first = Integer.MAX_VALUE, second = Integer.MAX_VALUE; 
        for (int j=0; j<N; j++) 
        { 
            if (i == j) 
                continue; 
  
            if (adj[i][j] <= first) 
            { 
                second = first; 
                first = adj[i][j]; 
            } 
            else if (adj[i][j] <= second && 
                    adj[i][j] != first) 
                second = adj[i][j]; 
        } 
        return second; 
    } 
  
    // fungsi yang diambil sebagai argumen : 
    // curr_bound -> batas bawah dari simpul root 
    // curr_weight-> menyimpan bobot jalur sejauh ini
    // level-> level saat ini sambil bergerak di space tree ( pencarian )
    // curr_path[] -> tempat solusi disimpan yang nanti nya akan disalin ke final_path[]
    static void TSPRec(int adj[][], int curr_bound, int curr_weight, 
                int level, int curr_path[]) 
    { 
        // base case adalah ketika kita telah mencapai level N yang berarti kita telah
           // membahas semua node satu kali
        if (level == N) 
        { 
            // memeriksa dengan if apakah ada tepi dari titik terakhir di jalur kembali ke
               // titik pertama
            if (adj[curr_path[level - 1]][curr_path[0]] != 0) 
            { 
                    // curr_res memiliki bobot total dari solusi yang kita dapatkan 
                int curr_res = curr_weight + 
                        adj[curr_path[level-1]][curr_path[0]]; 
      
                // Perbarui final_res dan curr_res jika hasil saat ini lebih baik
                if (curr_res < final_res) 
                { 
                    copyToFinal(curr_path); 
                    final_res = curr_res; 
                } 
            } 
            return; 
        } 
  
        // for digunakan untuk melakukan iterasi semua simpul space tree dengan pencarian secara
           // rekrusif
        for (int i = 0; i < N; i++) 
        { 
            //Pertimbangkan simpul berikutnya jika tidak sama (diagonal entri dalam matrik
              // adjaceny dan belum dikunjungi)
            if (adj[curr_path[level-1]][i] != 0 && 
                    visited[i] == false) 
            { 
                int temp = curr_bound; 
                curr_weight += adj[curr_path[level - 1]][i]; 
  
                // perhitungan yang berbeda dari curr_bound untuk level 2
                   // pengujian setelah level lainnya
                if (level==1) 
                curr_bound -= ((firstMin(adj, curr_path[level - 1]) + 
                                firstMin(adj, i))/2); 
                else
                curr_bound -= ((secondMin(adj, curr_path[level - 1]) + 
                                firstMin(adj, i))/2); 
  
                // curr_bound + curr_weight adalah batas bawah (low bound) yang sebenarnya untuk simpul
                   // yang telah kita inginkan 
                // Jika batas bawah (lower bound) saat ini < final_res, 
                    //kita perlu menjelajahi lebih lanjut simpulnya
                if (curr_bound + curr_weight < final_res) 
                { 
                    curr_path[level] = i; 
                    visited[i] = true; 
  
                    // panggil method TSPRec untuk level selanjutnya 
                    TSPRec(adj, curr_bound, curr_weight, level + 1, 
                        curr_path); 
                } 
  
                // setelah itu kita harus memangkas node dengan mengatur ulang semua perubahan 
                    //ke curr_weight and curr_bound 
                curr_weight -= adj[curr_path[level-1]][i]; 
                curr_bound = temp; 
  
                // setel ulang juga array yang dikunjungi 
                Arrays.fill(visited,false); 
                for (int j = 0; j <= level - 1; j++) 
                    visited[curr_path[j]] = true; 
            } 
        } 
    } 
  
    // Fungsi dibawah ini untuk mengatur final_path[]  
    static void TSP(int adj[][]) 
    { 
        int curr_path[] = new int[N + 1]; 
  
        // hitung batas bawah (lower bound)untu simpul root dengan menggunakan 
            //cara 1/2 * (jumlah firstMin + secondMin) untuk semua sisi. 
        // Dan juga inisialisasikan curr_path dan visite[]
        int curr_bound = 0; 
        Arrays.fill(curr_path, -1); 
        Arrays.fill(visited, false); 
  
        // Hitung batas awal (initial bound) 
        for (int i = 0; i < N; i++) 
            curr_bound += (firstMin(adj, i) + 
                        secondMin(adj, i)); 
  
        // membulatkan batas bawah (lower bound) ke angka (integer) 
        curr_bound = (curr_bound==1)? curr_bound/2 + 1 : 
                                    curr_bound/2; 
  
        // Kita mulau dari titik 1 jadi titik pertama dalam curr_path[] adalah 0 
        visited[0] = true; 
        curr_path[0] = 0; 
  
        // Panggil method TSPRec untuk curr_weight sama dengan 0 dan level 1
        TSPRec(adj, curr_bound, 0, 1, curr_path); 
    } 
      
    // Menjalankan kode
    public static void main(String[] args)  
    { 
        //Matrik penyesuaian untuk grafik yang diberikan
        int adj[][] = {{0, 10, 15, 20}, 
                        {10, 0, 35, 25}, 
                        {15, 35, 0, 30}, 
                        {20, 25, 30, 0}    }; 
  
        TSP(adj); 
  
        System.out.printf("Minimum cost : %d\n", final_res); 
        System.out.printf("Path Taken : "); 
        for (int i = 0; i <= N; i++)  
        { 
            System.out.printf("%d ", final_path[i]); 
        } 
    } 
    
}
