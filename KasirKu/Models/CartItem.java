package Models;
/**
 * ==========================================
 * CLASS MODEL: CART ITEM
 * ==========================================
 * Representasi item di keranjang belanja (Produk + Qty).
 * Menyimpan referensi produk dan jumlah yang dibeli.
 */
public class CartItem {
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
