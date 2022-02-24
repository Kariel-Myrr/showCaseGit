package com.antonov.hw4.service

val defaultString = ""
val defaultBoolean = false
val defaultInt = 0

data class PhotoJson(
    val currentUserCollections: List<CurrentUserCollectionsItem?>? = listOf(
        CurrentUserCollectionsItem()
    ),
    val color: String? = defaultString,
    val createdAt: String? = defaultString,
    val description: String? = defaultString,
    val likedByUser: Boolean? = defaultBoolean,
    val urls: Urls? = Urls(),
    val updatedAt: String? = defaultString,
    val width: Int? = defaultInt,
    val blurHash: String? = defaultString,
    val links: Links? = Links(),
    val id: String? = defaultString,
    val user: User? = User(),
    val height: Int? = defaultInt,
    val likes: Int? = defaultInt
)

data class Links(
    val portfolio: String? = defaultString,
    val self: String? = defaultString,
    val html: String? = defaultString,
    val photos: String? = defaultString,
    val likes: String? = defaultString,
    val download: String? = defaultString,
    val download_location: String? = defaultString
)

data class ProfileImage(
    val small: String? = defaultString,
    val large: String? = defaultString,
    val medium: String? = defaultString
)

data class User(
    val totalPhotos: Int? = defaultInt,
    val twitterUsername: String? = defaultString,
    val bio: String? = defaultString,
    val totalLikes: Int? = defaultInt,
    val portfolioUrl: String? = defaultString,
    val profileImage: ProfileImage? = ProfileImage(),
    val name: String? = defaultString,
    val location: String? = defaultString,
    val totalCollections: Int? = defaultInt,
    val links: Links? = Links(),
    val id: String? = defaultString,
    val instagramUsername: String? = defaultString,
    val username: String? = defaultString
)

data class Urls(
    val small: String? = defaultString,
    val thumb: String? = defaultString,
    val raw: String? = defaultString,
    val regular: String? = defaultString,
    val full: String? = defaultString,
)

data class CurrentUserCollectionsItem(
    val coverPhoto: Any? = "",
    val updatedAt: String? = defaultString,
    val lastCollectedAt: String? = defaultString,
    val id: Int? = defaultInt,
    val title: String? = defaultString,
    val publishedAt: String? = defaultString,
    val user: Any? = ""
)