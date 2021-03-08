package com.abstraksi.desktopwallpaper.data.remote.response
import com.google.gson.annotations.SerializedName


data class ListPhotoPinterestResponse(
        @SerializedName("status")
    val status: String = "",
        @SerializedName("code")
    val code: Int = 0,
        @SerializedName("data")
    val `data`: BoardData = BoardData(),
        @SerializedName("message")
    val message: String = "",
        @SerializedName("endpoint_name")
    val endpointName: String = ""
)

data class BoardData(
        @SerializedName("user")
    val user: User = User(),
        @SerializedName("pins")
    val pins: List<Pin> = listOf(),
        @SerializedName("board")
    val board: Board = Board()
)

data class User(
    @SerializedName("about")
    val about: String = "",
    @SerializedName("image_small_url")
    val imageSmallUrl: String = "",
    @SerializedName("location")
    val location: String = "",
    @SerializedName("id")
    val id: String = "",
    @SerializedName("pin_count")
    val pinCount: Int = 0,
    @SerializedName("follower_count")
    val followerCount: Int = 0,
    @SerializedName("profile_url")
    val profileUrl: String = "",
    @SerializedName("full_name")
    val fullName: String = ""
)

data class Pin(
        @SerializedName("repin_count")
    val repinCount: Int = 0,
        @SerializedName("native_creator")
    val nativeCreator: NativeCreator = NativeCreator(),
        @SerializedName("embed")
    val embed: Embed? = null,
        @SerializedName("id")
    val id: String = "",
        @SerializedName("pinner")
    val pinner: Pinner = Pinner(),
        @SerializedName("description")
    val description: String = "",
        @SerializedName("aggregated_pin_data")
    val aggregatedPinData: AggregatedPinData = AggregatedPinData(),
        @SerializedName("is_video")
    val isVideo: Boolean = false,
        @SerializedName("story_pin_data")
    val storyPinData: Any? = null,
        @SerializedName("domain")
    val domain: String = "",
        @SerializedName("attribution")
    val attribution: Any? = null,
        @SerializedName("link")
    val link: Any? = null,
        @SerializedName("images")
    val images: Images = Images(),
        @SerializedName("dominant_color")
    val dominantColor: String = ""
)

data class Embed(
    @SerializedName("src")
    val src: String = "",
    @SerializedName("height")
    val height: Int = 0,
    @SerializedName("width")
    val width: Int = 0,
    @SerializedName("type")
    val type: String = ""
)

data class Board(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("url")
    val url: String = "",
    @SerializedName("id")
    val id: String = "",
    @SerializedName("pin_count")
    val pinCount: Int = 0,
    @SerializedName("description")
    val description: String = "",
    @SerializedName("follower_count")
    val followerCount: Int = 0,
    @SerializedName("image_thumbnail_url")
    val imageThumbnailUrl: String = ""
)

data class NativeCreator(
    @SerializedName("id")
    val id: String = ""
)

data class Pinner(
    @SerializedName("about")
    val about: String = "",
    @SerializedName("image_small_url")
    val imageSmallUrl: String = "",
    @SerializedName("location")
    val location: String = "",
    @SerializedName("id")
    val id: String = "",
    @SerializedName("pin_count")
    val pinCount: Int = 0,
    @SerializedName("follower_count")
    val followerCount: Int = 0,
    @SerializedName("profile_url")
    val profileUrl: String = "",
    @SerializedName("full_name")
    val fullName: String = ""
)

data class AggregatedPinData(
    @SerializedName("aggregated_stats")
    val aggregatedStats: AggregatedStats = AggregatedStats()
)

data class Images(
        @SerializedName("237x")
    val x237: Image = Image(),
        @SerializedName("564x")
    val x564: Image = Image()
)

data class AggregatedStats(
    @SerializedName("saves")
    val saves: Int = 0,
    @SerializedName("done")
    val done: Int = 0
)

data class Image(
    @SerializedName("width")
    val width: Int = 0,
    @SerializedName("height")
    val height: Int = 0,
    @SerializedName("url")
    val url: String = ""
)
