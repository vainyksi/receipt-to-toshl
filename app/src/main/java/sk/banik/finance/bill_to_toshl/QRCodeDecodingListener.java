package sk.banik.finance.bill_to_toshl;

public interface QRCodeDecodingListener {

    void onQRCodeDecodedSuccessfully(String qrCode);

    void onQRCodeDecodeFailed();
}
