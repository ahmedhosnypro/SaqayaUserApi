package com.saqaya.exception

class ResourceAlreadyExistsException(
    resource: String, private val field: String
) : RuntimeException(
    "$resource with $field already exists"
) {
    override val message: String?
        get() = super.message
}