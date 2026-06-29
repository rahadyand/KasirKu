package Models;
import java.util.*;

/**
 * ==========================================
 * CLASS MODEL: TRANSACTION
 * ==========================================
 * Model transaksi pembayaran (berisi data belanjaan + invoice).
 * Menyimpan ID transaksi, daftar item yang dibeli, dan total harga.
 */
public class Transaction {
    private String transactionId;
    private List<CartItem> items;
    private double totalHarga;

    public Transaction(String transactionId, List<CartItem> cartItems) {
        this.transactionId = transactionId;
        
        // Mengubah Stack menjadi List untuk menyimpan riwayat item transaksi secara berurutan
        this.items = new ArrayList<>(cartItems);
        this.totalHarga = 0;
        for (CartItem item : this.items) {
            this.totalHarga += item.getSubtotal();
        }
    }

    public String getTransactionId() { return transactionId; }
    public List<CartItem> getItems() { return items; }
    public double getTotalHarga() { return totalHarga; }
}
