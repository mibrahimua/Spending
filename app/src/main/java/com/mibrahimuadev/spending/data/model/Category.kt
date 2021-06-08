package com.mibrahimuadev.spending.data.model

data class Category(
    val categoryId: Int = 0,

    val categoryName: String? = "",

    val categoryType: CategoryType,

    val iconId: Int? = 0,

    val iconName: String? = "",
)