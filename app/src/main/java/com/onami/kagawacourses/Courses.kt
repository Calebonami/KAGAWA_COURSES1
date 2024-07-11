package com.onami.kagawacourses

data class Course(
    val id: String = "",
    val title: String = "",
    val price: String = "",
    val imageUrl: String = "",
    val completed: Boolean = false,
    val favorite: Boolean = false,
    val archived: Boolean = false,
    val description: String = "",
    val keywords: List<String> = emptyList()
) {
    // No-argument constructor required by Firestore
    constructor() : this("", "", "", "", false, false, false, "",emptyList())

    companion object {
        const val FIELD_TITLE = "title"
        const val FIELD_PRICE = "price"
        const val FIELD_IMAGE_URL = "imageUrl"
        const val FIELD_DESCRIPTION = "description"
        const val FIELD_KEYWORDS = "keywords"
    }
}


