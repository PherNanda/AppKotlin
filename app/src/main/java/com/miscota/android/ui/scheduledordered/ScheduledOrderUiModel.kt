package com.miscota.android.ui.scheduledordered

import android.os.Build
import androidx.annotation.RequiresApi
import com.miscota.android.api.autoship.response.AutoShipResponse
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

sealed class ScheduledOrderUiModel(
    open val uid: Long = UUID.randomUUID().mostSignificantBits
) {

    object LoaderItem : ScheduledOrderUiModel()

    object SpacerItem : ScheduledOrderUiModel()

    data class ButtonsItem(
        val autoShipId: String
    ) : ScheduledOrderUiModel()

    data class Product(
        override val uid: Long = UUID.randomUUID().leastSignificantBits,
        val productId: Long,
        val productName: String,
        val price: String,
        val oldPrice: String,
        val discount: String,
        val image: String,
        val description: String,
        val quantity: Int,
    ) : ScheduledOrderUiModel()

    data class Header(
        override val uid: Long = UUID.randomUUID().leastSignificantBits,
        val scheduleNo: String,
        val nextDeliver: String?,
        val frequency: Int,
        val shippingAddress: String,
        val estimation: String,
        val status: String,
    ) : ScheduledOrderUiModel()
}


@RequiresApi(Build.VERSION_CODES.O)
fun AutoShipResponse.toScheduledOrderUiModel(estimation: Double): ScheduledOrderUiModel{
    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    //val date: Date? = fmt.parse(this.nextDeliveryDate) //fmtOut.format(date)
    val fmtOut = SimpleDateFormat("dd 'de' MMMM yyyy", Locale.getDefault())
    //val deliverDate: String = fmtOut.format(nextDeliveryDate) ?: ""
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
    var nextDeliveredDate: LocalDate? = null
    var nextDeliveredDates: String? = null

    if (nextDeliveryDate == "Pendiente") {
        nextDeliveredDates = nextDeliveryDate
        println(" nextDeliveredDatesss $nextDeliveryDate")
    }
    else if (nextDeliveryDate != "Pendiente" && nextDeliveryDate != ""){
        println(" line nextDeliveryDate $nextDeliveryDate")
        nextDeliveredDate = LocalDate.parse(nextDeliveryDate, formatter)
        println(" nextDeliveredDate $nextDeliveredDate")
    }else{

        nextDeliveredDates = "Sin datos"
    }

    return ScheduledOrderUiModel.Header(
        scheduleNo = "Pedido programado #"+this.autoShippingId.toString(),
        nextDeliver = if (nextDeliveryDate == "Pendiente") nextDeliveryDate else if (nextDeliveryDate != "Pendiente" && nextDeliveryDate != "") "${nextDeliveredDate?.dayOfMonth}-${nextDeliveredDate?.monthValue}-${nextDeliveredDate?.year}" else nextDeliveredDates,
        frequency = frequency.toInt(),
        shippingAddress = "Shipping Address",
        estimation = "$estimation €",
        status = "Active"
    )
}

fun AutoShipResponse.Item.toScheduledOrderUiModel(): ScheduledOrderUiModel{
    return ScheduledOrderUiModel.Product(
        productId = this.productId,
        quantity = this.quantity,
        price = this.price.toString(),
        oldPrice = "0,0e",
        discount = "-1%",
        image = this.thumb,
        description = "description",
        productName = this.title
    )
}

fun AutoShipResponse.toScheduledOrderUiModelButtons(): ScheduledOrderUiModel {
    return ScheduledOrderUiModel.ButtonsItem(
        autoShipId = this.autoShippingId.toString()
    )
}