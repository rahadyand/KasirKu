import java.util.*;

/**
 * ============================================================================
 * SISTEM POINT OF SALE (POS) CLI - KASIRKU
 * ============================================================================
 * 
 * Arsitektur Program (OOP & Struktur Data):
 * 1. Product          : Representasi model data barang/produk di POS.
 * 2. CartItem         : Representasi item di keranjang belanja (Produk + Qty).
 * 3. Transaction      : Model transaksi pembayaran (berisi data belanjaan + invoice).
 * 4. InventoryManager : Manajemen stok barang menggunakan HashMap<String, Product>.
 * 5. ShoppingCart     : Manajemen keranjang belanja aktif menggunakan Stack<CartItem>.
 * 6. PaymentQueue     : Manajemen antrean pembayaran kasir menggunakan Queue<Transaction>.
 * 7. Main             : Entry point program & User Interface CLI berbasis Scanner.
 */

// ==========================================
// 1. CLASS MODEL: PRODUCT
// ==========================================
class Product {
    private String id;
    private String nama;
    private double harga;
    private int stok;

    public Product(String id, String nama, double harga, int stok) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
        this.stok = stok;
    }

    // Getter dan Setter - Mengambil dan mengubah atribut produk
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public double getHarga() { return harga; }
    public void setHarga(double harga) { this.harga = harga; }
    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }

    @Override
    public String toString() {
        return String.format("ID: %-5s | Nama: %-20s | Harga: Rp %,10.2f | Stok: %3d", 
                id, nama, harga, stok);
    }
}

// ==========================================
// 2. CLASS MODEL: CART ITEM
// ==========================================
class CartItem {
    private Product product;
    private int jumlahBeli;

    public CartItem(Product product, int jumlahBeli) {
        this.product = product;
        this.jumlahBeli = jumlahBeli;
    }

    // Getter dan Setter - Mengambil dan mengubah atribut item keranjang
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public int getJumlahBeli() { return jumlahBeli; }
    public void setJumlahBeli(int jumlahBeli) { this.jumlahBeli = jumlahBeli; }

    // Menghitung subtotal belanjaan per item
    public double getSubtotal() {
        return product.getHarga() * jumlahBeli;
    }

    @Override
    public String toString() {
        return String.format("%-20s x %-3d (Rp %,10.2f) -> Subtotal: Rp %,12.2f", 
                product.getNama(), jumlahBeli, product.getHarga(), getSubtotal());
    }
}

// ==========================================
// 3. CLASS MODEL: TRANSACTION
// ==========================================
class Transaction {
    private String transactionId;
    private List<CartItem> items;
    private double totalHarga;

    public Transaction(String transactionId, Stack<CartItem> cartStack) {
        this.transactionId = transactionId;
        
        // Mengubah Stack menjadi List untuk menyimpan riwayat item transaksi secara berurutan
        this.items = new ArrayList<>(cartStack);
        this.totalHarga = 0;
        for (CartItem item : this.items) {
            this.totalHarga += item.getSubtotal();
        }
    }

    public String getTransactionId() { return transactionId; }
    public List<CartItem> getItems() { return items; }
    public double getTotalHarga() { return totalHarga; }
}

// ==========================================
// 4. CLASS MANAGER: INVENTORY MANAGER (HASHMAP)
// ==========================================
class InventoryManager {
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

// ==========================================
// 5. CLASS MANAGER: SHOPPING CART (STACK)
// ==========================================
class ShoppingCart {
    private Stack<CartItem> cart;

    public ShoppingCart() {
        // Menggunakan Stack untuk mengimplementasikan prinsip LIFO (Last In First Out)
        this.cart = new Stack<>();
    }

    /**
     * ========================================================================
     * ANALISIS STRUKTUR DATA & KOMPLEKSITAS WAKTU: PUSH (TAMBAH ITEM)
     * ========================================================================
     * - Mengapa Menggunakan Stack untuk Keranjang Belanja?
     *   Operasi keranjang belanja membutuhkan fitur pembatalan transaksi terakhir (Undo/Void). 
     *   Struktur data Stack sangat cocok karena elemen terakhir yang dimasukkan adalah elemen 
     *   pertama yang akan dikeluarkan (prinsip LIFO).
     * 
     * - Kompleksitas Waktu (Time Complexity):
     *   * Rata-rata (Average Case): O(1) / Constant Time.
     *   * Penjelasan: Operasi push menambahkan elemen baru ke bagian atas (top) Stack. Di Java, 
     *     Stack didukung oleh array dinamis (Vector). Penambahan elemen di indeks akhir membutuhkan 
     *     waktu konstan O(1). Jika array penuh dan memerlukan resizing, kompleksitasnya menjadi O(N) 
     *     untuk menyalin elemen lama, tetapi secara rata-rata teramortisasi menjadi O(1).
     */
    public boolean addItem(CartItem item, InventoryManager im) {
        if (item == null) return false;

        Product p = im.cariBarang(item.getProduct().getId());
        if (p == null) {
            System.out.println("-> Error: Produk tidak ditemukan di katalog!");
            return false;
        }

        // Validasi stok sebelum dimasukkan ke keranjang
        if (p.getStok() < item.getJumlahBeli()) {
            System.out.println("-> Error: Stok tidak cukup! (Stok saat ini: " + p.getStok() + ")");
            return false;
        }

        // Potong stok di database barang secara real-time
        p.setStok(p.getStok() - item.getJumlahBeli());

        // Push ke dalam stack
        cart.push(item);
        return true;
    }

    /**
     * ========================================================================
     * ANALISIS STRUKTUR DATA & KOMPLEKSITAS WAKTU: POP (VOID/UNDO ITEM TERAKHIR)
     * ========================================================================
     * - Prinsip Kerja:
     *   Mengambil elemen paling atas dari Stack (elemen yang paling baru masuk), 
     *   kemudian mengembalikan stok barang tersebut ke database InventoryManager.
     * 
     * - Kompleksitas Waktu (Time Complexity):
     *   * O(1) / Constant Time.
     *   * Penjelasan: Operasi pop menghapus elemen paling ujung (top) pada stack. Karena kita 
     *     langsung mengakses indeks teratas tanpa perlu memindahkan atau menggeser elemen-elemen 
     *     lainnya (seperti pada ArrayList jika menghapus elemen di tengah), waktu eksekusi selalu 
     *     konstan terlepas dari berapa banyak item yang ada di keranjang.
     */
    public CartItem undoLastItem(InventoryManager im) {
        if (cart.isEmpty()) {
            System.out.println("-> Peringatan: Keranjang belanja kosong! Tidak ada item untuk di-Undo.");
            return null;
        }

        // Keluarkan item teratas dari stack (LIFO)
        CartItem removedItem = cart.pop();

        // Kembalikan stok produk ke database barang
        Product p = im.cariBarang(removedItem.getProduct().getId());
        if (p != null) {
            p.setStok(p.getStok() + removedItem.getJumlahBeli());
        }

        return removedItem;
    }

    public boolean isEmpty() {
        return cart.isEmpty();
    }

    public Stack<CartItem> getCartItems() {
        return cart;
    }

    public void tampilkanKeranjang() {
        if (cart.isEmpty()) {
            System.out.println("Keranjang belanja kosong.");
            return;
        }
        System.out.println("--- DAFTAR ITEM DI KERANJANG (LIFO Order) ---");
        for (int i = cart.size() - 1; i >= 0; i--) {
            System.out.println(cart.get(i));
        }
    }
}

// ==========================================
// 6. CLASS MANAGER: PAYMENT QUEUE (QUEUE)
// ==========================================
class PaymentQueue {
    private Queue<Transaction> antrean;

    public PaymentQueue() {
        // Menggunakan LinkedList sebagai implementasi konkret antrean (Queue)
        this.antrean = new LinkedList<>();
    }

    /**
     * ========================================================================
     * ANALISIS STRUKTUR DATA & KOMPLEKSITAS WAKTU: ENQUEUE (MASUK ANTREAN KASIR)
     * ========================================================================
     * - Mengapa Menggunakan Queue?
     *   Antrean kasir mengikuti prinsip keadilan pelayanan: pelanggan yang pertama kali 
     *   selesai belanja harus dilayani terlebih dahulu (prinsip FIFO - First In First Out).
     * 
     * - Kompleksitas Waktu (Time Complexity):
     *   * O(1) / Constant Time.
     *   * Penjelasan: Enqueue dilakukan dengan menyisipkan elemen di akhir (tail) LinkedList. 
     *     Karena LinkedList menyimpan pointer referensi langsung ke node tail, penyisipan elemen 
     *     baru di bagian belakang dapat dilakukan dalam waktu konstan tanpa menyusuri antrean.
     */
    public void enqueue(Transaction txn) {
        if (txn != null) {
            antrean.offer(txn);
        }
    }

    /**
     * ========================================================================
     * ANALISIS STRUKTUR DATA & KOMPLEKSITAS WAKTU: DEQUEUE (PROSES PEMBAYARAN KASIR)
     * ========================================================================
     * - Prinsip Kerja:
     *   Mengambil dan mengeluarkan transaksi pelanggan dari antrean terdepan (head) 
     *   untuk diproses pembayarannya, kemudian mencetak struk belanja.
     * 
     * - Kompleksitas Waktu (Time Complexity):
     *   * O(1) / Constant Time.
     *   * Penjelasan: Operasi dequeue dilakukan dengan mengambil elemen terdepan dari LinkedList. 
     *     Karena referensi node head disimpan secara langsung, kita hanya perlu mengubah pointer 
     *     head = head.next dan memutuskan link node yang dihapus. Tidak ada pergeseran elemen 
     *     yang terjadi, sehingga kompleksitasnya selalu O(1).
     */
    public Transaction dequeue() {
        if (antrean.isEmpty()) {
            System.out.println("-> Peringatan: Antrean kasir kosong! Tidak ada transaksi untuk diproses.");
            return null;
        }

        // Ambil dan keluarkan transaksi terdepan (FIFO)
        Transaction txn = antrean.poll();

        // Proses Pembayaran dan Cetak Struk Belanja
        System.out.println("\n==================================================");
        System.out.println("            STRUK PEMBAYARAN RESMI                ");
        System.out.println("==================================================");
        System.out.println("ID Transaksi : " + txn.getTransactionId());
        System.out.println("--------------------------------------------------");
        int index = 1;
        for (CartItem item : txn.getItems()) {
            System.out.printf("%d. %-18s x%-3d  Rp %,12.2f\n", 
                    index++, item.getProduct().getNama(), item.getJumlahBeli(), item.getSubtotal());
        }
        System.out.println("--------------------------------------------------");
        System.out.printf("TOTAL BAYAR  : Rp %,12.2f\n", txn.getTotalHarga());
        System.out.println("STATUS       : LUNAS");
        System.out.println("==================================================\n");

        return txn;
    }

    public int size() {
        return antrean.size();
    }

    public boolean isEmpty() {
        return antrean.isEmpty();
    }
}

// ==========================================
// 7. MAIN CLASS (CLI & RUNNER)
// ==========================================
public class Main {
    // Method untuk membersihkan layar terminal (clear screen) secara cross-platform
    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }

    // Method pembantu untuk menahan tampilan agar tidak langsung terhapus, lalu clear screen
    private static void pauseAndClear(Scanner scanner) {
        System.out.print("\nTekan Enter untuk melanjutkan...");
        scanner.nextLine();
        clearScreen();
    }

    public static void main(String[] args) {
        InventoryManager inventory = new InventoryManager();
        PaymentQueue paymentQueue = new PaymentQueue();
        
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
                    pauseAndClear(scanner);
                    break;

                case 2:
                    System.out.print("Masukkan ID Produk: ");
                    String id = scanner.nextLine().trim();
                    Product p = inventory.cariBarang(id);
                    if (p == null) {
                        System.out.println("-> Error: ID Produk \"" + id + "\" tidak ditemukan!");
                        pauseAndClear(scanner);
                        break;
                    }

                    System.out.print("Masukkan Jumlah Beli: ");
                    int qty = -1;
                    try {
                        qty = Integer.parseInt(scanner.nextLine().trim());
                        if (qty <= 0) {
                            System.out.println("-> Error: Jumlah beli harus lebih besar dari 0!");
                            pauseAndClear(scanner);
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("-> Error: Input jumlah harus berupa angka bulat!");
                        pauseAndClear(scanner);
                        break;
                    }

                    CartItem item = new CartItem(p, qty);
                    boolean success = currentCart.addItem(item, inventory);
                    if (success) {
                        System.out.printf("-> Sukses: %s (%d pcs) masuk ke keranjang.\n", p.getNama(), qty);
                    }
                    pauseAndClear(scanner);
                    break;

                case 3:
                    CartItem undone = currentCart.undoLastItem(inventory);
                    if (undone != null) {
                        System.out.printf("-> Sukses: Void berhasil. Barang [%s] sebanyak %d pcs dikembalikan ke katalog.\n", 
                                undone.getProduct().getNama(), undone.getJumlahBeli());
                    }
                    pauseAndClear(scanner);
                    break;

                case 4:
                    currentCart.tampilkanKeranjang();
                    pauseAndClear(scanner);
                    break;

                case 5:
                    if (currentCart.isEmpty()) {
                        System.out.println("-> Error: Tidak dapat checkout karena keranjang belanja kosong!");
                        pauseAndClear(scanner);
                        break;
                    }

                    // Membuat transaksi baru
                    String txnId = String.format("TXN-%04d", transactionCounter++);
                    Transaction newTxn = new Transaction(txnId, currentCart.getCartItems());
                    
                    // Masukkan ke antrean pembayaran
                    paymentQueue.enqueue(newTxn);
                    System.out.println("-> Sukses: Transaksi " + txnId + " berhasil didaftarkan ke antrean kasir.");

                    // Reset keranjang belanja aktif untuk pelanggan berikutnya
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
}
