package sk.banik.finance.bill_to_toshl;

import android.graphics.ImageFormat;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;

import java.nio.ByteBuffer;

public class QRCodeImageDecoder implements ImageAnalysis.Analyzer{
    private final QRCodeDecodingListener listener;

    public QRCodeImageDecoder(final QRCodeDecodingListener listener) {
        this.listener = listener;
    }

    @Override
    public void analyze(@NonNull final ImageProxy image) {
        if (image.getFormat() == ImageFormat.YUV_420_888
                || image.getFormat() == ImageFormat.YUV_422_888
                || image.getFormat() == ImageFormat.YUV_444_888) {

            final ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
            final byte[] imageData = new byte[byteBuffer.capacity()];
            byteBuffer.get(imageData);

            final PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
                    imageData,
                    image.getWidth(), image.getHeight(),
                    0, 0,
                    image.getWidth(), image.getHeight(),
                    false
            );

            final BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                final Result result = new QRCodeMultiReader().decode(binaryBitmap);
                listener.onQRCodeDecodedSuccessfully(result.getText());

            } catch (FormatException | ChecksumException | NotFoundException e) {
                listener.onQRCodeDecodeFailed();
            }
        }

        image.close();
    }
}
