package sk.banik.finance.bill_to_toshl

interface QRCodeFoundListener {
    fun onQRCodeFound(_qrCode: String?)
    fun qrCodeNotFound()
}