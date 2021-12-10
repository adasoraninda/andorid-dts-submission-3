package com.adasoraninda.githubuserdts.common

sealed class DaoMessageResult {
    data class Save(val message: Int? = null) : DaoMessageResult()
    data class Delete(val message: Int? = null) : DaoMessageResult()
}