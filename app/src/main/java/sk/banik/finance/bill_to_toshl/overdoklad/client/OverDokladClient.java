package sk.banik.finance.bill_to_toshl.overdoklad.client;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OverDokladClient {
    // curl --location --request POST 'https://ekasa.financnasprava.sk/mdu/api/v1/opd/receipt/find' \
    //--header 'Content-Type: application/json' \
    //--data-raw '{
    //    "receiptId" : "O-4368AD5B1E7A40F2A8AD5B1E7A50F2F5"
    //}'

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static long lastRequestTime = 0;

    private final OkHttpClient client;

    public OverDokladClient() {
        client = new OkHttpClient.Builder()
                .build();
    }

    public CompletableFuture<Response> findReceipt(final String receiptId) {

        while (lastRequestTime + 5_000 > System.currentTimeMillis()) {
            System.out.println("---------- Security limit ------------------");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        lastRequestTime = System.currentTimeMillis();

        final String json = "{\n" +
                "    \"receiptId\" : \"" + receiptId + "\"\n" +
                "}";
        final RequestBody body = RequestBody.create(json, JSON);
        final Request request = new Request.Builder()
                .url("https://ekasa.financnasprava.sk/mdu/api/v1/opd/receipt/find")
                .post(body)
                .build();

        final CompletableFuture<Response> result = new CompletableFuture<>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull final Call call, @NonNull final IOException e) {
                result.completeExceptionally(
                        new OverDokladApiException("Loading data for receipt " + receiptId + " failed", e));
            }

            @Override
            public void onResponse(@NonNull final Call call, @NonNull final Response response) {
                result.complete(response);
            }
        });
        return result;
    }
}
