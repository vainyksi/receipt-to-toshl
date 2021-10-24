package sk.banik.finance.bill_to_toshl

interface QRCodeDecodingListener {

    fun onQRCodeDecodedSuccessfully(_qrCode: String?)

    fun onQRCodeDecodeFailed()

}