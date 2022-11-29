package ir.dunijet.dunipool.apiManager.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CoinsAbout (
    var webSite :String? = "no-data",
    var twitter :String? = "no-data",
    var gitHub :String? = "no-data",
    var reddit :String? = "no-data",
    var desc :String? = "no-data",
        ) :Parcelable