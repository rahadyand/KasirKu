package Managers;
 
import java.util.*;
import Models.Product;

/**
 * ==========================================
 * CLASS MANAGER: INVENTORY MANAGER (HASHMAP)
 * ==========================================
 * Manajemen stok barang menggunakan HashMap<String, Product>.
 * Menggunakan ID produk sebagai key untuk pencarian O(1) rata-rata.
 */
public class InventoryManager {
    private Map<String, Product> databaseBarang;

    public InventoryManager() {
        // Inisialisasi menggunakan HashMap untuk mengelola data barang dengan ID sebagai key dan Product sebagai value
        this.databaseBarang = new HashMap<>();
    }

    public void tambahBarang(Product product) {
        if (product != null) {
            databaseBarang.put(product.getId(), product);
        }
    }

    /**
     * ========================================================================
     * ANALISIS STRUKTUR DATA & KOMPLEKSITAS WAKTU: PENCARIAN BARANG
     * ========================================================================
     * - Mengapa Menggunakan HashMap?
     *   HashMap menyimpan data dalam bentuk pasangan Key-Value. Dengan memetakan ID Barang 
     *   (sebagai Key) ke objek Product (sebagai Value), kita dapat langsung melompat 
     *   ke alamat memori barang tersebut melalui perhitungan hash code tanpa perlu 
     *   melakukan iterasi pencarian linear dari awal hingga akhir.
     * 
     * - Kompleksitas Waktu (Time Complexity):
     *   * Rata-rata (Average Case): O(1)/ Constant Time. Waktu pencarian barang tidak dipengaruhi 
     *     oleh jumlah total barang (N) yang ada di dalam database.
     *   * Kasus Terburuk (Worst Case): O(N) / Linear Time. Terjadi hanya jika terjadi hash collision 
     *     yang ekstrem (semua key menunjuk pada bucket yang sama). Java 8+ mengoptimalkan
     *     worst-case ini menjadi O(log N) menggunakan struktur Red Black Tree.
     */
    public Product cariBarang(String id) {
        return databaseBarang.get(id);
    }

    public Collection<Product> getAllProducts() {
        return databaseBarang.values();
    }
}
