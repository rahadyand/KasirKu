package Database;
 
import Models.Product;
import Managers.InventoryManager;

/**
 * ==========================================
 * CLASS DATABASE INITIALIZER
 * ==========================================
 * Mengelola inisialisasi semua data dummy/database produk.
 * Memisahkan logika data dari logika aplikasi utama.
 */
public class DatabaseInitializer {
    
    /**
     * Inisialisasi semua data dummy produk ke dalam inventory manager
     * @param inventory InventoryManager yang akan diisi dengan data
     */
    public static void initializeProducts(InventoryManager inventory) {
        // Inisialisasi Data Dummy Produk
        inventory.tambahBarang(new Product("P01", "Buku Tulis A5", 5000, 30));
        inventory.tambahBarang(new Product("P02", "Pensil 2B", 2500, 50));
        inventory.tambahBarang(new Product("P03", "Penghapus Karet", 1500, 20));
        inventory.tambahBarang(new Product("P04", "Penggaris Besi", 4500, 15));
        inventory.tambahBarang(new Product("P05", "Spidol Papan Tulis", 8000, 10));
        inventory.tambahBarang(new Product("P06", "Pulpen Hitam", 3000, 100));
        inventory.tambahBarang(new Product("P07", "Pulpen Biru", 3000, 100));
        inventory.tambahBarang(new Product("P08", "Pulpen Merah", 3000, 50));
        inventory.tambahBarang(new Product("P09", "Buku Tulis B5", 6000, 40));
        inventory.tambahBarang(new Product("P10", "Buku Gambar A4", 4000, 25));
        inventory.tambahBarang(new Product("P11", "Buku Gambar A3", 7500, 20));
        inventory.tambahBarang(new Product("P12", "Kertas HVS A4 70gr", 45000, 10));
        inventory.tambahBarang(new Product("P13", "Kertas HVS A4 80gr", 50000, 10));
        inventory.tambahBarang(new Product("P14", "Kertas HVS F4 70gr", 48000, 15));
        inventory.tambahBarang(new Product("P15", "Tipe-X", 5500, 30));
        inventory.tambahBarang(new Product("P16", "Rautan Pensil", 2000, 45));
        inventory.tambahBarang(new Product("P17", "Spidol Hitam", 7500, 25));
        inventory.tambahBarang(new Product("P18", "Spidol Biru", 7500, 20));
        inventory.tambahBarang(new Product("P19", "Spidol Merah", 7500, 15));
        inventory.tambahBarang(new Product("P20", "Spidol Kecil Hitam", 2000, 50));
        inventory.tambahBarang(new Product("P21", "Spidol Kecil Biru", 2000, 50));
        inventory.tambahBarang(new Product("P22", "Spidol Warna isi 12", 15000, 12));
        inventory.tambahBarang(new Product("P23", "Pensil Warna isi 12", 18000, 15));
        inventory.tambahBarang(new Product("P24", "Pensil Warna isi 24", 35000, 10));
        inventory.tambahBarang(new Product("P25", "Krayon isi 12", 14000, 20));
        inventory.tambahBarang(new Product("P26", "Krayon isi 24", 27000, 10));
        inventory.tambahBarang(new Product("P27", "Cat Air", 25000, 8));
        inventory.tambahBarang(new Product("P28", "Kuas Lukis Set", 12000, 15));
        inventory.tambahBarang(new Product("P29", "Palet Cat Air", 5000, 20));
        inventory.tambahBarang(new Product("P30", "Penggaris Plastik", 2000, 40));
    }
}
