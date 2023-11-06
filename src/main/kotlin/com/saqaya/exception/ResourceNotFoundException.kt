package com.saqaya.exception

class ResourceNotFoundException(
    resourceName: String?, private val resourceId: String?
) : RuntimeException(
    String.format("%s not found with id %s", resourceName, resourceId)
) {
    override val message: String?
        get() = super.message
}