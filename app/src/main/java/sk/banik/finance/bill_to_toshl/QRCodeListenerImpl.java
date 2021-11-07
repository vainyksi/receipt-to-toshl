package sk.banik.finance.bill_to_toshl;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import okhttp3.Response;
import sk.banik.finance.bill_to_toshl.overdoklad.client.OverDokladApiException;
import sk.banik.finance.bill_to_toshl.overdoklad.client.OverDokladClient;

public class QRCodeListenerImpl implements QRCodeDecodingListener {

    private final Button qrCodeFoundButton;
    private final Context context;

    public QRCodeListenerImpl(final Button qrCodeFoundButton, final Context context) {
        this.qrCodeFoundButton = qrCodeFoundButton;
        this.context = context;
    }

    @Override
    public void onQRCodeDecodedSuccessfully(@Nullable final String receiptId) {
        qrCodeFoundButton.setText(context.getString(R.string.qr_code_found, receiptId));
        qrCodeFoundButton.setVisibility(View.VISIBLE);

//        TODO("validate that input is the receipt ID")
//
//        TODO("create toshl REST client" +
//                "prepare receipt record locally" +
//                "store internally - to be able to list scanned receipts" +
//                "receipt record may be stored on toshl, it has to be able to update the record later with receipt data")
//
//        TODO("create overdoklad REST client" +
//                "load receipt data" +
//                "update local receipt record")
//

        // https://ekasa.financnasprava.sk/mdu/api/v1/opd/
        // https://ekasa.financnasprava.sk/mdu/api/v1/opd/receipt/find

//        final String receiptId = "O-4368AD5B1E7A40F2A8AD5B1E7A50F2F5";
        final OverDokladClient client = new OverDokladClient();
        final CompletableFuture<Response> responseFuture = client.findReceipt(receiptId);

        responseFuture.thenApply(response -> {
            try {
                final String body = response.body().string();
                qrCodeFoundButton.setText(body);


                System.out.println("RESPONSE: \n" + body);

                return new CompletableFuture<>().complete(body);
            } catch (IOException e) {
                return new CompletableFuture<>().completeExceptionally(
                        new OverDokladApiException("Loading data for receipt " + receiptId + " failed", e));
            }
        });

//        TODO("save receipt record to toshl" +
//                "handle duplicates before the save" +
//                "update existing, only those without receipt data")

    }

    @Override
    public void onQRCodeDecodeFailed() {
        qrCodeFoundButton.setVisibility(View.INVISIBLE);
    }
}
