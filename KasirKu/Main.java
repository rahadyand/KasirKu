import java.util.*;

// Import dari package Models
import Models.Product;
import Models.CartItem;
import Models.Transaction;
 
// Import dari package Managers
import Managers.InventoryManager;
import Managers.ShoppingCart;
import Managers.PaymentQueue;
 
// Import dari package Database
import Database.DatabaseInitializer;

/**
 * ============================================================================
 * SISTEM POINT OF SALE (POS) CLI - KASIRKU (REFACTORED VERSION)
 * ============================================================================
 * 
 * Arsitektur Program (OOP & Struktur Data):
 * 1. Product          : Representasi model data barang/produk di POS.
 * 2. CartItem         : Representasi item di keranjang belanja (Produk + Qty).
 * 3. Transaction      : Model transaksi pembayaran (berisi data belanjaan + invoice).
 * 4. InventoryManager : Manajemen stok barang menggunakan HashMap<String, Product>.
 * 5. ShoppingCart     : Manajemen keranjang belanja aktif menggunakan Stack<CartItem>.
 * 6. PaymentQueue     : Manajemen antrean pembayaran kasir menggunakan Queue<Transaction>.
 * 7. DatabaseInitializer : Inisialisasi semua data dummy produk.
 * 8. Main             : Entry point program & User Interface CLI berbasis Scanner.
 */

public class Main {
    private static InventoryManager inventory;
    private static PaymentQueue paymentQueue;

    public static void main(String[] args) {
        // Inisialisasi manager
        inventory = new InventoryManager();
        paymentQueue = new PaymentQueue();

        // Inisialisasi data dummy produk dari DatabaseInitializer
        DatabaseInitializer.initializeProducts(inventory);

        ShoppingCart currentCart = new ShoppingCart();
        Scanner scanner = new Scanner(System.in);
        int transactionCounter = 1;
        boolean running = true;

        // Bersihkan layar saat pertama kali program dijalankan
        clearScreen();

        System.out.println("==================================================");
        System.out.println("          KasirKu - Point of Sale System          ");
        System.out.println("==================================================");

        while (running) {           
            System.out.println("\n========== MENU UTAMA KasirKu ==========");
            System.out.println("1. Lihat Katalog Barang");
            System.out.println("2. Tambah Barang ke Keranjang");
            System.out.println("3. Void/Undo Barang Terakhir");
            System.out.println("4. Lihat Keranjang Belanja Aktif");
            System.out.println("5. Selesai Belanja (Masuk Antrean Kasir)");
            System.out.println("6. Proses Pembayaran Antrean Terdepan");
            System.out.println("7. Keluar Program");
            System.out.println("====================================");
            
            // Status KasirKu saat ini
            System.out.printf("[STATUS] Item Keranjang: %d | Antrean Kasir: %d\n", 
                    currentCart.getCartItems().size(), paymentQueue.size());
            System.out.print("Pilih Opsi (1-7): ");

            int pilihan = -1;
            try {
                pilihan = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("-> Error: Input harus berupa angka!");
                pauseAndClear(scanner);
                continue;
            }

            switch (pilihan) {
                case 1:
                    displayCatalog();
                    pauseAndClear(scanner);
                    break;

                case 2:
                    addItemToCart(scanner, currentCart);
                    pauseAndClear(scanner);
                    break;

                case 3:
                    undoLastItem(currentCart);
                    pauseAndClear(scanner);
                    break;

                case 4:
                    currentCart.tampilkanKeranjang();
                    pauseAndClear(scanner);
                    break;

                case 5:
                    checkoutToQueue(scanner, currentCart, transactionCounter++);
                    currentCart = new ShoppingCart();
                    pauseAndClear(scanner);
                    break;

                case 6:
                    paymentQueue.dequeue();
                    pauseAndClear(scanner);
                    break;

                case 7:
                    System.out.println("Keluar dari program KasirKu. Terima kasih!");
                    running = false;
                    break;

                default:
                    System.out.println("-> Error: Pilihan menu tidak valid (1-7)!");
                    pauseAndClear(scanner);
                    break;
            }
        }
        scanner.close();
    }

    /**
     * Menampilkan katalog barang yang tersedia
     */
    private static void displayCatalog() {
        System.out.println("\n================= KATALOG BARANG ==================");
        System.out.printf("%-5s | %-20s | %-14s | %-4s\n", "ID", "Nama Produk", "Harga Satuan", "Stok");
        System.out.println("---------------------------------------------------");
        
        // Salin produk ke List untuk diurutkan berdasarkan ID
        List<Product> sortedProducts = new ArrayList<>(inventory.getAllProducts());
        sortedProducts.sort(Comparator.comparing(Product::getId));
        
        for (Product p : sortedProducts) {
            System.out.printf("%-5s | %-20s | Rp %,11.2f | %-4d\n", 
                    p.getId(), p.getNama(), p.getHarga(), p.getStok());
        }
        System.out.println("===================================================");
    }

    /**
     * Menambahkan item ke keranjang belanja
     */
    private static void addItemToCart(Scanner scanner, ShoppingCart currentCart) {
        System.out.print("Masukkan ID Produk: ");
        String id = scanner.nextLine().trim();
        Product p = inventory.cariBarang(id);
        
        if (p == null) {
            System.out.println("-> Error: ID Produk \"" + id + "\" tidak ditemukan!");
            return;
        }

        System.out.print("Masukkan Jumlah Beli: ");
        int qty = -1;
        try {
            qty = Integer.parseInt(scanner.nextLine().trim());
            if (qty <= 0) {
                System.out.println("-> Error: Jumlah beli harus lebih besar dari 0!");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("-> Error: Input jumlah harus berupa angka bulat!");
            return;
        }

        CartItem item = new CartItem(p, qty);
        boolean success = currentCart.addItem(item, inventory);
        if (success) {
            System.out.printf("-> Sukses: %s (%d pcs) masuk ke keranjang.\n", p.getNama(), qty);
        }
    }

    /**
     * Membatalkan/undo item terakhir dari keranjang
     */
    private static void undoLastItem(ShoppingCart currentCart) {
        CartItem undone = currentCart.undoLastItem(inventory);
        if (undone != null) {
            System.out.printf("-> Sukses: Void berhasil. Barang [%s] sebanyak %d pcs dikembalikan ke katalog.\n", 
                    undone.getProduct().getNama(), undone.getJumlahBeli());
        }
    }

    /**
     * Checkout keranjang dan masukkan ke antrean pembayaran
     */
    private static void checkoutToQueue(Scanner scanner, ShoppingCart currentCart, int transactionCounter) {
        if (currentCart.isEmpty()) {
            System.out.println("-> Error: Tidak dapat checkout karena keranjang belanja kosong!");
            return;
        }

        // Membuat transaksi baru
        String txnId = String.format("TXN-%04d", transactionCounter);
        Transaction newTxn = new Transaction(txnId, new ArrayList<>(currentCart.getCartItems()));
        
        // Masukkan ke antrean pembayaran
        paymentQueue.enqueue(newTxn);
        System.out.println("-> Sukses: Transaksi " + txnId + " berhasil didaftarkan ke antrean kasir.");
    }

    /**
     * Utility: Pause sebentar dan bersihkan layar
     */
    private static void pauseAndClear(Scanner scanner) {
        System.out.print("Tekan ENTER untuk lanjut...");
        scanner.nextLine();
        clearScreen();
    }

    /**
     * Utility: Bersihkan layar terminal
     */
    private static void clearScreen() {
        // Bekerja untuk Windows
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } 
            // Bekerja untuk Linux & macOS
            else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            // Jika tidak berhasil, print newlines sebagai fallback
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
}
