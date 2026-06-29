package Managers;
 
import java.util.*;
import Models.CartItem;
import Models.Product;
import Managers.InventoryManager;

/**
 * ==========================================
 * CLASS MANAGER: SHOPPING CART (STACK)
 * ==========================================
 * Manajemen keranjang belanja aktif menggunakan Stack<CartItem>.
 * Mengimplementasikan prinsip LIFO (Last In First Out) untuk fitur Void/Undo.
 */
public class ShoppingCart {
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
     *     waktu konstan karena hanya membutuhkan assignment reference ke array.
     *   * Worst Case: O(N) / Linear Time. Terjadi ketika array dinamis perlu di-resize (expand capacity).
     *     Resize Array membutuhkan copying seluruh elemen ke array baru yang lebih besar, 
     *     namun ini bersifat amortized O(1) per operation.
     */
    public boolean addItem(CartItem item, InventoryManager inventory) {
        Product p = item.getProduct();
        if (p.getStok() < item.getJumlahBeli()) {
            System.out.println("-> Error: Stok tidak cukup! Stok tersedia: " + p.getStok() + " pcs");
            return false;
        }
        
        // Mengurangi stok dari inventory
        p.setStok(p.getStok() - item.getJumlahBeli());
        
        // Menambahkan item ke keranjang (PUSH)
        cart.push(item);
        return true;
    }

    /**
     * ========================================================================
     * ANALISIS STRUKTUR DATA & KOMPLEKSITAS WAKTU: POP (UNDO ITEM)
     * ========================================================================
     * - Operasi Pop:
     *   Mengeluarkan elemen terakhir yang dimasukkan (prinsip LIFO). Sangat sesuai untuk 
     *   fitur pembatalan barang terakhir (Void/Undo).
     * 
     * - Kompleksitas Waktu (Time Complexity):
     *   * Rata-rata (Average Case): O(1) / Constant Time.
     *   * Penjelasan: Operasi pop hanya menghapus elemen dari bagian atas Stack tanpa 
     *     perlu shifting atau reordering elemen lainnya.
     */
    public CartItem undoLastItem(InventoryManager inventory) {
        if (cart.isEmpty()) {
            System.out.println("-> Error: Keranjang belanja kosong!");
            return null;
        }
        
        // Mengeluarkan item terakhir dari keranjang (POP)
        CartItem lastItem = cart.pop();
        
        // Mengembalikan stok ke inventory
        Product p = lastItem.getProduct();
        p.setStok(p.getStok() + lastItem.getJumlahBeli());
        
        return lastItem;
    }

    public void tampilkanKeranjang() {
        if (cart.isEmpty()) {
            System.out.println("-> Keranjang belanja kosong!");
            return;
        }
        
        System.out.println("\n================= KERANJANG BELANJA ==================");
        double total = 0;
        int itemCount = 0;
        
        // Iterasi dari bawah ke atas untuk menampilkan urutan yang benar
        for (CartItem item : cart) {
            System.out.println(item);
            total += item.getSubtotal();
            itemCount++;
        }
        
        System.out.println("======================================================");
        System.out.printf("Total Item: %d | Total Harga: Rp %,12.2f\n", itemCount, total);
        System.out.println("======================================================");
    }

    public Stack<CartItem> getCartItems() {
        return cart;
    }

    public boolean isEmpty() {
        return cart.isEmpty();
    }
}
