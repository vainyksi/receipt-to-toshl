package sk.banik.finance.bill_to_toshl

class QRCodeListenerImpl : QRCodeDecodingListener {
    override fun onQRCodeDecodedSuccessfully(_qrCode: String?) {
        TODO("Not yet implemented")

        TODO("validate that input is the receipt ID")

        TODO("create toshl REST client" +
                "prepare receipt record locally" +
                "store internally - to be able to list scanned receipts" +
                "receipt record may be stored on toshl, it has to be able to update the record later with receipt data")

        TODO("create overdoklad REST client" +
                "load receipt data" +
                "update local receipt record")

        TODO("save receipt record to toshl" +
                "handle duplicates before the save" +
                "update existing, only those without receipt data")
    }

    override fun onQRCodeDecodeFailed() {
        TODO("Not yet implemented")
    }
}